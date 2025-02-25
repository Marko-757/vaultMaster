import React, { useState } from "react";
import FileList from "../components/fileList";
import PasswordInformation from "../components/passwordInformation";
import AddPasswordForm from "../components/addPasswordForm";
import AddFolderForm from "../components/addFolderForm";
import "./personal_pw_manager.css";
import { useNavigate } from "react-router";

function PersonalPwManager() {
  // Dummy password data
  const [passwords, setPasswords] = useState([
    { id: 1, name: "Google Account", username: "user@gmail.com", password: "password123", website: "https://google.com", folderId: 1 },
    { id: 2, name: "GitHub", username: "devuser", password: "devpassword", website: "https://github.com", folderId: 1 },
  ]);
  const [files, setFiles] = useState([]);

  // Dummy folder data: two items each by default
  const [passwordFolders, setPasswordFolders] = useState([
    { id: 1, name: "Personal Passwords" },
    { id: 2, name: "Work Passwords" },
  ]);
  const [fileFolders, setFileFolders] = useState([
    { id: 1, name: "Personal Files" },
    { id: 2, name: "Work Files" },
  ]);

  // Selections
  const [selectedPasswordFolder, setSelectedPasswordFolder] = useState(null);
  const [selectedFileFolder, setSelectedFileFolder] = useState(null);
  const [selectedPassword, setSelectedPassword] = useState(null);

  // Adding forms states
  const [isAddingPassword, setIsAddingPassword] = useState(false);
  const [isAddingPasswordFolder, setIsAddingPasswordFolder] = useState(false);
  const [isAddingFileFolder, setIsAddingFileFolder] = useState(false);

  // "See More"/"See Less" states for folder lists (preview count = 2)
  const previewCount = 2;
  const [expandedPasswordFolders, setExpandedPasswordFolders] = useState(false);
  const [expandedFileFolders, setExpandedFileFolders] = useState(false);
  const displayedPasswordFolders =
    passwordFolders.length > previewCount
      ? expandedPasswordFolders
        ? passwordFolders
        : passwordFolders.slice(0, previewCount)
      : passwordFolders;
  const displayedFileFolders =
    fileFolders.length > previewCount
      ? expandedFileFolders
        ? fileFolders
        : fileFolders.slice(0, previewCount)
      : fileFolders;

  // Filter passwords by selected folder
  const filteredPasswords = selectedPasswordFolder
    ? passwords.filter((p) => p.folderId === selectedPasswordFolder.id)
    : passwords;

  // Folder add and delete functions
  const deletePasswordFolder = (folderId) => {
    if (window.confirm("Are you sure you want to delete this folder?")) {
      const updatedFolders = passwordFolders.filter((folder) => folder.id !== folderId);
      setPasswordFolders(updatedFolders);
      const updatedPasswords = passwords.map((p) =>
        p.folderId === folderId ? { ...p, folderId: null } : p
      );
      setPasswords(updatedPasswords);
    }
  };

  const deleteFileFolder = (folderId) => {
    if (window.confirm("Are you sure you want to delete this folder?")) {
      const updatedFolders = fileFolders.filter((folder) => folder.id !== folderId);
      setFileFolders(updatedFolders);
      const updatedFiles = files.filter((file) => file.folderId !== folderId);
      setFiles(updatedFiles);
    }
  };

  // Add functions
  const addPassword = (newPassword) => {
    setPasswords([
      ...passwords,
      { id: passwords.length + 1, folderId: selectedPasswordFolder?.id || null, ...newPassword },
    ]);
    setIsAddingPassword(false);
  };

  const addPasswordFolder = (folderName) => {
    setPasswordFolders([...passwordFolders, { id: passwordFolders.length + 1, name: folderName }]);
    setIsAddingPasswordFolder(false);
  };

  const addFileFolder = (folderName) => {
    setFileFolders([...fileFolders, { id: fileFolders.length + 1, name: folderName }]);
    setIsAddingFileFolder(false);
  };

  // Delete Confirmation Modal for Passwords
  const [passwordToDelete, setPasswordToDelete] = useState(null);
  const confirmDeletePassword = () => {
    if (passwordToDelete) {
      setPasswords(passwords.filter((p) => p.id !== passwordToDelete.id));
      if (selectedPassword && selectedPassword.id === passwordToDelete.id) {
        setSelectedPassword(null);
      }
      setPasswordToDelete(null);
    }
  };

  // Inline editing for password details in right column
  const [isEditing, setIsEditing] = useState(false);
  const [editData, setEditData] = useState({});
  const startEditing = () => {
    setIsEditing(true);
    setEditData(selectedPassword);
  };
  const cancelEditing = () => {
    setIsEditing(false);
    setEditData({});
  };
  const saveEditing = () => {
    setPasswords(passwords.map((p) => (p.id === editData.id ? editData : p)));
    setSelectedPassword(editData);
    setIsEditing(false);
    setEditData({});
  };

  const navigate = useNavigate();

  return (
    <div className="pw-manager-container">
      <div className="three-column-container">
        {/* Left Column: Folders */}
        <div className="left-column">
          <div className="sidebar-heading">
            <div className="sidebar-heading-top">Personal</div>
            <div className="sidebar-heading-bottom">Passwords & Files</div>
          </div>
          <hr className="divider" />
          {/* Password Folders Section */}
          <div className="sidebar-section">
            <div className="section-header">
              <h2>Passwords</h2>
              <button className="section-add-button" onClick={() => setIsAddingPasswordFolder(true)}>+</button>
            </div>
            <div className="folders-list-container scrollable">
              {displayedPasswordFolders.map((folder) => (
                <div key={folder.id} className="folder-row">
                  <button className="sidebar-item-button" onClick={() => setSelectedPasswordFolder(folder)}>
                    <span className="team-name">{folder.name}</span>
                  </button>
                  <button className="folder-delete-button" onClick={() => deletePasswordFolder(folder.id)}>X</button>
                </div>
              ))}
            </div>
            {passwordFolders.length > previewCount && (
              <button className="see-toggle-button" onClick={() => setExpandedPasswordFolders(!expandedPasswordFolders)}>
                {expandedPasswordFolders ? "See Less" : "See More"}
              </button>
            )}
          </div>
          <hr className="divider" />
          {/* File Folders Section */}
          <div className="sidebar-section">
            <div className="section-header">
              <h2>Files</h2>
              <button className="section-add-button" onClick={() => setIsAddingFileFolder(true)}>+</button>
            </div>
            <div className="folders-list-container scrollable">
              {displayedFileFolders.map((folder) => (
                <div key={folder.id} className="folder-row">
                  <button className="sidebar-item-button" onClick={() => setSelectedFileFolder(folder)}>
                    <span className="team-name">{folder.name}</span>
                  </button>
                  <button className="folder-delete-button" onClick={() => deleteFileFolder(folder.id)}>X</button>
                </div>
              ))}
            </div>
            {fileFolders.length > previewCount && (
              <button className="see-toggle-button" onClick={() => setExpandedFileFolders(!expandedFileFolders)}>
                {expandedFileFolders ? "See Less" : "See More"}
              </button>
            )}
          </div>
        </div>

        {/* Middle Column: Password Names List */}
        <div className="middle-column">
          <h2 className="column-heading">Passwords</h2>
          <hr className="middle-divider" />
          <div className="password-name-list">
            {filteredPasswords.length === 0 ? (
              <div className="no-passwords">No Passwords Found</div>
            ) : (
              filteredPasswords.map((password) => (
                <button
                  key={password.id}
                  className="password-name-button"
                  onClick={() => { setSelectedPassword(password); setIsEditing(false); }}
                >
                  {password.name}
                </button>
              ))
            )}
          </div>
          <button className="add-password-button" onClick={() => setIsAddingPassword(true)}>
            Add Password
          </button>
        </div>

        {/* Right Column: Password Details / Add/Edit Form */}
        <div className="right-column">
          {isAddingPassword ? (
            <div className="password-detail-view">
              <AddPasswordForm
                folders={passwordFolders}
                selectedFolder={selectedPasswordFolder}
                onSave={addPassword}
                onCancel={() => setIsAddingPassword(false)}
              />
            </div>
          ) : isAddingPasswordFolder ? (
            <div className="password-detail-view">
              <AddFolderForm
                formType="password"
                onSave={addPasswordFolder}
                onCancel={() => setIsAddingPasswordFolder(false)}
              />
            </div>
          ) : isAddingFileFolder ? (
            <div className="password-detail-view">
              <AddFolderForm
                formType="file"
                onSave={addFileFolder}
                onCancel={() => setIsAddingFileFolder(false)}
              />
            </div>
          ) : selectedPassword ? (
            isEditing ? (
              <div className="edit-form-container">
                <h2>Edit Password</h2>
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
                <div className="edit-actions">
                  <button className="save-button" onClick={saveEditing}>Save</button>
                  <button className="cancel-button" onClick={cancelEditing}>Cancel</button>
                </div>
              </div>
            ) : (
              <div className="password-detail-view">
                <button className="close-detail-button" onClick={() => setSelectedPassword(null)}>X</button>
                <PasswordInformation password={selectedPassword} />
                <div className="password-actions">
                  <button className="edit-password-button" onClick={startEditing}>Edit</button>
                  <button className="delete-password-button" onClick={() => setPasswordToDelete(selectedPassword)}>
                    Delete
                  </button>
                </div>
              </div>
            )
          ) : (
            <div className="no-password-selected">No Password Selected</div>
          )}
        </div>
      </div>

      {/* Delete Confirmation Modal */}
      {passwordToDelete && (
        <div className="delete-modal">
          <div className="delete-modal-content">
            <p>Are you sure you want to delete "{passwordToDelete.name}"?</p>
            <div className="delete-modal-buttons">
              <button className="confirm-delete-button" onClick={confirmDeletePassword}>
                Confirm
              </button>
              <button className="cancel-delete-button" onClick={() => setPasswordToDelete(null)}>
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default PersonalPwManager;
