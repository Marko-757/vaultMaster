import React, { useEffect, useState } from "react";
import {
  createRole,
  getRolesForTeam,
  getGlobalPermissions,
  updateRolePermissions,
} from "../api/teamRoleService";
import "./manageRoles.css";

const permissionCategories = {
  "Team Management": ["TEAM_MANAGE", "MANAGE_TEAM_ROLES", "INVITE_TEAM_MEMBER", "VIEW_TEAM_MEMBERS"],
  "Password Management": [
    "MANAGE_TEAM_PASSWORDS",
    "PASSWORD_VIEW",
    "PASSWORD_EDIT",
    "PASSWORD_DELETE",
    "MOVE_TEAM_PASSWORD",
    "MANAGE_PASSWORD_FOLDERS",
  ],
  "File Management": [
    "MANAGE_TEAM_FILES",
    "FILE_VIEW",
    "FILE_UPLOAD",
    "FILE_DELETE",
    "DOWNLOAD_TEAM_FILE",
    "MOVE_TEAM_FILE",
    "MANAGE_FILE_FOLDERS",
  ],
  "Advanced Permissions": ["MANAGE_ITEM_PERMISSIONS"],
};

const ManageRoles = ({ selectedTeamId, onBack }) => {
  const [roles, setRoles] = useState([]);
  const [newRoleName, setNewRoleName] = useState("");
  const [selectedRole, setSelectedRole] = useState(null);
  const [permissions, setPermissions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [toggleLoading, setToggleLoading] = useState(false);
  const [showAddModal, setShowAddModal] = useState(false);

  useEffect(() => {
    if (selectedTeamId) {
      setSelectedRole(null);
      loadRoles();
      loadGlobalPermissions();
    }
  }, [selectedTeamId]);

  const loadRoles = async () => {
    try {
      const response = await getRolesForTeam(selectedTeamId);
      setRoles(response.data);
    } catch (err) {
      console.error("Failed to load roles", err);
    }
  };

  const loadGlobalPermissions = async () => {
    try {
      const response = await getGlobalPermissions();
      setPermissions(response.data);
    } catch (err) {
      console.error("Failed to load permissions", err);
    }
  };

  const handleAddRole = async () => {
    if (!newRoleName.trim()) return;

    try {
      setLoading(true);
      await createRole(selectedTeamId, newRoleName);
      setNewRoleName("");
      setShowAddModal(false);
      await loadRoles();
    } catch (err) {
      console.error("Failed to create role", err);
    } finally {
      setLoading(false);
    }
  };

  const handlePermissionToggle = async (permissionName, isEnabled) => {
    if (!selectedRole) return;

    try {
      setToggleLoading(true);
      const currentPermissions = selectedRole.permissions?.map((p) => p.name) || [];
      const updatedPermissionNames = isEnabled
        ? [...new Set([...currentPermissions, permissionName])]
        : currentPermissions.filter((name) => name !== permissionName);

      await updateRolePermissions(selectedRole.roleId, updatedPermissionNames);
      const updatedRolesRes = await getRolesForTeam(selectedTeamId);
      const updatedRoles = updatedRolesRes.data;
      setRoles(updatedRoles);

      const refreshedRole = updatedRoles.find((r) => r.roleId === selectedRole.roleId);
      setSelectedRole(refreshedRole);
    } catch (err) {
      console.error("Failed to update permissions", err);
    } finally {
      setToggleLoading(false);
    }
  };

  const renderPermissionSection = (sectionTitle, permissionNames) => {
    const sectionPermissions = permissions.filter((p) => permissionNames.includes(p.name));
    if (sectionPermissions.length === 0) return null;

    return (
      <div className="permission-section" key={sectionTitle}>
        <h4 className="permission-category">{sectionTitle}</h4>
        {sectionPermissions.map((permission) => {
          const isEnabled = selectedRole.permissions?.some((p) => p.name === permission.name);
          return (
            <div key={permission.permissionId} className="permission-item">
              <div className="permission-info">
                <h4>{permission.displayName || permission.name}</h4>
                <p>{permission.description}</p>
              </div>
              <label className="toggle-switch">
                <input
                  type="checkbox"
                  checked={isEnabled}
                  onChange={(e) => handlePermissionToggle(permission.name, e.target.checked)}
                  disabled={toggleLoading}
                />
                <span className="slider"></span>
              </label>
            </div>
          );
        })}
        <hr className="permission-divider" />
      </div>
    );
  };

  return (
    <>
      <div className="manage-roles-container">
        <div className="roles-list-column">
          <div className="roles-header">
            <button className="back-button" onClick={onBack}>
              ←
            </button>
            <h2>Roles</h2>
          </div>

          <div className="roles-scroll-container">
            {roles.map((role) => (
              <div
                key={role.roleId}
                className={`role-item ${selectedRole?.roleId === role.roleId ? "selected" : ""}`}
                onClick={() => setSelectedRole(role)}
              >
                <div className="role-name">{role.roleName}</div>
                {role.isOwner && <span className="owner-badge">Owner</span>}
              </div>
            ))}
          </div>

          <div className="add-role-container">
            <button className="add-role-btn" onClick={() => setShowAddModal(true)}>
              + Add Role
            </button>
          </div>
        </div>

        <div className="permissions-column">
          {selectedRole ? (
            <>
              <h3>Global Permissions for {selectedRole.roleName}</h3>
              <div className="permissions-scroll-container">
                {Object.entries(permissionCategories).map(([category, perms]) =>
                  renderPermissionSection(category, perms)
                )}
              </div>
            </>
          ) : (
            <div className="no-role-selected">
              <p>Select a role to view and edit permissions</p>
            </div>
          )}
        </div>
      </div>

      {showAddModal && (
        <div className="roles-modal-overlay">
          <div className="roles-modal-content">
            <h3>Create New Role</h3>
            <input
              type="text"
              placeholder="Enter role name"
              value={newRoleName}
              onChange={(e) => setNewRoleName(e.target.value)}
              disabled={loading}
            />
            <div className="roles-modal-actions">
              <button
                className="add-role-btn"
                onClick={handleAddRole}
                disabled={loading || !newRoleName.trim()}
              >
                {loading ? "Adding..." : "Create"}
              </button>
              <button
                className="cancel-role-btn"
                onClick={() => {
                  setShowAddModal(false);
                  setNewRoleName("");
                }}
                disabled={loading}
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default ManageRoles;
