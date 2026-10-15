import React, { useState, useEffect } from 'react';
import { jobService } from '../services/jobService';
import { applicationService } from '../services/applicationService';
import { getUserResumes } from '../services/resumeService';

export default function JobMatching({ user, onNavigate, onSelectJob }) {
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [successMsg, setSuccessMsg] = useState('');
  
  // Filters
  const [keyword, setKeyword] = useState('');
  const [location, setLocation] = useState('All');
  const [employmentType, setEmploymentType] = useState('All');
  const [minMatchScore, setMinMatchScore] = useState(0);
  const [userResumes, setUserResumes] = useState([]);
  const [selectedResumeId, setSelectedResumeId] = useState(null);
  const [applyingJobId, setApplyingJobId] = useState(null);

  useEffect(() => {
    if (user?.id) {
      loadResumesAndJobs();
    }
  }, [user]);

  const loadResumesAndJobs = async () => {
    setLoading(true);
    setError('');
    try {
      const resumes = await getUserResumes(user.id);
      setUserResumes(resumes);
      const activeResId = resumes.length > 0 ? resumes[0].id : null;
      setSelectedResumeId(activeResId);

      const jobResults = await jobService.searchJobs({
        userId: user.id,
        resumeId: activeResId,
      });
      setJobs(jobResults);
    } catch (err) {
      console.error('Failed to load jobs:', err);
      setError(err?.message || 'Failed to load software engineering jobs.');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async (e) => {
    if (e) e.preventDefault();
    setLoading(true);
    setError('');
    setSuccessMsg('');
    try {
      const results = await jobService.searchJobs({
        keyword,
        location: location === 'All' ? '' : location,
        employmentType: employmentType === 'All' ? '' : employmentType,
        minMatchScore: Number(minMatchScore),
        resumeId: selectedResumeId,
        userId: user.id,
      });
      setJobs(results);
    } catch (err) {
      setError(err?.message || 'Failed to search jobs.');
    } finally {
      setLoading(false);
    }
  };

  const handleApply = async (jobId) => {
    setApplyingJobId(jobId);
    setError('');
    setSuccessMsg('');
    try {
      await applicationService.applyForJob({
        userId: user.id,
        jobId: jobId,
        resumeId: selectedResumeId,
        notes: 'Applied via AI Career Assistant Job Matching portal',
      });
      setSuccessMsg('🎉 Application submitted successfully!');
      setTimeout(() => setSuccessMsg(''), 4000);
    } catch (err) {
      setError(err?.message || 'Failed to apply for job.');
    } finally {
      setApplyingJobId(null);
    }
  };

  const getScoreBadgeClass = (score) => {
    if (score >= 75) return 'badge-high';
    if (score >= 50) return 'badge-medium';
    return 'badge-low';
  };

  return (
    <div className="job-matching-container animate-fade-in" style={{ padding: '2rem' }}>
      <div className="page-header" style={{ marginBottom: '1.5rem' }}>
        <h1 style={{ fontSize: '1.8rem', fontWeight: 700, marginBottom: '0.4rem' }}>💼 AI Job Matching Engine</h1>
        <p style={{ color: '#94a3b8' }}>Discover opportunities aligned with your extracted technical skill vectors</p>
      </div>

      {/* Filter Bar */}
      <form onSubmit={handleSearch} style={{ background: '#1e293b', padding: '1.25rem', borderRadius: '12px', border: '1px solid #334155', marginBottom: '1.5rem', display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '1rem', alignItems: 'end' }}>
        <div>
          <label style={{ fontSize: '12px', color: '#94a3b8', display: 'block', marginBottom: '4px' }}>Search Keyword:</label>
          <input
            type="text"
            placeholder="e.g. Java, Python, React..."
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
            style={{ width: '100%', padding: '0.5rem 0.8rem', background: '#0f172a', border: '1px solid #334155', borderRadius: '6px', color: '#fff' }}
          />
        </div>

        <div>
          <label style={{ fontSize: '12px', color: '#94a3b8', display: 'block', marginBottom: '4px' }}>Location:</label>
          <select
            value={location}
            onChange={(e) => setLocation(e.target.value)}
            style={{ width: '100%', padding: '0.5rem 0.8rem', background: '#0f172a', border: '1px solid #334155', borderRadius: '6px', color: '#fff' }}
          >
            <option value="All">All Locations</option>
            <option value="Hyderabad">Hyderabad</option>
            <option value="Bengaluru">Bengaluru</option>
            <option value="Remote">Remote</option>
            <option value="Mumbai">Mumbai</option>
            <option value="Pune">Pune</option>
            <option value="Chennai">Chennai</option>
          </select>
        </div>

        <div>
          <label style={{ fontSize: '12px', color: '#94a3b8', display: 'block', marginBottom: '4px' }}>Employment Type:</label>
          <select
            value={employmentType}
            onChange={(e) => setEmploymentType(e.target.value)}
            style={{ width: '100%', padding: '0.5rem 0.8rem', background: '#0f172a', border: '1px solid #334155', borderRadius: '6px', color: '#fff' }}
          >
            <option value="All">All Types</option>
            <option value="Full-time">Full-time</option>
            <option value="Contract">Contract</option>
            <option value="Part-time">Part-time</option>
          </select>
        </div>

        <div>
          <label style={{ fontSize: '12px', color: '#94a3b8', display: 'block', marginBottom: '4px' }}>Min Match Score ({minMatchScore}%):</label>
          <input
            type="range"
            min="0"
            max="90"
            step="10"
            value={minMatchScore}
            onChange={(e) => setMinMatchScore(Number(e.target.value))}
            style={{ width: '100%', accentColor: '#6366f1' }}
          />
        </div>

        <button type="submit" className="btn-primary" style={{ padding: '0.55rem 1rem', background: 'linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%)', border: 'none', borderRadius: '6px', color: '#fff', fontWeight: 600, cursor: 'pointer' }}>
          🔍 Search & Match
        </button>
      </form>

      {successMsg && (
        <div style={{ background: 'rgba(16, 185, 129, 0.15)', border: '1px solid rgba(16, 185, 129, 0.3)', padding: '0.85rem 1.25rem', borderRadius: '8px', color: '#10b981', marginBottom: '1.5rem', fontWeight: 600 }}>
          {successMsg}
        </div>
      )}

      {error && (
        <div style={{ background: 'rgba(244, 63, 94, 0.15)', border: '1px solid rgba(244, 63, 94, 0.3)', padding: '0.85rem 1.25rem', borderRadius: '8px', color: '#f43f5e', marginBottom: '1.5rem', fontWeight: 600 }}>
          ⚠️ {error}
        </div>
      )}

      {loading ? (
        <div style={{ textAlign: 'center', padding: '4rem 0', color: '#94a3b8' }}>
          <div className="spinner" style={{ width: '32px', height: '32px', margin: '0 auto 1rem auto' }}></div>
          <p>Matching your extracted skills against open roles...</p>
        </div>
      ) : jobs.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '3rem', background: '#1e293b', borderRadius: '12px', border: '1px solid #334155' }}>
          <h3>No Jobs Found Matching Criteria</h3>
          <p style={{ color: '#94a3b8', margin: '0.5rem 0' }}>Try lowering the minimum match score or clearing search filters.</p>
        </div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(340px, 1fr))', gap: '1.5rem' }}>
          {jobs.map((job) => (
            <div key={job.jobId || job.id} className="card" style={{ background: '#1e293b', border: '1px solid #334155', borderRadius: '12px', padding: '1.25rem', display: 'flex', flexDirection: 'column', gap: '0.85rem' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                <div>
                  <h3 style={{ fontSize: '1.15rem', fontWeight: 700, color: '#f8fafc', marginBottom: '0.25rem' }}>{job.title}</h3>
                  <p style={{ color: '#818cf8', fontWeight: 600, fontSize: '0.9rem' }}>{job.companyName}</p>
                </div>
                <div style={{ padding: '0.25rem 0.6rem', borderRadius: '20px', fontWeight: 700, fontSize: '0.8rem', background: job.matchScore >= 75 ? 'rgba(16, 185, 129, 0.2)' : 'rgba(245, 158, 11, 0.2)', color: job.matchScore >= 75 ? '#10b981' : '#f59e0b' }}>
                  {job.matchScore}% Match
                </div>
              </div>

              <div style={{ display: 'flex', gap: '1rem', fontSize: '0.8rem', color: '#94a3b8', flexWrap: 'wrap' }}>
                <span>📍 {job.location}</span>
                <span>💼 {job.employmentType}</span>
                <span>💰 {job.salary}</span>
              </div>

              {/* Skill Matching Tags */}
              <div style={{ display: 'flex', flexDirection: 'column', gap: '0.4rem', margin: '0.4rem 0' }}>
                {job.matchingSkills && job.matchingSkills.length > 0 && (
                  <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.3rem', alignItems: 'center' }}>
                    <span style={{ fontSize: '0.75rem', color: '#10b981', fontWeight: 600 }}>Matching:</span>
                    {job.matchingSkills.map((sk, idx) => (
                      <span key={idx} style={{ background: 'rgba(16, 185, 129, 0.12)', border: '1px solid rgba(16, 185, 129, 0.25)', color: '#a7f3d0', padding: '0.15rem 0.5rem', borderRadius: '4px', fontSize: '0.75rem' }}>
                        ✓ {sk}
                      </span>
                    ))}
                  </div>
                )}

                {job.missingSkills && job.missingSkills.length > 0 && (
                  <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.3rem', alignItems: 'center' }}>
                    <span style={{ fontSize: '0.75rem', color: '#f59e0b', fontWeight: 600 }}>Gap:</span>
                    {job.missingSkills.slice(0, 3).map((sk, idx) => (
                      <span key={idx} style={{ background: 'rgba(245, 158, 11, 0.12)', border: '1px solid rgba(245, 158, 11, 0.25)', color: '#fde68a', padding: '0.15rem 0.5rem', borderRadius: '4px', fontSize: '0.75rem' }}>
                        ⚡ {sk}
                      </span>
                    ))}
                  </div>
                )}
              </div>

              <div style={{ display: 'flex', gap: '0.75rem', marginTop: 'auto', paddingTop: '0.5rem' }}>
                <button
                  className="btn-secondary"
                  onClick={() => onSelectJob && onSelectJob(job.jobId || job.id)}
                  style={{ flex: 1, padding: '0.45rem', background: '#334155', border: 'none', borderRadius: '6px', color: '#fff', fontSize: '0.85rem', cursor: 'pointer' }}
                >
                  View Details
                </button>
                <button
                  className="btn-primary"
                  disabled={applyingJobId === (job.jobId || job.id)}
                  onClick={() => handleApply(job.jobId || job.id)}
                  style={{ flex: 1, padding: '0.45rem', background: 'linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%)', border: 'none', borderRadius: '6px', color: '#fff', fontSize: '0.85rem', fontWeight: 600, cursor: 'pointer' }}
                >
                  {applyingJobId === (job.jobId || job.id) ? 'Applying...' : 'Apply Now'}
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
