import axios from "axios";

// Base URLs
const BASE_FILE_URL = "http://localhost:8080/api/files";
const BASE_FOLDER_URL = "http://localhost:8080/api/files/folders";

// Upload one or more files (optional folderId)
export const uploadFiles = async (files, folderId = null) => {
  const formData = new FormData();
  files.forEach((file) => formData.append("files", file));
  if (folderId) formData.append("folderId", folderId);

  const response = await axios.post(`${BASE_FILE_URL}/upload`, formData, {
    headers: { "Content-Type": "multipart/form-data" },
    withCredentials: true,
  });

  return response.data;
};


// Get all personal files
export const getAllFiles = async () => {
  const response = await axios.get(`${BASE_FILE_URL}/all`, {
    withCredentials: true,
  });
  return response.data;
};

// Get files by folder ID
export const getFilesByFolder = async (folderId) => {
  const response = await axios.get(`${BASE_FILE_URL}/folder/${folderId}`, {
    withCredentials: true,
  });
  return response.data;
};

// Get metadata for a file by ID
export const getFileById = async (fileId) => {
  const response = await axios.get(`${BASE_FILE_URL}/${fileId}`, {
    withCredentials: true,
  });
  return response.data;
};

// Download a file by its S3 key
export const downloadFile = async (key) => {
  const response = await axios.get(`${BASE_FILE_URL}/download/${key}`, {
    responseType: "blob",
    withCredentials: true,
  });
  return response.data;
};

// Delete a file by its file ID
export const deleteFile = async (fileId) => {
  const response = await axios.delete(`${BASE_FILE_URL}/delete/${fileId}`, {
    withCredentials: true,
  });
  return response.data;
};


export const createFileFolder = async ({ folderName }) => {
    const response = await axios.post(
      `${BASE_FOLDER_URL}`,
      { folderName },
      { withCredentials: true }
    );
    return response.data;
  };
  
  export const getAllFileFolders = async () => {
    const response = await axios.get(`${BASE_FOLDER_URL}`, {
      withCredentials: true,
    });
    return response.data;
  };
  
