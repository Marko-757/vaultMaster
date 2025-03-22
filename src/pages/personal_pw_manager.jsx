import React, { useState, useRef, useEffect } from "react";
import FileList from "../components/fileList";
import PasswordInformation from "../components/passwordInformation";
import AddPasswordForm from "../components/addPasswordForm";
import AddFolderForm from "../components/addFolderForm";
import { logout } from "../api/authService";
import "./personal_pw_manager.css";
import { useNavigate } from "react-router";
import profileIcon from "../Assets/defaultProfileImage.png";
import * as personalPWService from "../api/personalPWService";

function PersonalPwManager() {
  const navigate = useNavigate();

  const [dropdownOpen, setDropdownOpen] = useState(false);
  const dropdownRef = useRef(null);

  const [folderDropdownOpen, setFolderDropdownOpen] = useState(null);
  const folderDropdownRef = useRef(null);

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

  const [isRenamingFolder, setIsRenamingFolder] = useState(false);
  const [folderBeingRenamed, setFolderBeingRenamed] = useState(null);

  const [passwordToDelete, setPasswordToDelete] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [editData, setEditData] = useState({});

  const [decryptedPassword, setDecryptedPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);

  const toggleDropdown = () => setDropdownOpen(!dropdownOpen);

  useEffect(() => {
    function handleClickOutside(event) {
      if (
        folderDropdownRef.current &&
        !folderDropdownRef.current.contains(event.target)
      ) {
        setFolderDropdownOpen(null);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [fetchedFolders, fetchedPasswords] = await Promise.all([
          personalPWService.getAllPasswordFolders(),
          personalPWService.getAllPasswords(),
        ]);

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

  const deletePasswordFolder = async (folderId) => {
    const confirmDelete = window.confirm(
      "Are you sure you want to delete this folder?"
    );
    if (!confirmDelete) return;

    try {
      await personalPWService.deletePasswordFolder(folderId);
      setPasswordFolders(
        passwordFolders.filter((folder) => folder.folderId !== folderId)
      );
      setPasswords(
        passwords.filter((password) => password.folderId !== folderId)
      );
      alert("Folder deleted successfully!");

      if (
        selectedPasswordFolder &&
        selectedPasswordFolder.folderId === folderId
      ) {
        setSelectedPasswordFolder(null);
      }
    } catch (error) {
      console.error("Error deleting folder:", error);
      alert("Failed to delete folder.");
    }
  };

  const renameFolder = (folder) => {
    setFolderBeingRenamed(folder);
    setIsRenamingFolder(true);
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

  const confirmDeletePassword = async () => {
    if (passwordToDelete) {
      try {
        await personalPWService.deletePassword(passwordToDelete.entryId);
        const updatedPasswords = await personalPWService.getAllPasswords();
        setPasswords(updatedPasswords);
        if (
          selectedPassword &&
          selectedPassword.entryId === passwordToDelete.entryId
        ) {
          setSelectedPassword(null);
        }
        setPasswordToDelete(null);
      } catch (err) {
        console.error("Failed to delete password:", err);
        alert("Failed to delete password.");
      }
    }
  };

  const startEditing = async () => {
    try {
      const decryptedPassword = await personalPWService.getDecryptedPassword(
        selectedPassword.entryId
      );
      setEditData({ ...selectedPassword, passwordHash: decryptedPassword });
      setIsEditing(true);
    } catch (error) {
      console.error("🚨 Error decrypting password for editing:", error);
      alert("Failed to decrypt password for editing.");
    }
  };

  const cancelEditing = () => {
    setIsEditing(false);
    setEditData({});
  };

  const saveEditing = async () => {
    try {
      await personalPWService.updatePasswordEntry(editData.entryId, editData);
      setPasswords(
        passwords.map((p) => (p.entryId === editData.entryId ? editData : p))
      );
      setSelectedPassword(editData);
      setDecryptedPassword(editData.passwordHash);
      setIsEditing(false);
      alert("Password updated successfully!");
    } catch (error) {
      console.error("Failed to update password:", error);
      alert("Failed to update password.");
    }
  };

  return (
    <div className="pw-manager-container">
    <button className="home-button" onClick={() => navigate("/home")}>
      🏠︎
    </button>

    <div className="three-column-container">
      {/* Profile Icon */}
      <div className="profile-container" ref={dropdownRef}>
        <img
          src={profileIcon}
          alt="Profile"
          className="profile-icon"
          onClick={toggleDropdown}
        />
        {dropdownOpen && (
          <div className="profile-dropdown">
            <button onClick={() => navigate("/settings")}>
              Profile Settings
            </button>
            <button onClick={handleLogout}>Log Out</button>
          </div>
        )}
      </div>

      {/* LEFT COLUMN */}
      <div className="left-column">
        <div className="sidebar-heading">
          <div className="sidebar-heading-top">Personal</div>
          <div className="sidebar-heading-bottom">Passwords & Files</div>
        </div>
        <hr className="divider" />

        {/* All Passwords (stationary) */}
        <button
          className="btn btn-outline-primary mb-3 w-100"
          onClick={() => setSelectedPasswordFolder(null)}
        >
          All Passwords
        </button>

        {/* Scrollable Accordion Section */}
        <div className="left-scrollable-section">
          <div className="accordion-wrapper">
            <div className="accordion w-100" id="foldersAccordion">
              {/* Password Folders Accordion */}
              <div className="accordion-item">
                <h2 className="accordion-header" id="headingPasswords">
                  <button
                    className="accordion-button"
                    type="button"
                    data-bs-toggle="collapse"
                    data-bs-target="#collapsePasswords"
                    aria-expanded="true"
                    aria-controls="collapsePasswords"
                  >
                    Password Folders
                  </button>
                </h2>
                <div
                  id="collapsePasswords"
                  className="accordion-collapse collapse show"
                  aria-labelledby="headingPasswords"
                  data-bs-parent="#foldersAccordion"
                >
                  <div className="accordion-body">
                    {passwordFolders.map((folder) => (
                      <div
                        key={folder.folderId}
                        className="btn-group w-100 mb-2 folder-split-button"
                      >
                        <button
                          type="button"
                          className="btn btn-primary flex-grow-1"
                          onClick={() => setSelectedPasswordFolder(folder)}
                        >
                          {folder.folderName}
                        </button>
                        <button
                          type="button"
                          className="btn btn-primary dropdown-toggle dropdown-toggle-split flex-shrink-0 small-dropdown"
                          data-bs-toggle="dropdown"
                          aria-expanded="false"
                        >
                          <span className="visually-hidden">Toggle Dropdown</span>
                        </button>
                        <ul className="dropdown-menu">
                          <li>
                            <button
                              className="dropdown-item"
                              onClick={() => renameFolder(folder)}
                            >
                              Rename
                            </button>
                          </li>
                          <li>
                            <button
                              className="dropdown-item"
                              onClick={() => deletePasswordFolder(folder.folderId)}
                            >
                              Delete
                            </button>
                          </li>
                        </ul>
                      </div>
                    ))}

                    <button
                      className="btn btn-success w-100"
                      onClick={() => setIsAddingPasswordFolder(true)}
                    >
                      + Add Folder
                    </button>
                  </div>
                </div>
              </div>

              {/* File Folders Accordion */}
              <div className="accordion-item">
                <h2 className="accordion-header" id="headingFiles">
                  <button
                    className="accordion-button collapsed"
                    type="button"
                    data-bs-toggle="collapse"
                    data-bs-target="#collapseFiles"
                    aria-expanded="false"
                    aria-controls="collapseFiles"
                  >
                    File Folders
                  </button>
                </h2>
                <div
                  id="collapseFiles"
                  className="accordion-collapse collapse"
                  aria-labelledby="headingFiles"
                  data-bs-parent="#foldersAccordion"
                >
                  <div className="accordion-body">
                    {fileFolders.map((folder) => (
                      <div key={folder.id} className="folder-row">
                        <button className="sidebar-item-button">
                          {folder.name}
                        </button>
                      </div>
                    ))}
                    <button
                      className="btn btn-success w-100"
                      onClick={() => setIsAddingFileFolder(true)}
                    >
                      + Add File Folder
                    </button>
                  </div>
                </div>
              </div>
            </div>
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
                      const decrypted =
                        await personalPWService.getDecryptedPassword(
                          password.entryId
                        );
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
          <button
            className="add-password-button"
            onClick={() => setIsAddingPassword(true)}
          >
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
          ) : isRenamingFolder ? (
            <AddFolderForm
              formType="password"
              initialFolderName={folderBeingRenamed.folderName}
              onSave={async ({ folderName }) => {
                try {
                  await personalPWService.renamePasswordFolder(
                    folderBeingRenamed.folderId,
                    folderName
                  );
                  setPasswordFolders(
                    passwordFolders.map((f) =>
                      f.folderId === folderBeingRenamed.folderId
                        ? { ...f, folderName }
                        : f
                    )
                  );
                  setIsRenamingFolder(false);
                  setFolderBeingRenamed(null);
                  alert("Folder renamed successfully!");
                } catch (error) {
                  console.error("Error renaming folder:", error);
                  alert("Failed to rename folder.");
                }
              }}
              onCancel={() => {
                setIsRenamingFolder(false);
                setFolderBeingRenamed(null);
              }}
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
                    onChange={(e) =>
                      setEditData({ ...editData, accountName: e.target.value })
                    }
                  />
                </div>
                <div className="form-group">
                  <label>Username:</label>
                  <input
                    type="text"
                    value={editData.username || ""}
                    onChange={(e) =>
                      setEditData({ ...editData, username: e.target.value })
                    }
                  />
                </div>
                <div className="form-group">
                  <label>Password:</label>
                  <input
                    type="text"
                    value={editData.passwordHash || ""}
                    onChange={(e) =>
                      setEditData({ ...editData, passwordHash: e.target.value })
                    }
                  />
                </div>
                <div className="form-group">
                  <label>Website:</label>
                  <input
                    type="text"
                    value={editData.website || ""}
                    onChange={(e) =>
                      setEditData({ ...editData, website: e.target.value })
                    }
                  />
                </div>
                <div className="edit-actions">
                  <button className="save-button" onClick={saveEditing}>
                    Save
                  </button>
                  <button className="cancel-button" onClick={cancelEditing}>
                    Cancel
                  </button>
                </div>
              </div>
            ) : (
              <div className="password-detail-view">
                <button
                  className="close-detail-button"
                  onClick={() => setSelectedPassword(null)}
                >
                  X
                </button>
                <PasswordInformation
                  password={selectedPassword}
                  decryptedPassword={decryptedPassword}
                  showPassword={showPassword}
                  setShowPassword={setShowPassword}
                />
                <div className="password-actions">
                  <button
                    className="edit-password-button"
                    onClick={startEditing}
                  >
                    Edit
                  </button>
                  <button
                    className="delete-password-button"
                    onClick={() => setPasswordToDelete(selectedPassword)}
                  >
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
            <p>
              Are you sure you want to delete "{passwordToDelete.accountName}"?
            </p>
            <div className="delete-modal-buttons">
              <button
                className="confirm-delete-button"
                onClick={confirmDeletePassword}
              >
                Confirm
              </button>
              <button
                className="cancel-delete-button"
                onClick={() => setPasswordToDelete(null)}
              >
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
