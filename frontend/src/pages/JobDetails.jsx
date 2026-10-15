import React, { useState, useEffect } from 'react';
import { jobService } from '../services/jobService';
import { applicationService } from '../services/applicationService';
import { getUserResumes } from '../services/resumeService';

export default function JobDetails({ jobId, user, onNavigateBack }) {
  const [job, setJob] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [successMsg, setSuccessMsg] = useState('');
  const [userResumes, setUserResumes] = useState([]);
  const [selectedResumeId, setSelectedResumeId] = useState(null);
  const [applying, setApplying] = useState(false);

  useEffect(() => {
    if (jobId && user?.id) {
      loadJobDetails();
    }
  }, [jobId, user]);

  const loadJobDetails = async () => {
    setLoading(true);
    setError('');
    try {
      const resumes = await getUserResumes(user.id);
      setUserResumes(resumes);
      const activeResId = resumes.length > 0 ? resumes[0].id : null;
      setSelectedResumeId(activeResId);

      // Fetch matched search results to include user-specific skill match
      const matchedList = await jobService.searchJobs({
        userId: user.id,
        resumeId: activeResId,
      });

      const matchedJob = matchedList.find((j) => (j.jobId || j.id) === Number(jobId));
      if (matchedJob) {
        setJob(matchedJob);
      } else {
        const rawJob = await jobService.getJobById(jobId);
        setJob(rawJob);
      }
    } catch (err) {
      console.error('Failed to load job details:', err);
      setError(err?.message || 'Failed to load job details.');
    } finally {
      setLoading(false);
    }
  };

  const handleApply = async () => {
    setApplying(true);
    setError('');
    setSuccessMsg('');
    try {
      await applicationService.applyForJob({
        userId: user.id,
        jobId: Number(jobId),
        resumeId: selectedResumeId,
        notes: 'Applied from Job Details view',
      });
      setSuccessMsg('🎉 Application submitted successfully!');
    } catch (err) {
      setError(err?.message || 'Failed to submit application.');
    } finally {
      setApplying(false);
    }
  };

  if (loading) {
    return (
      <div style={{ padding: '3rem', textAlign: 'center', color: '#94a3b8' }}>
        <div className="spinner" style={{ width: '32px', height: '32px', margin: '0 auto 1rem auto' }}></div>
        <p>Loading job details...</p>
      </div>
    );
  }

  if (error && !job) {
    return (
      <div style={{ padding: '2rem' }}>
        <button className="btn-secondary" onClick={onNavigateBack} style={{ marginBottom: '1rem' }}>
          ← Back to Jobs
        </button>
        <div className="alert alert-danger" style={{ color: '#f43f5e', background: 'rgba(244, 63, 94, 0.1)', padding: '1rem', borderRadius: '8px' }}>
          ⚠️ {error}
        </div>
      </div>
    );
  }

  return (
    <div className="job-details-container animate-fade-in" style={{ padding: '2rem', maxWidth: '900px', margin: '0 auto' }}>
      <button className="btn-secondary" onClick={onNavigateBack} style={{ marginBottom: '1.5rem', padding: '0.4rem 0.8rem', background: '#334155', border: 'none', borderRadius: '6px', color: '#fff', cursor: 'pointer' }}>
        ← Back to Job Search
      </button>

      {successMsg && (
        <div style={{ background: 'rgba(16, 185, 129, 0.15)', border: '1px solid rgba(16, 185, 129, 0.3)', padding: '1rem', borderRadius: '8px', color: '#10b981', marginBottom: '1.5rem', fontWeight: 600 }}>
          {successMsg}
        </div>
      )}

      {error && (
        <div style={{ background: 'rgba(244, 63, 94, 0.15)', border: '1px solid rgba(244, 63, 94, 0.3)', padding: '1rem', borderRadius: '8px', color: '#f43f5e', marginBottom: '1.5rem', fontWeight: 600 }}>
          ⚠️ {error}
        </div>
      )}

      {job && (
        <div className="card" style={{ background: '#1e293b', border: '1px solid #334155', borderRadius: '12px', padding: '2rem', display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          {/* Title Header */}
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
            <div>
              <h1 style={{ fontSize: '1.8rem', fontWeight: 700, color: '#f8fafc', marginBottom: '0.4rem' }}>{job.title}</h1>
              <p style={{ fontSize: '1.1rem', color: '#818cf8', fontWeight: 600 }}>{job.companyName}</p>
            </div>

            {job.matchScore !== undefined && (
              <div style={{ padding: '0.4rem 1rem', borderRadius: '20px', fontWeight: 700, fontSize: '1.1rem', background: job.matchScore >= 75 ? 'rgba(16, 185, 129, 0.2)' : 'rgba(245, 158, 11, 0.2)', color: job.matchScore >= 75 ? '#10b981' : '#f59e0b' }}>
                {job.matchScore}% Match
              </div>
            )}
          </div>

          {/* Quick Meta Stats */}
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '1rem', background: '#0f172a', padding: '1rem', borderRadius: '8px' }}>
            <div>
              <span style={{ fontSize: '0.8rem', color: '#94a3b8' }}>Location</span>
              <div style={{ fontWeight: 600, color: '#f8fafc' }}>📍 {job.location}</div>
            </div>
            <div>
              <span style={{ fontSize: '0.8rem', color: '#94a3b8' }}>Employment Type</span>
              <div style={{ fontWeight: 600, color: '#f8fafc' }}>💼 {job.employmentType}</div>
            </div>
            <div>
              <span style={{ fontSize: '0.8rem', color: '#94a3b8' }}>Salary</span>
              <div style={{ fontWeight: 600, color: '#10b981' }}>💰 {job.salary}</div>
            </div>
            <div>
              <span style={{ fontSize: '0.8rem', color: '#94a3b8' }}>Experience</span>
              <div style={{ fontWeight: 600, color: '#f8fafc' }}>⌛ {job.experienceRequired}</div>
            </div>
          </div>

          {/* Description */}
          <div>
            <h3 style={{ fontSize: '1.1rem', fontWeight: 600, marginBottom: '0.5rem', color: '#f8fafc' }}>Description</h3>
            <p style={{ color: '#cbd5e1', lineHeight: 1.6 }}>{job.description}</p>
          </div>

          {/* Skills Breakdown */}
          <div>
            <h3 style={{ fontSize: '1.1rem', fontWeight: 600, marginBottom: '0.75rem', color: '#f8fafc' }}>Required Technical Skills</h3>

            {job.matchingSkills && job.matchingSkills.length > 0 && (
              <div style={{ marginBottom: '0.8rem' }}>
                <span style={{ fontSize: '0.85rem', color: '#10b981', fontWeight: 600, display: 'block', marginBottom: '0.3rem' }}>✓ Your Matching Skills:</span>
                <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.4rem' }}>
                  {job.matchingSkills.map((sk, idx) => (
                    <span key={idx} style={{ background: 'rgba(16, 185, 129, 0.15)', border: '1px solid rgba(16, 185, 129, 0.3)', color: '#a7f3d0', padding: '0.3rem 0.7rem', borderRadius: '6px', fontSize: '0.85rem' }}>
                      {sk}
                    </span>
                  ))}
                </div>
              </div>
            )}

            {job.missingSkills && job.missingSkills.length > 0 && (
              <div>
                <span style={{ fontSize: '0.85rem', color: '#f59e0b', fontWeight: 600, display: 'block', marginBottom: '0.3rem' }}>⚡ Skill Gaps to Acquire:</span>
                <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.4rem' }}>
                  {job.missingSkills.map((sk, idx) => (
                    <span key={idx} style={{ background: 'rgba(245, 158, 11, 0.15)', border: '1px solid rgba(245, 158, 11, 0.3)', color: '#fde68a', padding: '0.3rem 0.7rem', borderRadius: '6px', fontSize: '0.85rem' }}>
                      {sk}
                    </span>
                  ))}
                </div>
              </div>
            )}
          </div>

          {/* Apply Actions */}
          <div style={{ display: 'flex', gap: '1rem', marginTop: '1rem', borderTop: '1px solid #334155', paddingTop: '1.25rem' }}>
            <button
              className="btn-primary"
              disabled={applying}
              onClick={handleApply}
              style={{ padding: '0.75rem 2rem', background: 'linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%)', border: 'none', borderRadius: '6px', color: '#fff', fontWeight: 700, cursor: 'pointer', fontSize: '1rem' }}
            >
              {applying ? 'Submitting Application...' : '🚀 Submit Job Application'}
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
