import React, { useState, useEffect } from "react";
import { uploadTeamFiles, getTeamFileFolders } from "../api/teamPWFileService";
import "./addTeamFileForm.css";

const AddTeamFileForm = ({ teamId, folderId: initialFolderId, onSuccess }) => {
  const [selectedFiles, setSelectedFiles] = useState([]);
  const [selectedFolderId, setSelectedFolderId] = useState(initialFolderId || "");
  const [fileFolders, setFileFolders] = useState([]);
  const [dragging, setDragging] = useState(false);

  useEffect(() => {
    async function fetchFolders() {
      try {
        const res = await getTeamFileFolders(teamId);
        setFileFolders(res.data);
      } catch (err) {
        console.error("Failed to load file folders:", err);
      }
    }
    fetchFolders();
  }, [teamId]);

  const handleFileChange = (e) => {
    setSelectedFiles(Array.from(e.target.files));
  };

  const handleDrop = (e) => {
    e.preventDefault();
    setDragging(false);
    setSelectedFiles(Array.from(e.dataTransfer.files));
  };

  const handleDragOver = (e) => {
    e.preventDefault();
    setDragging(true);
  };

  const handleDragLeave = () => setDragging(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!selectedFiles.length) return alert("Please select files to upload.");

    const formData = new FormData();
    selectedFiles.forEach((file) => formData.append("files", file));
    formData.append("teamId", teamId);
    if (selectedFolderId) formData.append("folderId", selectedFolderId);

    try {
      await uploadTeamFiles(formData);
      onSuccess();
    } catch (err) {
      console.error("Upload failed:", err);
      alert("Failed to upload files.");
    }
  };

  return (
    <div className="add-file-form-container">
      <h2 className="form-title">Upload Files</h2>
      <form className="add-file-form" onSubmit={handleSubmit}>
        <div
          className={`drop-zone ${dragging ? "drag-over" : ""}`}
          onDragOver={handleDragOver}
          onDragLeave={handleDragLeave}
          onDrop={handleDrop}
        >
          <p>Drag & drop files here</p>
          <p>or</p>
          <input type="file" multiple onChange={handleFileChange} className="file-input" />
        </div>

        {selectedFiles.length > 0 && (
          <div className="file-preview">
            <p>Selected Files:</p>
            <ul>
              {selectedFiles.map((file, index) => (
                <li key={index}>{file.name}</li>
              ))}
            </ul>
          </div>
        )}

        <select
          value={selectedFolderId}
          onChange={(e) => setSelectedFolderId(e.target.value)}
        >
          <option value="">No Folder</option>
          {fileFolders.map((folder) => (
            <option key={folder.folderId} value={folder.folderId}>
              {folder.folderName}
            </option>
          ))}
        </select>

        <div className="form-buttons">
          <button type="submit" className="upload-button">Upload Files</button>
          <button type="button" className="cancel-button" onClick={onSuccess}>Cancel</button>
        </div>
      </form>
    </div>
  );
};

export default AddTeamFileForm;
