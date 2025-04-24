import React, { useState } from "react";
import { createTeamPassword } from "../api/teamPWFileService";
import "./addTeamPasswordForm.css";

const AddTeamPasswordForm = ({ teamId, folderId, onSuccess }) => {
  const [form, setForm] = useState({
    accountName: "",
    username: "",
    plaintextPassword: "",
    website: "",
  });

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        ...form,
        teamId,
        folderId,
      };
      await createTeamPassword(payload);
      onSuccess();
    } catch (err) {
      alert("Failed to add password.");
      console.error(err);
    }
  };

  return (
    <form className="add-team-password-form" onSubmit={handleSubmit}>
      <input
        name="accountName"
        value={form.accountName}
        onChange={handleChange}
        placeholder="Account Name"
        required
      />
      <input
        name="username"
        value={form.username}
        onChange={handleChange}
        placeholder="Username"
        required
      />
      <input
        name="plaintextPassword"
        value={form.plaintextPassword}
        onChange={handleChange}
        placeholder="Password"
        required
        type="password"
      />
      <input
        name="website"
        value={form.website}
        onChange={handleChange}
        placeholder="Website (optional)"
      />

      <div className="form-buttons">
        <button type="submit">Add Password</button>
        <button type="button" className="cancel-button" onClick={onSuccess}>
          Cancel
        </button>
      </div>
    </form>
  );
};

export default AddTeamPasswordForm;
