import axios from "axios";

const BASE_URL = "http://localhost:8080/api";
const TOKEN = localStorage.getItem("jwtToken");

const headers = {
  Authorization: `Bearer ${TOKEN}`,
  withCredentials: true,
};

export const redeemInvitationCode = (code) =>
  axios.post(`${BASE_URL}/team-invitations/redeem`, { code }, { headers });

export const getMyTeamMemberships = () =>
  axios.get(`${BASE_URL}/team-members/my`, { headers });

export const getPasswordsVisibleToRole = (roleId) =>
  axios.get(`${BASE_URL}/team/passwords/visible/${roleId}`, { headers });