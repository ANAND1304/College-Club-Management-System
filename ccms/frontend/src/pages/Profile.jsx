import { useAuth } from '../context/AuthContext';

export default function Profile() {
  const { user } = useAuth();

  const fields = [
    { label: 'Full Name',  value: user?.name },
    { label: 'Email',      value: user?.email },
    { label: 'Role',       value: user?.role },
  ];

  return (
    <div className="page" style={{ maxWidth: 640 }}>
      <h1 style={{ fontSize: '1.9rem', fontWeight: 800, marginBottom: '2rem' }}>My Profile</h1>

      <div className="card" style={{ padding: '2rem' }}>
        {/* Avatar */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem', marginBottom: '2rem' }}>
          <div className="avatar" style={{ width: 72, height: 72, fontSize: '1.8rem', borderRadius: 18 }}>
            {user?.name?.charAt(0).toUpperCase()}
          </div>
          <div>
            <h2 style={{ fontFamily: 'Syne', fontWeight: 700, fontSize: '1.4rem' }}>{user?.name}</h2>
            <span style={{
              display: 'inline-block', marginTop: 6, padding: '3px 12px',
              borderRadius: 20, fontSize: '0.78rem', fontWeight: 600,
              background: 'rgba(79,70,229,0.15)', color: 'var(--primary-light)',
            }}>{user?.role}</span>
          </div>
        </div>

        {/* Fields */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
          {fields.map(({ label, value }) => (
            <div key={label} style={{ display: 'flex', flexDirection: 'column', gap: 4, paddingBottom: '1.25rem', borderBottom: '1px solid var(--border)' }}>
              <div style={{ fontSize: '0.75rem', color: 'var(--muted)', fontWeight: 600, textTransform: 'uppercase', letterSpacing: '0.05em' }}>{label}</div>
              <div style={{ fontSize: '0.95rem', fontWeight: 500, color: 'var(--text)' }}>{value || '—'}</div>
            </div>
          ))}
        </div>

        <div style={{ marginTop: '1.5rem', padding: '1rem', background: 'rgba(79,70,229,0.08)', borderRadius: 'var(--radius)', border: '1px solid rgba(79,70,229,0.2)' }}>
          <p style={{ fontSize: '0.82rem', color: 'var(--muted)' }}>
            Profile editing coming soon. Contact your administrator to update your details.
          </p>
        </div>
      </div>
    </div>
  );
}
