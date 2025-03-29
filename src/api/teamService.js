import axios from 'axios';

const API_URL = '/api/teams';

/**
 * Create a new team.
 * @param {string} teamName - The name of the new team.
 * @returns {Promise} - Resolves with the created team.
 */
export const createTeam = async (teamName) => {
  try {
    const response = await axios.post(API_URL, { teamName });
    return response.data;
  } catch (error) {
    console.error("Error creating team:", error);
    throw error;
  }
};

/**
 * Get all teams for the logged-in user.
 * @returns {Promise} - Resolves with the list of teams.
 */
export const getAllTeamsForUser = async () => {
  try {
    const response = await axios.get(`${API_URL}/user`);
    return response.data;
  } catch (error) {
    console.error("Error fetching user's teams:", error);
    throw error;
  }
};

/**
 * Get all teams.
 * @returns {Promise} - Resolves with the list of all teams.
 */
export const getAllTeams = async () => {
  try {
    const response = await axios.get(API_URL);
    return response.data;
  } catch (error) {
    console.error("Error fetching all teams:", error);
    throw error;
  }
};

/**
 * Get a team by its ID.
 * @param {string} teamId - The ID of the team.
 * @returns {Promise} - Resolves with the team data.
 */
export const getTeamById = async (teamId) => {
  try {
    const response = await axios.get(`${API_URL}/${teamId}`);
    return response.data;
  } catch (error) {
    console.error("Error fetching team by ID:", error);
    throw error;
  }
};

/**
 * Update a team name by its ID.
 * @param {string} teamId - The ID of the team to update.
 * @param {string} newTeamName - The new team name.
 * @returns {Promise} - Resolves with the updated team data.
 */
export const updateTeamName = async (teamId, newTeamName) => {
  try {
    const response = await axios.put(`${API_URL}/${teamId}`, { teamName: newTeamName });
    return response.data;
  } catch (error) {
    console.error("Error updating team name:", error);
    throw error;
  }
};

/**
 * Delete a team by its ID.
 * @param {string} teamId - The ID of the team to delete.
 * @returns {Promise} - Resolves with a success message.
 */
export const deleteTeam = async (teamId) => {
  try {
    const response = await axios.delete(`${API_URL}/${teamId}`);
    return response.data;
  } catch (error) {
    console.error("Error deleting team:", error);
    throw error;
  }
};
