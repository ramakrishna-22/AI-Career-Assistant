const API_BASE_URL = 'http://localhost:8080/api/tailoring';

async function handleResponse(response) {
  let data = null;

  const contentType = response.headers.get('content-type') || '';

  if (contentType.includes('application/json')) {
    try {
      data = await response.json();
    } catch {
      data = null;
    }
  } else {
    const text = await response.text();
    data = text ? { message: text } : null;
  }

  if (!response.ok) {
    const errorMessage =
      data?.message || `Request failed with status ${response.status}`;

    throw new Error(errorMessage);
  }

  return data;
}

export const tailoringService = {
  async tailorResume({
    userId,
    resumeId = null,
    jobId = null,
    targetJobDescription = '',
  }) {
    const response = await fetch(`${API_BASE_URL}/optimize`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        userId,
        resumeId,
        jobId,
        targetJobDescription,
      }),
    });

    return handleResponse(response);
  },

  async getUserTailoredResumes(userId) {
    const response = await fetch(`${API_BASE_URL}/user/${userId}`);

    return handleResponse(response);
  },
};

export default tailoringService;
