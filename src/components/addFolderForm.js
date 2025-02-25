import React, { useState } from "react";
import "./addFolderForm.css";

function AddFolderForm({ formType, onSave, onCancel }) {
  const [folderName, setFolderName] = useState("");

  const handleSave = () => {
    if (folderName.trim() === "") {
      alert("Folder name cannot be empty.");
      return;
    }
    onSave(folderName);
  };

  return (
    <div className="add-folder-form">
      <h3>{formType === "password" ? "Create New Password Folder" : "Create New File Folder"}</h3>
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
