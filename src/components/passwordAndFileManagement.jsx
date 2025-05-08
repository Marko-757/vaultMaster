import React, { useEffect, useState } from "react";
import {
  getTeamPasswordFolders,
  getTeamFileFolders,
  getPasswordsInFolder,
  getTeamFilesByFolder,
  decryptTeamPassword,
  renameTeamPasswordFolder,
  renameTeamFileFolder,
  getTeamPasswords,
  getTeamFilesByTeam,
  updateTeamPassword,
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
  getCurrentUserRoleForTeam,
} from "../api/teamRoleService";
import AddTeamFileForm from "./addTeamFileForm";

const PasswordAndFileManagement = ({ selectedTeamId, onBack }) => {
  const [folderType, setFolderType] = useState("password");
  const [passwordFolders, setPasswordFolders] = useState([]);
  const [fileFolders, setFileFolders] = useState([]);
  const [selectedFolder, setSelectedFolder] = useState(null);
  const [items, setItems] = useState([]);
  const [selectedItem, setSelectedItem] = useState(null);
  const [decryptedPassword, setDecryptedPassword] = useState(null);
  const [showPassword, setShowPassword] = useState(false);
  const [selectedRoleId, setSelectedRoleId] = useState("");
  const [passwordPermissions, setPasswordPermissions] = useState([]);
  const [showFolderDetails, setShowFolderDetails] = useState(false);
  const [showAddPasswordForm, setShowAddPasswordForm] = useState(false);
  const [showAddTeamFileForm, setShowAddTeamFileForm] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [editData, setEditData] = useState(null);

  useEffect(() => {
    if (selectedTeamId) {
      setSelectedFolder(null);
      setSelectedItem(null);
      setItems([]);
      loadFolders(selectedTeamId);
      fetchRole(selectedTeamId);
    }
  }, [selectedTeamId]);

  const fetchRole = async (teamId) => {
    try {
      const res = await getCurrentUserRoleForTeam(teamId);
      setSelectedRoleId(res.data.roleId);
    } catch (err) {
      console.error("Failed to fetch user role for team:", err);
    }
  };

  const loadFolders = async (teamId) => {
    try {
      const [pwRes, fileRes] = await Promise.all([
        getTeamPasswordFolders(teamId),
        getTeamFileFolders(teamId),
      ]);
      setPasswordFolders(pwRes.data);
      setFileFolders(fileRes.data);
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
        effectivePerms.add("PASSWORD_EDIT");
        effectivePerms.add("PASSWORD_DELETE");
      } else if (
        folderType === "file" &&
        globalPerms.includes("MANAGE_TEAM_FILES")
      ) {
        effectivePerms.add("FILE_VIEW");
        effectivePerms.add("FILE_DELETE");
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
    setDecryptedPassword(null);
    try {
      if (folderType === "password") {
        if (folder.folderId === "ALL") {
          const pwRes = await getTeamPasswords(selectedTeamId);
          const passwords = pwRes.data.map((p) => ({
            id: p.teamPasswordId,
            label: p.accountName,
            type: "password",
            ...p,
          }));
          setItems(passwords);
        } else {
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
        }
      } else {
        if (folder.folderId === "ALL") {
          const res = await getTeamFilesByTeam(selectedTeamId);
          const files = res.data.map((f) => ({
            id: f.fileId,
            label: f.originalFilename,
            type: "file",
            ...f,
          }));
          setItems(files);
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
    setShowAddPasswordForm(false);
  
    if (item.type === "password") {
      try {
        const [itemPermsRes, folderPermsRes, globalPermsRes] = await Promise.all([
          getItemPermissionsForRole({
            roleId: selectedRoleId,
            itemId: item.teamPasswordId,
            itemType: "password",
          }),
          selectedFolder?.folderId !== "ALL"
            ? getFolderPermissionsForRole({
                roleId: selectedRoleId,
                folderId: selectedFolder.folderId,
                folderType: "password",
              })
            : Promise.resolve({ data: [] }),
          getPermissionsForRole(selectedRoleId),
        ]);
  
        const itemPerms = itemPermsRes.data.map((p) => p.permissionName || p.name);
        const folderPerms = folderPermsRes.data.map((p) => p.permissionName || p.name);
        const globalPerms = globalPermsRes.data.map((p) => p.permissionName || p.name);
  
        const effectivePerms = new Set([...itemPerms, ...folderPerms]);
  
        if (globalPerms.includes("MANAGE_TEAM_PASSWORDS")) {
          effectivePerms.add("PASSWORD_EDIT");
          effectivePerms.add("PASSWORD_DELETE");
        }
  
        setPasswordPermissions([...effectivePerms]);
  
        try {
          const decryptRes = await decryptTeamPassword(item.teamPasswordId);
          setDecryptedPassword(decryptRes.data.decryptedPassword);
        } catch (decryptErr) {
          console.warn("Decryption failed:", decryptErr);
          setDecryptedPassword(null); // Still allow metadata view
        }
  
        console.log("Item:", item);
        console.log("Item perms:", itemPerms);
        console.log("Folder perms:", folderPerms);
        console.log("Global perms:", globalPerms);
        console.log("Effective perms:", [...effectivePerms]);
      } catch (err) {
        console.error("Permission loading failed:", err);
        setPasswordPermissions([]);
        setDecryptedPassword(null);
      }
    } else {
      setPasswordPermissions([]);
    }
  };
  
  

  const startEditing = () => {
    console.log("Editing started"); 
    setEditData({
      teamPasswordId: selectedItem.teamPasswordId,
      accountName: selectedItem.accountName,
      username: selectedItem.username,
      website: selectedItem.website,
      plaintextPassword: decryptedPassword,
    });
    setIsEditing(true);
  };

  const cancelEditing = () => {
    setEditData(null);
    setIsEditing(false);
  };

  const saveEdit = async () => {
    try {
      await updateTeamPassword(editData.teamPasswordId, editData);
      const updated = {
        ...selectedItem,
        ...editData,
      };
      setSelectedItem(updated);
      setIsEditing(false);
      setDecryptedPassword(editData.plaintextPassword);
      alert("Password updated!");
    } catch (err) {
      console.error("Update failed:", err);
      alert("Failed to update password.");
    }
  };

  const foldersToDisplay =
    folderType === "password"
      ? [{ folderId: "ALL", folderName: "All Passwords" }, ...passwordFolders]
      : [{ folderId: "ALL", folderName: "All Files" }, ...fileFolders];

  const showAddPasswordButton = folderType === "password";

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
              setShowAddTeamFileForm(false);
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
              setShowAddPasswordForm(false);
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

      {/* Items Column */}
      <div className="team-manager-column item-column">
        <div className="item-column-header">
          <h4 style={{ textAlign: "center" }}>
            {selectedFolder
              ? `${selectedFolder.folderName} Contents`
              : "Passwords / Files"}
          </h4>
          <div className="header-actions">
            {folderType === "password" && (
              <button
                className="add-password-button"
                onClick={() => {
                  setSelectedItem(null);
                  setShowFolderDetails(false);
                  setShowAddPasswordForm(true);
                  setShowAddTeamFileForm(false);
                }}
              >
                + Add Password
              </button>
            )}
            {folderType === "file" && (
              <button
                className="add-password-button"
                onClick={() => {
                  setSelectedItem(null);
                  setShowFolderDetails(false);
                  setShowAddTeamFileForm(true);
                  setShowAddPasswordForm(false);
                }}
              >
                + Upload File
              </button>
            )}
            {selectedFolder && (
              <button
                className="folder-details-button"
                onClick={() => {
                  setSelectedItem(null);
                  setShowAddPasswordForm(false);
                  setShowAddTeamFileForm(false);
                  setShowFolderDetails(true);
                }}
              >
                Folder Details
              </button>
            )}
          </div>
        </div>

        <div className="manager-scroll">
          {selectedFolder ? (
            items.length === 0 ? (
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
            )
          ) : (
            <div className="manager-empty">
              Select a folder or add a new item.
            </div>
          )}
        </div>
      </div>

      {/* Details Column */}
      <div className="team-manager-column details-column">
        {selectedItem ? (
          selectedItem.type === "password" ? (
            <>
              {isEditing ? (
                <div className="team-password-info">
                  <input
                    name="accountName"
                    value={editData.accountName}
                    onChange={(e) =>
                      setEditData({ ...editData, accountName: e.target.value })
                    }
                    placeholder="Account Name"
                  />
                  <input
                    name="username"
                    value={editData.username}
                    onChange={(e) =>
                      setEditData({ ...editData, username: e.target.value })
                    }
                    placeholder="Username"
                  />
                  <input
                    name="plaintextPassword"
                    value={editData.plaintextPassword}
                    onChange={(e) =>
                      setEditData({
                        ...editData,
                        plaintextPassword: e.target.value,
                      })
                    }
                    placeholder="Password"
                  />
                  <input
                    name="website"
                    value={editData.website}
                    onChange={(e) =>
                      setEditData({ ...editData, website: e.target.value })
                    }
                    placeholder="Website (optional)"
                  />
                  <div className="team-edit-buttons">
                    <button className="rename-save-button" onClick={saveEdit}>
                      Save
                    </button>
                    <button
                      className="rename-cancel-button"
                      onClick={cancelEditing}
                    >
                      Cancel
                    </button>
                  </div>
                </div>
              ) : (
                <>
                  <TeamPasswordInformation
                    password={selectedItem}
                    decryptedPassword={decryptedPassword}
                    showPassword={showPassword}
                    setShowPassword={setShowPassword}
                    canEdit={
                      passwordPermissions.includes("PASSWORD_EDIT") ||
                      passwordPermissions.includes("MANAGE_TEAM_PASSWORDS")
                    }
                    onEditClick={startEditing}
                  />

                  <TeamPasswordPermissions
                    selectedTeamId={selectedTeamId}
                    passwordId={selectedItem.teamPasswordId}
                  />
                </>
              )}
            </>
          ) : (
            <>
              <TeamPasswordInformation
                password={selectedItem}
                decryptedPassword={decryptedPassword}
                showPassword={showPassword}
                setShowPassword={setShowPassword}
                canEdit={
                  passwordPermissions.includes("PASSWORD_EDIT") ||
                  passwordPermissions.includes("MANAGE_TEAM_PASSWORDS")
                }
                onEditClick={startEditing}
              />

              <TeamPasswordPermissions
                selectedTeamId={selectedTeamId}
                fileId={selectedItem.fileId}
              />
            </>
          )
        ) : showAddPasswordForm ? (
          <AddTeamPasswordForm
            teamId={selectedTeamId}
            folderId={selectedFolder?.folderId || ""}
            onSuccess={() => {
              if (selectedFolder) loadItems(selectedFolder);
              setShowAddPasswordForm(false);
            }}
          />
        ) : showAddTeamFileForm ? (
          <AddTeamFileForm
            teamId={selectedTeamId}
            folderId={selectedFolder?.folderId || ""}
            onSuccess={() => {
              if (selectedFolder) loadItems(selectedFolder);
              setShowAddTeamFileForm(false);
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

export default PasswordAndFileManagement;
