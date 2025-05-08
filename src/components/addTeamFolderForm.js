import React, { useState } from "react";
import {
  createTeamPasswordFolder,
  createTeamFileFolder,
} from "../api/teamPWFileService";
import "./addPasswordForm.css"; 

const AddTeamFolderForm = ({ teamId, folderType, onCreate, onCancel }) => {
  const [folderName, setFolderName] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!folderName.trim()) {
      alert("Folder name cannot be empty.");
      return;
    }

    try {
      if (folderType === "password") {
        await createTeamPasswordFolder(teamId, folderName);
      } else {
        await createTeamFileFolder({ teamId, folderName });
      }

      setFolderName("");

      if (onCreate) {
        await onCreate();
      }
    } catch (err) {
      console.error("Failed to create folder:", err);
      alert("Folder creation failed.");
    }
  };

  return (
    <form onSubmit={handleSubmit} className="add-password-form">
      <h3>Add New {folderType === "password" ? "Password" : "File"} Folder</h3>
      <input
        type="text"
        value={folderName}
        onChange={(e) => setFolderName(e.target.value)}
        placeholder="Folder Name"
        required
      />
      <div className="form-buttons">
        <button type="submit">Save</button>
        <button type="button" onClick={onCancel}>
          Cancel
        </button>
      </div>
    </form>
  );
};

export default AddTeamFolderForm;
