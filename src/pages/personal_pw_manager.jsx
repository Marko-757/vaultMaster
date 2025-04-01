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
import FileInformation from "../components/fileInformation";
import Toast from "bootstrap/js/dist/toast";
import { FaEye, FaEyeSlash, FaRegCopy } from "react-icons/fa";

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

  const [isRenamingPasswordFolder, setIsRenamingPasswordFolder] =
    useState(false);
  const [folderBeingRenamed, setFolderBeingRenamed] = useState(null);

  const [isRenamingFileFolder, setIsRenamingFileFolder] = useState(false);
  const [fileFolderBeingRenamed, setFileFolderBeingRenamed] = useState(null);

  const [passwordToDelete, setPasswordToDelete] = useState(null);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [editData, setEditData] = useState({});

  const [decryptedPassword, setDecryptedPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);

  const [selectedFileFolder, setSelectedFileFolder] = useState(null);
  const [files, setFiles] = useState([]);
  const [selectedFile, setSelectedFile] = useState(null);
  const [viewMode, setViewMode] = useState("passwords");
  const [isAddingFile, setIsAddingFile] = useState(false);

  const [showEditPassword, setShowEditPassword] = useState(false);
  const [showTooltip, setShowTooltip] = useState(false);
  const toggleDropdown = () => setDropdownOpen(!dropdownOpen);

  const [searchTerm, setSearchTerm] = useState("");

  const filteredFiles = selectedFileFolder
    ? files.filter((f) => f.folderId === selectedFileFolder.folderId)
    : files;

  const showToast = (message, delay = 2000) => {
    const toastId = `toast-${Date.now()}`;
    const toastHTML = `
        <div id="${toastId}" class="toast text-bg-primary fade" role="alert" aria-live="assertive" aria-atomic="true" style="min-width: 250px;">
          <div class="d-flex">
            <div class="toast-body">${message}</div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
          </div>
        </div>
      `;
    const container = document.getElementById("toast-container");
    if (!container) return;
    container.insertAdjacentHTML("beforeend", toastHTML);
    const toastEl = document.getElementById(toastId);
    const toast = new Toast(toastEl, { delay: Number(delay), autohide: true });
    toast.show();
    toastEl.addEventListener("hidden.bs.toast", () => toastEl.remove());
  };

  const handleLogout = async () => {
    try {
      await logout();
      navigate("/auth/login");
    } catch (err) {
      console.error("Logout failed", err);
    }
  };

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

  const handleCopy = (text) => {
    navigator.clipboard
      .writeText(text)
      .then(() => showToast("Password copied to clipboard!"))
      .catch(() => showToast("Failed to copy password."));
  };

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [pwFolders, pwEntries, fileEntries, fileFolderList] =
          await Promise.all([
            personalPWService.getAllPasswordFolders(),
            personalPWService.getAllPasswords(),
            personalFileService.getAllFiles(),
            personalFileService.getAllFileFolders(),
          ]);
        setPasswordFolders(pwFolders);
        setPasswords(pwEntries);
        setFiles(fileEntries);
        setFileFolders(fileFolderList);
      } catch (err) {
        console.error("Failed to fetch data", err);
      }
    };
    fetchData();
  }, []);

  const filteredPasswords = selectedPasswordFolder
  ? passwords.filter(
      (p) =>
        p.folderId === selectedPasswordFolder.folderId &&
        p.accountName.toLowerCase().includes(searchTerm.toLowerCase())
    )
  : passwords.filter((p) =>
      p.accountName.toLowerCase().includes(searchTerm.toLowerCase())
    );



  const addPassword = async (entry) => {
    try {
      const created = await personalPWService.createPasswordEntry(entry);
      const decrypted = await personalPWService.getDecryptedPassword(
        created.entryId
      );
      setPasswords((prev) => [...prev, created]);
      setSelectedPassword(created);
      setDecryptedPassword(decrypted);
      setIsAddingPassword(false);
      showToast("Password added!", "success");
    } catch {
      showToast("Failed to add password.", "danger");
    }
  };

  const addPasswordFolder = async (passwordFolder) => {
    try {
      setPasswordFolders((prev) => [...prev, passwordFolder]);
      setIsAddingPasswordFolder(false);
      showToast("Password folder created!", 3000);
    } catch {
      showToast("Failed to create password folder.", 3000);
    }
  };

  const deletePasswordFolder = async (passwordFolderId) => {
    const confirmDelete = window.confirm(
      "Are you sure you want to delete this folder?"
    );
    if (!confirmDelete) return;

    try {
      await personalPWService.deletePasswordFolder(passwordFolderId);
      setPasswordFolders(
        passwordFolders.filter(
          (passwordFolder) =>
            passwordFolder.passwordFolderId !== passwordFolderId
        )
      );
      setPasswords(
        passwords.filter(
          (password) => password.passwordFolderId !== passwordFolderId
        )
      );
      showToast("Folder deleted successfully!");

      if (
        selectedPasswordFolder &&
        selectedPasswordFolder.passwordFolderId === passwordFolderId
      ) {
        setSelectedPasswordFolder(null);
      }
    } catch (error) {
      console.error("Error deleting folder:", error);
      showToast("Failed to delete folder.");
    }
  };

  const renamePasswordFolder = (passwordFolder) => {
    setIsRenamingPasswordFolder(true);
    setFolderBeingRenamed({
      ...passwordFolder,
      passwordFolderId:
        passwordFolder.passwordFolderId || passwordFolder.folderId,
    });
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
        setSelectedPassword(null);
        setPasswordToDelete(null);
        showToast("Password deleted!", 3000);
      } catch (err) {
        console.log("Failed to delete password:", err);
        showToast("Failed to delete password.", 3000);
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
        passwordFolderId: selectedPassword.passwordFolderId,
        passwordHash: decrypted,
      });

      console.log("Edit data set:", editData);
      setIsEditing(true);
    } catch (error) {
      console.error("Error decrypting password for editing:", error);
      showToast("Failed to decrypt password for editing.");
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
      showToast("Password updated!", 3000);
    } catch {
      showToast("Failed to update password.", 3000);
    }
  };

  ////// Files///////
  const addFileFolder = async (fileFolder) => {
    try {
      setFileFolders((prev) => [...prev, fileFolder]);
      setIsAddingFileFolder(false);
      showToast("File folder created!", 3000);
    } catch {
      showToast("Failed to create file folder.", 3000);
    }
  };

  const handleFileUpload = async (formData) => {
    try {
      await personalFileService.uploadFiles(
        formData.getAll("files"),
        formData.get("folderId") || null
      );
      const updated = await personalFileService.getAllFiles();
      setFiles(updated);
      setIsAddingFile(false);
      showToast("File(s) uploaded!", "success");
    } catch {
      showToast("File upload failed.", "danger");
    }
  };

  const renameFileFolder = (fileFolder) => {
    const folderId =
      fileFolder.fileFolderId || fileFolder.folderId || fileFolder.id;

    if (!folderId) {
      console.error("Missing file folder ID for renaming:", fileFolder);
      showToast("Error: Invalid file folder selected.", "danger");
      return;
    }

    setIsRenamingFileFolder(true);
    setFileFolderBeingRenamed({ ...fileFolder, fileFolderId: folderId });
    setIsAddingFileFolder(false);
    setSelectedFileFolder(null);
  };

  const handleDeleteFileFolder = async (folderId) => {
    const confirmDelete = window.confirm(
      "Are you sure you want to delete this file folder?"
    );
    if (!confirmDelete) return;

    try {
      await personalFileService.deleteFileFolder(folderId);
      setFileFolders((prev) =>
        prev.filter(
          (folder) =>
            folder.fileFolderId !== folderId && folder.folderId !== folderId
        )
      );
      if (
        selectedFileFolder &&
        (selectedFileFolder.fileFolderId === folderId ||
          selectedFileFolder.folderId === folderId)
      ) {
        setSelectedFileFolder(null);
      }
      showToast("File folder deleted successfully!");
    } catch (err) {
      console.error("Error deleting file folder:", err);
      showToast("Failed to delete file folder.", "danger");
    }
  };

  return (
    <div className="pw-manager-container">
      <button className="home-button" onClick={() => navigate("/home")}>
        🏠︎
      </button>
      <NavbarVaultMaster onLogout={handleLogout} />
      <div className="three-column-container">
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
                      {passwordFolders.map((passwordFolder) => (
                        <div
                          key={passwordFolder.passwordFolderId}
                          className="btn-group w-100 mb-2 folder-split-button"
                        >
                          <button
                            type="button"
                            className={`btn btn-primary flex-grow-1 ${
                              selectedPasswordFolder?.folderId ===
                              passwordFolder.folderId
                                ? "active-folder"
                                : ""
                            }`}
                            onClick={() => {
                              setSelectedPasswordFolder(passwordFolder);
                              setViewMode("passwords");
                            }}
                          >
                            {passwordFolder.folderName}
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
                                onClick={() =>
                                  renamePasswordFolder(passwordFolder)
                                }
                              >
                                Rename
                              </button>
                            </li>
                            <li>
                              <button
                                className="dropdown-item"
                                onClick={() =>
                                  deletePasswordFolder(
                                    passwordFolder.passwordFolderId
                                  )
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
                          setIsRenamingPasswordFolder(false);
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
                          key={folder.fileFolderId}
                          className="btn-group w-100 mb-2 folder-split-button"
                        >
                          <button
                            type="button"
                            className={`btn btn-primary flex-grow-1 ${
                              (selectedFileFolder?.fileFolderId ||
                                selectedFileFolder?.folderId) ===
                              (folder.fileFolderId || folder.folderId)
                                ? "active-folder"
                                : ""
                            }`}
                            onClick={() => {
                              setSelectedFileFolder(folder);
                              setViewMode("files");
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
                                onClick={() => {
                                  const folderId =
                                    folder.fileFolderId ||
                                    folder.folderId ||
                                    folder.id;

                                  if (!folderId) {
                                    console.error(
                                      "Missing file folder ID:",
                                      folder
                                    );
                                    showToast(
                                      "Error: Unable to rename folder.",
                                      "danger"
                                    );
                                    return;
                                  }

                                  setIsRenamingFileFolder(true);
                                  setFileFolderBeingRenamed({
                                    ...folder,
                                    fileFolderId: folderId,
                                  });

                                  setIsAddingFile(false);
                                  setIsAddingFileFolder(false);
                                  setIsAddingPassword(false);
                                  setIsAddingPasswordFolder(false);
                                  setIsRenamingPasswordFolder(false);
                                  setSelectedFile(null);
                                }}
                              >
                                Rename
                              </button>
                            </li>
                            <li>
                              <button
                                className="dropdown-item"
                                onClick={() => {
                                  const folderId =
                                    folder.fileFolderId ||
                                    folder.folderId ||
                                    folder.id;

                                  if (!folderId) {
                                    console.error(
                                      "Missing file folder ID:",
                                      folder
                                    );
                                    showToast(
                                      "Error: Unable to delete folder.",
                                      "danger"
                                    );
                                    return;
                                  }

                                  handleDeleteFileFolder(folderId);
                                }}
                              >
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

          {viewMode === "passwords" && (
            <div className="search-bar-container compact-search">
              <input
                type="text"
                className="form-control"
                placeholder="Search passwords..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>
          )}

          <div className="entry-list">
            {viewMode === "passwords" ? (
              <>
                <button
                  className="add-password-button"
                  onClick={() => {
                    setIsAddingPassword(true);
                    setIsAddingPasswordFolder(false);
                    setIsRenamingPasswordFolder(false);
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
                    setIsRenamingPasswordFolder(false);
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
                          setIsRenamingPasswordFolder(false);
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
          ) : isRenamingPasswordFolder ? (
            <AddFolderForm
              formType="password"
              initialFolderName={folderBeingRenamed.folderName}
              onSave={async ({ folderName }) => {
                try {
                  await personalPWService.renamePasswordFolder(
                    folderBeingRenamed.passwordFolderId,
                    folderName
                  );
                  setPasswordFolders(
                    passwordFolders.map((f) => {
                      const id = f.passwordFolderId || f.folderId || f.id;
                      const renamedId =
                        folderBeingRenamed.passwordFolderId ||
                        folderBeingRenamed.folderId ||
                        folderBeingRenamed.id;
                      return id === renamedId ? { ...f, folderName } : f;
                    })
                  );
                  setIsRenamingPasswordFolder(false);
                  setFolderBeingRenamed(null);
                  showToast("Password folder renamed!");
                } catch (error) {
                  console.error("Error renaming folder:", error);
                  showToast("Failed to rename folder.");
                }
              }}
              onCancel={() => {
                setIsRenamingPasswordFolder(false);
                setFolderBeingRenamed(null);
              }}
            />
          ) : isAddingFile ? (
            <AddFileForm
              folders={fileFolders}
              selectedFolder={selectedFileFolder}
              onSave={handleFileUpload}
              onCancel={() => setIsAddingFile(false)}
            />
          ) : isAddingFileFolder ? (
            <AddFolderForm
              formType="file"
              onSave={addFileFolder}
              onCancel={() => setIsAddingFileFolder(false)}
            />
          ) : isRenamingFileFolder && fileFolderBeingRenamed ? (
            <AddFolderForm
              formType="file"
              initialFolderName={fileFolderBeingRenamed.folderName}
              onSave={async ({ folderName }) => {
                try {
                  await personalFileService.renameFileFolder(
                    fileFolderBeingRenamed.fileFolderId,
                    folderName
                  );

                  setFileFolders(
                    fileFolders.map((f) =>
                      f.folderId === fileFolderBeingRenamed.folderId
                        ? { ...f, folderName }
                        : f
                    )
                  );

                  setIsRenamingFileFolder(false);
                  setFileFolderBeingRenamed(null);
                  showToast("Folder renamed successfully!");
                } catch (error) {
                  console.error("Error renaming folder:", error);
                  showToast("Failed to rename folder.");
                }
              }}
              onCancel={() => {
                setIsRenamingFileFolder(false);
                setFileFolderBeingRenamed(null);
              }}
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
                      onChange={(e) =>
                        setEditData({
                          ...editData,
                          accountName: e.target.value,
                        })
                      }
                    />
                  </div>
                  <div>
                    <label>Username</label>
                    <input
                      type="text"
                      value={editData.username}
                      onChange={(e) =>
                        setEditData({ ...editData, username: e.target.value })
                      }
                    />
                  </div>
                  <div>
                    <label>Website</label>
                    <input
                      type="text"
                      value={editData.website}
                      onChange={(e) =>
                        setEditData({ ...editData, website: e.target.value })
                      }
                    />
                  </div>
                  <div>
                    <label>Password</label>
                    <div
                      style={{
                        display: "flex",
                        alignItems: "center",
                        gap: "10px",
                      }}
                    >
                      <input
                        type={showEditPassword ? "text" : "password"}
                        value={editData.passwordHash}
                        onChange={(e) =>
                          setEditData({
                            ...editData,
                            passwordHash: e.target.value,
                          })
                        }
                        style={{ flex: 1 }}
                      />
                      <span
                        className="icon-button"
                        onClick={() => setShowEditPassword((prev) => !prev)}
                        title={
                          showEditPassword ? "Hide password" : "Show password"
                        }
                      >
                        {showEditPassword ? <FaEyeSlash /> : <FaEye />}
                      </span>
                      <span
                        className="icon-button"
                        onClick={handleCopy}
                        title="Copy password"
                      >
                        <FaRegCopy />
                      </span>
                    </div>
                  </div>
                  <div className="form-buttons">
                    <button
                      type="button"
                      className="btn btn-success me-2"
                      onClick={saveEditing}
                    >
                      Save
                    </button>
                    <button
                      type="button"
                      className="btn btn-secondary"
                      onClick={cancelEditing}
                    >
                      Cancel
                    </button>
                  </div>
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
                    onClick={() => {
                      setPasswordToDelete(selectedPassword);
                      setIsDeleteModalOpen(true);
                    }}
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
              showToast={showToast}
            />
          ) : viewMode === "files" ? (
            <div className="no-password-selected">No File Selected</div>
          ) : (
            <div className="no-password-selected">No Password Selected</div>
          )}
        </div>
      </div>

      <div
        className="toast-container position-fixed bottom-0 end-0 p-3"
        style={{ zIndex: 9999 }}
        id="toast-container"
      ></div>
      {isDeleteModalOpen && (
        <div className="overlay">
          <div className="modal">
            <div className="modal-content">
              <h2>Are you sure you want to delete this password?</h2>
              <div className="modal-buttons">
                <button
                  type="button"
                  className="confirm-button"
                  onClick={async () => {
                    await confirmDeletePassword();
                    setIsDeleteModalOpen(false);
                  }}
                >
                  Yes, Delete
                </button>
                <button
                  type="button"
                  className="cancel-button"
                  onClick={() => setIsDeleteModalOpen(false)}
                >
                  Cancel
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default PersonalPwManager;
