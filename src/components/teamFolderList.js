import React, { useEffect, useState } from "react";
import { getTeamPasswordFolders, getTeamFileFolders } from "../api/teamPWFileService";
import { HiDotsVertical } from "react-icons/hi";
import "./teamFolderList.css";

const TeamFolderList = ({ teamId, onSelectFolder, onOpenFolderPermissions }) => {
  const [passwordFolders, setPasswordFolders] = useState([]);
  const [fileFolders, setFileFolders] = useState([]);

  useEffect(() => {
    if (!teamId) return;
    const fetchFolders = async () => {
      try {
        const [pwRes, fileRes] = await Promise.all([
          getTeamPasswordFolders(teamId),
          getTeamFileFolders(teamId),
        ]);
        setPasswordFolders(pwRes.data);
        setFileFolders(fileRes.data);
      } catch (err) {
        console.error("Error loading folders", err);
      }
    };
    fetchFolders();
  }, [teamId]);

  const renderFolderList = (folders, type) =>
    folders.map((folder) => (
      <div key={folder.folderId} className="folder-button-container">
        <div
          className="folder-button"
          onClick={() => onSelectFolder(folder, type)}
        >
          {folder.folderName}
        </div>
        <HiDotsVertical
          className="folder-settings-icon"
          title="Folder Actions"
          onClick={() => onOpenFolderPermissions(folder, type)}
        />
      </div>
    ));

  return (
    <div className="folder-list-column">
      <div className="folder-section">
        <h3>Password Folders</h3>
        {renderFolderList(passwordFolders, "password")}
      </div>

      <div className="folder-section">
        <h3>File Folders</h3>
        {renderFolderList(fileFolders, "file")}
      </div>
    </div>
  );
};

export default TeamFolderList;
