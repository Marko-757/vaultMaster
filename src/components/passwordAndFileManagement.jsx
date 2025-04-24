import React, { useEffect, useState } from "react";
import {
  getTeamPasswordFolders,
  getTeamFileFolders,
  getPasswordsInFolder,
  getTeamFilesByFolder,
  decryptTeamPassword,
  renameTeamPasswordFolder,
  renameTeamFileFolder,
} from "../api/teamPWFileService";
import TeamPasswordInformation from "./teamPasswordInformation";
import TeamPasswordPermissions from "./teamPasswordPermissions";
import "./passwordAndFileManagement.css";
import TeamFileInformation from "./teamFileInformation";
import TeamFilePermissions from "./teamFilePermissions";
import FolderDetailsPanel from "./folderDetailsPanel";
import AddTeamPasswordForm from "./addTeamPasswordForm";
import {
  getFolderPermissionsForRole,
  getItemPermissionsForRole,
  getPermissionsForRole,
} from "../api/teamRoleService";
import { getCurrentUserRoleForTeam } from "../api/teamRoleService";

const TeamPasswordFileManager = ({ selectedTeamId, onBack }) => {
  const [folderType, setFolderType] = useState("password");
  const [passwordFolders, setPasswordFolders] = useState([]);
  const [fileFolders, setFileFolders] = useState([]);
  const [selectedFolder, setSelectedFolder] = useState(null);
  const [items, setItems] = useState([]);
  const [selectedItem, setSelectedItem] = useState(null);
  const [decryptedPassword, setDecryptedPassword] = useState(null);
  const [showPassword, setShowPassword] = useState(false);
  const [selectedRoleId, setSelectedRoleId] = useState("");
  const [assignedPermissions, setAssignedPermissions] = useState([]);
  const [showFolderDetails, setShowFolderDetails] = useState(false);
  const [showAddPasswordForm, setShowAddPasswordForm] = useState(false);
  const [passwordPermissions, setPasswordPermissions] = useState([]);
  const [showAddPassword, setShowAddPassword] = useState(false);

  useEffect(() => {
    const fetchRole = async () => {
      try {
        const res = await getCurrentUserRoleForTeam(selectedTeamId);
        setSelectedRoleId(res.data.roleId);
      } catch (err) {
        console.error("Failed to fetch role for user in team:", err);
      }
    };

    if (selectedTeamId) {
      setSelectedFolder(null);
      setSelectedItem(null);
      setItems([]);
      loadFolders(selectedTeamId);
      fetchRole();
    }
  }, [selectedTeamId]);

  const loadFolders = async (selectedTeamId) => {
    try {
      const [pwRes, fileRes] = await Promise.all([
        getTeamPasswordFolders(selectedTeamId),
        getTeamFileFolders(selectedTeamId),
      ]);
      setPasswordFolders(pwRes.data);
      setFileFolders(fileRes.data);

      setSelectedFolder(null);
      setSelectedItem(null);
      setShowFolderDetails(false);
    } catch (err) {
      console.error("Failed to load folders:", err);
    }
  };

  const checkFolderAndGlobalPermissions = async (folderId, folderType) => {
    try {
      const [folderRes, globalRes] = await Promise.all([
        getFolderPermissionsForRole({
          roleId: selectedRoleId,
          folderId,
          folderType,
        }),
        getPermissionsForRole(selectedRoleId),
      ]);

      const folderPerms = folderRes.data.map((p) => p.permissionName);
      const globalPerms = globalRes.data.map((p) => p.permissionName);

      const effectivePerms = new Set(folderPerms);

      if (
        folderType === "password" &&
        globalPerms.includes("MANAGE_TEAM_PASSWORDS")
      ) {
        effectivePerms.add("CREATE_PASSWORD");
        effectivePerms.add("EDIT_PASSWORD");
        effectivePerms.add("DELETE_PASSWORD");
      } else if (
        folderType === "file" &&
        globalPerms.includes("MANAGE_TEAM_FILES")
      ) {
        effectivePerms.add("UPLOAD_FILE");
        effectivePerms.add("DELETE_FILE");
      }

      return [...effectivePerms];
    } catch (err) {
      console.error("Permission fetch failed:", err);
      return [];
    }
  };

  const loadItems = async (folder) => {
    setSelectedFolder(folder);
    setItems([]);
    setSelectedItem(null);
    try {
      if (folderType === "password") {
        const [pwRes, perms] = await Promise.all([
          getPasswordsInFolder(folder.folderId),
          checkFolderAndGlobalPermissions(folder.folderId, "password"),
        ]);

        const passwords = pwRes.data.map((p) => ({
          id: p.teamPasswordId,
          label: p.accountName,
          type: "password",
          ...p,
        }));
        setItems(passwords);
        setPasswordPermissions(perms);

        try {
          const roleId = selectedRoleId;
          const resPerms = await getFolderPermissionsForRole({
            roleId,
            folderId: folder.folderId,
            folderType: "password",
          });
          const folderPerms = resPerms.data.map((p) => p.permissionName);
          setPasswordPermissions(folderPerms); // reuse same state
        } catch (err) {
          console.error("Failed to fetch folder permissions:", err);
          setPasswordPermissions([]);
        }
      } else {
        const res = await getTeamFilesByFolder(folder.folderId);
        const files = res.data.map((f) => ({
          id: f.fileId,
          label: f.originalFilename,
          type: "file",
          ...f,
        }));
        setItems(files);
      }
    } catch (err) {
      console.error("Error loading folder contents:", err);
    }
  };

  const handleRenameFolder = async (folderId, newName) => {
    try {
      if (folderType === "password") {
        await renameTeamPasswordFolder(folderId, newName);
        const updated = await getTeamPasswordFolders(selectedTeamId);
        setPasswordFolders(updated.data);
      } else {
        await renameTeamFileFolder(folderId, newName);
        const updated = await getTeamFileFolders(selectedTeamId);
        setFileFolders(updated.data);
      }

      // Update selected folder in state
      setSelectedFolder((prev) =>
        prev?.folderId === folderId ? { ...prev, folderName: newName } : prev
      );
    } catch (err) {
      console.error("Rename failed:", err);
      alert("Failed to rename folder.");
    }
  };

  const handleSelectItem = async (item) => {
    setSelectedItem(item);
    setShowPassword(false);
    setShowFolderDetails(false);
    setShowAddPassword(false);

    if (item.type === "password") {
      try {
        const res = await decryptTeamPassword(item.id);
        setDecryptedPassword(res.data.decryptedPassword);
      } catch {
        setDecryptedPassword(null);
      }

      try {
        const res = await getItemPermissionsForRole({
          roleId: selectedRoleId,
          itemId: item.type === "password" ? item.teamPasswordId : item.fileId,
          itemType: item.type,
        });
        setPasswordPermissions(res.data.map((p) => p.permissionName));
      } catch (e) {
        console.error("Failed to fetch password permissions:", e);
        setPasswordPermissions([]);
      }
    } else {
      setPasswordPermissions([]);
    }
  };

  const foldersToDisplay =
    folderType === "password" ? passwordFolders : fileFolders;

  const canCreatePassword = passwordPermissions.includes("CREATE_PASSWORD");
  console.log(
    "Can create password?",
    canCreatePassword,
    "Permissions:",
    passwordPermissions
  );

  return (
    <div className="team-manager-layout">
      {/* Folders Column */}
      <div className="team-manager-column folder-column">
        <div className="manager-header">
          <button className="back-button" onClick={onBack}>
            ←
          </button>
          <h2>Folders</h2>
        </div>

        <div className="folder-toggle-buttons">
          <button
            className={`folder-toggle ${
              folderType === "password" ? "active" : ""
            }`}
            onClick={() => {
              setFolderType("password");
              setSelectedFolder(null);
              setSelectedItem(null);
              setShowFolderDetails(false);
            }}
          >
            Passwords
          </button>
          <button
            className={`folder-toggle ${folderType === "file" ? "active" : ""}`}
            onClick={() => {
              setFolderType("file");
              setSelectedFolder(null);
              setSelectedItem(null);
              setShowFolderDetails(false);
            }}
          >
            Files
          </button>
        </div>

        <div className="manager-scroll">
          {foldersToDisplay.length === 0 ? (
            <div className="manager-empty">No {folderType} folders found.</div>
          ) : (
            foldersToDisplay.map((folder) => (
              <div
                key={folder.folderId}
                className={`manager-item ${
                  selectedFolder?.folderId === folder.folderId ? "selected" : ""
                }`}
                onClick={() => {
                  setSelectedItem(null);
                  setShowFolderDetails(false);
                  loadItems(folder);
                }}
              >
                <span>{folder.folderName}</span>
              </div>
            ))
          )}
        </div>
      </div>

      {/* Item Column */}
      <div className="team-manager-column item-column">
        {selectedFolder ? (
          <>
            <div className="item-column-header">
              <h2>{selectedFolder.folderName} Contents</h2>
              <div className="header-actions">
                {passwordPermissions.includes("CREATE_PASSWORD") && (
                  <button
                    className="add-password-button"
                    onClick={() => {
                      setSelectedItem(null);
                      setShowFolderDetails(false);
                      setShowAddPasswordForm(true);
                    }}
                  >
                    + Add Password
                  </button>
                )}

                <button
                  className="folder-details-button"
                  onClick={() => {
                    setSelectedItem(null);
                    setShowAddPasswordForm(false);
                    setShowFolderDetails(true);
                  }}
                >
                  Folder Details
                </button>
              </div>
            </div>

            <div className="manager-scroll">
              {items.length === 0 ? (
                <div className="manager-empty">
                  No items found in this folder.
                </div>
              ) : (
                items.map((item) => (
                  <div
                    key={item.id}
                    className={`manager-item ${
                      selectedItem?.id === item.id ? "selected" : ""
                    }`}
                    onClick={() => {
                      setShowFolderDetails(false);
                      handleSelectItem(item);
                    }}
                  >
                    {item.label}
                  </div>
                ))
              )}
            </div>
          </>
        ) : (
          <div className="manager-empty">Select a folder to view items.</div>
        )}
      </div>

      {/* Details Column */}
      <div className="team-manager-column details-column">
        {selectedItem ? (
          selectedItem.type === "password" ? (
            <>
              <TeamPasswordInformation
                password={selectedItem}
                decryptedPassword={decryptedPassword}
                showPassword={showPassword}
                setShowPassword={setShowPassword}
                canEdit={passwordPermissions.includes("EDIT_PASSWORD")}
              />
              <TeamPasswordPermissions
                selectedTeamId={selectedTeamId}
                passwordId={selectedItem.teamPasswordId}
              />
            </>
          ) : (
            <>
              <TeamFileInformation
                file={selectedItem}
                onClose={() => setSelectedItem(null)}
                onDelete={(fileId) =>
                  setItems((prev) => prev.filter((f) => f.fileId !== fileId))
                }
              />
              <TeamFilePermissions
                selectedTeamId={selectedTeamId}
                fileId={selectedItem.fileId}
              />
            </>
          )
        ) : showAddPasswordForm && selectedFolder ? (
          <AddTeamPasswordForm
            teamId={selectedTeamId}
            folderId={selectedFolder.folderId}
            onSuccess={() => {
              loadItems(selectedFolder);
              setShowAddPasswordForm(false);
            }}
          />
        ) : showFolderDetails && selectedFolder ? (
          <FolderDetailsPanel
            folder={selectedFolder}
            folderType={folderType}
            teamId={selectedTeamId}
            onRename={handleRenameFolder}
            onClose={() => setShowFolderDetails(false)}
          />
        ) : (
          <div className="manager-empty">
            Select a password, file, or click an action.
          </div>
        )}
      </div>
    </div>
  );
};

export default TeamPasswordFileManager;
