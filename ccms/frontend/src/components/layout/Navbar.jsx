import { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import toast from 'react-hot-toast';

export default function Navbar() {
  const { user, logout, isAdmin } = useAuth();
  const navigate = useNavigate();
  const { pathname } = useLocation();
  const [menuOpen, setMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    toast.success('Logged out successfully');
    navigate('/login');
  };

  const navLinks = [
    { label: 'Dashboard', path: '/dashboard' },
    { label: 'Clubs',     path: '/clubs' },
    { label: 'Events',    path: '/events' },
    ...(isAdmin ? [{ label: 'Admin', path: '/admin' }] : []),
  ];

  const isActive = (path) => pathname === path || pathname.startsWith(path + '/');

  if (!user) return null;

  return (
    <nav style={{
      background: 'rgba(26, 26, 36, 0.95)',
      backdropFilter: 'blur(12px)',
      borderBottom: '1px solid #2e2e45',
      padding: '0 2rem',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      height: 64,
      position: 'sticky',
      top: 0,
      zIndex: 200,
    }}>
      {/* Logo */}
      <Link to="/dashboard" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', textDecoration: 'none' }}>
        <div style={{
          width: 32, height: 32,
          background: 'linear-gradient(135deg, #4f46e5, #818cf8)',
          borderRadius: 8,
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          fontFamily: 'Syne', fontWeight: 800, fontSize: '1rem', color: '#fff',
        }}>C</div>
        <span style={{ fontFamily: 'Syne', fontWeight: 800, fontSize: '1.2rem', color: '#f1f1f5', letterSpacing: '-0.5px' }}>
          Club<span style={{ color: '#818cf8' }}>Hub</span>
        </span>
      </Link>

      {/* Nav links — desktop */}
      <div style={{ display: 'flex', gap: '0.25rem', alignItems: 'center' }}>
        {navLinks.map(({ label, path }) => (
          <Link
            key={path}
            to={path}
            style={{
              padding: '6px 14px',
              borderRadius: 8,
              fontSize: '0.875rem',
              fontWeight: isActive(path) ? 600 : 400,
              color: isActive(path) ? '#f1f1f5' : '#8b8ba8',
              textDecoration: 'none',
              background: isActive(path) ? '#242436' : 'transparent',
              transition: 'all 0.15s',
            }}
          >{label}</Link>
        ))}
      </div>

      {/* Right side: user */}
      <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
        <Link to="/profile" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', textDecoration: 'none', padding: '4px 10px', borderRadius: 8, border: '1px solid transparent', transition: 'border-color 0.15s' }}
          onMouseEnter={e => e.currentTarget.style.borderColor = '#2e2e45'}
          onMouseLeave={e => e.currentTarget.style.borderColor = 'transparent'}>
          <div className="avatar" style={{ width: 30, height: 30, fontSize: '0.8rem' }}>
            {user.name?.charAt(0).toUpperCase()}
          </div>
          <div>
            <div style={{ fontSize: '0.8rem', fontWeight: 600, color: '#f1f1f5', lineHeight: 1.1 }}>{user.name}</div>
            <div style={{ fontSize: '0.7rem', color: '#8b8ba8', lineHeight: 1.1 }}>{user.role}</div>
          </div>
        </Link>

        <button
          onClick={handleLogout}
          className="btn btn-outline"
          style={{ padding: '6px 14px', fontSize: '0.8rem' }}
        >Logout</button>
      </div>
    </nav>
  );
}
