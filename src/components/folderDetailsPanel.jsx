import React, { useState } from "react";
import "./folderDetailsPanel.css";
import TeamFolderPermissions from "./teamFolderPermissions";

const FolderDetailsPanel = ({
  folder,
  folderType,
  teamId,
  onRename,
  onDelete,
  onClose,
}) => {
  const [newName, setNewName] = useState(folder.folderName);
  const [isRenaming, setIsRenaming] = useState(false);

  const handleRename = () => {
    if (newName.trim() && newName !== folder.folderName) {
      onRename(folder.folderId, newName);
      setIsRenaming(false);
    }
  };

  const handleDelete = () => {
    if (
      window.confirm(
        `Are you sure you want to delete folder '${folder.folderName}'?`
      )
    ) {
      onDelete(folder.folderId);
    }
  };

  return (
    <div className="folder-details-panel">
      <div className="panel-header">
        <h3>Folder Details</h3>
      </div>

      <div className="panel-section">
        <label className="section-label">Folder Name</label>
        {isRenaming ? (
          <div className="rename-controls">
            <input
              type="text"
              value={newName}
              onChange={(e) => setNewName(e.target.value)}
              className="rename-input"
            />
            <button onClick={handleRename} className="rename-save-button">
              Save
            </button>
            <button
              onClick={() => {
                setNewName(folder.folderName);
                setIsRenaming(false);
              }}
              className="rename-cancel-button"
            >
              Cancel
            </button>
          </div>
        ) : (
          <div className="rename-display">
            <span>{folder.folderName}</span>
            <button
              className="rename-toggle-button"
              onClick={() => setIsRenaming(true)}
            >
              Rename
            </button>
          </div>
        )}
      </div>

      <div className="panel-section">
        <label className="section-label">Permissions</label>
        <TeamFolderPermissions
          folderId={folder.folderId}
          folderType={folderType}
          selectedTeamId={teamId}
        />
      </div>

      <div className="panel-section">
        <button className="delete-button" onClick={handleDelete}>
          Delete Folder
        </button>
      </div>
    </div>
  );
};

export default FolderDetailsPanel;
