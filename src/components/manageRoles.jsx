import React, { useState } from "react";
import { FaUserCircle } from "react-icons/fa";
import { useNavigate } from "react-router-dom"; // Needed for account settings navigation
import "./manageRoles.css";

const ManageRoles = ({ selectedTeam, onBack }) => {
  const [roles, setRoles] = useState([]);
  const [showAddRoleModal, setShowAddRoleModal] = useState(false);
  const [newRoleName, setNewRoleName] = useState("");

  const navigate = useNavigate(); // Hook for navigation

  const toggleRole = (id) => {
    setRoles(
      roles.map((role) =>
        role.id === id ? { ...role, expanded: !role.expanded } : role
      )
    );
  };

  const openAddRoleModal = () => {
    setNewRoleName("");
    setShowAddRoleModal(true);
  };

  const addRole = () => {
    if (newRoleName.trim() !== "") {
      const newRole = {
        id: roles.length + 1,
        name: newRoleName,
        passwords: [],
        files: [],
        expanded: false,
      };
      setRoles([...roles, newRole]);
      setShowAddRoleModal(false);
    }
  };

  const deleteRole = (id) => {
    if (window.confirm("Are you sure you want to delete this role?")) {
      setRoles(roles.filter((role) => role.id !== id));
    }
  };

  return (
    <div className="roles-container">
      {/* Wrapper for Banner, Back Button & Profile */}
      <div className="banner-wrapper">
        <button className="back-button" onClick={onBack}>←</button>
        <h2 className="section-banner">{selectedTeam} - Password and File Management</h2>
        <button className="profile-button" onClick={() => navigate("/settings")}>
          <FaUserCircle />
        </button>        
        </div>

      <div className="roles-list">
        {roles.map((role) => (
          <div key={role.id} className="role-item">
            <div className="role-header" onClick={() => toggleRole(role.id)}>
              {role.name}
              <span className="toggle-icon">{role.expanded ? "▲" : "▼"}</span>
            </div>

            {role.expanded && (
              <div className="role-details">
                <div className="view-section">
                  <strong>Can View:</strong>
                  <div className="view-items">
                    <div className="passwords">
                      <p>Passwords:</p>
                      {role.passwords.length > 0 ? (
                        <div className="view-box">{role.passwords.join(", ")}</div>
                      ) : (
                        <span>None</span>
                      )}
                    </div>

                    <div className="files">
                      <p>Files:</p>
                      {role.files.length > 0 ? (
                        <div className="view-box">{role.files.join(", ")}</div>
                      ) : (
                        <span>None</span>
                      )}
                    </div>
                  </div>
                </div>

                <div className="role-actions">
                  <button className="settings-button">⚙️</button>
                  <button className="delete-button" onClick={() => deleteRole(role.id)}>🗑️</button>
                </div>
              </div>
            )}
          </div>
        ))}
      </div>

      <button className="add-role-button" onClick={openAddRoleModal}>
        Add Role
      </button>

      {showAddRoleModal && (
        <div className="modal">
          <div className="modal-content">
            <h3>Add New Role</h3>
            <input
              type="text"
              placeholder="Enter Role Name"
              value={newRoleName}
              onChange={(e) => setNewRoleName(e.target.value)}
            />
            <div className="modal-buttons">
              <button className="confirm-button" onClick={addRole}>Add</button>
              <button className="cancel-button" onClick={() => setShowAddRoleModal(false)}>Cancel</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ManageRoles;
