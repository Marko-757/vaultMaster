import axios from "axios";

const API_BASE = "/api/roles";

export const createRole = (teamId, name) =>
  axios.post(API_BASE, { teamId, roleName: name });

export const getRolesForTeam = (teamId) => {
  const token = localStorage.getItem("jwtToken");
  return axios.get(`${API_BASE}/team/${teamId}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
    withCredentials: true,
  });
};

export const getItemPermissionsForRole = ({ roleId, itemId, itemType }) => {
  const token = localStorage.getItem("jwtToken");
  return axios.get(`/api/permissions/item`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
    withCredentials: true,
    params: {
      roleId,
      itemId,
      itemType,
    },
  });
};

export const getFolderPermissionsForRole = ({
  roleId,
  folderId,
  folderType,
}) => {
  const token = localStorage.getItem("jwtToken");
  return axios.get(`/api/permissions/folder`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
    withCredentials: true,
    params: {
      roleId,
      folderId,
      folderType,
    },
  });
};

export const getGlobalPermissions = () => axios.get("/api/permissions");

export const updateRolePermissions = (roleId, permissionNames) =>
  axios.post(`${API_BASE}/${roleId}/permissions`, permissionNames);

export const assignItemPermissions = (payload) => {
  const token = localStorage.getItem("jwtToken");
  return axios.post("/api/permissions/item", payload, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
    withCredentials: true,
  });
};

export const getPermissionsForRole = (roleId) => {
  const token = localStorage.getItem("jwtToken");
  return axios.get(`/api/roles/${roleId}/permissions`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
    withCredentials: true,
  });
};

export const removeItemPermission = (payload) => {
  const token = localStorage.getItem("jwtToken");
  return axios.delete("/api/permissions/item", {
    data: payload,
    headers: {
      Authorization: `Bearer ${token}`,
    },
    withCredentials: true,
  });
};

export const getRoleForUserInTeam = (userId, teamId) => {
  const token = localStorage.getItem("jwtToken");
  return axios.get(`/api/team-members/role`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
    withCredentials: true,
    params: {
      userId,
      teamId,
    },
  });
};

export const getCurrentUserRoleForTeam = (teamId) => {
  const token = localStorage.getItem("jwtToken");
  return axios.get(`/api/roles/team/${teamId}/my-role`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
    withCredentials: true,
  });
};



