import React, { useState } from "react";
import PasswordInformation from "./passwordInformation";
import "./fileList.css";

const FileList = ({ passwords, onDeletePassword, onUpdatePassword, onAddPassword }) => {
  const [expandedIds, setExpandedIds] = useState({});
  const [editingId, setEditingId] = useState(null);
  const [editData, setEditData] = useState({});
  // Instead of storing just an id, we store the entire password object for deletion
  const [passwordToDelete, setPasswordToDelete] = useState(null);

  const toggleExpand = (id) => {
    setExpandedIds((prev) => ({ ...prev, [id]: !prev[id] }));
  };

  const startEditing = (password) => {
    setEditingId(password.id);
    setEditData(password);
  };

  const cancelEditing = () => {
    setEditingId(null);
    setEditData({});
  };

  const saveEditing = () => {
    onUpdatePassword(editData);
    setEditingId(null);
    setEditData({});
  };

  const confirmDelete = () => {
    if (passwordToDelete) {
      onDeletePassword(passwordToDelete.id);
      setPasswordToDelete(null);
    }
  };

  return (
    <div className="password-list">
      {passwords.length === 0 ? (
        <p className="empty-message">No passwords available.</p>
      ) : (
        passwords.map((password) => (
          <div key={password.id} className="password-item">
            <div className="password-row">
              <span className="password-name">{password.name}</span>
              <button className="expand-button" onClick={() => toggleExpand(password.id)}>
                {expandedIds[password.id] ? "▲" : "▼"}
              </button>
              {expandedIds[password.id] && editingId !== password.id && (
                <button className="edit-button" onClick={() => startEditing(password)}>
                  Edit
                </button>
              )}
              <button className="delete-button" onClick={() => setPasswordToDelete(password)}>
                X
              </button>
            </div>
            {expandedIds[password.id] && (
              <div className="password-details">
                {editingId === password.id ? (
                  <div className="edit-form">
                    <div className="form-group">
                      <label>Account Name:</label>
                      <input
                        type="text"
                        value={editData.name || ""}
                        onChange={(e) => setEditData({ ...editData, name: e.target.value })}
                      />
                    </div>
                    <div className="form-group">
                      <label>Username:</label>
                      <input
                        type="text"
                        value={editData.username || ""}
                        onChange={(e) => setEditData({ ...editData, username: e.target.value })}
                      />
                    </div>
                    <div className="form-group">
                      <label>Password:</label>
                      <input
                        type="text"
                        value={editData.password || ""}
                        onChange={(e) => setEditData({ ...editData, password: e.target.value })}
                      />
                    </div>
                    <div className="form-group">
                      <label>Website:</label>
                      <input
                        type="text"
                        value={editData.website || ""}
                        onChange={(e) => setEditData({ ...editData, website: e.target.value })}
                      />
                    </div>
                    <button className="save-button" onClick={saveEditing}>Save</button>
                    <button className="cancel-button" onClick={cancelEditing}>Cancel</button>
                  </div>
                ) : (
                  <PasswordInformation password={password} />
                )}
              </div>
            )}
          </div>
        ))
      )}
      <button className="add-password-button" onClick={onAddPassword}>
        Add Password
      </button>

      {passwordToDelete && (
        <div className="delete-modal">
          <div className="delete-modal-content">
            <p>Are you sure you want to delete "{passwordToDelete.name}"?</p>
            <div className="delete-modal-buttons">
              <button className="confirm-delete-button" onClick={confirmDelete}>Confirm</button>
              <button className="cancel-delete-button" onClick={() => setPasswordToDelete(null)}>Cancel</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default FileList;
