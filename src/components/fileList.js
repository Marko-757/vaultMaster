import React from "react";
import "./fileList.css";

function FileList({ passwords, onSelectPassword, onAddPassword, onDeletePassword }) {
  return (
    <div className="file-list">
      {passwords.length === 0 ? (
        <div className="empty-message">No passwords available. Add one using the "+" button.</div>
      ) : (
        passwords.map((password) => (
          <div key={password.id} className="password-item">
            <span onClick={() => onSelectPassword(password)}>{password.name}</span>
            <button
              className="delete-button"
              onClick={() => onDeletePassword(password.id)}
              title="Delete Password"
            >
              🗑️
            </button>
          </div>
        ))
      )}
      <button className="add-button" onClick={onAddPassword}>
        +
      </button>
    </div>
  );
}

export default FileList;
