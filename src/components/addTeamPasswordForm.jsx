import React, { useEffect, useState } from "react";
import { createTeamPassword, getTeamPasswordFolders } from "../api/teamPWFileService";
import "./addTeamPasswordForm.css";

const AddTeamPasswordForm = ({ teamId, folderId: initialFolderId, onSuccess }) => {
  const [accountName, setAccountName] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [website, setWebsite] = useState("");
  const [selectedFolderId, setSelectedFolderId] = useState(initialFolderId || "");
  const [passwordFolders, setPasswordFolders] = useState([]);

  useEffect(() => {
    async function fetchFolders() {
      try {
        const res = await getTeamPasswordFolders(teamId);
        setPasswordFolders(res.data);
      } catch (err) {
        console.error("Failed to load folders:", err);
      }
    }
    fetchFolders();
  }, [teamId]);

  const generateStrongPassword = () => {
    const strongPassword = Math.random().toString(36).slice(-10) + "!A1";
    setPassword(strongPassword);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await createTeamPassword({
        teamId,
        folderId: selectedFolderId || null,
        accountName,
        username,
        plaintextPassword: password, // ✅ IMPORTANT
        website,
      });
      onSuccess();
    } catch (err) {
      console.error("Failed to save password:", err);
      alert("Failed to save password");
    }
  };

  return (
    <div className="add-password-form-container">
      <h2 className="form-title">Add New Password</h2>
      <form className="add-password-form" onSubmit={handleSubmit}>
        <input
          type="text"
          placeholder="Account Name (e.g., GitHub)"
          value={accountName}
          onChange={(e) => setAccountName(e.target.value)}
          required
        />
        <input
          type="text"
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
        />
        <input
          type="text"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <button
          type="button"
          className="generate-password-button"
          onClick={generateStrongPassword}
        >
          Generate Strong Password
        </button>
        <input
          type="text"
          placeholder="Website (optional)"
          value={website}
          onChange={(e) => setWebsite(e.target.value)}
        />
        <select
          value={selectedFolderId}
          onChange={(e) => setSelectedFolderId(e.target.value)}
        >
          <option value="">No Folder</option>
          {passwordFolders.map((folder) => (
            <option key={folder.folderId} value={folder.folderId}>
              {folder.folderName}
            </option>
          ))}
        </select>
        <div className="form-buttons">
          <button type="submit" className="save-button">
            Save Password
          </button>
          <button
            type="button"
            className="cancel-button"
            onClick={() => onSuccess()}
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  );
};

export default AddTeamPasswordForm;
