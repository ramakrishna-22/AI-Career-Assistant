import React, { useState, useEffect } from 'react';
import { analyzeResume } from '../services/resumeService';

export default function ResumeAnalysis({
  resumeId,
  user,
  onNavigateToUpload,
  onNavigateToDashboard,
  onNavigateToRecommendations
}) {
  const [analysis, setAnalysis] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const loadAnalysis = async () => {
      if (!resumeId || !user?.id) {
        setError('Resume ID or user information is missing.');
        setLoading(false);
        return;
      }

      try {
        setLoading(true);
        setError('');

        const result = await analyzeResume(resumeId, user.id);
        setAnalysis(result);
      } catch (err) {
        console.error('Resume analysis failed:', err);
        setError(
          err?.response?.data?.message ||
          err?.message ||
          'Failed to analyze resume.'
        );
      } finally {
        setLoading(false);
      }
    };

    loadAnalysis();
  }, [resumeId, user]);

  if (loading) {
    return (
      <div className="resume-analysis" style={{ padding: '2rem', textAlign: 'center' }}>
        <h2>AI Resume Analysis</h2>
        <p>Analyzing your resume...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="resume-analysis" style={{ padding: '2rem' }}>
        <h2>AI Resume Analysis</h2>
        <p className="error-alert" style={{ color: '#f43f5e', marginBottom: '1rem' }}>{error}</p>
        <div style={{ display: 'flex', gap: '1rem' }}>
          <button className="btn-primary" onClick={onNavigateToUpload}>
            Upload Another Resume
          </button>
          <button className="btn-secondary" onClick={onNavigateToDashboard}>
            Back to Dashboard
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="resume-analysis" style={{ padding: '2rem' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
        <h2>AI Resume Analysis</h2>
        <button className="btn-secondary" onClick={onNavigateToDashboard}>
          ← Back to Dashboard
        </button>
      </div>

      {analysis && (
        <div className="card" style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          <h3>Analysis Results</h3>

          <div>
            <strong>Summary:</strong>
            <p style={{ marginTop: '0.5rem', color: '#cbd5e1' }}>
              {analysis.summary || 'No summary available.'}
            </p>
          </div>

          <div>
            <strong>Detected Skills:</strong>
            {analysis.skills?.length > 0 ? (
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem', marginTop: '0.5rem' }}>
                {analysis.skills.map((skill, index) => (
                  <span key={index} className="skill-chip">
                    {skill}
                  </span>
                ))}
              </div>
            ) : (
              <p style={{ color: '#94a3b8' }}>No skills extracted.</p>
            )}
          </div>

          <div>
            <strong>Experience:</strong>
            <p style={{ marginTop: '0.5rem', color: '#cbd5e1' }}>
              {analysis.experience || 'No experience information available.'}
            </p>
          </div>

          <div>
            <strong>Education:</strong>
            <p style={{ marginTop: '0.5rem', color: '#cbd5e1' }}>
              {analysis.education || 'No education information available.'}
            </p>
          </div>

          <div className="analysis-actions" style={{ display: 'flex', gap: '1rem', marginTop: '1rem', alignItems: 'flex-start', flexWrap: 'wrap' }}>
            <button className="btn-secondary" onClick={onNavigateToDashboard}>
              Back to Dashboard
            </button>

            <div
              className="module-card"
              style={{
                borderLeft: '4px solid var(--accent-indigo)',
                flex: '1 1 100%',
              }}
            >
              <h3 style={{ fontSize: '1.2rem', marginBottom: '1rem' }}>
                🚀 Improvement Suggestions
              </h3>

              <ul
                style={{
                  paddingLeft: '1.25rem',
                  color: 'var(--text-secondary)',
                  display: 'flex',
                  flexDirection: 'column',
                  gap: '0.6rem',
                }}
              >
                {analysis.suggestions && analysis.suggestions.length > 0 ? (
                  analysis.suggestions.map((suggestion, idx) => (
                    <li key={idx} style={{ lineHeight: 1.5 }}>
                      {suggestion}
                    </li>
                  ))
                ) : (
                  <li>Your resume structure looks clean and well-balanced!</li>
                )}
              </ul>
            </div>

            {onNavigateToRecommendations && (
              <button
                className="btn-primary"
                onClick={() => onNavigateToRecommendations(resumeId)}
                style={{
                  background: 'linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%)',
                }}
              >
                🎯 View Career Recommendations
              </button>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
