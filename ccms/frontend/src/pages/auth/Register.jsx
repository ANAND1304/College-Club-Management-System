import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authAPI } from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import toast from 'react-hot-toast';

const FIELDS = [
  { label: 'Full Name *',     name: 'name',       type: 'text',     placeholder: 'Jane Doe',          required: true  },
  { label: 'Email Address *', name: 'email',      type: 'email',    placeholder: 'jane@college.edu',  required: true  },
  { label: 'Password *',      name: 'password',   type: 'password', placeholder: 'Min. 6 characters', required: true  },
  { label: 'Department',      name: 'department', type: 'text',     placeholder: 'Computer Science',  required: false },
  { label: 'Phone',           name: 'phone',      type: 'tel',      placeholder: '+91 99999 00000',   required: false },
];

export default function Register() {
  const [form, setForm]       = useState({ name: '', email: '', password: '', department: '', phone: '' });
  const [loading, setLoading] = useState(false);
  const [errors, setErrors]   = useState({});
  const { login }  = useAuth();
  const navigate   = useNavigate();

  const validate = () => {
    const e = {};
    if (!form.name)                      e.name     = 'Name is required';
    if (!form.email)                     e.email    = 'Email is required';
    if (!form.password)                  e.password = 'Password is required';
    else if (form.password.length < 6)   e.password = 'Must be at least 6 characters';
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
      const { data } = await authAPI.register(form);
      login(data.token, { id: data.id, name: data.name, email: data.email, role: data.role });
      toast.success('Account created! Welcome to ClubHub 🎉');
      navigate('/dashboard');
    } catch (err) {
      const msg = err.response?.data?.message
        || Object.values(err.response?.data?.fieldErrors || {})[0]
        || 'Registration failed';
      toast.error(msg);
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
      <div style={{ width: '100%', maxWidth: 460 }}>
        <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
          <h1 style={{ fontFamily: 'Syne', fontWeight: 800, fontSize: '1.75rem' }}>Create account</h1>
          <p style={{ color: 'var(--muted)', marginTop: '0.35rem', fontSize: '0.9rem' }}>Join ClubHub — your campus community</p>
        </div>

        <div className="card" style={{ padding: '2rem' }}>
          <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            {FIELDS.map(({ label, name, type, placeholder, required }) => (
              <div className="form-group" key={name}>
                <label className="form-label">{label}</label>
                <input
                  className="form-input"
                  type={type}
                  name={name}
                  value={form[name]}
                  onChange={handleChange}
                  placeholder={placeholder}
                  required={required}
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
              {loading ? 'Creating account…' : 'Create Account'}
            </button>
          </form>

          <p style={{ textAlign: 'center', marginTop: '1.5rem', color: 'var(--muted)', fontSize: '0.875rem' }}>
            Already have an account?{' '}
            <Link to="/login" style={{ color: 'var(--primary-light)', fontWeight: 600 }}>Sign in</Link>
          </p>
        </div>
      </div>
    </div>
  );
}
