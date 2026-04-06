import { useState, useEffect, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { clubAPI } from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import toast from 'react-hot-toast';

const CATEGORIES = ['All', 'Technical', 'Cultural', 'Sports', 'Academic', 'Social', 'Other'];

function ClubModal({ onClose, onCreated }) {
  const [form, setForm]       = useState({ name: '', description: '', category: '', imageUrl: '' });
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await clubAPI.create(form);
      toast.success('Club created successfully!');
      onCreated();
      onClose();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to create club');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={e => e.target === e.currentTarget && onClose()}>
      <div className="modal">
        <h2 style={{ fontFamily: 'Syne', fontWeight: 700, marginBottom: '1.5rem' }}>Create New Club</h2>
        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          {[
            { label: 'Club Name *',  name: 'name',        type: 'text' },
            { label: 'Description',  name: 'description', type: 'text' },
            { label: 'Image URL',    name: 'imageUrl',    type: 'url'  },
          ].map(({ label, name, type }) => (
            <div className="form-group" key={name}>
              <label className="form-label">{label}</label>
              <input className="form-input" type={type} value={form[name]} required={name === 'name'}
                onChange={e => setForm(f => ({ ...f, [name]: e.target.value }))} />
            </div>
          ))}
          <div className="form-group">
            <label className="form-label">Category</label>
            <select className="form-input" value={form.category} onChange={e => setForm(f => ({ ...f, category: e.target.value }))}>
              <option value="">Select category</option>
              {CATEGORIES.slice(1).map(c => <option key={c} value={c}>{c}</option>)}
            </select>
          </div>
          <div style={{ display: 'flex', gap: '0.75rem', marginTop: '0.5rem' }}>
            <button type="button" onClick={onClose} className="btn btn-outline" style={{ flex: 1, justifyContent: 'center' }}>Cancel</button>
            <button type="submit"  disabled={loading} className="btn btn-primary" style={{ flex: 1, justifyContent: 'center', opacity: loading ? 0.7 : 1 }}>
              {loading ? 'Creating…' : 'Create Club'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default function Clubs() {
  const { isAdmin } = useAuth();
  const [clubs,      setClubs]      = useState([]);
  const [loading,    setLoading]    = useState(true);
  const [search,     setSearch]     = useState('');
  const [category,   setCategory]   = useState('All');
  const [showModal,  setShowModal]  = useState(false);
  const [joining,    setJoining]    = useState({});

  const load = useCallback(() => {
    setLoading(true);
    clubAPI.getAll().then(r => setClubs(r.data)).finally(() => setLoading(false));
  }, []);

  useEffect(() => { load(); }, [load]);

  const handleJoin = async (id, name) => {
    setJoining(j => ({ ...j, [id]: true }));
    try {
      await clubAPI.join(id);
      toast.success(`Join request sent for "${name}"!`);
    } catch (err) {
      toast.error(err.response?.data?.message || 'Could not send request');
    } finally {
      setJoining(j => ({ ...j, [id]: false }));
    }
  };

  const handleDelete = async (id, name) => {
    if (!window.confirm(`Delete club "${name}"?`)) return;
    try {
      await clubAPI.delete(id);
      toast.success('Club deleted');
      load();
    } catch {
      toast.error('Failed to delete club');
    }
  };

  const filtered = clubs.filter(c => {
    const matchSearch   = c.name.toLowerCase().includes(search.toLowerCase()) ||
                          c.description?.toLowerCase().includes(search.toLowerCase());
    const matchCategory = category === 'All' || c.category === category;
    return matchSearch && matchCategory;
  });

  return (
    <div className="page">
      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem', flexWrap: 'wrap', gap: '1rem' }}>
        <div>
          <h1 style={{ fontSize: '1.9rem', fontWeight: 800 }}>Clubs</h1>
          <p style={{ color: 'var(--muted)', marginTop: '0.25rem', fontSize: '0.9rem' }}>{clubs.length} clubs on campus</p>
        </div>
        {isAdmin && (
          <button className="btn btn-primary" onClick={() => setShowModal(true)}>
            + New Club
          </button>
        )}
      </div>

      {/* Filters */}
      <div style={{ display: 'flex', gap: '0.75rem', marginBottom: '1.5rem', flexWrap: 'wrap' }}>
        <input
          className="form-input"
          placeholder="Search clubs…"
          value={search}
          onChange={e => setSearch(e.target.value)}
          style={{ maxWidth: 300 }}
        />
        <div style={{ display: 'flex', gap: '0.4rem', flexWrap: 'wrap' }}>
          {CATEGORIES.map(cat => (
            <button key={cat} onClick={() => setCategory(cat)}
              className={`badge ${category === cat ? 'badge-primary' : ''}`}
              style={{
                cursor: 'pointer', border: '1px solid', padding: '5px 14px',
                borderColor: category === cat ? 'var(--primary)' : 'var(--border)',
                background: category === cat ? 'rgba(79,70,229,0.15)' : 'transparent',
                color: category === cat ? 'var(--primary-light)' : 'var(--muted)',
                borderRadius: 20, fontSize: '0.8rem', fontWeight: 500,
              }}
            >{cat}</button>
          ))}
        </div>
      </div>

      {/* Grid */}
      {loading ? (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '1.25rem' }}>
          {[1,2,3,4,5,6].map(i => <div key={i} className="skeleton" style={{ height: 200, borderRadius: 'var(--radius-lg)' }} />)}
        </div>
      ) : filtered.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '4rem', color: 'var(--muted)' }}>
          <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>🏛️</div>
          <p>No clubs found{search ? ` for "${search}"` : ''}.</p>
        </div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '1.25rem' }}>
          {filtered.map(club => (
            <div key={club.id} className="card" style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
              {/* Club header */}
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                <div style={{ display: 'flex', gap: '0.75rem', alignItems: 'center' }}>
                  <div className="avatar" style={{ width: 44, height: 44, fontSize: '1.1rem', borderRadius: 10 }}>
                    {club.name.charAt(0)}
                  </div>
                  <div>
                    <div style={{ fontWeight: 700, fontSize: '0.95rem' }}>{club.name}</div>
                    {club.category && <span className="badge badge-primary" style={{ marginTop: 3 }}>{club.category}</span>}
                  </div>
                </div>
                <span style={{ color: 'var(--muted)', fontSize: '0.78rem', whiteSpace: 'nowrap' }}>
                  👥 {club.memberCount}
                </span>
              </div>

              {/* Description */}
              {club.description && (
                <p style={{ color: 'var(--muted)', fontSize: '0.85rem', lineHeight: 1.55, flex: 1 }}>
                  {club.description.length > 110 ? club.description.slice(0, 110) + '…' : club.description}
                </p>
              )}

              {/* Actions */}
              <div style={{ display: 'flex', gap: '0.5rem', marginTop: 'auto' }}>
                <Link to={`/clubs/${club.id}`} className="btn btn-outline" style={{ flex: 1, justifyContent: 'center', fontSize: '0.82rem' }}>
                  View Details
                </Link>
                <button
                  onClick={() => handleJoin(club.id, club.name)}
                  disabled={joining[club.id]}
                  className="btn btn-primary"
                  style={{ flex: 1, justifyContent: 'center', fontSize: '0.82rem', opacity: joining[club.id] ? 0.7 : 1 }}
                >
                  {joining[club.id] ? 'Joining…' : 'Join'}
                </button>
              </div>

              {isAdmin && (
                <button onClick={() => handleDelete(club.id, club.name)} className="btn btn-danger" style={{ width: '100%', justifyContent: 'center', fontSize: '0.8rem' }}>
                  Delete Club
                </button>
              )}
            </div>
          ))}
        </div>
      )}

      {showModal && <ClubModal onClose={() => setShowModal(false)} onCreated={load} />}
    </div>
  );
}
