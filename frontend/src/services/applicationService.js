const API_BASE_URL = 'http://localhost:8080/api/applications';

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

export const applicationService = {
  async applyForJob({ userId, jobId, resumeId, notes }) {
    const response = await fetch(`${API_BASE_URL}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ userId, jobId, resumeId, notes }),
    });
    return handleResponse(response);
  },

  async getUserApplications(userId) {
    const response = await fetch(`${API_BASE_URL}/user/${userId}`);
    return handleResponse(response);
  },

  async getApplicationById(id, userId) {
    const response = await fetch(`${API_BASE_URL}/${id}?userId=${userId}`);
    return handleResponse(response);
  },

  async updateApplicationStatus(id, userId, status) {
    const response = await fetch(`${API_BASE_URL}/${id}/status?userId=${userId}&status=${status}`, {
      method: 'PUT',
    });
    return handleResponse(response);
  },

  async deleteApplication(id, userId) {
    const response = await fetch(`${API_BASE_URL}/${id}?userId=${userId}`, {
      method: 'DELETE',
    });
    if (response.status === 240 || response.status === 204) {
      return { success: true };
    }
    return handleResponse(response);
  },

  async getApplicationStats(userId) {
    const response = await fetch(`${API_BASE_URL}/stats/user/${userId}`);
    return handleResponse(response);
  }
};

export default applicationService;
