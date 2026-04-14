import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ShieldAlert, Search, X } from 'lucide-react';
import { getTimeline, listBundles, searchEvents } from '../api/eventApi';
import { getSeverityClass, getSeverityColor, formatTimestamp } from '../utils/helpers';

export default function Timeline() {
  const { bundleId } = useParams();
  const [bundles, setBundles] = useState([]);
  const [selectedBundle, setSelectedBundle] = useState(bundleId || '');
  const [timelineData, setTimelineData] = useState(null);
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(false);
  const [selectedEvent, setSelectedEvent] = useState(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [sourceFilter, setSourceFilter] = useState('');
  const navigate = useNavigate();

  useEffect(() => { listBundles().then(r => setBundles(r.data || [])).catch(() => {}); }, []);
  useEffect(() => { if (bundleId) setSelectedBundle(bundleId); }, [bundleId]);
  useEffect(() => {
    if (selectedBundle) {
      setLoading(true); setSelectedEvent(null);
      getTimeline(selectedBundle)
        .then(r => { setTimelineData(r.data); setEvents(r.data?.events || []); })
        .catch(() => { setTimelineData(null); setEvents([]); })
        .finally(() => setLoading(false));
    }
  }, [selectedBundle]);

  const handleSearch = () => {
    if (!selectedBundle) return;
    setLoading(true);
    const params = { bundleId: selectedBundle };
    if (searchQuery) params.q = searchQuery;
    if (sourceFilter) params.sourceType = sourceFilter;
    searchEvents(params).then(r => setEvents(r.data || [])).catch(() => setEvents([])).finally(() => setLoading(false));
  };

  const handleReset = () => {
    setSearchQuery(''); setSourceFilter('');
    if (selectedBundle) {
      setLoading(true);
      getTimeline(selectedBundle).then(r => setEvents(r.data?.events || [])).catch(() => setEvents([])).finally(() => setLoading(false));
    }
  };

  const stats = { total: events.length, critical: events.filter(e => (e.severity||0) >= 9).length, high: events.filter(e => (e.severity||0) >= 7 && (e.severity||0) < 9).length, medium: events.filter(e => (e.severity||0) >= 4 && (e.severity||0) < 7).length, low: events.filter(e => (e.severity||0) < 4).length };
  const sourceTypes = [...new Set(events.map(e => e.sourceType).filter(Boolean))].sort();
  const getSevLabel = (sev) => { if (sev == null) return 'INFO'; if (sev >= 9) return 'CRITICAL'; if (sev >= 7) return 'HIGH'; if (sev >= 4) return 'MEDIUM'; if (sev >= 1) return 'LOW'; return 'INFO'; };

  return (
    <div className="animate-in">
      <div className="page-header">
        <h2>Event Timeline</h2>
        <p>Unified chronological view of all events in a bundle</p>
      </div>
      <div className="filter-bar">
        <div className="form-group">
          <label className="form-label">Bundle</label>
          <select className="form-select" value={selectedBundle} onChange={e => { setSelectedBundle(e.target.value); navigate(`/timeline/${e.target.value}`); }} style={{ minWidth: 220 }}>
            <option value="">Select a bundle...</option>
            {bundles.map(b => <option key={b.id} value={b.id}>#{b.id} — {b.name || b.bundleKey}</option>)}
          </select>
        </div>
        <div className="form-group">
          <label className="form-label">Source Type</label>
          <select className="form-select" value={sourceFilter} onChange={e => setSourceFilter(e.target.value)}>
            <option value="">All Sources</option>
            {sourceTypes.map(s => <option key={s} value={s}>{s}</option>)}
          </select>
        </div>
        <div className="form-group">
          <label className="form-label">Search</label>
          <input className="form-input" placeholder="Search events..." value={searchQuery} onChange={e => setSearchQuery(e.target.value)} onKeyDown={e => e.key === 'Enter' && handleSearch()} style={{ minWidth: 200 }} />
        </div>
        <button className="btn btn-primary" onClick={handleSearch} style={{ alignSelf: 'flex-end' }}><Search size={14} /> Search</button>
        <button className="btn btn-secondary" onClick={handleReset} style={{ alignSelf: 'flex-end' }}>Reset</button>
      </div>

      {loading && <div className="loading-container"><div className="loading-spinner" /><p>Loading timeline...</p></div>}
      {!loading && !selectedBundle && <div className="empty-state"><ShieldAlert size={48} style={{ opacity: 0.3, marginBottom: 16 }} /><h3>Select a bundle</h3><p>Choose a bundle from the dropdown to view its event timeline.</p></div>}
      {!loading && selectedBundle && events.length === 0 && <div className="empty-state"><ShieldAlert size={48} style={{ opacity: 0.3, marginBottom: 16 }} /><h3>No events found</h3><p>This bundle has no events, or no events match your filters.</p></div>}

      {!loading && events.length > 0 && (
        <>
          <div className="card-grid card-grid-4" style={{ marginBottom: 24 }}>
            <div className="card" style={{ padding: '16px 20px' }}><div style={{ fontSize: 24, fontWeight: 700 }}>{stats.total}</div><div style={{ fontSize: 12, color: 'var(--text-secondary)' }}>Total Events</div></div>
            <div className="card" style={{ padding: '16px 20px' }}><div style={{ fontSize: 24, fontWeight: 700, color: 'var(--sev-critical)' }}>{stats.critical}</div><div style={{ fontSize: 12, color: 'var(--text-secondary)' }}>Critical</div></div>
            <div className="card" style={{ padding: '16px 20px' }}><div style={{ fontSize: 24, fontWeight: 700, color: 'var(--sev-high)' }}>{stats.high}</div><div style={{ fontSize: 12, color: 'var(--text-secondary)' }}>High</div></div>
            <div className="card" style={{ padding: '16px 20px' }}><div style={{ fontSize: 24, fontWeight: 700, color: 'var(--sev-medium)' }}>{stats.medium}</div><div style={{ fontSize: 12, color: 'var(--text-secondary)' }}>Medium</div></div>
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 340px', gap: 20 }}>
            <div className="card" style={{ padding: 0, maxHeight: 'calc(100vh - 380px)', overflowY: 'auto' }}>
              <div style={{ padding: '16px 20px', borderBottom: '1px solid var(--border)' }}>
                <div style={{ fontSize: 14, fontWeight: 600 }}>Chronological Event Timeline</div>
                <div style={{ fontSize: 12, color: 'var(--text-secondary)' }}>{events.length} events • Bundle #{selectedBundle}{timelineData?.bundleKey && ` • ${timelineData.bundleKey}`}</div>
              </div>
              <div style={{ paddingLeft: 10 }}>
                {events.map((evt, i) => (
                  <div key={evt.id || i} className="rca-timeline-item" style={{ cursor: 'pointer', background: selectedEvent?.id === evt.id ? 'var(--accent-glow)' : 'transparent', borderLeftColor: selectedEvent?.id === evt.id ? 'var(--accent)' : 'var(--border)' }} onClick={() => setSelectedEvent(evt)}>
                    <div className="rca-timeline-dot" style={{ background: getSeverityColor(evt.severity || 0) }} />
                    <div style={{ flex: 1 }}>
                      <div style={{ display: 'flex', gap: 12, alignItems: 'center', marginBottom: 4, flexWrap: 'wrap' }}>
                        <span style={{ color: 'var(--text-secondary)', fontSize: 12, fontFamily: 'monospace', minWidth: 140 }}>{formatTimestamp(evt.tsUtc)}</span>
                        <span className={`badge ${getSeverityClass(getSevLabel(evt.severity))}`}>{getSevLabel(evt.severity)}</span>
                        {evt.sourceType && <span style={{ fontSize: 11, color: 'var(--text-muted)' }}>{evt.sourceType}</span>}
                        {evt.action && <span style={{ fontWeight: 600, color: 'var(--text-primary)', fontSize: 13 }}>{evt.action}</span>}
                      </div>
                      <div style={{ color: 'var(--text-secondary)', fontSize: 13, lineHeight: 1.5 }}>{evt.message || `${evt.action || 'event'} ${evt.objectValue || ''}`.trim()}</div>
                      <div style={{ display: 'flex', gap: 12, marginTop: 4, fontSize: 11, color: 'var(--text-muted)', flexWrap: 'wrap' }}>
                        {evt.userName && <span>👤 {evt.userName}</span>}
                        {evt.host && <span>🖥 {evt.host}</span>}
                        {evt.srcIp && <span>{evt.srcIp}{evt.srcPort ? `:${evt.srcPort}` : ''}{evt.dstIp ? ` → ${evt.dstIp}${evt.dstPort ? `:${evt.dstPort}` : ''}` : ''}</span>}
                        {evt.result && <span style={{ color: (evt.result.toLowerCase().includes('fail') || evt.result.toLowerCase().includes('deny')) ? 'var(--sev-critical)' : 'var(--accent-green)' }}>{evt.result}</span>}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            <div className="inspector-panel" style={{ alignSelf: 'start', position: 'sticky', top: 32, maxHeight: 'calc(100vh - 380px)', overflowY: 'auto' }}>
              <h3 style={{ padding: '16px 20px', borderBottom: '1px solid var(--border)', margin: 0, fontSize: 14, fontWeight: 600, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span>Event Inspector</span>
                {selectedEvent && <button className="btn btn-ghost" onClick={() => setSelectedEvent(null)} style={{ padding: 4 }}><X size={16} /></button>}
              </h3>
              {selectedEvent ? (
                <div>
                  {[
                    ['Event ID', `#${selectedEvent.id}`, { color: 'var(--accent)' }],
                    ['Timestamp', formatTimestamp(selectedEvent.tsUtc)],
                    selectedEvent.tsOriginal && ['Original TS', selectedEvent.tsOriginal],
                    ['Severity', `${selectedEvent.severity ?? '—'} (${getSevLabel(selectedEvent.severity)})`, { color: getSeverityColor(selectedEvent.severity || 0) }],
                    selectedEvent.sourceType && ['Source', selectedEvent.sourceType],
                    selectedEvent.userName && ['User', selectedEvent.userName],
                    selectedEvent.host && ['Host', selectedEvent.host],
                    (selectedEvent.srcIp || selectedEvent.dstIp) && ['Network', `${selectedEvent.srcIp || '?'}${selectedEvent.srcPort ? `:${selectedEvent.srcPort}` : ''} → ${selectedEvent.dstIp || '?'}${selectedEvent.dstPort ? `:${selectedEvent.dstPort}` : ''}`],
                    selectedEvent.protocol && ['Protocol', selectedEvent.protocol],
                    selectedEvent.action && ['Action', selectedEvent.action],
                    selectedEvent.objectValue && ['Object', selectedEvent.objectValue],
                    selectedEvent.result && ['Result', selectedEvent.result, { color: (selectedEvent.result.toLowerCase().includes('fail') || selectedEvent.result.toLowerCase().includes('deny')) ? 'var(--sev-critical)' : 'var(--accent-green)' }],
                  ].filter(Boolean).map(([label, value, style], i) => (
                    <div key={i} className="inspector-row"><span className="label">{label}</span><span className="value" style={style}>{value}</span></div>
                  ))}
                  {selectedEvent.message && <div style={{ padding: '12px 20px', borderTop: '1px solid var(--border)', fontSize: 13, color: 'var(--text-secondary)', lineHeight: 1.6 }}>{selectedEvent.message}</div>}
                  {selectedEvent.metadataJson && <JsonBlock title="Metadata" json={selectedEvent.metadataJson} />}
                  {selectedEvent.iocsJson && <JsonBlock title="IOCs" json={selectedEvent.iocsJson} />}
                  {selectedEvent.correlationKeysJson && <JsonBlock title="Correlation Keys" json={selectedEvent.correlationKeysJson} />}
                  {selectedEvent.rawRefJson && <JsonBlock title="Raw Reference" json={selectedEvent.rawRefJson} />}
                </div>
              ) : (
                <div className="empty-state" style={{ padding: '40px 20px' }}><h3 style={{ fontSize: 14 }}>No event selected</h3><p style={{ fontSize: 12 }}>Click any event in the timeline to inspect it.</p></div>
              )}
            </div>
          </div>
        </>
      )}
    </div>
  );
}

function JsonBlock({ title, json }) {
  let formatted;
  try { formatted = JSON.stringify(JSON.parse(json), null, 2); } catch { formatted = json; }
  return (
    <div style={{ padding: '0 20px', marginTop: 12 }}>
      <div style={{ fontSize: 11, fontWeight: 600, textTransform: 'uppercase', letterSpacing: '0.5px', color: 'var(--text-muted)', marginBottom: 6 }}>{title}</div>
      <pre style={{ background: 'var(--bg-input)', border: '1px solid var(--border)', borderRadius: 'var(--radius-sm)', padding: 12, fontSize: 11, fontFamily: "monospace", color: 'var(--text-secondary)', whiteSpace: 'pre-wrap', wordBreak: 'break-all', maxHeight: 200, overflowY: 'auto', margin: 0 }}>{formatted}</pre>
    </div>
  );
}
