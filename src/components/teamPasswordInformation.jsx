import React from "react";
import { FaEye, FaEyeSlash, FaRegCopy } from "react-icons/fa";
import "./teamPasswordInformation.css";

function TeamPasswordInformation({
  password,
  decryptedPassword,
  showPassword,
  setShowPassword,
  canEdit,
  onEditClick,
}) {
  const handleCopy = (text) => {
    navigator.clipboard
      .writeText(text)
      .then(() => alert("Password copied to clipboard!"))
      .catch(() => alert("Failed to copy password."));
  };

  const displayedPassword = showPassword
    ? decryptedPassword || "[Unable to decrypt]"
    : "••••••••";

  return (
    <div className="team-password-info">
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
        <div className="file-actions">
          <button className="file-action-button" onClick={onEditClick}>
            Edit
          </button>
          <button className="file-action-button delete">
            Delete
          </button>
        </div>
      )}
    </div>
  );
}

TeamPasswordInformation.defaultProps = {
  canEdit: false,
};

export default TeamPasswordInformation;
