import React from "react";
import { FaRegFile, FaDownload, FaTrash } from "react-icons/fa";
import { downloadTeamFile, deleteTeamFile } from "../api/teamPWFileService";
import "./teamFileInformation.css";

function TeamFileInformation({ file, onClose, onDelete }) {
  if (!file) return null;

  const handleDownload = async () => {
    try {
      const response = await downloadTeamFile(file.fileId);
      
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', file.originalFilename);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (err) {
      console.error("Failed to download file:", err);
      alert("Failed to download file");
    }
  };

  const handleDelete = async () => {
    if (window.confirm(`Are you sure you want to delete ${file.originalFilename}?`)) {
      try {
        await deleteTeamFile(file.fileId);
        onDelete(file.fileId);
        onClose();
      } catch (err) {
        console.error("Failed to delete file:", err);
        alert("Failed to delete file");
      }
    }
  };

  // Format file size to readable format
  const formatFileSize = (bytes) => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  };

  return (
    <div className="team-file-info">
      <h4>{file.originalFilename}</h4>
      <p><strong>File Type:</strong> {file.fileType || "—"}</p>
      <p><strong>Size:</strong> {formatFileSize(file.fileSize)}</p>
      <p><strong>Uploaded:</strong> {new Date(file.uploadDate).toLocaleString()}</p>
      
      <div className="file-actions">
        <button className="file-action-button" onClick={handleDownload}>
          <FaDownload /> Download
        </button>
        <button className="file-action-button delete" onClick={handleDelete}>
          <FaTrash /> Delete
        </button>
      </div>
    </div>
  );
}

export default TeamFileInformation;