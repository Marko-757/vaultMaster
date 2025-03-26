import React from "react";
import * as personalFileService from "../api/personalFileService";

function FileInformation({ file, onClose, onDelete }) {
  const handleDownload = async () => {
    try {
      const blob = await personalFileService.downloadFile(file.fileKey);
      const url = window.URL.createObjectURL(new Blob([blob]));
      const a = document.createElement("a");
      a.href = url;
      a.download = file.originalFilename;
      document.body.appendChild(a);
      a.click();
      a.remove();
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error("Failed to download file:", error);
      alert("Failed to download file.");
    }
  };

  const handleDelete = async () => {
    const confirmDelete = window.confirm(`Delete ${file.originalFilename}?`);
    if (!confirmDelete) return;

    try {
      await personalFileService.deleteFile(file.fileId);
      alert("File deleted successfully!");
      onDelete(file.fileId);
    } catch (error) {
      console.error("Failed to delete file:", error);
      alert("Failed to delete file.");
    }
  };

  return (
    <div className="file-detail-view">
      <button className="close-detail-button" onClick={onClose}>
        X
      </button>

      <h3>File Details</h3>
      <p>
        <strong>Name:</strong> {file.originalFilename}
      </p>
      <p>
        <strong>Type:</strong> {file.extension || "Unknown"}
      </p>
      <p>
        <strong>Size:</strong>{" "}
        {file.fileSize > 1024
          ? `${(file.fileSize / 1024).toFixed(2)} KB`
          : `${file.fileSize} bytes`}
      </p>

      <div className="file-actions">
        <button className="download-button" onClick={handleDownload}>
          Download
        </button>
        <button className="delete-file-button" onClick={handleDelete}>
          Delete
        </button>
      </div>
    </div>
  );
}

export default FileInformation;
