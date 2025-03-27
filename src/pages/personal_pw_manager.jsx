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
import NavbarVaultMaster from "../components/navbarVaultMaster";
import AddFileForm from "../components/addFileForm";
import AddFileFolderForm from "../components/addFileFolderForm";
import * as personalFileService from "../api/personalFileService";
import FileInformation from "../components/FileInformation";

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

  const [selectedFileFolder, setSelectedFileFolder] = useState(null);
  const [files, setFiles] = useState([]);
  const [selectedFile, setSelectedFile] = useState(null);
  const [viewMode, setViewMode] = useState("passwords");
  const [isAddingFile, setIsAddingFile] = useState(false);

  const filteredFiles = selectedFileFolder
    ? files.filter(
        (f) =>
          f.folderId === selectedFileFolder.folderId ||
          f.folderId === selectedFileFolder.id
      )
    : files;

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
        const [fetchedPasswordFolders, fetchedPasswords, fetchedFiles] =
          await Promise.all([
            personalPWService.getAllPasswordFolders(),
            personalPWService.getAllPasswords(),
            personalFileService.getAllFiles(),
          ]);

        setPasswordFolders(fetchedPasswordFolders);
        setPasswords(fetchedPasswords);
        setFiles(fetchedFiles);

        try {
          const fetchedFileFolders =
            await personalFileService.getAllFileFolders();
          setFileFolders(fetchedFileFolders);
        } catch (err) {
          console.warn("Failed to fetch file folders (optional):", err.message);
        }
      } catch (error) {
        console.error("Error fetching password or file data:", error);
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
    try {
      const createdEntry = await personalPWService.createPasswordEntry(
        newPasswordEntry
      );

      // Decrypt the password for immediate UI display
      const decrypted = await personalPWService.getDecryptedPassword(
        createdEntry.entryId
      );
      console.log("Received decrypted password from backend:", decrypted);

      // Save encrypted entry, then set decrypted for preview
      setPasswords((prev) => [...prev, createdEntry]);
      setSelectedPassword(createdEntry);
      setDecryptedPassword(decrypted);
      setShowPassword(false);
      setIsEditing(false);
      setIsAddingPassword(false);
    } catch (error) {
      console.error("Error adding or decrypting password:", error);
      alert("Failed to add password.");
    }
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
    setIsRenamingFolder(true);
    setFolderBeingRenamed(folder);
    setIsAddingPassword(false);
    setIsAddingPasswordFolder(false);
    setSelectedPassword(null);
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
    console.log("Starting editing...");
    try {
      const decrypted = await personalPWService.getDecryptedPassword(
        selectedPassword.entryId
      );

      console.log("Decrypted password:", decrypted);

      setEditData({
        entryId: selectedPassword.entryId,
        accountName: selectedPassword.accountName,
        username: selectedPassword.username,
        website: selectedPassword.website,
        folderId: selectedPassword.folderId,
        passwordHash: decrypted,
      });

      console.log("Edit data set:", editData);
      setIsEditing(true); // Ensure this is triggered.
    } catch (error) {
      console.error("Error decrypting password for editing:", error);
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

  ////// Files///////
  const addFileFolder = async (newFolder) => {
    setFileFolders((prev) => [...prev, newFolder]);
    setIsAddingFileFolder(false);
  };

  return (
    <div className="pw-manager-container">
      <button className="home-button" onClick={() => navigate("/home")}>
        🏠︎
      </button>
      <NavbarVaultMaster onLogout={handleLogout} />
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
        {/* left column */}
        <div className="left-column">
          <div className="sidebar-heading">
            <div className="sidebar-heading-top">Personal</div>
            <div className="sidebar-heading-bottom">Passwords & Files</div>
          </div>
          <hr className="divider" />

          {/* All View Buttons (Passwords & Files) */}
          <div className="all-view-buttons">
            <button
              className={`btn btn-outline-primary mb-2 w-100 ${
                selectedPasswordFolder === null && viewMode === "passwords"
                  ? "active-folder"
                  : ""
              }`}
              onClick={() => {
                setSelectedPasswordFolder(null);
                setViewMode("passwords");
              }}
            >
              All Passwords
            </button>

            <button
              className={`btn btn-outline-primary mb-2 w-100 ${
                selectedFileFolder === null && viewMode === "files"
                  ? "active-folder"
                  : ""
              }`}
              onClick={() => {
                setSelectedFileFolder(null);
                setViewMode("files");
              }}
            >
              All Files
            </button>
          </div>

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
                    className="accordion-collapse collapse"
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
                            className={`btn btn-primary flex-grow-1 ${
                              selectedPasswordFolder?.folderId ===
                              folder.folderId
                                ? "active-folder"
                                : ""
                            }`}
                            onClick={() => {
                              setSelectedPasswordFolder(folder);
                              setViewMode("passwords");
                            }}
                          >
                            {folder.folderName}
                          </button>

                          <button
                            type="button"
                            className="btn btn-primary dropdown-toggle dropdown-toggle-split flex-shrink-0 small-dropdown"
                            data-bs-toggle="dropdown"
                            aria-expanded="false"
                          >
                            <span className="visually-hidden">
                              Toggle Dropdown
                            </span>
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
                                onClick={() =>
                                  deletePasswordFolder(folder.folderId)
                                }
                              >
                                Delete
                              </button>
                            </li>
                          </ul>
                        </div>
                      ))}
                      <button
                        className="btn btn-success w-100"
                        onClick={() => {
                          setIsAddingPasswordFolder(true);
                          setIsAddingFileFolder(false);
                          setIsAddingPassword(false);
                          setIsRenamingFolder(false);
                          setSelectedPassword(null);
                        }}
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
                        <div
                          key={folder.folderId || folder.id}
                          className="btn-group w-100 mb-2 folder-split-button"
                        >
                          <button
                            type="button"
                            className={`btn btn-success flex-grow-1 ${
                              selectedFileFolder?.folderId ===
                                folder.folderId ||
                              selectedFileFolder?.id === folder.id
                                ? "active-folder"
                                : ""
                            }`}
                            onClick={() => {
                              setSelectedFileFolder(folder);
                              setViewMode("files");
                            }}
                          >
                            {folder.folderName || folder.name}
                          </button>

                          <button
                            type="button"
                            className="btn btn-primary dropdown-toggle dropdown-toggle-split flex-shrink-0 small-dropdown"
                            data-bs-toggle="dropdown"
                            aria-expanded="false"
                          >
                            <span className="visually-hidden">
                              Toggle Dropdown
                            </span>
                          </button>
                          <ul className="dropdown-menu">
                            <li>
                              <button className="dropdown-item" disabled>
                                Rename
                              </button>
                            </li>
                            <li>
                              <button className="dropdown-item" disabled>
                                Delete
                              </button>
                            </li>
                          </ul>
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
          <div className="sticky-header">
            <h2 className="column-heading">
              {viewMode === "passwords" ? "Passwords" : "Files"}
            </h2>
          </div>

          <div className="entry-list">
            {viewMode === "passwords" ? (
              <>
                <button
                  className="add-password-button"
                  onClick={() => {
                    setIsAddingPassword(true);
                    setIsAddingPasswordFolder(false);
                    setIsRenamingFolder(false);
                    setIsAddingFile(false);
                    setIsAddingFileFolder(false);
                    setSelectedPassword(null);
                    setSelectedFile(null);
                  }}
                >
                  + Add Password
                </button>

                {filteredPasswords.length === 0 ? (
                  <div className="no-passwords">No Passwords Found</div>
                ) : (
                  [...filteredPasswords]
                    .sort((a, b) => a.accountName.localeCompare(b.accountName))
                    .map((password) => (
                      <button
                        key={password.entryId}
                        className={`entry-name-button ${
                          selectedPassword?.entryId === password.entryId
                            ? "active-password"
                            : ""
                        }`}
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
                            setSelectedFile(null); // clear file selection
                          } catch (error) {
                            console.error("Failed to decrypt password", error);
                            setSelectedPassword(password);
                            setDecryptedPassword("");
                            setSelectedFile(null);
                          }
                        }}
                      >
                        {password.accountName}
                      </button>
                    ))
                )}
              </>
            ) : (
              <>
                <button
                  className="add-password-button"
                  onClick={() => {
                    setIsAddingFile(true);
                    setIsAddingFileFolder(false);
                    setIsAddingPassword(false);
                    setIsAddingPasswordFolder(false);
                    setIsRenamingFolder(false);
                    setSelectedPassword(null);
                    setSelectedFile(null);
                  }}
                >
                  + Upload File
                </button>

                {filteredFiles.length === 0 ? (
                  <div className="no-passwords">No Files Found</div>
                ) : (
                  [...filteredFiles]
                    .sort((a, b) =>
                      a.originalFilename.localeCompare(b.originalFilename)
                    )
                    .map((file) => (
                      <button
                        key={file.fileId}
                        className={`entry-name-button ${
                          selectedFile?.fileId === file.fileId
                            ? "active-file"
                            : ""
                        }`}
                        onClick={() => {
                          setSelectedFile(file);
                          setSelectedPassword(null);
                          setIsAddingFile(false);
                          setIsAddingFileFolder(false);
                          setIsAddingPassword(false);
                          setIsAddingPasswordFolder(false);
                          setIsRenamingFolder(false);
                        }}
                      >
                        {file.originalFilename}
                      </button>
                    ))
                )}
              </>
            )}
          </div>
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
          ) : isAddingFileFolder ? (
            <AddFileFolderForm
              onSave={(newFolder) => {
                setFileFolders((prev) => [...prev, newFolder]);
                setIsAddingFileFolder(false);
              }}
              onCancel={() => setIsAddingFileFolder(false)}
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
          ) : isAddingFile ? (
            <AddFileForm
              folders={fileFolders}
              selectedFolder={selectedFileFolder}
              onSave={async (formData) => {
                try {
                  await personalFileService.uploadFiles(
                    formData.getAll("files"),
                    formData.get("folderId") || null
                  );
                  const updatedFiles = await personalFileService.getAllFiles();
                  setFiles(updatedFiles);
                  setIsAddingFile(false);
                  alert("File(s) uploaded successfully!");
                } catch (error) {
                  console.error("Failed to upload file(s):", error);
                  alert("Failed to upload file(s).");
                }
              }}
              onCancel={() => setIsAddingFile(false)}
            />
          ) : selectedPassword ? (
                isEditing ? (
                  <div className="edit-form-container">
                    {/* Render the Edit Form */}
                    <h2>Edit Password</h2>
                    <form>
                      <div>
                        <label>Account Name</label>
                        <input
                          type="text"
                          value={editData.accountName}
                          onChange={(e) => setEditData({ ...editData, accountName: e.target.value })}
                        />
                      </div>
                      <div>
                        <label>Username</label>
                        <input
                          type="text"
                          value={editData.username}
                          onChange={(e) => setEditData({ ...editData, username: e.target.value })}
                        />
                      </div>
                      <div>
                        <label>Website</label>
                        <input
                          type="text"
                          value={editData.website}
                          onChange={(e) => setEditData({ ...editData, website: e.target.value })}
                        />
                      </div>
                      <div>
                        <label>Password</label>
                        <input
                          type="password"
                          value={editData.passwordHash}
                          onChange={(e) => setEditData({ ...editData, passwordHash: e.target.value })}
                        />
                      </div>
                      <button type="button" onClick={saveEditing}>Save</button>
                      <button type="button" onClick={cancelEditing}>Cancel</button>
                    </form>
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
          ) : selectedFile ? (
            <FileInformation
              file={selectedFile}
              onClose={() => setSelectedFile(null)}
              onDelete={(deletedFileId) => {
                setFiles((prev) =>
                  prev.filter((f) => f.fileId !== deletedFileId)
                );
                setSelectedFile(null);
              }}
            />
          ) : viewMode === "files" ? (
            <div className="no-password-selected">No File Selected</div>
          ) : (
            <div className="no-password-selected">No Password Selected</div>
          )}
        </div>
      </div>
    </div>
  );
}

export default PersonalPwManager;
