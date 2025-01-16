import React from "react";
import "./passwordInformation.css";

function PasswordInformation({ password }) {
  if (!password) {
    return <div className="password-info">No password selected.</div>;
  }

  return (
    <div className="password-info">
      <h3>Password Details</h3>
      <div className="info-field">
        <label>Account Name:</label>
        <span>{password.name}</span>
      </div>
      <div className="info-field">
        <label>Username:</label>
        <span>{password.username}</span>
      </div>
      <div className="info-field">
        <label>Password:</label>
        <span>{password.password}</span>
      </div>
      {password.website && (
        <div className="info-field">
          <label>Website:</label>
          <a href={password.website} target="_blank" rel="noopener noreferrer">
            {password.website}
          </a>
        </div>
      )}
    </div>
  );
}

export default PasswordInformation;
