import React, { useState, useEffect } from 'react';
import { uploadResume, getUserResumes, getDownloadUrl } from '../services/resumeService';

export default function ResumeUpload({ user, onNavigateToDashboard, onNavigateToAnalysis }) {
  const [selectedFile, setSelectedFile] = useState(null);
  const [resumes, setResumes] = useState([]);
  const [loading, setLoading] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    if (user?.id) {
      fetchResumes();
    }
  }, [user]);

  const fetchResumes = async () => {
    setLoading(true);
    try {
      const data = await getUserResumes(user.id);
      setResumes(data || []);
    } catch (err) {
      setError(err.message || 'Failed to fetch uploaded resumes.');
    } finally {
      setLoading(false);
    }
  };

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setSelectedFile(file);
      setError('');
      setSuccess('');
    }
  };

  const handleUpload = async (e) => {
    e.preventDefault();
    if (!selectedFile) {
      setError('Please select a file to upload (.pdf, .doc, .docx).');
      return;
    }

    setUploading(true);
    setError('');
    setSuccess('');

    try {
      const result = await uploadResume(selectedFile, user.id);
      setSuccess(`Successfully uploaded "${result.fileName}"!`);
      setSelectedFile(null);
      // Reset file input element
      const fileInput = document.getElementById('resume-file-input');
      if (fileInput) fileInput.value = '';
      fetchResumes();
    } catch (err) {
      setError(err.message || 'Failed to upload resume.');
    } finally {
      setUploading(false);
    }
  };

  const formatFileSize = (bytes) => {
    if (!bytes) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i];
  };

  const formatDate = (dateStr) => {
    if (!dateStr) return '';
    try {
      return new Date(dateStr).toLocaleString();
    } catch {
      return dateStr;
    }
  };

  return (
    <div className="dashboard-container">
      <header className="navbar">
        <div className="nav-brand" style={{ cursor: 'pointer' }} onClick={onNavigateToDashboard}>
          <span>⚡</span> AI Career Assistant
        </div>
        <div className="user-pill">
          <button className="btn-secondary" onClick={onNavigateToDashboard}>
            ← Back to Dashboard
          </button>
        </div>
      </header>

      <main className="dashboard-content">
        <div className="page-header" style={{ marginBottom: '2rem' }}>
          <h2>Resume Management & Upload</h2>
          <p style={{ color: 'var(--text-secondary)' }}>
            Upload your resume in PDF, DOC, or DOCX format to receive automated structural and skill analysis.
          </p>
        </div>

        {error && <div className="alert alert-danger">⚠️ {error}</div>}
        {success && <div className="alert alert-success">✅ {success}</div>}

        {/* Upload Card */}
        <div className="auth-card" style={{ maxWidth: '100%', marginBottom: '2.5rem', padding: '2rem' }}>
          <form onSubmit={handleUpload}>
            <div className="dropzone-area">
              <span style={{ fontSize: '2.5rem', marginBottom: '0.75rem', display: 'block' }}>📁</span>
              <label htmlFor="resume-file-input" className="form-label" style={{ cursor: 'pointer', fontSize: '1.1rem' }}>
                {selectedFile ? selectedFile.name : 'Choose a Resume File'}
              </label>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem', marginTop: '0.25rem' }}>
                Supported formats: <strong>.pdf, .doc, .docx</strong> (Max 10MB)
              </p>
              <input
                id="resume-file-input"
                type="file"
                accept=".pdf,.doc,.docx"
                onChange={handleFileChange}
                style={{ display: 'none' }}
                disabled={uploading}
              />
              <button
                type="button"
                className="btn-secondary"
                style={{ marginTop: '1rem' }}
                onClick={() => document.getElementById('resume-file-input').click()}
                disabled={uploading}
              >
                Browse Files
              </button>
            </div>

            {selectedFile && (
              <div style={{ marginTop: '1rem', padding: '0.75rem 1rem', background: 'rgba(255,255,255,0.04)', borderRadius: 'var(--radius-md)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                  <strong>Selected:</strong> {selectedFile.name} ({formatFileSize(selectedFile.size)})
                </div>
                <button type="submit" className="btn-primary" style={{ width: 'auto', marginTop: 0 }} disabled={uploading}>
                  {uploading ? <><span className="spinner"></span> Uploading...</> : 'Upload Resume'}
                </button>
              </div>
            )}
          </form>
        </div>

        {/* Resumes List Section */}
        <section>
          <div className="section-title">
            <h3>Your Uploaded Resumes</h3>
            <span className="badge-coming-soon">{resumes.length} File{resumes.length !== 1 ? 's' : ''}</span>
          </div>

          {loading ? (
            <div style={{ textAlignment: 'center', padding: '3rem', color: 'var(--text-secondary)' }}>
              <span className="spinner" style={{ margin: '0 auto 1rem display: block' }}></span> Loading resumes...
            </div>
          ) : resumes.length === 0 ? (
            <div className="module-card" style={{ textAlign: 'center', padding: '3rem' }}>
              <p style={{ color: 'var(--text-muted)' }}>No resumes uploaded yet. Upload your first resume above to get started!</p>
            </div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              {resumes.map((item) => (
                <div key={item.id} className="module-card" style={{ flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between', padding: '1.25rem 1.75rem' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '1.25rem' }}>
                    <div className="module-icon" style={{ marginBottom: 0, width: '42px', height: '42px', fontSize: '1.25rem' }}>
                      📄
                    </div>
                    <div>
                      <h4 style={{ fontSize: '1.05rem', marginBottom: '0.25rem' }}>{item.fileName}</h4>
                      <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>
                        Size: {formatFileSize(item.fileSize)} • Uploaded: {formatDate(item.uploadedAt)}
                      </p>
                    </div>
                  </div>

                  <div style={{ display: 'flex', gap: '0.75rem' }}>
                    <a
                      href={getDownloadUrl(item.id, user.id)}
                      download
                      className="btn-secondary"
                      style={{ fontSize: '0.85rem', padding: '0.5rem 1rem' }}
                    >
                      ⬇️ Download
                    </a>
                    <button
                      className="btn-primary"
                      style={{ width: 'auto', marginTop: 0, fontSize: '0.85rem', padding: '0.5rem 1.25rem' }}
                      onClick={() => onNavigateToAnalysis(item.id)}
                    >
                      🔍 Analyze Resume
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </section>
      </main>
    </div>
  );
}
