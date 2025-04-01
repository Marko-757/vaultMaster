import React from "react";
import * as personalFileService from "../api/personalFileService";
import "./fileInformation.css";

function FileInformation({ file, onClose, onDelete, showToast }) {
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

      showToast("File downloaded!", 3000);
    } catch (error) {
      console.error("Failed to download file:", error);
      showToast("Failed to download file.", 3000);
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

  const getFileExtensionLabel = (filename) => {
    const parts = filename.split(".");
    if (parts.length < 2) return "Unknown";
    const ext = parts.pop().toLowerCase();
    return `.${ext}`;
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
        <strong>Type:</strong> {getFileExtensionLabel(file.originalFilename)}
      </p>

      <p>
        <strong>Size:</strong>{" "}
        {file.fileSize > 1024
          ? `${(file.fileSize / 1024).toFixed(2)} KB`
          : `${file.fileSize} bytes`}
      </p>
      <div className="file-actions">
        <button
          className="file-action-button download-button"
          onClick={handleDownload}
        >
          Download
        </button>
        <button
          className="file-action-button delete-button"
          onClick={handleDelete}
        >
          Delete
        </button>
      </div>
    </div>
  );
}

export default FileInformation;
