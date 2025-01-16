import React, { useState } from "react";
import PasswordGenerator from "./passwordGenerator";
import "./addPasswordForm.css";

function AddPasswordForm({ folders, selectedFolder, onSave, onCancel }) {
  const [formData, setFormData] = useState({
    name: "",
    username: "",
    password: "",
    website: "",
    folderId: selectedFolder?.id || null,
  });

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSave = () => {
    if (formData.name && formData.username && formData.password) {
      onSave(formData);
    } else {
      alert("Please fill out all fields.");
    }
  };

  return (
    <div className="add-password-form">
      <h3>Add New Password</h3>
      <input
        type="text"
        name="name"
        placeholder="Account Name (e.g., GitHub)"
        value={formData.name}
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
          <option key={folder.id} value={folder.id}>
            {folder.name}
          </option>
        ))}
      </select>
      <PasswordGenerator onGenerate={(password) => setFormData({ ...formData, password })} />
      <div className="form-buttons">
        <button onClick={handleSave}>Save</button>
        <button onClick={onCancel}>Cancel</button>
      </div>
    </div>
  );
}

export default AddPasswordForm;
