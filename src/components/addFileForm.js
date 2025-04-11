import React, { useState } from "react";
import "./addPasswordForm.css"; 

function AddFileForm({ folders = [], selectedFolder, onSave, onCancel }) {
  const [formData, setFormData] = useState({
    folderId: selectedFolder?.folderId || "",
    files: [],
  });

  const [uploading, setUploading] = useState(false);

  const handleFileChange = (e) => {
    setFormData((prev) => ({
      ...prev,
      files: Array.from(e.target.files),
    }));
  };

  const handleFolderChange = (e) => {
    const folderId = e.target.value || null;
    setFormData((prev) => ({ ...prev, folderId }));
  };

  const handleSubmit = async () => {
    if (uploading) return;
    setUploading(true);

    if (formData.files.length === 0) {
      alert("Please select at least one file to upload.");
      setUploading(false);
      return;
    }

    try {
      const formDataToSend = new FormData();
      formData.files.forEach((file) => formDataToSend.append("files", file));
      if (formData.folderId) {
        formDataToSend.append("folderId", formData.folderId);
      }

      await onSave(formDataToSend); // Passed up to parent

    } catch (error) {
      console.error("Error uploading files:", error);
      alert("Failed to upload file(s).");
    } finally {
      setUploading(false);
    }
  };

  return (
    <div className="add-password-form">
      <h3>Add File(s)</h3>

      <input type="file" multiple onChange={handleFileChange} />

      <select value={formData.folderId || ""} onChange={handleFolderChange}>
        <option value="">No Folder</option>
        {folders.map((folder) => (
          <option key={folder.folderId} value={folder.folderId}>
            {folder.folderName}
          </option>
        ))}
      </select>

      <div className="form-buttons">
        <button onClick={handleSubmit} disabled={uploading}>
          {uploading ? "Uploading..." : "Upload"}
        </button>
        <button onClick={onCancel}>Cancel</button>
      </div>
    </div>
  );
}

export default AddFileForm;
