import React from "react";
import { FaEye, FaEyeSlash, FaRegCopy } from "react-icons/fa";
import "./passwordInformation.css";

function PasswordInformation({
  password,
  decryptedPassword,
  showPassword,
  setShowPassword,
}) {
  if (!password) return null;

  const maskedPassword = "•••••••••";
  const displayedPassword = showPassword
    ? decryptedPassword || "[Unable to decrypt]"
    : maskedPassword;

  const handleCopy = (text) => {
    navigator.clipboard
      .writeText(text)
      .then(() => alert("Password copied to clipboard!"))
      .catch(() => alert("Failed to copy password."));
  };

  return (
    <div className="password-info">
      <h3>{password.accountName}</h3>
      <p>
        <strong>Username:</strong> {password.username}
      </p>
      <p style={{ display: "flex", alignItems: "center", gap: "10px" }}>
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
    </div>
  );
}

export default PasswordInformation;
