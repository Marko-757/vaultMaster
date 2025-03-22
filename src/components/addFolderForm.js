import React, { useState, useEffect } from "react";
import "./addPasswordForm.css";
import { createPasswordFolder } from "../api/personalPWService";

function AddFolderForm({ formType, initialFolderName = "", onSave, onCancel }) {
  const [folderName, setFolderName] = useState(initialFolderName);

  useEffect(() => {
    setFolderName(initialFolderName);
  }, [initialFolderName]);

  const handleSave = async () => {
    if (!folderName.trim()) {
      alert("Please provide a folder name.");
      return;
    }

    try {
      if (initialFolderName) {
        // Renaming mode
        onSave({ folderName });
      } else {
        // Creating mode
        const newFolder = await createPasswordFolder({ folderName });
        onSave(newFolder);
      }
    } catch (error) {
      console.error("Error processing folder:", error);
      alert("Failed to save folder.");
    }
  };

  return (
    <div className="add-password-form">
      <h3>{initialFolderName ? "Rename Folder" : `Add New ${formType === "password" ? "Password" : "File"} Folder`}</h3>
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
