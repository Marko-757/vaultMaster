const BASE_URL = "http://localhost:8080/api/passwords/personal";

export async function createPasswordEntry(data) {
    const response = await fetch(BASE_URL, {
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
    const response = await fetch(`${BASE_URL}/entry/${entryId}`, {
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

  export const createPasswordFolder = async ({ folderName }) => {
    const response = await fetch(`${BASE_URL}/folder`, {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({ folderName }),
    });
  
    if (!response.ok) {
      throw new Error("Failed to create folder");
    }
  
    return await response.json(); 
  };

  export const renamePasswordFolder = async (folderId, folderName) => {
    const response = await fetch(`${BASE_URL}/folder/${folderId}`, {
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
  
    
  export const getDecryptedPassword = async (entryId) => {
    const response = await fetch(`http://localhost:8080/api/passwords/personal/entry/${entryId}/decrypt`, {
      method: "GET",
      credentials: "include",
    });
  
    if (!response.ok) {
      throw new Error("Failed to fetch decrypted password");
    }
  
    return await response.text(); 
  };
  
  
  export async function getAllPasswords() {
    const response = await fetch(`${BASE_URL}/me/passwords`, {
      credentials: "include",
    });
    if (!response.ok) throw new Error("Failed to fetch passwords");
    return await response.json();
  }

  export const deletePasswordFolder = async (folderId) => {
    const response = await fetch(`${BASE_URL}/folder/${folderId}`, {
      method: "DELETE",
      credentials: "include",
    });
  
    if (!response.ok) {
      throw new Error("Failed to delete folder");
    }
  
    return await response.text();
  };
  

  export async function getAllPasswordFolders() {
    const response = await fetch(`${BASE_URL}/folder/me/folders`, {
      credentials: "include",
    });
    if (!response.ok) throw new Error("Failed to fetch folders");
    return await response.json();
  }
  
  