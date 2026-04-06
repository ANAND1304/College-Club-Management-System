import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { clubAPI, eventAPI, adminAPI } from '../services/api';

const StatCard = ({ label, value, color, icon }) => (
  <div className="card" style={{ borderColor: `${color}33`, flex: '1 1 180px' }}>
    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
      <div>
        <div style={{ fontSize: '0.78rem', color: 'var(--muted)', fontWeight: 500, textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: 8 }}>{label}</div>
        <div style={{ fontSize: '2.2rem', fontFamily: 'Syne', fontWeight: 800, color, lineHeight: 1 }}>{value ?? '—'}</div>
      </div>
      <div style={{ fontSize: '1.5rem', opacity: 0.6 }}>{icon}</div>
    </div>
  </div>
);

const ClubCard = ({ club }) => (
  <Link to={`/clubs/${club.id}`} style={{ textDecoration: 'none', display: 'block' }}>
    <div className="card" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '1rem 1.25rem' }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
        <div className="avatar" style={{ width: 38, height: 38, fontSize: '0.9rem' }}>
          {club.name.charAt(0)}
        </div>
        <div>
          <div style={{ fontWeight: 600, fontSize: '0.9rem', color: 'var(--text)' }}>{club.name}</div>
          <div style={{ fontSize: '0.75rem', color: 'var(--muted)' }}>{club.category || 'General'}</div>
        </div>
      </div>
      <span className="badge badge-primary">{club.memberCount} members</span>
    </div>
  </Link>
);

const EventCard = ({ event }) => {
  const date = new Date(event.eventDate);
  const month = date.toLocaleString('default', { month: 'short' });
  const day   = date.getDate();
  const isPast = date < new Date();

  return (
    <div className="card" style={{ display: 'flex', gap: '1rem', padding: '1rem 1.25rem', opacity: isPast ? 0.6 : 1 }}>
      <div style={{ textAlign: 'center', minWidth: 44, background: 'var(--surface2)', borderRadius: 8, padding: '4px 0', alignSelf: 'flex-start' }}>
        <div style={{ fontSize: '0.65rem', fontWeight: 700, color: 'var(--primary-light)', textTransform: 'uppercase' }}>{month}</div>
        <div style={{ fontSize: '1.3rem', fontFamily: 'Syne', fontWeight: 800, color: 'var(--text)', lineHeight: 1.2 }}>{day}</div>
      </div>
      <div>
        <div style={{ fontWeight: 600, fontSize: '0.9rem' }}>{event.title}</div>
        <div style={{ fontSize: '0.75rem', color: 'var(--muted)', marginTop: 3 }}>
          🏢 {event.clubName}{event.location ? ` · 📍 ${event.location}` : ''}
        </div>
      </div>
    </div>
  );
};

export default function Dashboard() {
  const { user, isAdmin } = useAuth();
  const [stats,  setStats]  = useState(null);
  const [clubs,  setClubs]  = useState([]);
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([
      clubAPI.getAll(),
      eventAPI.getAll(),
      isAdmin ? adminAPI.getDashboard() : Promise.resolve(null),
    ]).then(([clubsRes, eventsRes, statsRes]) => {
      setClubs(clubsRes.data.slice(0, 5));
      setEvents(eventsRes.data.slice(0, 5));
      if (statsRes) setStats(statsRes.data);
    }).finally(() => setLoading(false));
  }, [isAdmin]);

  const greeting = () => {
    const h = new Date().getHours();
    if (h < 12) return 'Good morning';
    if (h < 17) return 'Good afternoon';
    return 'Good evening';
  };

  return (
    <div className="page">
      {/* Header */}
      <div style={{ marginBottom: '2rem' }}>
        <h1 style={{ fontSize: '1.9rem', fontWeight: 800 }}>
          {greeting()}, {user?.name?.split(' ')[0]} 👋
        </h1>
        <p style={{ color: 'var(--muted)', marginTop: '0.3rem' }}>
          Here's what's happening on campus today.
        </p>
      </div>

      {/* Admin stats */}
      {isAdmin && stats && (
        <div style={{ display: 'flex', gap: '1rem', marginBottom: '2.5rem', flexWrap: 'wrap' }}>
          <StatCard label="Total Users"      value={stats.totalUsers}      color="#4f46e5" icon="👥" />
          <StatCard label="Active Clubs"     value={stats.totalClubs}      color="#10b981" icon="🏛️" />
          <StatCard label="Total Events"     value={stats.totalEvents}     color="#f59e0b" icon="📅" />
          <StatCard label="Pending Requests" value={stats.pendingRequests} color="#ef4444" icon="⏳" />
          <StatCard label="Active Members"   value={stats.approvedMembers} color="#06b6d4" icon="✅" />
        </div>
      )}

      {/* Student role hint */}
      {!isAdmin && (
        <div style={{
          marginBottom: '2rem',
          padding: '1rem 1.25rem',
          background: 'rgba(79,70,229,0.08)',
          border: '1px solid rgba(79,70,229,0.2)',
          borderRadius: 'var(--radius)',
          display: 'flex', alignItems: 'center', gap: '0.75rem',
        }}>
          <span style={{ fontSize: '1.25rem' }}>🎓</span>
          <div>
            <div style={{ fontWeight: 600, fontSize: '0.9rem' }}>You're logged in as a Student</div>
            <div style={{ color: 'var(--muted)', fontSize: '0.8rem' }}>Browse clubs, join them, and attend events on campus!</div>
          </div>
        </div>
      )}

      {/* Two-column layout */}
      {loading ? (
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem' }}>
          {[1,2,3,4].map(i => <div key={i} className="skeleton" style={{ height: 80, borderRadius: 'var(--radius-lg)' }} />)}
        </div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '2rem' }}>
          {/* Clubs */}
          <section>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.875rem' }}>
              <h2 style={{ fontSize: '1.05rem', fontWeight: 700 }}>Clubs</h2>
              <Link to="/clubs" style={{ fontSize: '0.8rem', color: 'var(--primary-light)' }}>View all →</Link>
            </div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.6rem' }}>
              {clubs.length ? clubs.map(c => <ClubCard key={c.id} club={c} />) : <p style={{ color: 'var(--muted)', fontSize: '0.875rem' }}>No clubs yet.</p>}
            </div>
          </section>

          {/* Events */}
          <section>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.875rem' }}>
              <h2 style={{ fontSize: '1.05rem', fontWeight: 700 }}>Upcoming Events</h2>
              <Link to="/events" style={{ fontSize: '0.8rem', color: 'var(--primary-light)' }}>View all →</Link>
            </div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.6rem' }}>
              {events.length ? events.map(e => <EventCard key={e.id} event={e} />) : <p style={{ color: 'var(--muted)', fontSize: '0.875rem' }}>No events scheduled.</p>}
            </div>
          </section>
        </div>
      )}
    </div>
  );
}
