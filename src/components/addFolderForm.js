import React, { useState } from "react";
import "./addPasswordForm.css";
import { createPasswordFolder } from "../api/personalPWService";

function AddFolderForm({ formType, onSave, onCancel }) {
  const [folderName, setFolderName] = useState("");

  const handleSave = async () => {
    if (!folderName.trim()) {
      alert("Please provide a folder name.");
      return;
    }

    try {
      const newFolder = await createPasswordFolder({ folderName });
      onSave(newFolder);
    } catch (error) {
      console.error("Error creating folder:", error);
      alert("Failed to create folder.");
    }
  };

  return (
    <div className="add-password-form">
      <h3>Add New {formType === "password" ? "Password" : "File"} Folder</h3>
      <input
        type="text"
        placeholder="Folder Name"
        value={folderName}
        onChange={(e) => setFolderName(e.target.value)}
      />
      <div className="form-buttons">
        <button onClick={handleSave}>Save</button>
        <button onClick={onCancel}>Cancel</button>
      </div>
    </div>
  );
}

export default AddFolderForm;
