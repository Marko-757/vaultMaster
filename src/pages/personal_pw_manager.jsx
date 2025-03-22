import React, { useState, useRef, useEffect } from "react";
import FileList from "../components/fileList";
import PasswordInformation from "../components/passwordInformation";
import AddPasswordForm from "../components/addPasswordForm";
import AddFolderForm from "../components/addFolderForm";
import { logout } from "../api/authService";
import "./personal_pw_manager.css";
import { useNavigate } from "react-router";
import profileIcon from "../Assets/defaultProfileImage.png";
import {
  createPasswordEntry,
  createPasswordFolder,
  getAllPasswordFolders,
  getAllPasswords,
  getDecryptedPassword,
} from "../api/personalPWService";

function PersonalPwManager() {
  const navigate = useNavigate();

  const [dropdownOpen, setDropdownOpen] = useState(false);
  const dropdownRef = useRef(null);

  const [passwords, setPasswords] = useState([]);
  const [passwordFolders, setPasswordFolders] = useState([]);
  const [fileFolders, setFileFolders] = useState([]);

  const [selectedPasswordFolder, setSelectedPasswordFolder] = useState(null);
  const [selectedPassword, setSelectedPassword] = useState(null);

  const [isAddingPassword, setIsAddingPassword] = useState(false);
  const [isAddingPasswordFolder, setIsAddingPasswordFolder] = useState(false);
  const [isAddingFileFolder, setIsAddingFileFolder] = useState(false);

  const [expandedPasswordFolders, setExpandedPasswordFolders] = useState(false);
  const [expandedFileFolders, setExpandedFileFolders] = useState(false);
  const previewCount = 2;

  const [passwordToDelete, setPasswordToDelete] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [editData, setEditData] = useState({});

  const [decryptedPassword, setDecryptedPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);

  const toggleDropdown = () => setDropdownOpen(!dropdownOpen);

  useEffect(() => {
    function handleClickOutside(event) {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setDropdownOpen(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [fetchedFolders, fetchedPasswords] = await Promise.all([
          getAllPasswordFolders(),
          getAllPasswords(),
        ]);
  
        console.log("✅ Fetched Folders:", fetchedFolders);
        console.log("✅ Fetched Passwords:", fetchedPasswords);
  
        setPasswordFolders(fetchedFolders);
        setPasswords(fetchedPasswords);
      } catch (error) {
        console.error("🚨 Error fetching password data:", error);
      }
    };
  
    fetchData();
  }, []);
  
  
  

  const filteredPasswords = selectedPasswordFolder
    ? passwords.filter((p) => p.folderId === selectedPasswordFolder.folderId)
    : passwords;

  const handleLogout = async () => {
    try {
      await logout();
      navigate("/auth/login");
    } catch (error) {
      console.error("Logout failed", error);
    }
  };

  const addPassword = async (newPasswordEntry) => {
    setPasswords((prev) => [...prev, newPasswordEntry]);
    setIsAddingPassword(false);
  };
  

  const addPasswordFolder = async (newlyCreatedFolder) => {
    setPasswordFolders((prev) => [...prev, newlyCreatedFolder]);
    setIsAddingPasswordFolder(false);
  };

  const deletePasswordFolder = (folderId) => {
    const confirm = window.confirm("Are you sure you want to delete this folder?");
    if (!confirm) return;
    alert("Folder deletion logic not yet implemented.");
  };

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

  const confirmDeletePassword = () => {
    if (passwordToDelete) {
      setPasswords(passwords.filter((p) => p.entryId !== passwordToDelete.entryId));
      if (selectedPassword && selectedPassword.entryId === passwordToDelete.entryId) {
        setSelectedPassword(null);
      }
      setPasswordToDelete(null);
    }
  };

  const startEditing = () => {
    setIsEditing(true);
    setEditData(selectedPassword);
  };

  const cancelEditing = () => {
    setIsEditing(false);
    setEditData({});
  };

  const saveEditing = () => {
    setPasswords(passwords.map((p) => (p.entryId === editData.entryId ? editData : p)));
    setSelectedPassword(editData);
    setIsEditing(false);
    setEditData({});
  };

  return (
    <div className="pw-manager-container">
      <button className="home-button" onClick={() => navigate("/home")}>🏠︎</button>

      <div className="three-column-container">
        <div className="profile-container" ref={dropdownRef}>
          <img src={profileIcon} alt="Profile" className="profile-icon" onClick={toggleDropdown} />
          {dropdownOpen && (
            <div className="profile-dropdown">
              <button onClick={() => navigate("/settings")}>Profile Settings</button>
              <button onClick={handleLogout}>Log Out</button>
            </div>
          )}
        </div>

        <div className="left-column">
          <div className="sidebar-heading">
            <div className="sidebar-heading-top">Personal</div>
            <div className="sidebar-heading-bottom">Passwords & Files</div>
          </div>
          <hr className="divider" />
          <div className="sidebar-section">
            <div className="section-header">
              <h2>Passwords</h2>
              <button className="section-add-button" onClick={() => setIsAddingPasswordFolder(true)}>+</button>
            </div>
            <div className="folders-list-container scrollable">
              {displayedPasswordFolders.map((folder) => (
                <div key={folder.folderId} className="folder-row">
                  <button className="sidebar-item-button" onClick={() => setSelectedPasswordFolder(folder)}>
                    <span className="team-name">{folder.folderName}</span>
                  </button>
                  <button className="folder-delete-button" onClick={() => deletePasswordFolder(folder.folderId)}>X</button>
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
          <div className="sidebar-section">
            <div className="section-header">
              <h2>Files</h2>
              <button className="section-add-button" onClick={() => setIsAddingFileFolder(true)}>+</button>
            </div>
            <div className="folders-list-container scrollable">
              {displayedFileFolders.map((folder) => (
                <div key={folder.id} className="folder-row">
                  <button className="sidebar-item-button">
                    <span className="team-name">{folder.name}</span>
                  </button>
                  <button className="folder-delete-button" onClick={() => alert("Delete not implemented")}>X</button>
                </div>
              ))}
            </div>
          </div>
        </div>

        <div className="middle-column">
          <h2 className="column-heading">Passwords</h2>
          <hr className="middle-divider" />
          <div className="password-name-list">
            {filteredPasswords.length === 0 ? (
              <div className="no-passwords">No Passwords Found</div>
            ) : (
              filteredPasswords.map((password) => (
                <button
                  key={password.entryId}
                  className="password-name-button"
                  onClick={async () => {
                    try {
                      const decrypted = await getDecryptedPassword(password.entryId);
                      setSelectedPassword(password);
                      setDecryptedPassword(decrypted);
                      setShowPassword(false);
                      setIsEditing(false);
                    } catch (error) {
                      console.error("Failed to decrypt password", error);
                      setSelectedPassword(password);
                      setDecryptedPassword("");
                    }
                  }}
                >
                  {password.accountName}
                </button>
              ))
            )}
          </div>
          <button className="add-password-button" onClick={() => setIsAddingPassword(true)}>
            Add Password
          </button>
        </div>

        <div className="right-column">
          {isAddingPassword ? (
            <AddPasswordForm
              folders={passwordFolders}
              selectedFolder={selectedPasswordFolder}
              onSave={addPassword}
              onCancel={() => setIsAddingPassword(false)}
            />
          ) : isAddingPasswordFolder ? (
            <AddFolderForm
              formType="password"
              onSave={addPasswordFolder}
              onCancel={() => setIsAddingPasswordFolder(false)}
            />
          ) : selectedPassword ? (
            isEditing ? (
              <div className="edit-form-container">
                <h2>Edit Password</h2>
                <div className="form-group">
                  <label>Account Name:</label>
                  <input
                    type="text"
                    value={editData.accountName || ""}
                    onChange={(e) => setEditData({ ...editData, accountName: e.target.value })}
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
                    value={editData.passwordHash || ""}
                    onChange={(e) => setEditData({ ...editData, passwordHash: e.target.value })}
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
                <PasswordInformation
                  password={selectedPassword}
                  decryptedPassword={decryptedPassword}
                  showPassword={showPassword}
                  setShowPassword={setShowPassword}
                />
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

      {passwordToDelete && (
        <div className="delete-modal">
          <div className="delete-modal-content">
            <p>Are you sure you want to delete "{passwordToDelete.accountName}"?</p>
            <div className="delete-modal-buttons">
              <button className="confirm-delete-button" onClick={confirmDeletePassword}>Confirm</button>
              <button className="cancel-delete-button" onClick={() => setPasswordToDelete(null)}>Cancel</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default PersonalPwManager;
