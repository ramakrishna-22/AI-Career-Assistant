const API_BASE_URL = 'http://localhost:8080/api/jobs';

async function handleResponse(response) {
  let data;

  const contentType = response.headers.get('content-type') || '';

  if (contentType.includes('application/json')) {
    data = await response.json();
  } else {
    const text = await response.text();
    data = text ? { message: text } : {};
  }

  if (!response.ok) {
    const errorMessage =
      data?.message || `Request failed with status ${response.status}`;

    throw new Error(errorMessage);
  }

  return data;
}

export const jobService = {
  async getAllJobs() {
    const response = await fetch(API_BASE_URL);
    return handleResponse(response);
  },

  async getJobById(jobId) {
    const response = await fetch(`${API_BASE_URL}/${jobId}`);
    return handleResponse(response);
  },

  async searchJobs({
    keyword = '',
    location = '',
    employmentType = '',
    minMatchScore = 0,
    resumeId = null,
    userId = null,
  } = {}) {
    const params = new URLSearchParams();

    if (keyword) {
      params.append('keyword', keyword);
    }

    if (location) {
      params.append('location', location);
    }

    if (employmentType) {
      params.append('employmentType', employmentType);
    }

    if (minMatchScore > 0) {
      params.append('minMatchScore', minMatchScore);
    }

    if (resumeId != null) {
      params.append('resumeId', resumeId);
    }

    if (userId != null) {
      params.append('userId', userId);
    }

    const queryString = params.toString();

    const url = queryString
      ? `${API_BASE_URL}/search?${queryString}`
      : `${API_BASE_URL}/search`;

    const response = await fetch(url);

    return handleResponse(response);
  },

  async getRecommendedJobs(userId, limit = 5) {
    const params = new URLSearchParams();

    if (userId != null) {
      params.append('userId', userId);
    }

    if (limit != null) {
      params.append('limit', limit);
    }

    const queryString = params.toString();

    const url = queryString
      ? `${API_BASE_URL}/recommended?${queryString}`
      : `${API_BASE_URL}/recommended`;

    const response = await fetch(url);

    return handleResponse(response);
  },

  async matchJobsForResume(resumeId, userId) {
    if (resumeId == null) {
      throw new Error('resumeId is required');
    }

    const params = new URLSearchParams();

    if (userId != null) {
      params.append('userId', userId);
    }

    const queryString = params.toString();

    const url = queryString
      ? `${API_BASE_URL}/match/${resumeId}?${queryString}`
      : `${API_BASE_URL}/match/${resumeId}`;

    const response = await fetch(url);

    return handleResponse(response);
  },
};

export default jobService;
