import axios from "axios";

const TEAM_MEMBER_URL = "http://localhost:8080/api/team-members";

export const getTeamMembers = async (teamId) => {
  try {
    const token = localStorage.getItem("jwtToken");
    const response = await axios.get(`${TEAM_MEMBER_URL}/team/${teamId}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
      withCredentials: true,
    });
    return response.data;
  } catch (error) {
    console.error("Error fetching team members:", error);
    throw error;
  }
};
