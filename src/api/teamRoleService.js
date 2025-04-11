import axios from "axios";

const API_BASE = "/api/roles";

export const createRole = (teamId, name, createdBy) =>
  axios.post(`${API_BASE}`, {
    teamId,
    roleName: name,
    createdBy
  });


export const getRolesForTeam = (teamId) =>
  axios.get(`${API_BASE}/team/${teamId}`);

export const getTeamPasswords = (teamId) =>
  axios.get(`/api/teams/${teamId}/passwords`);

export const getTeamFiles = (teamId) =>
  axios.get(`/api/teams/${teamId}/files`);

export const assignRolePasswordAccess = (roleId, passwordIds) =>
  axios.post(`${API_BASE}/${roleId}/password-access`, passwordIds);

export const assignRoleFileAccess = (roleId, fileIds) =>
  axios.post(`${API_BASE}/${roleId}/file-access`, fileIds);
