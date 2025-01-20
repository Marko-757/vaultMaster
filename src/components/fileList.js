import React, { useState } from "react";
import "./fileList.css";

function FileList({ passwords, onSelectPassword, onAddPassword, onAddFile, onDeletePassword }) {
  const [showMenu, setShowMenu] = useState(false);

  const handleMenuClick = (option) => {
    setShowMenu(false);
    if (option === "password") {
      onAddPassword();
    } else if (option === "file") {
      onAddFile();
    }
  };

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
      <div className="add-button-container">
        <button
          className="add-button"
          onClick={() => setShowMenu(!showMenu)}
          title="Add"
        >
          +
        </button>
        {showMenu && (
          <div className="add-menu">
            <button onClick={() => handleMenuClick("password")}>Add Password</button>
            <button onClick={() => handleMenuClick("file")}>Upload File</button>
          </div>
        )}
      </div>
    </div>
  );
}

export default FileList;
