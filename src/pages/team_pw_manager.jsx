import React, { useState } from "react";
import Sidebar from "../components/sidebar";
import FileList from "../components/fileList";
import PasswordInformation from "../components/passwordInformation";
import AddPasswordForm from "../components/addPasswordForm";
import AddFolderForm from "../components/addFolderForm";
import { useNavigate } from "react-router";
import "./team_pw_manager.css";


function TeamPwManager() {
  const [passwords, setPasswords] = useState([]);
  const [passwordFolders, setPasswordFolders] = useState([]);
  const [fileFolders, setFileFolders] = useState([]);
  const [files, setFiles] = useState([]); // State for file content
  const [selectedFolder, setSelectedFolder] = useState(null); // null means "All Passwords" or "All Files"
  const [selectedPassword, setSelectedPassword] = useState(null);
  const [selectedFileFolder, setSelectedFileFolder] = useState(null); // Track selected file folder
  const [isAddingPassword, setIsAddingPassword] = useState(false);
  const [isAddingPasswordFolder, setIsAddingPasswordFolder] = useState(false);
  const [isAddingFileFolder, setIsAddingFileFolder] = useState(false);

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

  // Delete Folder for Passwords
  const deletePasswordFolder = (folderId) => {
    if (window.confirm("Are you sure you want to delete this folder?")) {
      const updatedFolders = passwordFolders.filter((folder) => folder.id !== folderId);
      setPasswordFolders(updatedFolders);

      // Remove folder association from passwords
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

      // Remove folder association from files
      const updatedFiles = files.filter((file) => file.folderId !== folderId);
      setFiles(updatedFiles);
    }
  };

  // Filtered Files by Folder
  const filteredFiles =
    selectedFileFolder === null
      ? files
      : files.filter((file) => file.folderId === selectedFileFolder.id);

  return (
    <div className="teams-manager-container">
      <div className="sidebar">
        <Sidebar
          title="Passwords"
          items={passwordFolders}
          onSelectItem={(folder) => setSelectedFolder(folder)}
          onAddItem={() => setIsAddingPasswordFolder(true)}
          onDeleteItem={deletePasswordFolder}
        />
        <Sidebar
          title="Files"
          items={fileFolders}
          onSelectItem={(folder) => setSelectedFileFolder(folder)} // Set the selected file folder
          onAddItem={() => setIsAddingFileFolder(true)}
          onDeleteItem={deleteFileFolder}
        />
      </div>
      <div className="main-content">
        <FileList
          passwords={passwords.filter(
            (password) => !selectedFolder || password.folderId === selectedFolder.id
          )}
          onSelectPassword={(password) => setSelectedPassword(password)}
          onAddPassword={() => setIsAddingPassword(true)}
          onDeletePassword={(id) => setPasswords(passwords.filter((password) => password.id !== id))}
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
        {selectedPassword && <PasswordInformation password={selectedPassword} />}
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

export default TeamPwManager;
