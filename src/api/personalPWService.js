const BASE_PASSWORD_URL = "http://localhost:8080/api/passwords/personal";
const BASE_FOLDER_URL = "http://localhost:8080/api/folders/personal";

export async function createPasswordEntry(data) {
  const response = await fetch(BASE_PASSWORD_URL, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    credentials: "include",
    body: JSON.stringify(data),
  });

  if (!response.ok) {
    const contentType = response.headers.get("Content-Type");
    const errorText = contentType?.includes("application/json")
      ? await response.json()
      : await response.text();
    throw new Error(errorText || "Failed to create password entry");
  }

  return await response.json();
}

export async function updatePasswordEntry(entryId, data) {
  const response = await fetch(`${BASE_PASSWORD_URL}/entry/${entryId}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify(data),
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(errorText || "Failed to update password entry");
  }
  return await response.text();
}

export const deletePassword = async (entryId) => {
  const response = await fetch(`${BASE_PASSWORD_URL}/entry/${entryId}`, {
    method: "DELETE",
    credentials: "include",
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(errorText || "Failed to delete password");
  }

  return await response.text();
};

export const getDecryptedPassword = async (entryId) => {
  const response = await fetch(
    `${BASE_PASSWORD_URL}/entry/${entryId}/decrypt`,
    {
      method: "GET",
      credentials: "include",
    }
  );

  if (!response.ok) {
    throw new Error("Failed to fetch decrypted password");
  }

  return await response.text();
};

export async function getAllPasswords() {
  const response = await fetch(`${BASE_PASSWORD_URL}/me/passwords`, {
    credentials: "include", 
  });

  if (!response.ok) throw new Error("Failed to fetch passwords");

  return await response.json();
}

export async function getPasswordsInFolder(folderId) {
  const response = await fetch(`${BASE_PASSWORD_URL}/folder/${folderId}`, {
    credentials: "include",
  });

  if (!response.ok) {
    throw new Error("Failed to fetch passwords in folder");
  }

  return await response.json();
}

export const createPasswordFolder = async ({ folderName }) => {
  const response = await fetch(`${BASE_FOLDER_URL}`, {
    method: "POST",
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ folderName }),
  });

  if (!response.ok) {
    throw new Error("Failed to create folder");
  }

  return await response.json();
};

export const getAllPasswordFolders = async () => {
  const response = await fetch(`${BASE_FOLDER_URL}/me`, {
    credentials: "include",
  });

  if (!response.ok) throw new Error("Failed to fetch folders");

  return await response.json();
};

export const renamePasswordFolder = async (folderId, folderName) => {
  const response = await fetch(`${BASE_FOLDER_URL}/${folderId}`, {
    method: "PUT",
    credentials: "include",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ folderName }),
  });

  if (!response.ok) {
    throw new Error("Failed to rename folder");
  }

  return await response.text();
};

export const deletePasswordFolder = async (folderId) => {
  const response = await fetch(`${BASE_FOLDER_URL}/${folderId}`, {
    method: "DELETE",
    credentials: "include",
  });

  if (!response.ok) {
    throw new Error("Failed to delete folder");
  }

  return await response.text();
};
