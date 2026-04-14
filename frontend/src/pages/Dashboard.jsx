import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Activity, ShieldAlert, Network, Server, Clock, AlertTriangle } from 'lucide-react';
import { listBundles } from '../api/eventApi';
import { formatTimestamp } from '../utils/helpers';

export default function Dashboard() {
  const [bundles, setBundles] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    listBundles()
      .then(r => setBundles(r.data || []))
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return <div className="loading-container"><div className="loading-spinner" /><p>Loading dashboard...</p></div>;
  }

  return (
    <div className="animate-in">
      <div className="page-header">
        <h2>Dashboard</h2>
        <p>Event Service — Unified incident log analysis</p>
      </div>

      <div className="card-grid card-grid-4" style={{ marginBottom: 24 }}>
        <div className="card">
          <div className="stat-card">
            <div className="stat-icon blue"><Activity size={22} /></div>
            <div className="stat-info">
              <h3 style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                <span className="status-dot online" />
                UP
              </h3>
              <p>Service Status</p>
            </div>
          </div>
        </div>
        <div className="card">
          <div className="stat-card">
            <div className="stat-icon purple"><ShieldAlert size={22} /></div>
            <div className="stat-info">
              <h3>{bundles.length}</h3>
              <p>Total Bundles</p>
            </div>
          </div>
        </div>
        <div className="card">
          <div className="stat-card">
            <div className="stat-icon green"><Network size={22} /></div>
            <div className="stat-info">
              <h3>—</h3>
              <p>Events Stored</p>
            </div>
          </div>
        </div>
        <div className="card">
          <div className="stat-card">
            <div className="stat-icon red"><AlertTriangle size={22} /></div>
            <div className="stat-info">
              <h3>—</h3>
              <p>Critical Events</p>
            </div>
          </div>
        </div>
      </div>

      <div className="card">
        <h3 style={{ marginBottom: 16, fontSize: 16, fontWeight: 600 }}>Recent Bundles</h3>
        {bundles.length === 0 ? (
          <div className="empty-state">
            <Server size={40} style={{ marginBottom: 12, opacity: 0.3 }} />
            <h3>No bundles yet</h3>
            <p>Upload logs via the Ingestion page to get started.</p>
          </div>
        ) : (
          <table className="data-table">
            <thead>
              <tr>
                <th>Bundle ID</th>
                <th>Bundle Key</th>
                <th>Name</th>
                <th>Created</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {bundles.map((b) => (
                <tr key={b.id} className="bundle-row" onClick={() => navigate(`/timeline/${b.id}`)}>
                  <td style={{ fontFamily: 'monospace', fontSize: 12, color: 'var(--accent)' }}>#{b.id}</td>
                  <td style={{ fontFamily: 'monospace', fontSize: 12 }}>{b.bundleKey}</td>
                  <td>{b.name || '—'}</td>
                  <td style={{ fontSize: 12 }}>{formatTimestamp(b.createdAt)}</td>
                  <td>
                    <button className="btn btn-ghost" style={{ padding: '4px 10px', fontSize: 12 }}
                      onClick={e => { e.stopPropagation(); navigate(`/timeline/${b.id}`); }}>
                      Timeline
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
