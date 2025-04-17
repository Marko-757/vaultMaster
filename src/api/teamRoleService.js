// src/api/teamRoleService.js

import axios from "axios";

const API_BASE = "/api/roles";

/**
 * Create a new role for a team.
 * Requires: { teamId, roleName }
 */
export const createRole = (teamId, name) =>
  axios.post(`${API_BASE}`, 
    {
      teamId: teamId,
      roleName: name
    },
    {
      headers: {
        "Content-Type": "application/json"
      }
    }
  );

/**
 * Get all roles for a specific team
 */
export const getRolesForTeam = (teamId) =>
  axios.get(`${API_BASE}/team/${teamId}`);

/**
 * Get passwords associated with a team (for permission selection)
 */
export const getTeamPasswords = (teamId) =>
  axios.get(`/api/teams/${teamId}/passwords`);

/**
 * Get files associated with a team (for permission selection)
 */
export const getTeamFiles = (teamId) =>
  axios.get(`/api/teams/${teamId}/files`);

/**
 * Assign password permissions to a role
 */
export const assignRolePasswordAccess = (roleId, passwordIds) =>
  axios.post(`${API_BASE}/${roleId}/password-access`, passwordIds);

/**
 * Assign file permissions to a role
 */
export const assignRoleFileAccess = (roleId, fileIds) =>
  axios.post(`${API_BASE}/${roleId}/file-access`, fileIds);
