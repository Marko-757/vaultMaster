import axios from "axios";

const API_BASE = "/api/roles";

export const createRole = (teamId, name) =>
  axios.post(API_BASE, { teamId, roleName: name });

export const getRolesForTeam = (teamId) =>
  axios.get(`${API_BASE}/team/${teamId}`);

export const getGlobalPermissions = () =>
  axios.get("/api/permissions");

export const updateRolePermissions = (roleId, permissionNames) =>
  axios.post(`${API_BASE}/${roleId}/permissions`, permissionNames);
