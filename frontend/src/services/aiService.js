const API_BASE_URL = 'http://localhost:8080/api/ai/resumes';

export const aiService = {
  /**
   * Run complete AI resume analysis
   */
  async analyzeResume(resumeId, userId) {
    const response = await fetch(`${API_BASE_URL}/${resumeId}/analyze?userId=${userId}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.message || 'Failed to complete AI resume analysis');
    }

    return await response.json();
  },

  /**
   * Get extracted & categorized skills for a resume
   */
  async getExtractedSkills(resumeId, userId) {
    const response = await fetch(`${API_BASE_URL}/${resumeId}/skills?userId=${userId}`);

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.message || 'Failed to fetch extracted skills');
    }

    return await response.json();
  },

  /**
   * Get career recommendations for a resume
   */
  async getCareerRecommendations(resumeId, userId) {
    const response = await fetch(`${API_BASE_URL}/${resumeId}/careers?userId=${userId}`);

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.message || 'Failed to fetch career recommendations');
    }

    return await response.json();
  },

  /**
   * Run complete analysis endpoint
   */
  async getCompleteAIAnalysis(resumeId, userId) {
    const response = await fetch(`${API_BASE_URL}/${resumeId}/complete-analysis?userId=${userId}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.message || 'Failed to generate complete AI career intelligence');
    }

    return await response.json();
  }
};
