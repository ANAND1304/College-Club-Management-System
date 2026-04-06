import { useState, useEffect, useCallback } from 'react';
import { eventAPI, clubAPI } from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import toast from 'react-hot-toast';

function EventModal({ onClose, onCreated, clubs }) {
  const [form, setForm]       = useState({ title: '', description: '', eventDate: '', location: '', maxParticipants: '', clubId: '' });
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await eventAPI.create({
        ...form,
        clubId: Number(form.clubId),
        maxParticipants: form.maxParticipants ? Number(form.maxParticipants) : null,
      });
      toast.success('Event created!');
      onCreated();
      onClose();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to create event');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={e => e.target === e.currentTarget && onClose()}>
      <div className="modal">
        <h2 style={{ fontFamily: 'Syne', fontWeight: 700, marginBottom: '1.5rem' }}>Create New Event</h2>
        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          {[
            { label: 'Event Title *',     name: 'title',           type: 'text',   required: true  },
            { label: 'Description',       name: 'description',     type: 'text',   required: false },
            { label: 'Event Date *',      name: 'eventDate',       type: 'date',   required: true  },
            { label: 'Location',          name: 'location',        type: 'text',   required: false },
            { label: 'Max Participants',  name: 'maxParticipants', type: 'number', required: false },
          ].map(({ label, name, type, required }) => (
            <div className="form-group" key={name}>
              <label className="form-label">{label}</label>
              <input
                className="form-input" type={type} required={required}
                value={form[name]} min={type === 'number' ? 1 : undefined}
                onChange={e => setForm(f => ({ ...f, [name]: e.target.value }))}
              />
            </div>
          ))}
          <div className="form-group">
            <label className="form-label">Club *</label>
            <select className="form-input" required value={form.clubId}
              onChange={e => setForm(f => ({ ...f, clubId: e.target.value }))}>
              <option value="">Select a club</option>
              {clubs.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
            </select>
          </div>
          <div style={{ display: 'flex', gap: '0.75rem', marginTop: '0.5rem' }}>
            <button type="button" onClick={onClose} className="btn btn-outline" style={{ flex: 1, justifyContent: 'center' }}>Cancel</button>
            <button type="submit" disabled={loading} className="btn btn-primary" style={{ flex: 1, justifyContent: 'center', opacity: loading ? 0.7 : 1 }}>
              {loading ? 'Creating…' : 'Create Event'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default function Events() {
  const { isAdmin } = useAuth();
  const [events,    setEvents]    = useState([]);
  const [clubs,     setClubs]     = useState([]);
  const [loading,   setLoading]   = useState(true);
  const [search,    setSearch]    = useState('');
  const [filter,    setFilter]    = useState('upcoming'); // upcoming | past | all
  const [showModal, setShowModal] = useState(false);

  const load = useCallback(() => {
    setLoading(true);
    Promise.all([eventAPI.getAll(), clubAPI.getAll()])
      .then(([eventsRes, clubsRes]) => { setEvents(eventsRes.data); setClubs(clubsRes.data); })
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => { load(); }, [load]);

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this event?')) return;
    try { await eventAPI.delete(id); toast.success('Event deleted'); load(); }
    catch { toast.error('Failed to delete event'); }
  };

  const today = new Date();
  const filtered = events.filter(ev => {
    const matchSearch = ev.title.toLowerCase().includes(search.toLowerCase())
      || ev.clubName?.toLowerCase().includes(search.toLowerCase())
      || ev.location?.toLowerCase().includes(search.toLowerCase());
    const isPast = new Date(ev.eventDate) < today;
    const matchFilter = filter === 'all'
      || (filter === 'upcoming' && !isPast)
      || (filter === 'past'     && isPast);
    return matchSearch && matchFilter;
  });

  const upcomingCount = events.filter(ev => new Date(ev.eventDate) >= today).length;
  const pastCount     = events.filter(ev => new Date(ev.eventDate) <  today).length;

  return (
    <div className="page">
      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem', flexWrap: 'wrap', gap: '1rem' }}>
        <div>
          <h1 style={{ fontSize: '1.9rem', fontWeight: 800 }}>Events</h1>
          <p style={{ color: 'var(--muted)', marginTop: '0.25rem', fontSize: '0.9rem' }}>
            {upcomingCount} upcoming · {pastCount} past
          </p>
        </div>
        {isAdmin && (
          <button className="btn btn-primary" onClick={() => setShowModal(true)}>+ New Event</button>
        )}
      </div>

      {/* Search + filter */}
      <div style={{ display: 'flex', gap: '0.75rem', marginBottom: '1.5rem', flexWrap: 'wrap', alignItems: 'center' }}>
        <input
          className="form-input"
          placeholder="Search events, clubs, locations…"
          value={search}
          onChange={e => setSearch(e.target.value)}
          style={{ maxWidth: 340 }}
        />
        <div style={{ display: 'flex', gap: '0.4rem' }}>
          {[['upcoming', 'Upcoming'], ['past', 'Past'], ['all', 'All']].map(([val, label]) => (
            <button key={val} onClick={() => setFilter(val)}
              style={{
                padding: '6px 16px', borderRadius: 20, border: '1px solid',
                borderColor: filter === val ? 'var(--primary)' : 'var(--border)',
                background: filter === val ? 'rgba(79,70,229,0.15)' : 'transparent',
                color: filter === val ? 'var(--primary-light)' : 'var(--muted)',
                fontSize: '0.82rem', fontWeight: 500, cursor: 'pointer',
              }}
            >{label}</button>
          ))}
        </div>
      </div>

      {/* Events grid */}
      {loading ? (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))', gap: '1.25rem' }}>
          {[1,2,3,4,5,6].map(i => <div key={i} className="skeleton" style={{ height: 180, borderRadius: 'var(--radius-lg)' }} />)}
        </div>
      ) : filtered.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '4rem', color: 'var(--muted)' }}>
          <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>📅</div>
          <p>No events found.</p>
        </div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))', gap: '1.25rem' }}>
          {filtered.map(ev => {
            const date  = new Date(ev.eventDate);
            const isPast = date < today;
            return (
              <div key={ev.id} className="card" style={{ display: 'flex', flexDirection: 'column', gap: '0.875rem', opacity: isPast ? 0.7 : 1 }}>
                {/* Date badge + title */}
                <div style={{ display: 'flex', gap: '1rem', alignItems: 'flex-start' }}>
                  <div style={{
                    minWidth: 52, textAlign: 'center',
                    background: isPast ? 'var(--surface3)' : 'rgba(79,70,229,0.12)',
                    borderRadius: 10, padding: '6px 4px', flexShrink: 0,
                  }}>
                    <div style={{ fontSize: '0.6rem', fontWeight: 700, color: isPast ? 'var(--muted)' : 'var(--primary-light)', textTransform: 'uppercase' }}>
                      {date.toLocaleString('default', { month: 'short' })}
                    </div>
                    <div style={{ fontSize: '1.4rem', fontFamily: 'Syne', fontWeight: 800, color: isPast ? 'var(--muted)' : 'var(--text)', lineHeight: 1.1 }}>
                      {date.getDate()}
                    </div>
                  </div>
                  <div>
                    <div style={{ fontWeight: 700, fontSize: '0.95rem' }}>{ev.title}</div>
                    <div style={{ display: 'flex', gap: '0.4rem', marginTop: 4, flexWrap: 'wrap' }}>
                      <span className="badge badge-primary" style={{ fontSize: '0.7rem' }}>{ev.clubName}</span>
                      {isPast && <span className="badge" style={{ background: 'var(--surface3)', color: 'var(--muted)', fontSize: '0.7rem' }}>Past</span>}
                    </div>
                  </div>
                </div>

                {ev.description && (
                  <p style={{ color: 'var(--muted)', fontSize: '0.85rem', lineHeight: 1.55 }}>
                    {ev.description.length > 120 ? ev.description.slice(0, 120) + '…' : ev.description}
                  </p>
                )}

                <div style={{ display: 'flex', flexDirection: 'column', gap: '3px' }}>
                  {ev.location        && <div style={{ fontSize: '0.8rem', color: 'var(--muted)' }}>📍 {ev.location}</div>}
                  {ev.maxParticipants && <div style={{ fontSize: '0.8rem', color: 'var(--muted)' }}>👥 Max {ev.maxParticipants} participants</div>}
                </div>

                {isAdmin && (
                  <button onClick={() => handleDelete(ev.id)} className="btn btn-danger" style={{ width: '100%', justifyContent: 'center', marginTop: 'auto', fontSize: '0.8rem' }}>
                    Delete Event
                  </button>
                )}
              </div>
            );
          })}
        </div>
      )}

      {showModal && <EventModal onClose={() => setShowModal(false)} onCreated={load} clubs={clubs} />}
    </div>
  );
}
