import axios from 'axios';

// ─── Axios instance ───────────────────────────────────────────────────────────
const API = axios.create({
  baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080/api',
  headers: { 'Content-Type': 'application/json' },
  timeout: 15000,
});

// Attach JWT from localStorage on every request
API.interceptors.request.use((config) => {
  const token = localStorage.getItem('ccms_token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// Redirect to login on 401
API.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      localStorage.removeItem('ccms_token');
      localStorage.removeItem('ccms_user');
      window.location.href = '/login';
    }
    return Promise.reject(err);
  }
);

// ─── Auth ─────────────────────────────────────────────────────────────────────
export const authAPI = {
  register: (data) => API.post('/auth/register', data),
  login:    (data) => API.post('/auth/login', data),
};

// ─── Clubs ────────────────────────────────────────────────────────────────────
export const clubAPI = {
  getAll:     ()        => API.get('/clubs'),
  getById:    (id)      => API.get(`/clubs/${id}`),
  getMembers: (id)      => API.get(`/clubs/${id}/members`),
  create:     (data)    => API.post('/clubs', data),
  update:     (id, data)=> API.put(`/clubs/${id}`, data),
  delete:     (id)      => API.delete(`/clubs/${id}`),
  join:       (id)      => API.post(`/clubs/${id}/join`),
  leave:      (id)      => API.delete(`/clubs/${id}/leave`),
};

// ─── Events ───────────────────────────────────────────────────────────────────
export const eventAPI = {
  getAll:    ()         => API.get('/events'),
  getById:   (id)       => API.get(`/events/${id}`),
  getByClub: (clubId)   => API.get(`/events/club/${clubId}`),
  create:    (data)     => API.post('/events', data),
  update:    (id, data) => API.put(`/events/${id}`, data),
  delete:    (id)       => API.delete(`/events/${id}`),
};

// ─── Admin ────────────────────────────────────────────────────────────────────
export const adminAPI = {
  getDashboard: ()            => API.get('/admin/dashboard'),
  getUsers:     ()            => API.get('/admin/users'),
  getPending:   ()            => API.get('/admin/memberships/pending'),
  approve:      (id)          => API.put(`/admin/memberships/${id}/approve`),
  reject:       (id)          => API.put(`/admin/memberships/${id}/reject`),
  toggleUser:   (id)          => API.put(`/admin/users/${id}/toggle`),
  promoteUser:  (id, role)    => API.put(`/admin/users/${id}/role?role=${role}`),
};

export default API;
