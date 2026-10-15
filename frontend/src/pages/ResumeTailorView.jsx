import React, { useState, useEffect } from 'react';
import { tailoringService } from '../services/tailoringService';
import { jobService } from '../services/jobService';

export default function ResumeTailorView({ user, onNavigate }) {
  const [tailorResult, setTailorResult] = useState(null);
  const [jobs, setJobs] = useState([]);
  const [selectedJobId, setSelectedJobId] = useState('');
  const [customJobDesc, setCustomJobDesc] = useState('');
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
      if (jobList.length > 0) {
        setSelectedJobId(jobList[0].id);
        await runTailoring(jobList[0].id, '');
      } else {
        await runTailoring(null, 'Java Spring Boot React SaaS developer role');
      }
    } catch (err) {
      console.error('Tailoring initial load error:', err);
    } finally {
      setLoading(false);
    }
  };

  const runTailoring = async (jobId, textDesc) => {
    setLoading(true);
    setError('');
    try {
      const res = await tailoringService.tailorResume({
        userId: user.id,
        jobId: jobId ? Number(jobId) : null,
        targetJobDescription: textDesc,
      });
      setTailorResult(res);
    } catch (err) {
      setError(err?.message || 'Failed to generate tailored resume suggestions.');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    runTailoring(selectedJobId, customJobDesc);
  };

  return (
    <div className="resume-tailor-container animate-fade-in" style={{ padding: '2rem', maxWidth: '1000px', margin: '0 auto' }}>
      <div className="page-header" style={{ marginBottom: '1.5rem' }}>
        <h1 style={{ fontSize: '1.8rem', fontWeight: 700, marginBottom: '0.4rem' }}>🎯 ATS Resume Tailoring Engine</h1>
        <p style={{ color: '#94a3b8' }}>Optimize resume summary, bullet points, and keyword density for target job postings</p>
      </div>

      {/* Target Job Selection */}
      <form onSubmit={handleSubmit} style={{ background: '#1e293b', padding: '1.25rem', borderRadius: '12px', border: '1px solid #334155', marginBottom: '2rem', display: 'flex', flexDirection: 'column', gap: '1rem' }}>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '1rem' }}>
          <div>
            <label style={{ fontSize: '12px', color: '#94a3b8', display: 'block', marginBottom: '4px' }}>Select Open Target Job:</label>
            <select
              value={selectedJobId}
              onChange={(e) => setSelectedJobId(e.target.value)}
              style={{ width: '100%', padding: '0.5rem 0.8rem', background: '#0f172a', border: '1px solid #334155', borderRadius: '6px', color: '#fff' }}
            >
              <option value="">-- Custom Job Description --</option>
              {jobs.map((j) => (
                <option key={j.id} value={j.id}>
                  {j.title} ({j.companyName})
                </option>
              ))}
            </select>
          </div>

          <div>
            <label style={{ fontSize: '12px', color: '#94a3b8', display: 'block', marginBottom: '4px' }}>Or Paste Job Description:</label>
            <input
              type="text"
              value={customJobDesc}
              onChange={(e) => setCustomJobDesc(e.target.value)}
              placeholder="Paste job posting text..."
              style={{ width: '100%', padding: '0.5rem 0.8rem', background: '#0f172a', border: '1px solid #334155', borderRadius: '6px', color: '#fff' }}
            />
          </div>
        </div>

        <button type="submit" className="btn-primary" style={{ alignSelf: 'flex-start', padding: '0.55rem 1.25rem', background: 'linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%)', border: 'none', borderRadius: '6px', color: '#fff', fontWeight: 600, cursor: 'pointer' }}>
          ✨ Optimize Resume for Job
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
          <p>Generating ATS optimizations and tailored content...</p>
        </div>
      ) : tailorResult && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          {/* ATS Score Header */}
          <div style={{ background: '#1e293b', border: '1px solid #334155', borderRadius: '12px', padding: '1.5rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem' }}>
            <div>
              <span style={{ fontSize: '0.85rem', color: '#94a3b8' }}>ATS Optimization Status</span>
              <h2 style={{ fontSize: '1.4rem', fontWeight: 700, color: '#f8fafc', marginTop: '0.2rem' }}>Job-Tailored Resume Optimization</h2>
            </div>
            <div style={{ background: 'rgba(16, 185, 129, 0.15)', border: '1px solid rgba(16, 185, 129, 0.3)', padding: '0.75rem 1.5rem', borderRadius: '12px', textAlign: 'center' }}>
              <span style={{ fontSize: '0.75rem', color: '#10b981', display: 'block', fontWeight: 600 }}>PROJECTED ATS SCORE</span>
              <span style={{ fontSize: '2rem', fontWeight: 800, color: '#10b981' }}>{tailorResult.atsMatchScore}%</span>
            </div>
          </div>

          {/* Tailored Professional Summary */}
          <div style={{ background: '#1e293b', border: '1px solid #334155', borderRadius: '12px', padding: '1.5rem' }}>
            <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: '#f8fafc', marginBottom: '0.5rem' }}>📝 Tailored Professional Summary</h3>
            <p style={{ color: '#cbd5e1', lineHeight: 1.6, background: '#0f172a', padding: '1rem', borderRadius: '8px', borderLeft: '4px solid #6366f1' }}>
              {tailorResult.tailoredSummary}
            </p>
          </div>

          {/* Recommended High-Impact Keywords */}
          <div style={{ background: '#1e293b', border: '1px solid #334155', borderRadius: '12px', padding: '1.5rem' }}>
            <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: '#f8fafc', marginBottom: '0.75rem' }}>🔑 Key ATS Keywords to Include</h3>
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem' }}>
              {tailorResult.suggestedKeywords && tailorResult.suggestedKeywords.map((kw, idx) => (
                <span key={idx} style={{ background: 'rgba(99, 102, 241, 0.15)', border: '1px solid rgba(99, 102, 241, 0.3)', color: '#818cf8', padding: '0.35rem 0.75rem', borderRadius: '6px', fontSize: '0.85rem', fontWeight: 600 }}>
                  + {kw}
                </span>
              ))}
            </div>
          </div>

          {/* Actionable Bullet Point Improvements */}
          <div style={{ background: '#1e293b', border: '1px solid #334155', borderRadius: '12px', padding: '1.5rem' }}>
            <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: '#f8fafc', marginBottom: '0.85rem' }}>🚀 Action-Oriented Bullet Point Enhancements</h3>
            <ul style={{ display: 'flex', flexDirection: 'column', gap: '0.65rem', paddingLeft: '1.25rem', color: '#cbd5e1' }}>
              {tailorResult.bulletPointSuggestions && tailorResult.bulletPointSuggestions.map((bp, idx) => (
                <li key={idx} style={{ lineHeight: 1.5 }}>{bp}</li>
              ))}
            </ul>
          </div>
        </div>
      )}
    </div>
  );
}
