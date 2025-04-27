import React, { useState } from "react";
import { createTeamPassword } from "../api/teamPWFileService";
import "./addTeamPasswordForm.css";

const AddTeamPasswordForm = ({ teamId, folderId, onSuccess }) => {
  const [accountName, setAccountName] = useState("");
  const [username, setUsername] = useState("");
  const [passwordValue, setPasswordValue] = useState("");
  const [url, setUrl] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await createTeamPassword({
        teamId,
        folderId,
        accountName,
        username,
        password: passwordValue,
        url,
      });
      alert("Password saved successfully!");
      setAccountName("");
      setUsername("");
      setPasswordValue("");
      setUrl("");
      if (onSuccess) onSuccess();
    } catch (err) {
      console.error("Failed to save password", err);
      alert("Failed to save password. Please try again.");
    }
  };

  return (
    <div className="add-password-form">
      <h2>Add New Password</h2>
      <form onSubmit={handleSubmit}>
        <label>Account Name:</label>
        <input
          type="text"
          value={accountName}
          onChange={(e) => setAccountName(e.target.value)}
          placeholder="Enter account name"
          required
        />

        <label>Username:</label>
        <input
          type="text"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          placeholder="Enter username"
          required
        />

        <label>Password:</label>
        <input
          type="text"
          value={passwordValue}
          onChange={(e) => setPasswordValue(e.target.value)}
          placeholder="Enter password"
          required
        />

        <label>URL (optional):</label>
        <input
          type="text"
          value={url}
          onChange={(e) => setUrl(e.target.value)}
          placeholder="Enter website URL"
        />

        <button type="submit" className="save-password-button">
          Save Password
        </button>
      </form>
    </div>
  );
};

export default AddTeamPasswordForm;
