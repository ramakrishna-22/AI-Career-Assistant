import React, { useState, useEffect } from 'react';
import { jobService } from '../services/jobService';
import { applicationService } from '../services/applicationService';
import { getUserResumes } from '../services/resumeService';

export default function Dashboard({ user, onLogout, onNavigate }) {
  const [recommendedJobs, setRecommendedJobs] = useState([]);
  const [appStats, setAppStats] = useState(null);
  const [latestResume, setLatestResume] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (user?.id) {
      loadDashboardData();
    }
  }, [user]);

  const loadDashboardData = async () => {
    setLoading(true);
    try {
      const [jobs, stats, resumes] = await Promise.all([
        jobService.getRecommendedJobs(user.id, 4).catch(() => []),
        applicationService.getApplicationStats(user.id).catch(() => null),
        getUserResumes(user.id).catch(() => []),
      ]);
      setRecommendedJobs(jobs);
      setAppStats(stats);
      if (resumes && resumes.length > 0) {
        setLatestResume(resumes[0]);
      }
    } catch (err) {
      console.error('Dashboard load error:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="dashboard-container animate-fade-in" style={{ padding: '2rem' }}>
      {/* Welcome Hero Banner */}
      <section className="hero-banner" style={{ background: 'linear-gradient(135deg, rgba(99, 102, 241, 0.15) 0%, rgba(139, 92, 246, 0.15) 100%)', padding: '2rem', borderRadius: '16px', border: '1px solid rgba(99, 102, 241, 0.3)', marginBottom: '2rem' }}>
        <h1 style={{ fontSize: '2rem', fontWeight: 800, color: '#f8fafc', marginBottom: '0.5rem' }}>Welcome back, {user?.name || 'User'} 👋</h1>
        <p style={{ color: '#cbd5e1', fontSize: '1rem', maxWidth: '700px' }}>Your AI-powered career hub is active. Track application progress, discover skill-matched roles, optimize resume content, and review personalized learning roadmaps.</p>
      </section>

      {/* Quick Actions */}
      <section style={{ marginBottom: '2.5rem' }}>
        <h2 style={{ fontSize: '1.3rem', fontWeight: 700, marginBottom: '1rem', color: '#f8fafc' }}>⚡ Quick Actions</h2>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '1rem' }}>
          <button onClick={() => onNavigate('resume-upload')} style={{ background: '#1e293b', border: '1px solid #334155', borderRadius: '10px', padding: '1.1rem', textAlign: 'left', cursor: 'pointer', display: 'flex', flexDirection: 'column', gap: '0.4rem' }}>
            <span style={{ fontSize: '1.4rem' }}>📤</span>
            <span style={{ fontWeight: 700, color: '#f8fafc' }}>Upload Resume</span>
            <span style={{ fontSize: '0.78rem', color: '#94a3b8' }}>Upload latest PDF/DOCX</span>
          </button>

          <button onClick={() => onNavigate('skill-gap')} style={{ background: '#1e293b', border: '1px solid #334155', borderRadius: '10px', padding: '1.1rem', textAlign: 'left', cursor: 'pointer', display: 'flex', flexDirection: 'column', gap: '0.4rem' }}>
            <span style={{ fontSize: '1.4rem' }}>⚡</span>
            <span style={{ fontWeight: 700, color: '#f8fafc' }}>Skill Gap Analysis</span>
            <span style={{ fontSize: '0.78rem', color: '#94a3b8' }}>Identify critical missing skills</span>
          </button>

          <button onClick={() => onNavigate('resume-tailor')} style={{ background: '#1e293b', border: '1px solid #334155', borderRadius: '10px', padding: '1.1rem', textAlign: 'left', cursor: 'pointer', display: 'flex', flexDirection: 'column', gap: '0.4rem' }}>
            <span style={{ fontSize: '1.4rem' }}>🎯</span>
            <span style={{ fontWeight: 700, color: '#f8fafc' }}>Resume Tailor</span>
            <span style={{ fontSize: '0.78rem', color: '#94a3b8' }}>Optimize for target job post</span>
          </button>

          <button onClick={() => onNavigate('job-matching')} style={{ background: '#1e293b', border: '1px solid #334155', borderRadius: '10px', padding: '1.1rem', textAlign: 'left', cursor: 'pointer', display: 'flex', flexDirection: 'column', gap: '0.4rem' }}>
            <span style={{ fontSize: '1.4rem' }}>💼</span>
            <span style={{ fontWeight: 700, color: '#f8fafc' }}>Find Jobs</span>
            <span style={{ fontSize: '0.78rem', color: '#94a3b8' }}>Discover skill-matched roles</span>
          </button>

          <button onClick={() => onNavigate('applications')} style={{ background: '#1e293b', border: '1px solid #334155', borderRadius: '10px', padding: '1.1rem', textAlign: 'left', cursor: 'pointer', display: 'flex', flexDirection: 'column', gap: '0.4rem' }}>
            <span style={{ fontSize: '1.4rem' }}>📋</span>
            <span style={{ fontWeight: 700, color: '#f8fafc' }}>Track Applications</span>
            <span style={{ fontSize: '0.78rem', color: '#94a3b8' }}>Review application status</span>
          </button>
        </div>
      </section>

      {/* Middle Row: Resume Status + Application Stats */}
      <section style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '1.5rem', marginBottom: '2.5rem' }}>
        {/* Resume Summary Card */}
        <div style={{ background: '#1e293b', border: '1px solid #334155', borderRadius: '12px', padding: '1.5rem', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
          <div>
            <h3 style={{ fontSize: '1.15rem', fontWeight: 700, color: '#f8fafc', marginBottom: '0.8rem' }}>📄 Active Resume Profile</h3>
            {latestResume ? (
              <div>
                <p style={{ fontWeight: 600, color: '#818cf8', fontSize: '1rem' }}>{latestResume.fileName}</p>
                <p style={{ color: '#94a3b8', fontSize: '0.85rem', marginTop: '0.2rem' }}>Uploaded on: {new Date(latestResume.uploadedAt).toLocaleDateString()}</p>
                <div style={{ background: 'rgba(16, 185, 129, 0.15)', color: '#10b981', border: '1px solid rgba(16, 185, 129, 0.3)', padding: '0.4rem 0.8rem', borderRadius: '6px', fontSize: '0.85rem', fontWeight: 600, display: 'inline-block', marginTop: '0.8rem' }}>
                  ✓ Active & Parsed
                </div>
              </div>
            ) : (
              <p style={{ color: '#94a3b8' }}>No resume uploaded yet. Upload a resume to unlock skill gap analysis.</p>
            )}
          </div>
          <button className="btn-secondary" onClick={() => onNavigate('resume-upload')} style={{ marginTop: '1.2rem', padding: '0.5rem', background: '#334155', border: 'none', borderRadius: '6px', color: '#fff', cursor: 'pointer' }}>
            Manage Resumes
          </button>
        </div>

        {/* Application Statistics */}
        <div style={{ background: '#1e293b', border: '1px solid #334155', borderRadius: '12px', padding: '1.5rem' }}>
          <h3 style={{ fontSize: '1.15rem', fontWeight: 700, color: '#f8fafc', marginBottom: '1rem' }}>📊 Application Tracker Summary</h3>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '0.8rem' }}>
            <div style={{ background: '#0f172a', padding: '0.8rem', borderRadius: '8px', textAlign: 'center' }}>
              <span style={{ fontSize: '0.75rem', color: '#94a3b8' }}>Applied</span>
              <div style={{ fontSize: '1.4rem', fontWeight: 700, color: '#60a5fa' }}>{appStats ? appStats.applied : 0}</div>
            </div>
            <div style={{ background: '#0f172a', padding: '0.8rem', borderRadius: '8px', textAlign: 'center' }}>
              <span style={{ fontSize: '0.75rem', color: '#94a3b8' }}>Interviews</span>
              <div style={{ fontSize: '1.4rem', fontWeight: 700, color: '#fbbf24' }}>{appStats ? appStats.interviews : 0}</div>
            </div>
            <div style={{ background: '#0f172a', padding: '0.8rem', borderRadius: '8px', textAlign: 'center' }}>
              <span style={{ fontSize: '0.75rem', color: '#94a3b8' }}>Selected</span>
              <div style={{ fontSize: '1.4rem', fontWeight: 700, color: '#34d399' }}>{appStats ? appStats.selected : 0}</div>
            </div>
          </div>
          <button className="btn-secondary" onClick={() => onNavigate('applications')} style={{ width: '100%', marginTop: '1.2rem', padding: '0.5rem', background: '#334155', border: 'none', borderRadius: '6px', color: '#fff', cursor: 'pointer' }}>
            View Application Tracker
          </button>
        </div>
      </section>

      {/* Recommended Jobs Widget */}
      <section>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
          <h2 style={{ fontSize: '1.3rem', fontWeight: 700, color: '#f8fafc' }}>🎯 Top Recommended Jobs</h2>
          <button className="btn-secondary" onClick={() => onNavigate('job-matching')} style={{ background: 'transparent', border: 'none', color: '#818cf8', cursor: 'pointer', fontWeight: 600 }}>
            View All Jobs →
          </button>
        </div>

        {recommendedJobs.length === 0 ? (
          <p style={{ color: '#94a3b8' }}>Upload your resume to see custom skill-matched software engineering roles.</p>
        ) : (
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '1rem' }}>
            {recommendedJobs.map((job) => (
              <div key={job.jobId || job.id} style={{ background: '#1e293b', border: '1px solid #334155', borderRadius: '10px', padding: '1.25rem', display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <h3 style={{ fontSize: '1.05rem', fontWeight: 700, color: '#f8fafc' }}>{job.title}</h3>
                  <span style={{ background: job.matchScore >= 75 ? 'rgba(16, 185, 129, 0.2)' : 'rgba(245, 158, 11, 0.2)', color: job.matchScore >= 75 ? '#10b981' : '#f59e0b', fontWeight: 700, padding: '0.2rem 0.5rem', borderRadius: '12px', fontSize: '0.75rem' }}>
                    {job.matchScore}% Match
                  </span>
                </div>
                <p style={{ color: '#94a3b8', fontSize: '0.85rem' }}>{job.companyName} • 📍 {job.location}</p>
                <button className="btn-primary" onClick={() => onNavigate('job-matching')} style={{ marginTop: 'auto', padding: '0.4rem', background: 'linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%)', border: 'none', borderRadius: '6px', color: '#fff', fontSize: '0.85rem', fontWeight: 600, cursor: 'pointer' }}>
                  View & Apply
                </button>
              </div>
            ))}
          </div>
        )}
      </section>
    </div>
  );
}
