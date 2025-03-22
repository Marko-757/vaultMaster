import React, { useState } from "react";
import PasswordGenerator from "./passwordGenerator";
import "./addPasswordForm.css";
import { createPasswordEntry } from "../api/personalPWService";

function AddPasswordForm({ folders = [], selectedFolder, onSave, onCancel }) {
  const [formData, setFormData] = useState({
    accountName: "",
    username: "",
    passwordHash: "",
    website: "",
    folderId: selectedFolder?.folderId || "",
  });

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSave = async () => {
    if (!formData.accountName || !formData.username || !formData.passwordHash) {
      alert("Please fill in all required fields.");
      return;
    }
  
    const dataToSubmit = {
      ...formData,
      website: formData.website?.trim() === "" ? null : formData.website,
    };
  
    try {
      console.log("📤 Submitting password entry:", dataToSubmit);
      const createdEntry = await createPasswordEntry(dataToSubmit); // <- this calls the function above
      onSave(createdEntry);
    } catch (error) {
      console.error("🚫 Error creating password:", error.message);
      alert("Failed to create password.");
    }
  };
  
  


  return (
    <div className="add-password-form">
      <h3>Add New Password</h3>
      <input
        type="text"
        name="accountName"
        placeholder="Account Name (e.g., GitHub)"
        value={formData.accountName}
        onChange={handleInputChange}
      />
      <input
        type="text"
        name="username"
        placeholder="Username"
        value={formData.username}
        onChange={handleInputChange}
      />
      <input
        type="text"
        name="passwordHash"
        placeholder="Password"
        value={formData.passwordHash}
        onChange={handleInputChange}
      />
      <input
        type="text"
        name="website"
        placeholder="Website (optional)"
        value={formData.website}
        onChange={handleInputChange}
      />
      <select
        name="folderId"
        value={formData.folderId || ""}
        onChange={(e) => setFormData({ ...formData, folderId: e.target.value || null })}
      >
        <option value="">No Folder</option>
        {folders.map((folder) => (
          <option key={folder.folderId} value={folder.folderId}>
            {folder.folderName}
          </option>
        ))}
      </select>
      <PasswordGenerator onGenerate={(password) => setFormData({ ...formData, passwordHash: password })} />
      <div className="form-buttons">
        <button onClick={handleSave}>Save</button>
        <button onClick={onCancel}>Cancel</button>
      </div>
    </div>
  );
}

export default AddPasswordForm;
