import React, { useState, useEffect } from 'react';
import { skillGapService } from '../services/skillGapService';
import { jobService } from '../services/jobService';

export default function SkillGapView({ user, onNavigate }) {
  const [analysis, setAnalysis] = useState(null);
  const [jobs, setJobs] = useState([]);
  const [targetRole, setTargetRole] = useState('Senior Java Developer');
  const [selectedJobId, setSelectedJobId] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (user?.id) {
      loadInitialData();
    }
  }, [user]);

  const loadInitialData = async () => {
    setLoading(true);
    try {
      const jobList = await jobService.getAllJobs().catch(() => []);
      setJobs(jobList);
      await runAnalysis(targetRole, selectedJobId);
    } catch (err) {
      console.error('Skill gap load error:', err);
    } finally {
      setLoading(false);
    }
  };

  const runAnalysis = async (role, jobId) => {
    setLoading(true);
    setError('');
    try {
      const res = await skillGapService.analyzeSkillGap({
        userId: user.id,
        targetRole: role,
        jobId: jobId ? Number(jobId) : null,
      });
      setAnalysis(res);
    } catch (err) {
      setError(err?.message || 'Failed to generate skill gap analysis.');
    } finally {
      setLoading(false);
    }
  };

  const handleFormSubmit = (e) => {
    e.preventDefault();
    runAnalysis(targetRole, selectedJobId);
  };

  return (
    <div className="skill-gap-container animate-fade-in" style={{ padding: '2rem', maxWidth: '1000px', margin: '0 auto' }}>
      <div className="page-header" style={{ marginBottom: '1.5rem' }}>
        <h1 style={{ fontSize: '1.8rem', fontWeight: 700, marginBottom: '0.4rem' }}>⚡ Skill Gap Analysis & Learning Roadmap</h1>
        <p style={{ color: '#94a3b8' }}>Evaluate technical readiness against software roles and unlock tailored learning paths</p>
      </div>

      {/* Target Selector Bar */}
      <form onSubmit={handleFormSubmit} style={{ background: '#1e293b', padding: '1.25rem', borderRadius: '12px', border: '1px solid #334155', marginBottom: '2rem', display: 'flex', gap: '1rem', flexWrap: 'wrap', alignItems: 'end' }}>
        <div style={{ flex: '1 1 200px' }}>
          <label style={{ fontSize: '12px', color: '#94a3b8', display: 'block', marginBottom: '4px' }}>Target Role Name:</label>
          <input
            type="text"
            value={targetRole}
            onChange={(e) => setTargetRole(e.target.value)}
            placeholder="e.g. Senior Java Developer"
            style={{ width: '100%', padding: '0.5rem 0.8rem', background: '#0f172a', border: '1px solid #334155', borderRadius: '6px', color: '#fff' }}
          />
        </div>

        <div style={{ flex: '1 1 200px' }}>
          <label style={{ fontSize: '12px', color: '#94a3b8', display: 'block', marginBottom: '4px' }}>Or Match Specific Open Job:</label>
          <select
            value={selectedJobId}
            onChange={(e) => setSelectedJobId(e.target.value)}
            style={{ width: '100%', padding: '0.5rem 0.8rem', background: '#0f172a', border: '1px solid #334155', borderRadius: '6px', color: '#fff' }}
          >
            <option value="">-- Custom Target Role --</option>
            {jobs.map((j) => (
              <option key={j.id} value={j.id}>
                {j.title} ({j.companyName})
              </option>
            ))}
          </select>
        </div>

        <button type="submit" className="btn-primary" style={{ padding: '0.55rem 1.25rem', background: 'linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%)', border: 'none', borderRadius: '6px', color: '#fff', fontWeight: 600, cursor: 'pointer' }}>
          🚀 Generate Gap Analysis
        </button>
      </form>

      {error && (
        <div style={{ background: 'rgba(244, 63, 94, 0.15)', border: '1px solid rgba(244, 63, 94, 0.3)', padding: '1rem', borderRadius: '8px', color: '#f43f5e', marginBottom: '1.5rem' }}>
          ⚠️ {error}
        </div>
      )}

      {loading ? (
        <div style={{ textAlign: 'center', padding: '4rem 0', color: '#94a3b8' }}>
          <div className="spinner" style={{ width: '32px', height: '32px', margin: '0 auto 1rem auto' }}></div>
          <p>Analyzing skill vectors and generating roadmap...</p>
        </div>
      ) : analysis && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          {/* Readiness Score Banner */}
          <div style={{ background: '#1e293b', border: '1px solid #334155', borderRadius: '12px', padding: '1.5rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem' }}>
            <div>
              <span style={{ fontSize: '0.85rem', color: '#94a3b8' }}>Target Profile Readiness</span>
              <h2 style={{ fontSize: '1.5rem', fontWeight: 700, color: '#f8fafc', marginTop: '0.2rem' }}>{analysis.targetRole}</h2>
            </div>
            <div style={{ background: 'rgba(99, 102, 241, 0.15)', border: '1px solid rgba(99, 102, 241, 0.3)', padding: '0.75rem 1.5rem', borderRadius: '12px', textAlign: 'center' }}>
              <span style={{ fontSize: '0.75rem', color: '#818cf8', display: 'block', fontWeight: 600 }}>READINESS SCORE</span>
              <span style={{ fontSize: '2rem', fontWeight: 800, color: analysis.readinessScore >= 70 ? '#10b981' : '#f59e0b' }}>
                {analysis.readinessScore}%
              </span>
            </div>
          </div>

          {/* Skill Matrix Grid */}
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '1.25rem' }}>
            {/* Matching Skills */}
            <div style={{ background: '#1e293b', border: '1px solid #334155', borderRadius: '12px', padding: '1.25rem' }}>
              <h3 style={{ fontSize: '1.05rem', color: '#10b981', fontWeight: 700, marginBottom: '0.8rem' }}>✓ Matching Skills ({analysis.matchingSkills ? analysis.matchingSkills.length : 0})</h3>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.4rem' }}>
                {analysis.matchingSkills && analysis.matchingSkills.length > 0 ? (
                  analysis.matchingSkills.map((sk, idx) => (
                    <span key={idx} style={{ background: 'rgba(16, 185, 129, 0.12)', border: '1px solid rgba(16, 185, 129, 0.25)', color: '#a7f3d0', padding: '0.25rem 0.6rem', borderRadius: '6px', fontSize: '0.8rem' }}>
                      {sk}
                    </span>
                  ))
                ) : (
                  <span style={{ color: '#94a3b8', fontSize: '0.85rem' }}>No matching skills found yet</span>
                )}
              </div>
            </div>

            {/* Critical Skill Gaps */}
            <div style={{ background: '#1e293b', border: '1px solid #334155', borderRadius: '12px', padding: '1.25rem' }}>
              <h3 style={{ fontSize: '1.05rem', color: '#f43f5e', fontWeight: 700, marginBottom: '0.8rem' }}>⚡ Critical Skill Gaps ({analysis.criticalGaps ? analysis.criticalGaps.length : 0})</h3>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.4rem' }}>
                {analysis.criticalGaps && analysis.criticalGaps.length > 0 ? (
                  analysis.criticalGaps.map((sk, idx) => (
                    <span key={idx} style={{ background: 'rgba(244, 63, 94, 0.12)', border: '1px solid rgba(244, 63, 94, 0.25)', color: '#fca5a5', padding: '0.25rem 0.6rem', borderRadius: '6px', fontSize: '0.8rem' }}>
                      {sk}
                    </span>
                  ))
                ) : (
                  <span style={{ color: '#10b981', fontSize: '0.85rem' }}>No critical gaps! Core skills met.</span>
                )}
              </div>
            </div>
          </div>

          {/* Actionable Learning Roadmap Timeline */}
          <div style={{ background: '#1e293b', border: '1px solid #334155', borderRadius: '12px', padding: '1.5rem' }}>
            <h3 style={{ fontSize: '1.15rem', fontWeight: 700, color: '#f8fafc', marginBottom: '1rem' }}>🗺️ Recommended Actionable Learning Roadmap</h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.85rem' }}>
              {analysis.learningRoadmap && analysis.learningRoadmap.length > 0 ? (
                analysis.learningRoadmap.map((step, idx) => (
                  <div key={idx} style={{ background: '#0f172a', padding: '1rem', borderRadius: '8px', borderLeft: '4px solid #6366f1', color: '#cbd5e1', fontSize: '0.9rem' }}>
                    {step}
                  </div>
                ))
              ) : (
                <p style={{ color: '#94a3b8' }}>Roadmap complete.</p>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
