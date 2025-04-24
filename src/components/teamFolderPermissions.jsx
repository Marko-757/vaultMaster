import React, { useEffect, useState } from "react";
import {
  getRolesForTeam,
  getGlobalPermissions,
  getFolderPermissionsForRole,
  assignItemPermissions,
  removeItemPermission,
} from "../api/teamRoleService";
import "./teamPasswordPermissions.css"; // Reuse styles

const permissionLabels = {
  MANAGE_PASSWORDS: "Manage Passwords",
  VIEW_PASSWORDS: "View Passwords",
  MANAGE_FILES: "Manage Files",
  VIEW_FILES: "View Files",
};

const folderPermissionsMap = {
  password: ["MANAGE_PASSWORDS", "VIEW_PASSWORDS"],
  file: ["MANAGE_FILES", "VIEW_FILES"],
};

const TeamFolderPermissions = ({ teamId, folderId, folderType }) => {
  const [roles, setRoles] = useState([]);
  const [permissions, setPermissions] = useState([]);
  const [selectedRoleId, setSelectedRoleId] = useState("");
  const [assignedPermissions, setAssignedPermissions] = useState([]);

  useEffect(() => {
    if (teamId) {
      getRolesForTeam(teamId)
        .then((res) => setRoles(res.data))
        .catch((err) => console.error("Failed to load roles", err));

      getGlobalPermissions()
        .then((res) => {
          const filtered = res.data.filter((p) =>
            folderPermissionsMap[folderType].includes(p.name)
          );
          setPermissions(filtered);
        })
        .catch((err) => console.error("Failed to load permissions", err));
    }
  }, [teamId, folderType]);

  const handleRoleChange = async (e) => {
    const roleId = e.target.value;
    setSelectedRoleId(roleId);
    try {
      const res = await getFolderPermissionsForRole({
        roleId,
        folderId,
        folderType,
      });
      const names = res.data.map((p) => p.name);
      setAssignedPermissions(names);
    } catch (err) {
      console.error("Failed to load folder permissions", err);
    }
  };

  const handleToggle = async (permName, enabled) => {
    const payload = {
      roleId: selectedRoleId,
      itemId: folderId,
      itemType: "folder",
      teamId,
      permissions: [permName],
    };

    try {
      if (enabled) {
        await assignItemPermissions(payload);
        setAssignedPermissions((prev) => [...new Set([...prev, permName])]);
      } else {
        await removeItemPermission(payload);
        setAssignedPermissions((prev) =>
          prev.filter((p) => p !== permName)
        );
      }
    } catch (err) {
      console.error("Failed to update folder permission", err);
    }
  };

  return (
    <div className="password-permission-container">
      <h4>Assign Folder Permissions to Role</h4>
      <p className="permissions-note">
        *Folder Permissions apply to all items unless overridden.
      </p>
      <select
        value={selectedRoleId}
        onChange={handleRoleChange}
        className="role-select"
      >
        <option value="">Select a role</option>
        {roles.map((role) => (
          <option key={role.roleId} value={role.roleId}>
            {role.roleName}
          </option>
        ))}
      </select>

      {selectedRoleId && (
        <div className="permission-grid">
          {permissions.map((perm) => {
            const isEnabled = assignedPermissions.includes(perm.name);
            return (
              <div key={perm.name} className="permission-toggle">
                <span className="perm-label">
                  {permissionLabels[perm.name] || perm.displayName}
                </span>
                <label className="toggle-switch">
                  <input
                    type="checkbox"
                    checked={isEnabled}
                    onChange={(e) =>
                      handleToggle(perm.name, e.target.checked)
                    }
                  />
                  <span className="slider"></span>
                </label>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};

export default TeamFolderPermissions;
