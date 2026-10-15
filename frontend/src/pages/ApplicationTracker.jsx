import React, { useState, useEffect } from 'react';
import { applicationService } from '../services/applicationService';

export default function ApplicationTracker({ user, onNavigate }) {
  const [applications, setApplications] = useState([]);
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');

  useEffect(() => {
    if (user?.id) {
      loadTrackerData();
    }
  }, [user]);

  const loadTrackerData = async () => {
    setLoading(true);
    setError('');
    try {
      const [appList, appStats] = await Promise.all([
        applicationService.getUserApplications(user.id),
        applicationService.getApplicationStats(user.id),
      ]);
      setApplications(appList);
      setStats(appStats);
    } catch (err) {
      console.error('Failed to load application tracker data:', err);
      setError(err?.message || 'Failed to load job applications.');
    } finally {
      setLoading(false);
    }
  };

  const handleStatusChange = async (appId, newStatus) => {
    try {
      await applicationService.updateApplicationStatus(appId, user.id, newStatus);
      loadTrackerData();
    } catch (err) {
      setError(err?.message || 'Failed to update application status.');
    }
  };

  const getStatusBadgeStyle = (status) => {
    switch (status) {
      case 'APPLIED':
        return { bg: 'rgba(59, 130, 246, 0.15)', color: '#60a5fa', border: 'rgba(59, 130, 246, 0.3)' };
      case 'SHORTLISTED':
        return { bg: 'rgba(168, 85, 247, 0.15)', color: '#c084fc', border: 'rgba(168, 85, 247, 0.3)' };
      case 'INTERVIEW':
        return { bg: 'rgba(245, 158, 11, 0.15)', color: '#fbbf24', border: 'rgba(245, 158, 11, 0.3)' };
      case 'SELECTED':
        return { bg: 'rgba(16, 185, 129, 0.15)', color: '#34d399', border: 'rgba(16, 185, 129, 0.3)' };
      case 'REJECTED':
        return { bg: 'rgba(244, 63, 94, 0.15)', color: '#f87171', border: 'rgba(244, 63, 94, 0.3)' };
      case 'WITHDRAWN':
        return { bg: 'rgba(148, 163, 184, 0.15)', color: '#94a3b8', border: 'rgba(148, 163, 184, 0.3)' };
      default:
        return { bg: 'rgba(148, 163, 184, 0.15)', color: '#94a3b8', border: 'rgba(148, 163, 184, 0.3)' };
    }
  };

  const filteredApps = statusFilter === 'ALL'
    ? applications
    : applications.filter((a) => a.status === statusFilter);

  return (
    <div className="application-tracker-container animate-fade-in" style={{ padding: '2rem' }}>
      <div className="page-header" style={{ marginBottom: '1.5rem' }}>
        <h1 style={{ fontSize: '1.8rem', fontWeight: 700, marginBottom: '0.4rem' }}>📋 Job Application Tracker</h1>
        <p style={{ color: '#94a3b8' }}>Monitor application stages, interview status, and response metrics</p>
      </div>

      {error && (
        <div style={{ background: 'rgba(244, 63, 94, 0.15)', border: '1px solid rgba(244, 63, 94, 0.3)', padding: '1rem', borderRadius: '8px', color: '#f43f5e', marginBottom: '1.5rem' }}>
          ⚠️ {error}
        </div>
      )}

      {/* Statistics Counter Cards */}
      {stats && (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(140px, 1fr))', gap: '1rem', marginBottom: '2rem' }}>
          <div style={{ background: '#1e293b', border: '1px solid #334155', padding: '1rem', borderRadius: '10px', textAlign: 'center' }}>
            <span style={{ fontSize: '0.8rem', color: '#94a3b8' }}>Total Applied</span>
            <div style={{ fontSize: '1.8rem', fontWeight: 800, color: '#f8fafc', marginTop: '0.2rem' }}>{stats.totalApplications}</div>
          </div>
          <div style={{ background: '#1e293b', border: '1px solid #334155', padding: '1rem', borderRadius: '10px', textAlign: 'center' }}>
            <span style={{ fontSize: '0.8rem', color: '#60a5fa' }}>Applied</span>
            <div style={{ fontSize: '1.8rem', fontWeight: 800, color: '#60a5fa', marginTop: '0.2rem' }}>{stats.applied}</div>
          </div>
          <div style={{ background: '#1e293b', border: '1px solid #334155', padding: '1rem', borderRadius: '10px', textAlign: 'center' }}>
            <span style={{ fontSize: '0.8rem', color: '#c084fc' }}>Shortlisted</span>
            <div style={{ fontSize: '1.8rem', fontWeight: 800, color: '#c084fc', marginTop: '0.2rem' }}>{stats.shortlisted}</div>
          </div>
          <div style={{ background: '#1e293b', border: '1px solid #334155', padding: '1rem', borderRadius: '10px', textAlign: 'center' }}>
            <span style={{ fontSize: '0.8rem', color: '#fbbf24' }}>Interviews</span>
            <div style={{ fontSize: '1.8rem', fontWeight: 800, color: '#fbbf24', marginTop: '0.2rem' }}>{stats.interviews}</div>
          </div>
          <div style={{ background: '#1e293b', border: '1px solid #334155', padding: '1rem', borderRadius: '10px', textAlign: 'center' }}>
            <span style={{ fontSize: '0.8rem', color: '#34d399' }}>Selected</span>
            <div style={{ fontSize: '1.8rem', fontWeight: 800, color: '#34d399', marginTop: '0.2rem' }}>{stats.selected}</div>
          </div>
          <div style={{ background: '#1e293b', border: '1px solid #334155', padding: '1rem', borderRadius: '10px', textAlign: 'center' }}>
            <span style={{ fontSize: '0.8rem', color: '#f87171' }}>Rejected</span>
            <div style={{ fontSize: '1.8rem', fontWeight: 800, color: '#f87171', marginTop: '0.2rem' }}>{stats.rejected}</div>
          </div>
        </div>
      )}

      {/* Filter Tabs */}
      <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap', marginBottom: '1.5rem' }}>
        {['ALL', 'APPLIED', 'SHORTLISTED', 'INTERVIEW', 'SELECTED', 'REJECTED', 'WITHDRAWN'].map((st) => (
          <button
            key={st}
            onClick={() => setStatusFilter(st)}
            style={{
              padding: '0.45rem 0.9rem',
              borderRadius: '20px',
              border: statusFilter === st ? '1px solid #6366f1' : '1px solid #334155',
              background: statusFilter === st ? 'rgba(99, 102, 241, 0.2)' : '#1e293b',
              color: statusFilter === st ? '#818cf8' : '#94a3b8',
              fontWeight: 600,
              fontSize: '0.8rem',
              cursor: 'pointer',
            }}
          >
            {st}
          </button>
        ))}
      </div>

      {loading ? (
        <div style={{ textAlign: 'center', padding: '4rem 0', color: '#94a3b8' }}>
          <div className="spinner" style={{ width: '32px', height: '32px', margin: '0 auto 1rem auto' }}></div>
          <p>Loading application history...</p>
        </div>
      ) : filteredApps.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '3rem', background: '#1e293b', borderRadius: '12px', border: '1px solid #334155' }}>
          <h3>No Applications Found</h3>
          <p style={{ color: '#94a3b8', margin: '0.5rem 0 1.5rem 0' }}>Explore open roles and apply to start tracking your career progression.</p>
          <button className="btn-primary" onClick={() => onNavigate && onNavigate('job-matching')}>
            🔍 Find Jobs Now
          </button>
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          {filteredApps.map((app) => {
            const badge = getStatusBadgeStyle(app.status);
            return (
              <div key={app.id} style={{ background: '#1e293b', border: '1px solid #334155', borderRadius: '10px', padding: '1.25rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem' }}>
                <div>
                  <h3 style={{ fontSize: '1.15rem', fontWeight: 700, color: '#f8fafc', marginBottom: '0.25rem' }}>{app.jobTitle}</h3>
                  <p style={{ color: '#818cf8', fontWeight: 600, fontSize: '0.9rem', marginBottom: '0.4rem' }}>{app.companyName} • 📍 {app.location}</p>
                  <span style={{ fontSize: '0.8rem', color: '#94a3b8' }}>
                    Applied: {new Date(app.appliedAt).toLocaleDateString()}
                  </span>
                </div>

                <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                  <div style={{ background: badge.bg, color: badge.color, border: `1px solid ${badge.border}`, padding: '0.35rem 0.85rem', borderRadius: '20px', fontWeight: 700, fontSize: '0.85rem' }}>
                    ● {app.status}
                  </div>

                  <select
                    value={app.status}
                    onChange={(e) => handleStatusChange(app.id, e.target.value)}
                    style={{ padding: '0.35rem 0.6rem', background: '#0f172a', border: '1px solid #334155', borderRadius: '6px', color: '#fff', fontSize: '0.8rem' }}
                  >
                    <option value="APPLIED">APPLIED</option>
                    <option value="SHORTLISTED">SHORTLISTED</option>
                    <option value="INTERVIEW">INTERVIEW</option>
                    <option value="SELECTED">SELECTED</option>
                    <option value="REJECTED">REJECTED</option>
                    <option value="WITHDRAWN">WITHDRAWN</option>
                  </select>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}
