import axios from "axios";

const BASE_URL = "http://localhost:8080/api/teams";

const getAuthToken = () => {
  return localStorage.getItem("jwtToken");
};

// Create a new team.
export const createTeam = async (teamName) => {
  try {
    const token = getAuthToken();
    const response = await axios.post(
      BASE_URL,
      { teamName },
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
        withCredentials: true,
      }
    );
    return response.data;
  } catch (error) {
    console.error("Error creating team:", error);
    throw error;
  }
};

// Get all teams for a user.
export const getAllTeamsForUser = async () => {
  try {
    const token = getAuthToken();
    const response = await axios.get(`${BASE_URL}/user`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
      withCredentials: true,
    });
    return response.data;
  } catch (error) {
    console.error("Error fetching user's teams:", error);
    throw error;
  }
};

// Get all teams (general access).
export const getAllTeams = async () => {
  try {
    const token = getAuthToken();
    const response = await axios.get(BASE_URL, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
      withCredentials: true,
    });
    return response.data;
  } catch (error) {
    console.error("Error fetching all teams:", error);
    throw error;
  }
};

// Get a team by its ID.
export const getTeamById = async (teamId) => {
  try {
    const token = getAuthToken();
    const response = await axios.get(`${BASE_URL}/${teamId}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
      withCredentials: true,
    });
    return response.data;
  } catch (error) {
    console.error("Error fetching team by ID:", error);
    throw error;
  }
};

// Method to update team name
export const updateTeamName = async (teamId, newTeamName) => {
  try {
    const token = localStorage.getItem("jwtToken");
    const response = await axios.put(
      `${BASE_URL}/${teamId}`,
      { teamName: newTeamName },
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
        withCredentials: true,
      }
    );
    return response.data;
  } catch (error) {
    console.error("Error updating team name:", error);
    throw error;
  }
};

// Delete a team by its ID.
export const deleteTeam = async (teamId) => {
  try {
    const token = getAuthToken();
    const response = await axios.delete(`${BASE_URL}/${teamId}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
      withCredentials: true,
    });
    return response.data;
  } catch (error) {
    console.error("Error deleting team:", error);
    throw error;
  }
};

export const getMembershipsForUser = async () => {
  try {
    const token = getAuthToken();
    const response = await axios.get(`${BASE_URL}/memberships`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
      withCredentials: true,
    });
    return response.data;
  } catch (error) {
    console.error("Error fetching memberships:", error);
    throw error;
  }
};

