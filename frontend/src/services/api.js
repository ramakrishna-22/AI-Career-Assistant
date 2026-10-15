const API_BASE_URL = 'http://localhost:8080';

/**
 * Helper to handle fetch responses and throw legible error messages
 */
async function handleResponse(response) {
  const contentType = response.headers.get('content-type');
  let data;

  if (contentType && contentType.includes('application/json')) {
    data = await response.json();
  } else {
    data = { message: await response.text() };
  }

  if (!response.ok) {
    // If structured field errors exist from validation
    if (data && typeof data === 'object' && !data.message) {
      const fieldErrors = Object.values(data).join(', ');
      throw new Error(fieldErrors || 'Validation failed. Please check your inputs.');
    }
    const errorMessage = data?.message || `Request failed with status ${response.status}`;
    throw new Error(errorMessage);
  }

  return data;
}

/**
 * Register User API call
 * POST /api/auth/register
 */
export async function registerUser(userData) {
  const response = await fetch(`${API_BASE_URL}/api/auth/register`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(userData),
  });

  return handleResponse(response);
}

/**
 * Login User API call
 * POST /api/auth/login
 */
export async function loginUser(credentials) {
  const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(credentials),
  });

  return handleResponse(response);
}
