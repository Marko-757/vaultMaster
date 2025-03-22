import React from "react";
import "./passwordInformation.css";

function PasswordInformation({ password, decryptedPassword, showPassword, setShowPassword }) {
  if (!password) return null;

  const maskedPassword = "•••••••••";
  const displayedPassword = showPassword
    ? decryptedPassword || "[Unable to decrypt]"
    : maskedPassword;

  return (
    <div className="password-info">
      <h3>{password.accountName}</h3>
      <p><strong>Username:</strong> {password.username}</p>
      <p>
        <strong>Password:</strong> {displayedPassword}
        <button
          onClick={() => setShowPassword(!showPassword)}
          style={{ marginLeft: "10px" }}
        >
          {showPassword ? "Hide" : "Show"}
        </button>
      </p>
      <p><strong>Website:</strong> {password.website || "—"}</p>
    </div>
  );
}

export default PasswordInformation;
