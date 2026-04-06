import { useState, useEffect, useCallback } from 'react';
import { adminAPI } from '../../services/api';
import toast from 'react-hot-toast';

const Tab = ({ label, active, onClick, badge }) => (
  <button onClick={onClick} style={{
    padding: '8px 20px', borderRadius: 8, border: 'none', cursor: 'pointer',
    fontWeight: active ? 600 : 400, fontSize: '0.875rem',
    background: active ? '#4f46e5' : 'transparent',
    color: active ? '#fff' : 'var(--muted)',
    display: 'flex', alignItems: 'center', gap: '0.4rem', transition: 'all 0.15s',
  }}>
    {label}
    {badge > 0 && (
      <span style={{ background: '#ef4444', color: '#fff', padding: '1px 7px', borderRadius: 10, fontSize: '0.72rem', fontWeight: 700 }}>
        {badge}
      </span>
    )}
  </button>
);

const StatCard = ({ label, value, color, icon }) => (
  <div className="card" style={{ borderColor: `${color}33`, flex: '1 1 150px' }}>
    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
      <div>
        <div style={{ fontSize: '0.72rem', color: 'var(--muted)', fontWeight: 600, textTransform: 'uppercase', letterSpacing: '0.06em', marginBottom: 8 }}>{label}</div>
        <div style={{ fontSize: '2rem', fontFamily: 'Syne', fontWeight: 800, color, lineHeight: 1 }}>{value ?? '—'}</div>
      </div>
      <div style={{ fontSize: '1.4rem', opacity: 0.6 }}>{icon}</div>
    </div>
  </div>
);

export default function AdminPanel() {
  const [tab,     setTab]     = useState('dashboard');
  const [stats,   setStats]   = useState(null);
  const [users,   setUsers]   = useState([]);
  const [pending, setPending] = useState([]);
  const [loading, setLoading] = useState(true);

  const loadAll = useCallback(async () => {
    setLoading(true);
    try {
      const [statsRes, usersRes, pendingRes] = await Promise.all([
        adminAPI.getDashboard(),
        adminAPI.getUsers(),
        adminAPI.getPending(),
      ]);
      setStats(statsRes.data);
      setUsers(usersRes.data);
      setPending(pendingRes.data);
    } catch {
      toast.error('Failed to load admin data');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { loadAll(); }, [loadAll]);

  const handleApprove = async (id) => {
    try { await adminAPI.approve(id); toast.success('Membership approved!'); loadAll(); }
    catch { toast.error('Failed to approve'); }
  };

  const handleReject = async (id) => {
    try { await adminAPI.reject(id); toast.success('Membership rejected'); loadAll(); }
    catch { toast.error('Failed to reject'); }
  };

  const handleToggle = async (id, name) => {
    try { await adminAPI.toggleUser(id); toast.success(`${name}'s status updated`); loadAll(); }
    catch { toast.error('Failed to update'); }
  };

  const handlePromote = async (id, currentRole) => {
    const next = currentRole === 'STUDENT' ? 'CLUB_HEAD' : currentRole === 'CLUB_HEAD' ? 'ADMIN' : 'STUDENT';
    if (!window.confirm(`Change role to ${next}?`)) return;
    try { await adminAPI.promoteUser(id, next); toast.success(`Role updated to ${next}`); loadAll(); }
    catch { toast.error('Failed to update role'); }
  };

  const ROLE_COLOR = { ADMIN: 'var(--primary-light)', CLUB_HEAD: 'var(--accent)', STUDENT: 'var(--muted)' };
  const ROLE_BG    = { ADMIN: 'rgba(79,70,229,0.12)', CLUB_HEAD: 'rgba(245,158,11,0.12)', STUDENT: 'var(--surface2)' };

  return (
    <div className="page">
      {/* Header */}
      <div style={{ marginBottom: '2rem' }}>
        <h1 style={{ fontSize: '1.9rem', fontWeight: 800 }}>Admin Panel</h1>
        <p style={{ color: 'var(--muted)', marginTop: '0.25rem', fontSize: '0.9rem' }}>Manage users, clubs, and membership requests</p>
      </div>

      {/* Tabs */}
      <div style={{ display: 'flex', gap: '0.4rem', marginBottom: '2rem', padding: '4px', background: 'var(--surface)', borderRadius: 10, width: 'fit-content', border: '1px solid var(--border)' }}>
        <Tab label="Dashboard"   active={tab === 'dashboard'} onClick={() => setTab('dashboard')} />
        <Tab label="Pending"     active={tab === 'pending'}   onClick={() => setTab('pending')}   badge={pending.length} />
        <Tab label="Users"       active={tab === 'users'}     onClick={() => setTab('users')} />
      </div>

      {loading ? (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))', gap: '1rem' }}>
          {[1,2,3,4,5].map(i => <div key={i} className="skeleton" style={{ height: 100, borderRadius: 'var(--radius-lg)' }} />)}
        </div>
      ) : (
        <>
          {/* ─── Dashboard Tab ─── */}
          {tab === 'dashboard' && stats && (
            <div>
              <div style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap', marginBottom: '2rem' }}>
                <StatCard label="Total Users"       value={stats.totalUsers}      color="#4f46e5" icon="👥" />
                <StatCard label="Active Clubs"      value={stats.totalClubs}      color="#10b981" icon="🏛️" />
                <StatCard label="Total Events"      value={stats.totalEvents}     color="#f59e0b" icon="📅" />
                <StatCard label="Pending Requests"  value={stats.pendingRequests} color="#ef4444" icon="⏳" />
                <StatCard label="Active Members"    value={stats.approvedMembers} color="#06b6d4" icon="✅" />
              </div>

              {pending.length > 0 && (
                <div className="card" style={{ borderColor: 'rgba(239,68,68,0.3)' }}>
                  <h3 style={{ fontWeight: 700, marginBottom: '1rem', color: 'var(--danger)' }}>
                    ⏳ {pending.length} Pending Membership Request{pending.length !== 1 ? 's' : ''}
                  </h3>
                  <p style={{ color: 'var(--muted)', fontSize: '0.875rem' }}>
                    Go to the <button onClick={() => setTab('pending')} style={{ background: 'none', border: 'none', color: 'var(--primary-light)', cursor: 'pointer', fontSize: '0.875rem', fontWeight: 600 }}>Pending tab</button> to review and approve or reject them.
                  </p>
                </div>
              )}
            </div>
          )}

          {/* ─── Pending Tab ─── */}
          {tab === 'pending' && (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.875rem' }}>
              {pending.length === 0 ? (
                <div className="card" style={{ textAlign: 'center', padding: '4rem', color: 'var(--muted)' }}>
                  <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>✅</div>
                  <p>All caught up! No pending requests.</p>
                </div>
              ) : pending.map(m => (
                <div key={m.membershipId} className="card" style={{
                  display: 'flex', justifyContent: 'space-between',
                  alignItems: 'center', flexWrap: 'wrap', gap: '1rem',
                  padding: '1.1rem 1.5rem',
                }}>
                  <div style={{ display: 'flex', gap: '1rem', alignItems: 'center' }}>
                    <div className="avatar" style={{ width: 44, height: 44, fontSize: '1rem' }}>
                      {m.userName?.charAt(0)}
                    </div>
                    <div>
                      <div style={{ fontWeight: 600 }}>{m.userName}</div>
                      <div style={{ color: 'var(--muted)', fontSize: '0.82rem' }}>{m.userEmail}</div>
                      <div style={{ fontSize: '0.8rem', marginTop: 4 }}>
                        Requesting to join{' '}
                        <strong style={{ color: 'var(--primary-light)' }}>{m.clubName}</strong>
                      </div>
                      <div style={{ color: 'var(--muted)', fontSize: '0.75rem', marginTop: 2 }}>
                        {m.requestedAt ? new Date(m.requestedAt).toLocaleString() : ''}
                      </div>
                    </div>
                  </div>
                  <div style={{ display: 'flex', gap: '0.5rem' }}>
                    <button onClick={() => handleApprove(m.membershipId)} className="btn btn-success" style={{ fontSize: '0.85rem' }}>
                      ✓ Approve
                    </button>
                    <button onClick={() => handleReject(m.membershipId)} className="btn btn-danger" style={{ fontSize: '0.85rem' }}>
                      ✕ Reject
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}

          {/* ─── Users Tab ─── */}
          {tab === 'users' && (
            <div className="table-wrapper">
              <table>
                <thead>
                  <tr>
                    {['Name', 'Email', 'Role', 'Department', 'Status', 'Joined', 'Actions'].map(h => (
                      <th key={h}>{h}</th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {users.map(u => (
                    <tr key={u.id}>
                      <td>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '0.625rem' }}>
                          <div className="avatar" style={{ width: 32, height: 32, fontSize: '0.8rem', flexShrink: 0 }}>
                            {u.name?.charAt(0)}
                          </div>
                          <span style={{ fontWeight: 500 }}>{u.name}</span>
                        </div>
                      </td>
                      <td style={{ color: 'var(--muted)', fontSize: '0.85rem' }}>{u.email}</td>
                      <td>
                        <span className="badge" style={{ background: ROLE_BG[u.role], color: ROLE_COLOR[u.role] }}>
                          {u.role}
                        </span>
                      </td>
                      <td style={{ color: 'var(--muted)', fontSize: '0.85rem' }}>{u.department || '—'}</td>
                      <td>
                        <span className={`badge ${u.active ? 'badge-success' : 'badge-danger'}`}>
                          {u.active ? 'Active' : 'Inactive'}
                        </span>
                      </td>
                      <td style={{ color: 'var(--muted)', fontSize: '0.82rem' }}>
                        {u.createdAt ? new Date(u.createdAt).toLocaleDateString() : '—'}
                      </td>
                      <td>
                        <div style={{ display: 'flex', gap: '0.4rem' }}>
                          <button onClick={() => handleToggle(u.id, u.name)} className="btn btn-outline" style={{ padding: '4px 10px', fontSize: '0.75rem' }}>
                            {u.active ? 'Disable' : 'Enable'}
                          </button>
                          <button onClick={() => handlePromote(u.id, u.role)} className="btn btn-outline" style={{ padding: '4px 10px', fontSize: '0.75rem', color: 'var(--primary-light)', borderColor: 'var(--primary)' }}>
                            Role ↑
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </>
      )}
    </div>
  );
}
