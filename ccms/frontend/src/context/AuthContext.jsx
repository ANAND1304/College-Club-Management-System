import { createContext, useContext, useState, useEffect } from 'react';
import axios from 'axios';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser]     = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token    = localStorage.getItem('ccms_token');
    const userData = localStorage.getItem('ccms_user');
    if (token && userData) {
      try {
        const parsed = JSON.parse(userData);
        setUser(parsed);
        axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      } catch {
        localStorage.removeItem('ccms_token');
        localStorage.removeItem('ccms_user');
      }
    }
    setLoading(false);
  }, []);

  const login = (token, userData) => {
    localStorage.setItem('ccms_token', token);
    localStorage.setItem('ccms_user', JSON.stringify(userData));
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    setUser(userData);
  };

  const logout = () => {
    localStorage.removeItem('ccms_token');
    localStorage.removeItem('ccms_user');
    delete axios.defaults.headers.common['Authorization'];
    setUser(null);
  };

  const value = {
    user,
    login,
    logout,
    loading,
    isAdmin:    user?.role === 'ADMIN',
    isClubHead: user?.role === 'CLUB_HEAD',
    isStudent:  user?.role === 'STUDENT',
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
};
