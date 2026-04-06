import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authAPI } from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import toast from 'react-hot-toast';

export default function Login() {
  const [form, setForm]       = useState({ email: '', password: '' });
  const [loading, setLoading] = useState(false);
  const [errors, setErrors]   = useState({});
  const { login } = useAuth();
  const navigate  = useNavigate();

  const validate = () => {
    const e = {};
    if (!form.email)    e.email    = 'Email is required';
    if (!form.password) e.password = 'Password is required';
    return e;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm(f => ({ ...f, [name]: value }));
    if (errors[name]) setErrors(er => ({ ...er, [name]: '' }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const errs = validate();
    if (Object.keys(errs).length) { setErrors(errs); return; }
    setLoading(true);
    try {
      const { data } = await authAPI.login(form);
      login(data.token, { id: data.id, name: data.name, email: data.email, role: data.role });
      toast.success(`Welcome back, ${data.name}!`);
      navigate('/dashboard');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Invalid email or password');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{
      minHeight: '100vh',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      background: 'radial-gradient(ellipse 80% 60% at 50% -10%, rgba(79,70,229,0.18) 0%, transparent 70%), var(--bg)',
      padding: '2rem',
    }}>
      <div style={{ width: '100%', maxWidth: 420 }}>
        {/* Header */}
        <div style={{ textAlign: 'center', marginBottom: '2.5rem' }}>
          <div style={{
            width: 56, height: 56,
            background: 'linear-gradient(135deg, #4f46e5, #818cf8)',
            borderRadius: 14,
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            margin: '0 auto 1rem',
            fontSize: '1.5rem',
            fontFamily: 'Syne', fontWeight: 800, color: '#fff',
            boxShadow: '0 8px 24px rgba(79,70,229,0.35)',
          }}>C</div>
          <h1 style={{ fontFamily: 'Syne', fontWeight: 800, fontSize: '1.75rem' }}>Welcome back</h1>
          <p style={{ color: 'var(--muted)', marginTop: '0.35rem', fontSize: '0.9rem' }}>Sign in to ClubHub</p>
        </div>

        {/* Card */}
        <div className="card" style={{ padding: '2rem' }}>
          <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1.1rem' }}>
            {[
              { label: 'Email address', name: 'email', type: 'email', placeholder: 'you@college.edu' },
              { label: 'Password',      name: 'password', type: 'password', placeholder: '••••••••' },
            ].map(({ label, name, type, placeholder }) => (
              <div className="form-group" key={name}>
                <label className="form-label">{label}</label>
                <input
                  className="form-input"
                  type={type}
                  name={name}
                  value={form[name]}
                  onChange={handleChange}
                  placeholder={placeholder}
                  autoComplete={name === 'email' ? 'email' : 'current-password'}
                  style={errors[name] ? { borderColor: 'var(--danger)' } : {}}
                />
                {errors[name] && (
                  <span style={{ fontSize: '0.78rem', color: 'var(--danger)' }}>{errors[name]}</span>
                )}
              </div>
            ))}

            <button
              type="submit"
              className="btn btn-primary"
              disabled={loading}
              style={{ width: '100%', justifyContent: 'center', marginTop: '0.5rem', padding: '11px', fontSize: '0.95rem', opacity: loading ? 0.7 : 1 }}
            >
              {loading ? 'Signing in…' : 'Sign In'}
            </button>
          </form>

          <p style={{ textAlign: 'center', marginTop: '1.5rem', color: 'var(--muted)', fontSize: '0.875rem' }}>
            Don't have an account?{' '}
            <Link to="/register" style={{ color: 'var(--primary-light)', fontWeight: 600 }}>Register</Link>
          </p>
        </div>

        {/* Demo hint */}
        <div style={{
          marginTop: '1.25rem',
          padding: '1rem',
          background: 'rgba(79,70,229,0.08)',
          border: '1px solid rgba(79,70,229,0.2)',
          borderRadius: 'var(--radius)',
          fontSize: '0.8rem',
          color: 'var(--muted)',
        }}>
          <strong style={{ color: 'var(--primary-light)' }}>Demo credentials:</strong>
          <br />Admin: <code style={{ color: 'var(--text-dim)' }}>admin@college.com</code> / <code style={{ color: 'var(--text-dim)' }}>password</code>
        </div>
      </div>
    </div>
  );
}
