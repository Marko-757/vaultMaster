import React, { useState, useEffect } from "react";
import "./addPasswordForm.css"; // Reuse shared styling
import { createFileFolder } from "../api/personalFileService";

function AddFileFolderForm({ initialFolderName = "", onSave, onCancel }) {
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
        // Renaming (will be handled in parent)
        onSave({ folderName });
      } else {
        // Creating a new file folder
        const newFolder = await createFileFolder({ folderName });
        onSave(newFolder);
      }
    } catch (error) {
      console.error("Error saving file folder:", error);
      alert("Failed to save file folder.");
    }
  };

  return (
    <div className="add-password-form">
      <h3>
        {initialFolderName ? "Rename File Folder" : "Add New File Folder"}
      </h3>
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

export default AddFileFolderForm;
