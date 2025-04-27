import axios from "axios";

const BASE_URL = "http://localhost:8080/api";

const PASSWORDS = `${BASE_URL}/team/passwords`;
const PASSWORD_FOLDERS = `${BASE_URL}/team/folders`;
const FILES = `${BASE_URL}/team-files`;
const FILE_FOLDERS = `${BASE_URL}/team-file-folders`;

const getAuthToken = () => {
  return localStorage.getItem("jwtToken");
};

// Team Passwords
export const createTeamPassword = (data) => {
  const token = localStorage.getItem("jwtToken");
  return axios.post(`${PASSWORDS}`, data, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
    withCredentials: true,
  });
};

export const updateTeamPassword = (passwordId, data) => {
  const token = localStorage.getItem("jwtToken");
  return axios.put(`${PASSWORDS}/${passwordId}`, data, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
    withCredentials: true,
  });
};

export const deleteTeamPassword = (passwordId) => {
  const token = localStorage.getItem("jwtToken");
  return axios.delete(`${PASSWORDS}/${passwordId}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
    withCredentials: true,
  });
};

export const getTeamPasswords = (teamId) => {
  const token = localStorage.getItem("jwtToken");
  return axios.get(`${PASSWORDS}/team/${teamId}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
    withCredentials: true,
  });
};

export const moveTeamPasswordToFolder = (teamPasswordId, folderId) => {
  const token = localStorage.getItem("jwtToken");
  return axios.put(`${PASSWORDS}/${teamPasswordId}/move`, { folderId }, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
    withCredentials: true,
  });
};

export const decryptTeamPassword = (teamPasswordId) => {
  const token = localStorage.getItem("jwtToken");
  return axios.get(`${PASSWORDS}/${teamPasswordId}/decrypt`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
    withCredentials: true,
  });
};

export const getPasswordsInFolder = (folderId) => {
  const token = getAuthToken();
  return axios.get(`${PASSWORDS}/folder/${folderId}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
    withCredentials: true,
  });
};

// Team Password Folders
export const createTeamPasswordFolder = (teamId, folderName) => {
  const token = localStorage.getItem("jwtToken");
  return axios.post(PASSWORD_FOLDERS, { teamId, folderName }, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
    withCredentials: true,
  });
};

export const getTeamPasswordFolders = (teamId) => {
  const token = getAuthToken();
  return axios.get(`${PASSWORD_FOLDERS}/${teamId}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
    withCredentials: true,
  });
};

export const renameTeamPasswordFolder = (folderId, newName) => {
  const token = getAuthToken();
  return axios.put(
    `${PASSWORD_FOLDERS}/${folderId}/rename`,
    { newName },
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
      withCredentials: true,
    }
  );
};

export const deleteTeamPasswordFolder = (folderId, deleteItems = false) => {
  const token = getAuthToken();
  return axios.delete(`${PASSWORD_FOLDERS}/${folderId}`, {
    params: { deleteItems },
    headers: {
      Authorization: `Bearer ${token}`,
    },
    withCredentials: true,
  });
};

export const assignPasswordFolderPermissions = (folderId, request) => {
  const token = getAuthToken();
  return axios.post(`${PASSWORD_FOLDERS}/${folderId}/permissions`, request, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
    withCredentials: true,
  });
};

// Team Files
export const uploadTeamFiles = (formData) => {
  const token = getAuthToken();
  return axios.post(`${FILES}/upload`, formData, {
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "multipart/form-data",
    },
    withCredentials: true,
  });
};

export const getTeamFileById = (fileId) => {
  const token = getAuthToken();
  return axios.get(`${FILES}/${fileId}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
    withCredentials: true,
  });
};

export const downloadTeamFile = (fileId) => {
  const token = getAuthToken();
  return axios.get(`${FILES}/${fileId}/download`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
    withCredentials: true,
    responseType: "blob",
  });
};

export const deleteTeamFile = (fileId) => {
  const token = getAuthToken();
  return axios.delete(`${FILES}/${fileId}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
    withCredentials: true,
  });
};

export const moveTeamFileToFolder = (fileId, folderId) => {
  const token = getAuthToken();
  return axios.put(`${FILES}/${fileId}/move-folder`, null, {
    params: { folderId },
    headers: {
      Authorization: `Bearer ${token}`,
    },
    withCredentials: true,
  });
};

export const getTeamFilesByFolder = (folderId) => {
  const token = getAuthToken();
  return axios.get(`${FILES}/folder/${folderId}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
    withCredentials: true,
  });
};

// Team File Folders
export const createTeamFileFolder = (folder) => {
  const token = getAuthToken();
  return axios.post(FILE_FOLDERS, folder, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
    withCredentials: true,
  });
};

export const getTeamFileFolders = (teamId) => {
  const token = getAuthToken();
  return axios.get(`${FILE_FOLDERS}/team/${teamId}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
    withCredentials: true,
  });
};

export const renameTeamFileFolder = (folderId, newName) => {
  const token = getAuthToken();
  return axios.put(
    `${FILE_FOLDERS}/${folderId}/rename`,
    { newName },
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
      withCredentials: true,
    }
  );
};

export const deleteTeamFileFolder = (folderId, deleteItems = false) => {
  const token = getAuthToken();
  return axios.delete(`${FILE_FOLDERS}/${folderId}`, {
    params: { deleteItems },
    headers: {
      Authorization: `Bearer ${token}`,
    },
    withCredentials: true,
  });
};

export const assignFileFolderPermissions = (folderId, request) => {
  const token = getAuthToken();
  return axios.post(`${FILE_FOLDERS}/${folderId}/permissions`, request, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
    withCredentials: true,
  });
};

// Permissions
export const getItemPermissionsForRole = ({ roleId, itemId, itemType }) => {
  const token = getAuthToken();
  return axios.get(`${BASE_URL}/permissions/item`, {
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

export const getFolderPermissionsForRole = ({ roleId, folderId, folderType }) => {
  if (!roleId) {
    console.error("getFolderPermissionsForRole called with missing roleId");
    return Promise.reject("Missing roleId");
  }

  const token = getAuthToken();
  return axios.get(`${BASE_URL}/permissions/folder`, {
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

export const assignItemPermissions = (payload) => {
  const token = getAuthToken();
  return axios.post(`${BASE_URL}/permissions/item`, payload, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
    withCredentials: true,
  });
};

export const removeItemPermissions = (payload) => {
  const token = getAuthToken();
  return axios.delete(`${BASE_URL}/permissions/item`, {
    data: payload,
    headers: {
      Authorization: `Bearer ${token}`,
    },
    withCredentials: true,
  });
};
