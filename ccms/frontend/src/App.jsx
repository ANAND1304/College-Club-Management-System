import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { AuthProvider, useAuth } from './context/AuthContext';

import Navbar     from './components/layout/Navbar';
import Login      from './pages/auth/Login';
import Register   from './pages/auth/Register';
import Dashboard  from './pages/Dashboard';
import Clubs      from './pages/clubs/Clubs';
import ClubDetail from './pages/clubs/ClubDetail';
import Events     from './pages/events/Events';
import AdminPanel from './pages/admin/AdminPanel';
import Profile    from './pages/Profile';

import './index.css';

// ─── Route guards ─────────────────────────────────────────────────────────────
const PrivateRoute = ({ children }) => {
  const { user, loading } = useAuth();
  if (loading) return <Loader />;
  return user ? children : <Navigate to="/login" replace />;
};

const AdminRoute = ({ children }) => {
  const { user, loading } = useAuth();
  if (loading) return <Loader />;
  if (!user) return <Navigate to="/login" replace />;
  return user.role === 'ADMIN' ? children : <Navigate to="/dashboard" replace />;
};

const PublicRoute = ({ children }) => {
  const { user, loading } = useAuth();
  if (loading) return <Loader />;
  return user ? <Navigate to="/dashboard" replace /> : children;
};

const Loader = () => (
  <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '100vh', gap: '0.5rem' }}>
    <div style={{ width: 8, height: 8, borderRadius: '50%', background: '#4f46e5', animation: 'pulse 1s ease-in-out 0s infinite' }} />
    <div style={{ width: 8, height: 8, borderRadius: '50%', background: '#6366f1', animation: 'pulse 1s ease-in-out 0.15s infinite' }} />
    <div style={{ width: 8, height: 8, borderRadius: '50%', background: '#818cf8', animation: 'pulse 1s ease-in-out 0.3s infinite' }} />
  </div>
);

// ─── App ──────────────────────────────────────────────────────────────────────
export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Toaster
          position="top-right"
          toastOptions={{
            duration: 3500,
            style: {
              background: '#1a1a24',
              color: '#f1f1f5',
              border: '1px solid #2e2e45',
              fontFamily: 'DM Sans, sans-serif',
              fontSize: '0.875rem',
            },
            success: { iconTheme: { primary: '#10b981', secondary: '#1a1a24' } },
            error:   { iconTheme: { primary: '#ef4444', secondary: '#1a1a24' } },
          }}
        />
        <Navbar />
        <Routes>
          <Route path="/"         element={<Navigate to="/dashboard" replace />} />
          <Route path="/login"    element={<PublicRoute><Login /></PublicRoute>} />
          <Route path="/register" element={<PublicRoute><Register /></PublicRoute>} />
          <Route path="/dashboard"   element={<PrivateRoute><Dashboard /></PrivateRoute>} />
          <Route path="/clubs"       element={<PrivateRoute><Clubs /></PrivateRoute>} />
          <Route path="/clubs/:id"   element={<PrivateRoute><ClubDetail /></PrivateRoute>} />
          <Route path="/events"      element={<PrivateRoute><Events /></PrivateRoute>} />
          <Route path="/profile"     element={<PrivateRoute><Profile /></PrivateRoute>} />
          <Route path="/admin"       element={<AdminRoute><AdminPanel /></AdminRoute>} />
          <Route path="*"            element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
