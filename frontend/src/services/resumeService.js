const API_BASE_URL = 'http://localhost:8080';

async function handleResponse(response) {
  const contentType = response.headers.get('content-type');
  let data;

  if (contentType && contentType.includes('application/json')) {
    data = await response.json();
  } else {
    data = { message: await response.text() };
  }

  if (!response.ok) {
    const errorMessage = data?.message || `Request failed with status ${response.status}`;
    throw new Error(errorMessage);
  }

  return data;
}

/**
 * Upload Resume file for user
 * POST /api/resumes/upload (multipart/form-data)
 */
export async function uploadResume(file, userId) {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('userId', userId);

  const response = await fetch(`${API_BASE_URL}/api/resumes/upload`, {
    method: 'POST',
    body: formData,
  });

  return handleResponse(response);
}

/**
 * Fetch all resumes belonging to user
 * GET /api/resumes/user/{userId}
 */
export async function getUserResumes(userId) {
  const response = await fetch(`${API_BASE_URL}/api/resumes/user/${userId}`, {
    method: 'GET',
  });

  return handleResponse(response);
}

/**
 * Get download URL for a resume
 */
export function getDownloadUrl(resumeId, userId) {
  return `${API_BASE_URL}/api/resumes/${resumeId}/download?userId=${userId}`;
}

/**
 * Analyze uploaded resume
 * POST /api/resumes/{resumeId}/analyze
 */
export async function analyzeResume(resumeId, userId) {
  const url = `${API_BASE_URL}/api/resumes/${resumeId}/analyze?userId=${userId}`;
  const response = await fetch(url, {
    method: 'POST',
  });

  return handleResponse(response);
}

// Export resumeService object for backwards compatibility and object-style access
export const resumeService = {
  uploadResume,
  getUserResumes,
  getDownloadUrl,
  analyzeResume,
};

export default resumeService;
