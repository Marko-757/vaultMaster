import React, { useState } from "react";
import FileList from "../components/fileList";  // Updated password list component
import PasswordInformation from "../components/passwordInformation";
import AddPasswordForm from "../components/addPasswordForm";
import AddFolderForm from "../components/addFolderForm";
import "./personal_pw_manager.css";
import { useNavigate } from "react-router";

function PersonalPwManager() {
  // Dummy password data
  const [passwords, setPasswords] = useState([
    { id: 1, name: "Google Account", username: "user@gmail.com", password: "password123", website: "https://google.com", folderId: null },
    { id: 2, name: "GitHub", username: "devuser", password: "devpassword", website: "https://github.com", folderId: null },
  ]);
  const [files, setFiles] = useState([]);
  
  // Dummy folders: 2 items by default for each section
  const [passwordFolders, setPasswordFolders] = useState([
    { id: 1, name: "Personal Passwords" },
    { id: 2, name: "Work Passwords" },
  ]);
  const [fileFolders, setFileFolders] = useState([
    { id: 1, name: "Personal Files" },
    { id: 2, name: "Work Files" },
  ]);

  const [selectedFolder, setSelectedFolder] = useState(null);
  const [selectedFileFolder, setSelectedFileFolder] = useState(null);
  const [isAddingPassword, setIsAddingPassword] = useState(false);
  const [isAddingPasswordFolder, setIsAddingPasswordFolder] = useState(false);
  const [isAddingFileFolder, setIsAddingFileFolder] = useState(false);

  // Delete Folder for Passwords
  const deletePasswordFolder = (folderId) => {
    if (window.confirm("Are you sure you want to delete this folder?")) {
      const updatedFolders = passwordFolders.filter((folder) => folder.id !== folderId);
      setPasswordFolders(updatedFolders);
      const updatedPasswords = passwords.map((password) =>
        password.folderId === folderId ? { ...password, folderId: null } : password
      );
      setPasswords(updatedPasswords);
    }
  };

  // Delete Folder for Files
  const deleteFileFolder = (folderId) => {
    if (window.confirm("Are you sure you want to delete this folder?")) {
      const updatedFolders = fileFolders.filter((folder) => folder.id !== folderId);
      setFileFolders(updatedFolders);
      const updatedFiles = files.filter((file) => file.folderId !== folderId);
      setFiles(updatedFiles);
    }
  };

  // Add Password
  const addPassword = (newPassword) => {
    setPasswords([
      ...passwords,
      { id: passwords.length + 1, folderId: selectedFolder?.id || null, ...newPassword },
    ]);
    setIsAddingPassword(false);
  };

  // Add Folder for Passwords
  const addPasswordFolder = (folderName) => {
    setPasswordFolders([...passwordFolders, { id: passwordFolders.length + 1, name: folderName }]);
    setIsAddingPasswordFolder(false);
  };

  // Add Folder for Files
  const addFileFolder = (folderName) => {
    setFileFolders([...fileFolders, { id: fileFolders.length + 1, name: folderName }]);
    setIsAddingFileFolder(false);
  };

  // Filter files by selected file folder
  const filteredFiles =
    selectedFileFolder === null
      ? files
      : files.filter((file) => file.folderId === selectedFileFolder.id);

  // For folder preview: show 2 items by default with See More/See Less toggling
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

  const navigate = useNavigate();

  return (
    <div className="pw-manager-container">
      {/* Left Sidebar */}
      <div className="sidebar">
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
                <button className="sidebar-item-button" onClick={() => setSelectedFolder(folder)}>
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

      {/* Main Content Area */}
      <div className="main-content">
        <FileList
          passwords={passwords.filter(
            (password) => !selectedFolder || password.folderId === selectedFolder.id
          )}
          onDeletePassword={(id) => setPasswords(passwords.filter((password) => password.id !== id))}
          onUpdatePassword={(updatedPassword) => {
            setPasswords(passwords.map(p => p.id === updatedPassword.id ? updatedPassword : p));
          }}
          onAddPassword={() => setIsAddingPassword(true)}
        />
        {isAddingPassword && (
          <AddPasswordForm
            folders={passwordFolders}
            selectedFolder={selectedFolder}
            onSave={addPassword}
            onCancel={() => setIsAddingPassword(false)}
          />
        )}
        {isAddingPasswordFolder && (
          <AddFolderForm
            onSave={addPasswordFolder}
            onCancel={() => setIsAddingPasswordFolder(false)}
          />
        )}
        {isAddingFileFolder && (
          <AddFolderForm
            onSave={addFileFolder}
            onCancel={() => setIsAddingFileFolder(false)}
          />
        )}
        {selectedFileFolder && (
          <div className="file-folder-content">
            <h3>{selectedFileFolder.name}</h3>
            <ul>
              {filteredFiles.map((file) => (
                <li key={file.id}>{file.name}</li>
              ))}
            </ul>
          </div>
        )}
      </div>
    </div>
  );
}

export default PersonalPwManager;
