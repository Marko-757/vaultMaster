import axios from "axios";

const API_URL = "/api/team-memberships";

export const joinTeamWithCode = async (code) => {
  return await axios.post(`${API_URL}/join`, { code });
};
