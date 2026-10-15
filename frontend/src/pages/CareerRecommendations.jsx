import React, { useState, useEffect } from 'react';
import { aiService } from '../services/aiService';
import { resumeService } from '../services/resumeService';

export default function CareerRecommendations({ user, resumeId, onNavigate }) {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [data, setData] = useState(null);
  const [resumes, setResumes] = useState([]);
  const [selectedResumeId, setSelectedResumeId] = useState(resumeId || null);

  useEffect(() => {
    if (user?.id) {
      fetchUserResumes();
    }
  }, [user]);

  useEffect(() => {
    if (selectedResumeId && user?.id) {
      loadAnalysisData(selectedResumeId);
    }
  }, [selectedResumeId, user]);

  const fetchUserResumes = async () => {
    if (!user?.id) return;
    try {
      setLoading(true);
      setError('');

      const userResumes = await resumeService.getUserResumes(user.id);

      setResumes(userResumes);

      if (!selectedResumeId && userResumes.length > 0) {
        setSelectedResumeId(userResumes[0].id);
      }
    } catch (err) {
      console.error('Failed to fetch user resumes:', err);
      setError('Failed to load your resumes.');
    } finally {
      setLoading(false);
    }
  };

  const loadAnalysisData = async (resId) => {
    if (!user?.id) return;
    setLoading(true);
    setError('');
    try {
      const result = await aiService.getCompleteAIAnalysis(resId, user.id);
      setData(result);
    } catch (err) {
      console.error('Failed to load AI career analysis:', err);
      setError(err?.message || 'Failed to load AI career recommendations.');
    } finally {
      setLoading(false);
    }
  };

  const getScoreBadgeClass = (score) => {
    if (score >= 75) return 'badge-high';
    if (score >= 50) return 'badge-medium';
    return 'badge-low';
  };

  return (
    <div className="recommendations-container animate-fade-in" style={{ padding: '2rem' }}>
      <div className="page-header" style={{ marginBottom: '1.5rem', display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <div>
          <button className="btn-secondary" onClick={() => onNavigate && onNavigate('dashboard')} style={{ marginBottom: '12px' }}>
            ← Back to Dashboard
          </button>
          <h1 style={{ fontSize: '1.8rem', fontWeight: 700 }}>🎯 AI Skill Extraction & Career Recommendations</h1>
          <p className="subtitle" style={{ color: '#94a3b8' }}>Extracted skill profiles matched against industry career vectors</p>
        </div>

        {resumes.length > 1 && (
          <div className="resume-selector-wrapper" style={{ marginTop: '12px' }}>
            <label style={{ fontSize: '13px', color: '#94a3b8', marginRight: '8px' }}>Target Resume:</label>
            <select
              value={selectedResumeId || ''}
              onChange={(e) => setSelectedResumeId(Number(e.target.value))}
              className="select-input"
              style={{ padding: '0.4rem 0.8rem', background: '#1e293b', color: '#f8fafc', border: '1px solid #334155', borderRadius: '6px' }}
            >
              {resumes.map((r) => (
                <option key={r.id} value={r.id}>
                  {r.fileName}
                </option>
              ))}
            </select>
          </div>
        )}
      </div>

      {loading && (
        <div className="loading-state" style={{ textAlign: 'center', padding: '4rem 0' }}>
          <div className="spinner" style={{ width: '32px', height: '32px', margin: '0 auto 1rem auto' }}></div>
          <p style={{ color: '#94a3b8' }}>Extracting skill vectors & matching career profiles...</p>
        </div>
      )}

      {error && (
        <div className="error-alert" style={{ background: 'rgba(244, 63, 94, 0.1)', border: '1px solid rgba(244, 63, 94, 0.3)', padding: '1rem', borderRadius: '8px', color: '#f43f5e', marginBottom: '1.5rem' }}>
          <span>⚠️ {error}</span>
          <button className="btn-small" onClick={() => fetchUserResumes()} style={{ marginLeft: '12px', padding: '0.2rem 0.6rem' }}>
            Retry
          </button>
        </div>
      )}

      {!loading && !error && resumes.length === 0 && (
        <div className="empty-card" style={{ textAlign: 'center', padding: '3rem', background: '#1e293b', borderRadius: '12px', border: '1px solid #334155' }}>
          <h3>No Resumes Uploaded Yet</h3>
          <p style={{ color: '#94a3b8', margin: '0.5rem 0 1.5rem 0' }}>Please upload a resume first to extract skills and generate personalized career recommendations.</p>
          <button className="btn-primary" onClick={() => onNavigate && onNavigate('resume-upload')}>
            📤 Upload Resume Now
          </button>
        </div>
      )}

      {!loading && !error && data && (
        <div className="recommendations-grid" style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          {/* Header Profile Summary */}
          <div className="card profile-header-card" style={{ background: '#1e293b', padding: '1.5rem', borderRadius: '12px', border: '1px solid #334155' }}>
            <div className="profile-info" style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
              <div className="avatar-circle" style={{ width: '48px', height: '48px', borderRadius: '50%', background: 'linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 700, fontSize: '1.2rem', color: '#fff' }}>
                {data.name ? data.name.charAt(0) : 'U'}
              </div>
              <div>
                <h2 style={{ fontSize: '1.4rem', fontWeight: 700 }}>{data.name || 'Candidate Profile'}</h2>
                <p className="contact-line" style={{ color: '#94a3b8', fontSize: '0.9rem', margin: '0.2rem 0' }}>📧 {data.email} | 📞 {data.phone}</p>
                {data.summary && <p className="summary-preview" style={{ color: '#cbd5e1', fontSize: '0.9rem', fontStyle: 'italic' }}>"{data.summary}"</p>}
              </div>
            </div>
          </div>

          {/* Categorized Skills Breakdown */}
          <div className="card skills-breakdown-card" style={{ background: '#1e293b', padding: '1.5rem', borderRadius: '12px', border: '1px solid #334155' }}>
            <div className="card-header" style={{ marginBottom: '1rem' }}>
              <h3 style={{ fontSize: '1.2rem', fontWeight: 600 }}>🏷️ Categorized Extracted Skills ({data.skills ? data.skills.length : 0})</h3>
            </div>
            {(!data.skills || data.skills.length === 0) ? (
              <p className="text-muted" style={{ color: '#94a3b8' }}>No technical skills detected in this resume.</p>
            ) : (
              <div className="skills-tags-grid" style={{ display: 'flex', flexWrap: 'wrap', gap: '0.6rem' }}>
                {data.skills.map((s, idx) => (
                  <div key={idx} className="skill-chip" style={{ background: 'rgba(99, 102, 241, 0.15)', border: '1px solid rgba(99, 102, 241, 0.3)', padding: '0.4rem 0.8rem', borderRadius: '20px', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    <span className="skill-name" style={{ color: '#a5b4fc', fontWeight: 600, fontSize: '0.85rem' }}>{s.name}</span>
                    <span className="skill-category-tag" style={{ background: 'rgba(255,255,255,0.1)', padding: '0.1rem 0.4rem', borderRadius: '4px', fontSize: '0.7rem', color: '#cbd5e1' }}>{s.category}</span>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Top Career Recommendations */}
          <div className="card careers-card" style={{ background: '#1e293b', padding: '1.5rem', borderRadius: '12px', border: '1px solid #334155' }}>
            <div className="card-header" style={{ marginBottom: '1rem' }}>
              <h3 style={{ fontSize: '1.2rem', fontWeight: 600 }}>🚀 Top Career Matches & Industry Alignment</h3>
            </div>

            {(!data.careerRecommendations || data.careerRecommendations.length === 0) ? (
              <p className="text-muted" style={{ color: '#94a3b8' }}>No specific career matches calculated.</p>
            ) : (
              <div className="career-list" style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                {data.careerRecommendations.map((career, idx) => (
                  <div key={idx} className="career-item-card" style={{ background: '#0f172a', padding: '1.2rem', borderRadius: '8px', border: '1px solid #1e293b' }}>
                    <div className="career-item-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.8rem' }}>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem' }}>
                        <span className="rank-badge" style={{ background: 'rgba(99, 102, 241, 0.2)', color: '#818cf8', fontWeight: 700, padding: '0.2rem 0.5rem', borderRadius: '4px', fontSize: '0.8rem' }}>#{idx + 1}</span>
                        <h3 className="role-title" style={{ fontSize: '1.1rem', fontWeight: 600, color: '#f8fafc' }}>{career.career}</h3>
                      </div>
                      <div className={`score-badge ${getScoreBadgeClass(career.matchScore)}`} style={{ fontWeight: 700, color: career.matchScore >= 75 ? '#10b981' : '#f59e0b' }}>
                        {career.matchScore}% Match
                      </div>
                    </div>

                    <div className="progress-bar-bg" style={{ background: '#1e293b', height: '6px', borderRadius: '3px', marginBottom: '0.8rem', overflow: 'hidden' }}>
                      <div
                        className="progress-bar-fill"
                        style={{ width: `${career.matchScore}%`, height: '100%', backgroundColor: career.matchScore >= 75 ? '#10b981' : '#f59e0b', transition: 'width 0.4s ease' }}
                      ></div>
                    </div>

                    <p className="career-reason" style={{ color: '#cbd5e1', fontSize: '0.9rem', lineHeight: 1.4 }}>{career.reason}</p>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
