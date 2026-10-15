import React from 'react';

export default function Navbar({ user, currentPage, onNavigate, onLogout }) {
  return (
    <header className="navbar" style={{ background: '#1e293b', borderBottom: '1px solid #334155', padding: '0.8rem 2rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
      <div className="nav-brand" style={{ cursor: 'pointer', fontSize: '1.2rem', fontWeight: 700, display: 'flex', alignItems: 'center', gap: '0.5rem', color: '#f8fafc' }} onClick={() => onNavigate('dashboard')}>
        <span>⚡</span> AI Career Assistant
      </div>

      <nav className="nav-links" style={{ display: 'flex', alignItems: 'center', gap: '0.6rem', flexWrap: 'wrap' }}>
        <button
          className={`nav-btn ${currentPage === 'dashboard' ? 'active' : ''}`}
          onClick={() => onNavigate('dashboard')}
          style={{ background: currentPage === 'dashboard' ? 'rgba(99, 102, 241, 0.2)' : 'transparent', color: currentPage === 'dashboard' ? '#818cf8' : '#94a3b8', border: 'none', padding: '0.45rem 0.75rem', borderRadius: '6px', cursor: 'pointer', fontWeight: 600, fontSize: '0.85rem' }}
        >
          Dashboard
        </button>

        <button
          className={`nav-btn ${currentPage === 'resume-upload' || currentPage === 'resume-analysis' ? 'active' : ''}`}
          onClick={() => onNavigate('resume-upload')}
          style={{ background: currentPage === 'resume-upload' || currentPage === 'resume-analysis' ? 'rgba(99, 102, 241, 0.2)' : 'transparent', color: currentPage === 'resume-upload' || currentPage === 'resume-analysis' ? '#818cf8' : '#94a3b8', border: 'none', padding: '0.45rem 0.75rem', borderRadius: '6px', cursor: 'pointer', fontWeight: 600, fontSize: '0.85rem' }}
        >
          📄 Resumes
        </button>

        <button
          className={`nav-btn ${currentPage === 'skill-gap' ? 'active' : ''}`}
          onClick={() => onNavigate('skill-gap')}
          style={{ background: currentPage === 'skill-gap' ? 'rgba(99, 102, 241, 0.2)' : 'transparent', color: currentPage === 'skill-gap' ? '#818cf8' : '#94a3b8', border: 'none', padding: '0.45rem 0.75rem', borderRadius: '6px', cursor: 'pointer', fontWeight: 600, fontSize: '0.85rem' }}
        >
          ⚡ Skill Gap
        </button>

        <button
          className={`nav-btn ${currentPage === 'resume-tailor' ? 'active' : ''}`}
          onClick={() => onNavigate('resume-tailor')}
          style={{ background: currentPage === 'resume-tailor' ? 'rgba(99, 102, 241, 0.2)' : 'transparent', color: currentPage === 'resume-tailor' ? '#818cf8' : '#94a3b8', border: 'none', padding: '0.45rem 0.75rem', borderRadius: '6px', cursor: 'pointer', fontWeight: 600, fontSize: '0.85rem' }}
        >
          🎯 Resume Tailor
        </button>

        <button
          className={`nav-btn ${currentPage === 'career-recommendations' ? 'active' : ''}`}
          onClick={() => onNavigate('career-recommendations')}
          style={{ background: currentPage === 'career-recommendations' ? 'rgba(99, 102, 241, 0.2)' : 'transparent', color: currentPage === 'career-recommendations' ? '#818cf8' : '#94a3b8', border: 'none', padding: '0.45rem 0.75rem', borderRadius: '6px', cursor: 'pointer', fontWeight: 600, fontSize: '0.85rem' }}
        >
          💡 Recommendations
        </button>

        <button
          className={`nav-btn ${currentPage === 'job-matching' || currentPage === 'job-details' ? 'active' : ''}`}
          onClick={() => onNavigate('job-matching')}
          style={{ background: currentPage === 'job-matching' || currentPage === 'job-details' ? 'rgba(99, 102, 241, 0.2)' : 'transparent', color: currentPage === 'job-matching' || currentPage === 'job-details' ? '#818cf8' : '#94a3b8', border: 'none', padding: '0.45rem 0.75rem', borderRadius: '6px', cursor: 'pointer', fontWeight: 600, fontSize: '0.85rem' }}
        >
          💼 Jobs
        </button>

        <button
          className={`nav-btn ${currentPage === 'applications' ? 'active' : ''}`}
          onClick={() => onNavigate('applications')}
          style={{ background: currentPage === 'applications' ? 'rgba(99, 102, 241, 0.2)' : 'transparent', color: currentPage === 'applications' ? '#818cf8' : '#94a3b8', border: 'none', padding: '0.45rem 0.75rem', borderRadius: '6px', cursor: 'pointer', fontWeight: 600, fontSize: '0.85rem' }}
        >
          📋 Applications
        </button>
      </nav>

      <div className="user-pill" style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
        <span className="user-name" style={{ color: '#cbd5e1', fontSize: '0.9rem', fontWeight: 600 }}>{user?.name || 'User'}</span>
        <button className="btn-secondary" onClick={onLogout} style={{ background: '#334155', color: '#f8fafc', border: 'none', padding: '0.4rem 0.8rem', borderRadius: '6px', cursor: 'pointer', fontSize: '0.85rem' }}>
          Logout
        </button>
      </div>
    </header>
  );
}
