import React, { useState } from "react";
import PasswordGenerator from "./passwordGenerator";
import "./addPasswordForm.css";

function AddPasswordForm({ folders = [], selectedFolder, onSave, onCancel }) {
  const [formData, setFormData] = useState({
    accountName: "",
    username: "",
    password: "",
    website: "",
    folderId: selectedFolder?.folderId || "",
  });

  const [saving, setSaving] = useState(false);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSave = async () => {
    if (saving) return;
    setSaving(true);

    console.log("handleSave triggered");

    if (!formData.accountName || !formData.username || !formData.password) {
      alert("Please fill in all required fields.");
      setSaving(false);
      return;
    }

    const dataToSubmit = {
      ...formData,
      passwordHash: formData.password,
      website: formData.website?.trim() === "" ? null : formData.website,
    };

    try {
      console.log("Submitting password entry to parent:", dataToSubmit);
      onSave(dataToSubmit);
    } catch (error) {
      console.error("Error passing password up:", error.message);
      alert("Failed to add password.");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="add-password-form">
      <h3>Add New Password</h3>
      <input
        type="text"
        name="accountName"
        placeholder="Account Name (e.g., GitHub)"
        value={formData.accountName}
        onChange={handleInputChange}
      />
      <input
        type="text"
        name="username"
        placeholder="Username"
        value={formData.username}
        onChange={handleInputChange}
      />
      <input
        type="text"
        name="password"
        placeholder="Password"
        value={formData.password}
        onChange={handleInputChange}
      />
      <PasswordGenerator
        onGenerate={(password) =>
          setFormData((prev) => ({ ...prev, password }))
        }
      />
      <input
        type="text"
        name="website"
        placeholder="Website (optional)"
        value={formData.website}
        onChange={handleInputChange}
      />
      <select
        name="folderId"
        value={formData.folderId || ""}
        onChange={(e) =>
          setFormData({ ...formData, folderId: e.target.value || null })
        }
      >
        <option value="">No Folder</option>
        {folders.map((folder) => (
          <option key={folder.folderId} value={folder.folderId}>
            {folder.folderName}
          </option>
        ))}
      </select>

      <div className="form-buttons">
        <button onClick={handleSave} disabled={saving}>
          {saving ? "Saving..." : "Save"}
        </button>
        <button onClick={onCancel}>Cancel</button>
      </div>
    </div>
  );
}

export default AddPasswordForm;
