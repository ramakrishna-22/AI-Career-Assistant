import React, { useState, useEffect } from 'react';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import ResumeUpload from './pages/ResumeUpload';
import ResumeAnalysis from './pages/ResumeAnalysis';
import CareerRecommendations from './pages/CareerRecommendations';
import JobMatching from './pages/JobMatching';
import JobDetails from './pages/JobDetails';
import ApplicationTracker from './pages/ApplicationTracker';
import SkillGapView from './pages/SkillGapView';
import ResumeTailorView from './pages/ResumeTailorView';
import Navbar from './components/Navbar';

export default function App() {
  const [currentPage, setCurrentPage] = useState('login');
  const [currentUser, setCurrentUser] = useState(null);
  const [activeResumeId, setActiveResumeId] = useState(null);
  const [selectedJobId, setSelectedJobId] = useState(null);

  // Load user session from localStorage on initial render if present
  useEffect(() => {
    const savedUser = localStorage.getItem('career_assistant_user');
    if (savedUser) {
      try {
        const user = JSON.parse(savedUser);
        setCurrentUser(user);
        setCurrentPage('dashboard');
      } catch (err) {
        localStorage.removeItem('career_assistant_user');
      }
    }
  }, []);

  const handleLoginSuccess = (user) => {
    setCurrentUser(user);
    localStorage.setItem('career_assistant_user', JSON.stringify(user));
    setCurrentPage('dashboard');
  };

  const handleLogout = () => {
    setCurrentUser(null);
    localStorage.removeItem('career_assistant_user');
    setCurrentPage('login');
  };

  const handleNavigateToAnalysis = (resumeId) => {
    setActiveResumeId(resumeId);
    setCurrentPage('resume-analysis');
  };

  const handleNavigateToRecommendations = (resumeId) => {
    if (resumeId) {
      setActiveResumeId(resumeId);
    }
    setCurrentPage('career-recommendations');
  };

  const handleSelectJob = (jobId) => {
    setSelectedJobId(jobId);
    setCurrentPage('job-details');
  };

  return (
    <div className="app-root" style={{ minHeight: '100vh', background: '#0f172a', color: '#f8fafc' }}>
      {/* Show Navbar for logged in user pages */}
      {currentUser && currentPage !== 'login' && currentPage !== 'register' && (
        <Navbar
          user={currentUser}
          currentPage={currentPage}
          onNavigate={(page) => setCurrentPage(page)}
          onLogout={handleLogout}
        />
      )}

      {currentPage === 'login' && (
        <Login
          onLoginSuccess={handleLoginSuccess}
          onNavigateToRegister={() => setCurrentPage('register')}
        />
      )}

      {currentPage === 'register' && (
        <Register
          onNavigateToLogin={() => setCurrentPage('login')}
        />
      )}

      {currentPage === 'dashboard' && (
        <Dashboard
          user={currentUser}
          onLogout={handleLogout}
          onNavigate={(page) => setCurrentPage(page)}
        />
      )}

      {currentPage === 'resume-upload' && (
        <ResumeUpload
          user={currentUser}
          onNavigateToDashboard={() => setCurrentPage('dashboard')}
          onNavigateToAnalysis={handleNavigateToAnalysis}
        />
      )}

      {currentPage === 'resume-analysis' && (
        <ResumeAnalysis
          resumeId={activeResumeId}
          user={currentUser}
          onNavigateToUpload={() => setCurrentPage('resume-upload')}
          onNavigateToDashboard={() => setCurrentPage('dashboard')}
          onNavigateToRecommendations={handleNavigateToRecommendations}
        />
      )}

      {currentPage === 'career-recommendations' && (
        <CareerRecommendations
          user={currentUser}
          resumeId={activeResumeId}
          onNavigate={(page) => setCurrentPage(page)}
        />
      )}

      {currentPage === 'job-matching' && (
        <JobMatching
          user={currentUser}
          onNavigate={(page) => setCurrentPage(page)}
          onSelectJob={handleSelectJob}
        />
      )}

      {currentPage === 'job-details' && (
        <JobDetails
          jobId={selectedJobId}
          user={currentUser}
          onNavigateBack={() => setCurrentPage('job-matching')}
        />
      )}

      {currentPage === 'applications' && (
        <ApplicationTracker
          user={currentUser}
          onNavigate={(page) => setCurrentPage(page)}
        />
      )}

      {currentPage === 'skill-gap' && (
        <SkillGapView
          user={currentUser}
          onNavigate={(page) => setCurrentPage(page)}
        />
      )}

      {currentPage === 'resume-tailor' && (
        <ResumeTailorView
          user={currentUser}
          onNavigate={(page) => setCurrentPage(page)}
        />
      )}
    </div>
  );
}
