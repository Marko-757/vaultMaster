import React, { useState } from "react";
import { FaEye, FaEyeSlash, FaRegCopy } from "react-icons/fa";
import "./teamPasswordInformation.css";
import { updateTeamPassword } from "../api/teamPWFileService";

function TeamPasswordInformation({
  password,
  decryptedPassword,
  showPassword,
  setShowPassword,
  canEdit,
}) {
  const [isEditing, setIsEditing] = useState(false);
  const [form, setForm] = useState({
    accountName: password.accountName,
    username: password.username,
    plaintextPassword: decryptedPassword,
    website: password.website,
  });

  const handleCopy = (text) => {
    navigator.clipboard
      .writeText(text)
      .then(() => alert("Password copied to clipboard!"))
      .catch(() => alert("Failed to copy password."));
  };

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSave = async () => {
    try {
      await updateTeamPassword(password.teamPasswordId, form);
      alert("Password updated!");
      setIsEditing(false);
      window.location.reload(); // Or use a smarter local state update
    } catch (err) {
      alert("Failed to update password.");
      console.error(err);
    }
  };

  const handleCancel = () => {
    setForm({
      accountName: password.accountName,
      username: password.username,
      plaintextPassword: decryptedPassword,
      website: password.website,
    });
    setIsEditing(false);
  };

  const displayedPassword = showPassword
    ? decryptedPassword || "[Unable to decrypt]"
    : "••••••••";

  return (
    <div className="team-password-info">
      {isEditing ? (
        <>
          <input
            name="accountName"
            value={form.accountName}
            onChange={handleChange}
            placeholder="Account Name"
          />
          <input
            name="username"
            value={form.username}
            onChange={handleChange}
            placeholder="Username"
          />
          <div className="password-row">
            <input
              name="plaintextPassword"
              value={form.plaintextPassword}
              onChange={handleChange}
              placeholder="Password"
              type={showPassword ? "text" : "password"}
            />
            <span
              className="icon-button"
              onClick={() => setShowPassword(!showPassword)}
              title={showPassword ? "Hide password" : "Show password"}
            >
              {showPassword ? <FaEyeSlash /> : <FaEye />}
            </span>
          </div>
          <input
            name="website"
            value={form.website}
            onChange={handleChange}
            placeholder="Website (optional)"
          />
          <div className="form-buttons">
            <button className="rename-save-button" onClick={handleSave}>
              Save
            </button>
            <button className="rename-cancel-button" onClick={handleCancel}>
              Cancel
            </button>
          </div>
        </>
      ) : (
        <>
          <h4>{password.accountName}</h4>
          <p>
            <strong>Username:</strong> {password.username}
          </p>
          <p className="password-row">
            <strong>Password:</strong> {displayedPassword}
            <span
              className="icon-button"
              onClick={() => setShowPassword(!showPassword)}
              title={showPassword ? "Hide password" : "Show password"}
            >
              {showPassword ? <FaEyeSlash /> : <FaEye />}
            </span>
            <span
              className="icon-button"
              onClick={() => handleCopy(decryptedPassword)}
              title="Copy password"
            >
              <FaRegCopy />
            </span>
          </p>
          <p>
            <strong>Website:</strong> {password.website || "—"}
          </p>
          {canEdit && (
            <button className="edit-button" onClick={() => setIsEditing(true)}>
              Edit Info
            </button>
          )}
        </>
      )}
    </div>
  );
}

TeamPasswordInformation.defaultProps = {
  canEdit: false,
};

export default TeamPasswordInformation;
