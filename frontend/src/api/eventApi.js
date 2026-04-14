import axios from 'axios';

const api = axios.create({
  baseURL: '/',
  timeout: 30000,
  headers: { 'Content-Type': 'application/json' }
});

export const listBundles = () => api.get('/bundles');
export const getBundle = (id) => api.get(`/bundles/${id}`);
export const getTimeline = (bundleId) => api.get(`/timeline?bundleId=${bundleId}`);
export const getEvents = (params) => api.get('/events', { params });
export const getEventsByBundle = (bundleId) => api.get(`/events/by-bundle/${bundleId}`);
export const ingestEvents = (payload) => api.post('/ingest', payload);
export const searchEvents = (params) => api.get('/events', { params });

export default api;
