import { useState, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { Upload, Filter, Clock, AlertTriangle, CheckCircle, Loader } from 'lucide-react';
import { ingestEvents } from '../api/eventApi';

export default function Ingest() {
  const [events, setEvents] = useState(null);
  const [fileName, setFileName] = useState('');
  const [processing, setProcessing] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);
  const [dragOver, setDragOver] = useState(false);
  const fileRef = useRef(null);
  const navigate = useNavigate();

  const handleFile = (file) => {
    if (!file) return;
    setFileName(file.name);
    setResult(null);
    setError(null);
    const reader = new FileReader();
    reader.onload = (e) => {
      try {
        const parsed = JSON.parse(e.target.result);
        const arr = Array.isArray(parsed) ? parsed : (parsed.events || [parsed]);
        setEvents(arr);
      } catch (err) {
        setError('Invalid JSON file: ' + err.message);
        setEvents(null);
      }
    };
    reader.readAsText(file);
  };

  const handleSubmit = async () => {
    if (!events || events.length === 0) return;
    setProcessing(true);
    setError(null);
    setResult(null);
    try {
      const res = await ingestEvents({ bundleName: fileName, description: `Uploaded from ${fileName}`, events });
      setResult(res.data);
    } catch (err) {
      setError(err.response?.data?.error || err.message);
    } finally {
      setProcessing(false);
    }
  };

  const severityStats = events ? {
    total: events.length,
    critical: events.filter(e => e.severity >= 9).length,
    high: events.filter(e => e.severity >= 7 && e.severity < 9).length,
    medium: events.filter(e => e.severity >= 4 && e.severity < 7).length,
    low: events.filter(e => e.severity < 4).length,
  } : null;

  return (
    <div className="animate-in">
      <div className="page-header">
        <h2>Log Ingestion</h2>
        <p>Upload CES-formatted JSON logs for timeline analysis</p>
      </div>
      <div className={`upload-zone ${dragOver ? 'dragover' : ''}`}
        onClick={() => fileRef.current?.click()}
        onDragOver={e => { e.preventDefault(); setDragOver(true); }}
        onDragLeave={() => setDragOver(false)}
        onDrop={e => { e.preventDefault(); setDragOver(false); handleFile(e.dataTransfer.files[0]); }}
        style={{ marginBottom: 24 }}>
        <Upload size={36} style={{ marginBottom: 12, color: 'var(--accent)', opacity: 0.7 }} />
        <h3>{fileName || 'Drop JSON file here or click to browse'}</h3>
        <p>{events ? `${events.length} events loaded` : 'Supports CES event format (.json)'}</p>
        <input ref={fileRef} type="file" accept=".json" style={{ display: 'none' }}
          onChange={e => handleFile(e.target.files[0])} />
      </div>
      {severityStats && (
        <div className="card-grid card-grid-4" style={{ marginBottom: 24 }}>
          <div className="card"><div style={{ fontSize: 28, fontWeight: 700 }}>{severityStats.total}</div><div style={{ fontSize: 13, color: 'var(--text-secondary)' }}>Total Events</div></div>
          <div className="card"><div style={{ fontSize: 28, fontWeight: 700, color: 'var(--sev-critical)' }}>{severityStats.critical}</div><div style={{ fontSize: 13, color: 'var(--text-secondary)' }}>Critical</div></div>
          <div className="card"><div style={{ fontSize: 28, fontWeight: 700, color: 'var(--sev-high)' }}>{severityStats.high}</div><div style={{ fontSize: 13, color: 'var(--text-secondary)' }}>High</div></div>
          <div className="card"><div style={{ fontSize: 28, fontWeight: 700, color: 'var(--sev-medium)' }}>{severityStats.medium}</div><div style={{ fontSize: 13, color: 'var(--text-secondary)' }}>Medium</div></div>
        </div>
      )}
      <div style={{ marginBottom: 24 }}>
        <button className="btn btn-primary" disabled={!events || processing} onClick={handleSubmit}>
          {processing ? <><Loader size={16} /> Processing...</> : 'Ingest Events'}
        </button>
      </div>
      {error && (
        <div className="card" style={{ borderColor: 'var(--sev-critical)', marginBottom: 24 }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 10, color: 'var(--sev-critical)' }}>
            <AlertTriangle size={18} /><span>{error}</span>
          </div>
        </div>
      )}
      {result && (
        <div className="card" style={{ borderColor: 'var(--accent-green)' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 16 }}>
            <CheckCircle size={20} style={{ color: 'var(--accent-green)' }} />
            <span style={{ fontSize: 16, fontWeight: 600 }}>Ingestion Complete</span>
          </div>
          <div style={{ display: 'flex', gap: 32, flexWrap: 'wrap' }}>
            <div><div style={{ fontSize: 12, color: 'var(--text-secondary)' }}>Bundle ID</div><div style={{ fontSize: 14, fontFamily: 'monospace', color: 'var(--accent)' }}>#{result.bundleId}</div></div>
            <div><div style={{ fontSize: 12, color: 'var(--text-secondary)' }}>Bundle Key</div><div style={{ fontSize: 14, fontFamily: 'monospace' }}>{result.bundleKey}</div></div>
            <div><div style={{ fontSize: 12, color: 'var(--text-secondary)' }}>Events Ingested</div><div style={{ fontSize: 20, fontWeight: 700, color: 'var(--accent)' }}>{result.eventCount}</div></div>
          </div>
          {result.bundleId && (
            <div style={{ marginTop: 20 }}>
              <button className="btn btn-primary" onClick={() => navigate(`/timeline/${result.bundleId}`)}>View Timeline</button>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
