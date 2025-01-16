import React, { useState } from "react";
import Sidebar from "../components/sidebar";
import FileList from "../components/fileList";
import PasswordInformation from "../components/passwordInformation";
import AddPasswordForm from "../components/addPasswordForm";
import AddFolderForm from "../components/addFolderForm";
import "./personal_pw_manager.css";

function PersonalPwManager() {
  const [passwords, setPasswords] = useState([]);
  const [folders, setFolders] = useState([]);
  const [selectedFolder, setSelectedFolder] = useState(null); // null means "All Passwords"
  const [selectedPassword, setSelectedPassword] = useState(null);
  const [isAddingPassword, setIsAddingPassword] = useState(false);
  const [isAddingFolder, setIsAddingFolder] = useState(false);

  // Add Password
  const addPassword = (newPassword) => {
    setPasswords([
      ...passwords,
      { id: passwords.length + 1, folderId: selectedFolder?.id || null, ...newPassword },
    ]);
    setIsAddingPassword(false);
  };

  // Delete Password
  const deletePassword = (passwordId) => {
    const updatedPasswords = passwords.filter((password) => password.id !== passwordId);
    setPasswords(updatedPasswords);
    if (selectedPassword?.id === passwordId) {
      setSelectedPassword(null);
    }
  };

  // Add Folder
  const addFolder = (folderName) => {
    setFolders([...folders, { id: folders.length + 1, name: folderName }]);
    setIsAddingFolder(false);
  };

  // Delete Folder
  const deleteFolder = (folderId) => {
    const updatedFolders = folders.filter((folder) => folder.id !== folderId);
    setFolders(updatedFolders);
    // Remove folder association from passwords
    const updatedPasswords = passwords.map((password) =>
      password.folderId === folderId ? { ...password, folderId: null } : password
    );
    setPasswords(updatedPasswords);
  };

  // Filtered Passwords
  const filteredPasswords =
    selectedFolder === null
      ? passwords
      : passwords.filter((password) => password.folderId === selectedFolder.id);

  return (
    <div className="pw-manager-container">
      <div className="sidebar">
        <Sidebar
          title="Passwords"
          items={folders}
          onSelectItem={(folder) => setSelectedFolder(folder)}
          onAddItem={() => setIsAddingFolder(true)}
          onDeleteItem={deleteFolder}
        />
        <Sidebar
          title="Files"
          items={[]} 
          onAddItem={() => alert("Add file functionality")}
          onDeleteItem={() => alert("Delete file functionality")}
        />
      </div>
      <div className="main-content">
        <FileList
          passwords={filteredPasswords}
          onSelectPassword={(password) => setSelectedPassword(password)}
          onAddPassword={() => setIsAddingPassword(true)}
          onDeletePassword={deletePassword}
        />
        {isAddingPassword ? (
          <AddPasswordForm
            folders={folders}
            selectedFolder={selectedFolder}
            onSave={addPassword}
            onCancel={() => setIsAddingPassword(false)}
          />
        ) : selectedPassword ? (
          <PasswordInformation password={selectedPassword} />
        ) : (
          <div className="placeholder">Select or add a password</div>
        )}
      </div>
      {isAddingFolder && (
        <AddFolderForm
          onSave={addFolder}
          onCancel={() => setIsAddingFolder(false)}
        />
      )}
    </div>
  );
}

export default PersonalPwManager;
