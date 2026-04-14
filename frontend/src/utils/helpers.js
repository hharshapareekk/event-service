export const getSeverityClass = (label) => {
  if (!label) return 'badge-info';
  switch (label.toUpperCase()) {
    case 'CRITICAL': return 'badge-critical';
    case 'HIGH': return 'badge-high';
    case 'MEDIUM': return 'badge-medium';
    case 'LOW': return 'badge-low';
    default: return 'badge-info';
  }
};

export const getSeverityColor = (severity) => {
  if (severity >= 9) return '#ff3366';
  if (severity >= 7) return '#ff6b35';
  if (severity >= 4) return '#ffc107';
  return '#22c55e';
};

export const getNodeColor = (type) => {
  switch (type) {
    case 'Event': return '#38bdf8';
    case 'User': return '#a855f7';
    case 'IP': return '#f97316';
    case 'Host': return '#22c55e';
    case 'IOC': return '#ef4444';
    default: return '#64748b';
  }
};

export const formatTimestamp = (ts) => {
  if (!ts) return '—';
  try {
    return new Date(ts).toLocaleString();
  } catch {
    return ts;
  }
};

export const truncate = (str, len = 40) => {
  if (!str) return '—';
  return str.length > len ? str.slice(0, len) + '...' : str;
};

export const KILL_CHAIN_ORDER = [
  'RECONNAISSANCE', 'WEAPONIZATION', 'DELIVERY',
  'EXPLOITATION', 'INSTALLATION', 'COMMAND_AND_CONTROL',
  'LATERAL_MOVEMENT', 'ACTIONS_ON_OBJECTIVES'
];
