import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { clubAPI, eventAPI } from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import toast from 'react-hot-toast';

const Tab = ({ label, active, onClick, count }) => (
  <button onClick={onClick} style={{
    padding: '8px 20px', borderRadius: 8, border: 'none', cursor: 'pointer',
    fontWeight: active ? 600 : 400, fontSize: '0.875rem',
    background: active ? '#4f46e5' : 'transparent',
    color: active ? '#fff' : 'var(--muted)',
    transition: 'all 0.15s',
  }}>
    {label}{count !== undefined ? ` (${count})` : ''}
  </button>
);

export default function ClubDetail() {
  const { id }         = useParams();
  const { isAdmin }    = useAuth();
  const navigate       = useNavigate();
  const [club,    setClub]    = useState(null);
  const [members, setMembers] = useState([]);
  const [events,  setEvents]  = useState([]);
  const [tab,     setTab]     = useState('about');
  const [joining, setJoining] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([
      clubAPI.getById(id),
      clubAPI.getMembers(id),
      eventAPI.getByClub(id),
    ]).then(([clubRes, membersRes, eventsRes]) => {
      setClub(clubRes.data);
      setMembers(membersRes.data);
      setEvents(eventsRes.data);
    }).catch(() => navigate('/clubs'))
    .finally(() => setLoading(false));
  }, [id, navigate]);

  const handleJoin = async () => {
    setJoining(true);
    try { await clubAPI.join(id); toast.success('Join request sent!'); }
    catch (e) { toast.error(e.response?.data?.message || 'Error'); }
    finally   { setJoining(false); }
  };

  const handleLeave = async () => {
    if (!window.confirm('Leave this club?')) return;
    try { await clubAPI.leave(id); toast.success('You have left the club'); }
    catch (e) { toast.error(e.response?.data?.message || 'Error'); }
  };

  if (loading) return (
    <div className="page">
      <div className="skeleton" style={{ height: 180, marginBottom: '1.5rem', borderRadius: 'var(--radius-lg)' }} />
      <div className="skeleton" style={{ height: 400, borderRadius: 'var(--radius-lg)' }} />
    </div>
  );

  if (!club) return null;

  return (
    <div className="page" style={{ maxWidth: 900 }}>
      {/* Club hero */}
      <div className="card" style={{ marginBottom: '1.5rem', padding: '2rem' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', gap: '1rem', flexWrap: 'wrap' }}>
          <div style={{ display: 'flex', gap: '1.25rem', alignItems: 'flex-start' }}>
            <div className="avatar" style={{ width: 64, height: 64, fontSize: '1.6rem', borderRadius: 14, flexShrink: 0 }}>
              {club.name.charAt(0)}
            </div>
            <div>
              <h1 style={{ fontSize: '1.75rem', fontWeight: 800, marginBottom: '0.5rem' }}>{club.name}</h1>
              <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap', marginBottom: '0.75rem' }}>
                {club.category  && <span className="badge badge-primary">{club.category}</span>}
                {!club.active   && <span className="badge badge-danger">Inactive</span>}
                <span className="badge" style={{ background: 'var(--surface2)', color: 'var(--muted)' }}>👥 {club.memberCount} members</span>
              </div>
              {club.description && <p style={{ color: 'var(--muted)', lineHeight: 1.65, maxWidth: 560 }}>{club.description}</p>}
              {club.createdByName && (
                <p style={{ fontSize: '0.78rem', color: 'var(--muted)', marginTop: '0.5rem' }}>
                  Created by {club.createdByName}
                </p>
              )}
            </div>
          </div>

          <div style={{ display: 'flex', gap: '0.5rem', flexShrink: 0 }}>
            <button onClick={handleJoin} disabled={joining} className="btn btn-primary">
              {joining ? 'Requesting…' : 'Join Club'}
            </button>
            <button onClick={handleLeave} className="btn btn-outline">Leave</button>
          </div>
        </div>
      </div>

      {/* Tabs */}
      <div style={{ display: 'flex', gap: '0.4rem', marginBottom: '1.25rem' }}>
        <Tab label="About"   active={tab === 'about'}   onClick={() => setTab('about')} />
        <Tab label="Members" active={tab === 'members'} onClick={() => setTab('members')} count={members.length} />
        <Tab label="Events"  active={tab === 'events'}  onClick={() => setTab('events')}  count={events.length} />
      </div>

      {/* Tab: About */}
      {tab === 'about' && (
        <div className="card">
          <h2 style={{ fontWeight: 700, marginBottom: '1rem' }}>About {club.name}</h2>
          {club.description
            ? <p style={{ color: 'var(--muted)', lineHeight: 1.75 }}>{club.description}</p>
            : <p style={{ color: 'var(--muted)' }}>No description provided.</p>
          }
          <div style={{ marginTop: '1.5rem', display: 'flex', gap: '1.5rem', flexWrap: 'wrap' }}>
            <div>
              <div style={{ fontSize: '0.75rem', color: 'var(--muted)', textTransform: 'uppercase', fontWeight: 600, letterSpacing: '0.05em' }}>Category</div>
              <div style={{ marginTop: 4, fontWeight: 500 }}>{club.category || 'General'}</div>
            </div>
            <div>
              <div style={{ fontSize: '0.75rem', color: 'var(--muted)', textTransform: 'uppercase', fontWeight: 600, letterSpacing: '0.05em' }}>Members</div>
              <div style={{ marginTop: 4, fontWeight: 500 }}>{club.memberCount}</div>
            </div>
            <div>
              <div style={{ fontSize: '0.75rem', color: 'var(--muted)', textTransform: 'uppercase', fontWeight: 600, letterSpacing: '0.05em' }}>Founded</div>
              <div style={{ marginTop: 4, fontWeight: 500 }}>
                {club.createdAt ? new Date(club.createdAt).toLocaleDateString() : 'N/A'}
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Tab: Members */}
      {tab === 'members' && (
        <div>
          {members.length === 0 ? (
            <div className="card" style={{ textAlign: 'center', padding: '3rem', color: 'var(--muted)' }}>
              <div style={{ fontSize: '2.5rem', marginBottom: '0.75rem' }}>👥</div>
              <p>No approved members yet.</p>
            </div>
          ) : (
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(260px, 1fr))', gap: '0.875rem' }}>
              {members.map(m => (
                <div key={m.membershipId} className="card" style={{ display: 'flex', gap: '0.875rem', alignItems: 'center', padding: '1rem 1.25rem' }}>
                  <div className="avatar" style={{ width: 40, height: 40, fontSize: '0.95rem', flexShrink: 0 }}>
                    {m.userName?.charAt(0)}
                  </div>
                  <div style={{ minWidth: 0 }}>
                    <div style={{ fontWeight: 600, fontSize: '0.9rem', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{m.userName}</div>
                    <div style={{ color: 'var(--muted)', fontSize: '0.75rem', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{m.userEmail}</div>
                    <span className="badge badge-info" style={{ marginTop: 4, fontSize: '0.68rem' }}>{m.clubRole}</span>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* Tab: Events */}
      {tab === 'events' && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.875rem' }}>
          {events.length === 0 ? (
            <div className="card" style={{ textAlign: 'center', padding: '3rem', color: 'var(--muted)' }}>
              <div style={{ fontSize: '2.5rem', marginBottom: '0.75rem' }}>📅</div>
              <p>No events scheduled for this club.</p>
            </div>
          ) : events.map(ev => (
            <div key={ev.id} className="card" style={{ display: 'flex', gap: '1.25rem', alignItems: 'flex-start', padding: '1.25rem' }}>
              <div style={{
                minWidth: 52, textAlign: 'center',
                background: 'var(--surface2)', borderRadius: 10, padding: '6px 4px',
              }}>
                <div style={{ fontSize: '0.62rem', fontWeight: 700, color: 'var(--primary-light)', textTransform: 'uppercase' }}>
                  {new Date(ev.eventDate).toLocaleString('default', { month: 'short' })}
                </div>
                <div style={{ fontSize: '1.4rem', fontFamily: 'Syne', fontWeight: 800, lineHeight: 1.1 }}>
                  {new Date(ev.eventDate).getDate()}
                </div>
              </div>
              <div>
                <div style={{ fontWeight: 700, fontSize: '1rem', marginBottom: '0.35rem' }}>{ev.title}</div>
                {ev.description && <p style={{ color: 'var(--muted)', fontSize: '0.85rem', lineHeight: 1.55, marginBottom: '0.5rem' }}>{ev.description}</p>}
                {ev.location && <div style={{ fontSize: '0.8rem', color: 'var(--muted)' }}>📍 {ev.location}</div>}
                {ev.maxParticipants && <div style={{ fontSize: '0.8rem', color: 'var(--muted)', marginTop: 2 }}>👥 Max {ev.maxParticipants} participants</div>}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
