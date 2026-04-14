import { Routes, Route, NavLink, useLocation } from 'react-router-dom';
import { LayoutDashboard, Upload, ShieldAlert, Network, FileText } from 'lucide-react';
import Dashboard from './pages/Dashboard.jsx';
import Ingest from './pages/Ingest.jsx';
import Timeline from './pages/Timeline.jsx';

const navItems = [
  { path: '/', icon: LayoutDashboard, label: 'Dashboard' },
  { path: '/ingest', icon: Upload, label: 'Log Ingestion' },
  { path: '/timeline', icon: ShieldAlert, label: 'Event Timeline' },
];

export default function App() {
  const location = useLocation();

  return (
    <div className="app-layout">
      {/* Sidebar */}
      <aside className="sidebar">
        <div className="sidebar-logo">
          <div className="logo-dot" />
          <h1>VULNURIS</h1>
        </div>
        <nav className="sidebar-nav">
          {navItems.map(({ path, icon: Icon, label }) => (
            <NavLink
              key={path}
              to={path}
              className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
              end={path === '/'}
            >
              <Icon size={18} />
              {label}
            </NavLink>
          ))}
        </nav>
        <div className="sidebar-footer">
          Event Service v1.0.0
        </div>
      </aside>

      {/* Main content */}
      <main className="main-content">
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/ingest" element={<Ingest />} />
          <Route path="/timeline" element={<Timeline />} />
          <Route path="/timeline/:bundleId" element={<Timeline />} />
        </Routes>
      </main>
    </div>
  );
}
