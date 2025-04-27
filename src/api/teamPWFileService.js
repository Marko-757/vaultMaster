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
  return axios.post("http://localhost:8080/api/team/passwords", data, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
    withCredentials: true,
  });
};

export const updateTeamPassword = (passwordId, data) =>
  axios.put(`${PASSWORDS}/${passwordId}`, data);

export const deleteTeamPassword = (passwordId) =>
  axios.delete(`${PASSWORDS}/${passwordId}`);

export const getTeamPasswords = (teamId) =>
  axios.get(`${PASSWORDS}/team/${teamId}`);

export const moveTeamPasswordToFolder = (teamPasswordId, folderId) =>
  axios.put(`${PASSWORDS}/${teamPasswordId}/move`, { folderId });

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
export const createTeamPasswordFolder = (teamId, folderName) =>
  axios.post(PASSWORD_FOLDERS, { teamId, folderName });

export const getTeamPasswordFolders = (teamId) => {
  const token = getAuthToken();
  return axios.get(`http://localhost:8080/api/team/folders/${teamId}`, {
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

export const deleteTeamPasswordFolder = (folderId, deleteItems = false) =>
  axios.delete(`${PASSWORD_FOLDERS}/${folderId}`, { params: { deleteItems } });

export const assignPasswordFolderPermissions = (folderId, request) =>
  axios.post(`${PASSWORD_FOLDERS}/${folderId}/permissions`, request);

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// Team Files
export const uploadTeamFiles = (formData) =>
  axios.post(`${FILES}/upload`, formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });

export const getTeamFileById = (fileId) => axios.get(`${FILES}/${fileId}`);

export const downloadTeamFile = (fileId) =>
  axios.get(`${FILES}/${fileId}/download`, { responseType: "blob" });

export const deleteTeamFile = (fileId) => axios.delete(`${FILES}/${fileId}`);

export const moveTeamFileToFolder = (fileId, folderId) =>
  axios.put(`${FILES}/${fileId}/move-folder`, null, { params: { folderId } });

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
export const createTeamFileFolder = (folder) =>
  axios.post(FILE_FOLDERS, folder);

export const getTeamFileFolders = (teamId) => {
  const token = getAuthToken();
  return axios.get(
    `http://localhost:8080/api/team-file-folders/team/${teamId}`,
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
      withCredentials: true,
    }
  );
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

export const deleteTeamFileFolder = (folderId, deleteItems = false) =>
  axios.delete(`${FILE_FOLDERS}/${folderId}`, { params: { deleteItems } });

export const assignFileFolderPermissions = (folderId, request) =>
  axios.post(`${FILE_FOLDERS}/${folderId}/permissions`, request);

///////////////////////////////////////////////////////////////////////////////////////

export const getItemPermissionsForRole = ({ roleId, itemId, itemType }) => {
  const token = localStorage.getItem("jwtToken");
  return axios.get("http://localhost:8080/api/permissions/item", {
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
  
    const token = localStorage.getItem("jwtToken");
  
    return axios.get("http://localhost:8080/api/permissions/folder", {
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
  const token = localStorage.getItem("jwtToken");
  return axios.post("http://localhost:8080/api/permissions/item", payload, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
    withCredentials: true,
  });
};

export const removeItemPermissions = (payload) => {
  const token = localStorage.getItem("jwtToken");
  return axios.delete("http://localhost:8080/api/permissions/item", {
    data: payload,
    headers: {
      Authorization: `Bearer ${token}`,
    },
    withCredentials: true,
  });
};
