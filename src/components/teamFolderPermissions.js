import React, { useEffect, useState } from "react";
import {
  getRolesForTeam,
  getGlobalPermissions,
  getFolderPermissionsForRole,
} from "../api/teamRoleService";

import axios from "axios";
import "./teamPasswordPermissions.css"; 

const folderPermissionLabels = {
  PASSWORD_VIEW: "View Passwords",
  PASSWORD_EDIT: "Edit Passwords",
  PASSWORD_DELETE: "Delete Passwords",
  FILE_VIEW: "View Files",
  FILE_UPLOAD: "Upload Files",
  FILE_DELETE: "Delete Files",
};

const TeamFolderPermissions = ({ selectedTeamId, folderId, folderType }) => {
  const [roles, setRoles] = useState([]);
  const [permissions, setPermissions] = useState([]);
  const [selectedRoleId, setSelectedRoleId] = useState("");
  const [assignedPermissions, setAssignedPermissions] = useState([]);

  useEffect(() => {
    if (selectedTeamId) {
      getRolesForTeam(selectedTeamId)
        .then((res) => setRoles(res.data))
        .catch((err) => console.error("Failed to load roles", err));

      loadPermissions();
    }
  }, [selectedTeamId]);

  const loadPermissions = async () => {
    try {
      const response = await getGlobalPermissions();
  
      const filtered = response.data.filter((perm) => {
        if (folderType === "password") {
          return (
            perm.name === "PASSWORD_VIEW" ||
            perm.name === "PASSWORD_EDIT" ||
            perm.name === "PASSWORD_DELETE"
          );
        } else {
          return (
            perm.name === "FILE_VIEW" ||
            perm.name === "FILE_UPLOAD" ||
            perm.name === "FILE_DELETE"
          );
        }
      });
  
      setPermissions(filtered);
    } catch (err) {
      console.error("Failed to load permissions", err);
    }
  };
  
  

  const handleToggle = async (permissionName, enabled) => {
    if (!selectedRoleId || !folderId) return;

    const payload = {
      roleId: selectedRoleId,
      folderId: folderId,
      folderType: folderType,
      permissions: [permissionName],
      teamId: selectedTeamId,
    };

    try {
      if (enabled) {
        await axios.post("/api/permissions/folder", payload);
        setAssignedPermissions((prev) => [...new Set([...prev, permissionName])]);
      } else {
        await axios.delete("/api/permissions/folder", {
          data: payload,
          headers: { Authorization: `Bearer ${localStorage.getItem("jwtToken")}` },
          withCredentials: true,
        });
        setAssignedPermissions((prev) =>
          prev.filter((perm) => perm !== permissionName)
        );
      }
    } catch (err) {
      console.error("Failed to update folder permission:", err);
    }
  };

  const handleRoleChange = async (e) => {
    const roleId = e.target.value;
    setSelectedRoleId(roleId);

    try {
      const res = await getFolderPermissionsForRole({
        roleId,
        folderId,
        folderType,
      });
      const permissionNames = res.data.map((p) => p.name);
      setAssignedPermissions(permissionNames);
    } catch (err) {
      console.error("Failed to load folder permissions:", err);
      setAssignedPermissions([]);
    }
  };

  return (
    <div className="password-permission-container">
      <h4>Assign Folder Permissions to Role</h4>
      <p className="permissions-note">
        *Folder Permissions apply to all items in the folder unless overridden
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
              <div key={perm.id} className="permission-toggle">
                <span className="perm-label">
                  {folderPermissionLabels[perm.name] ||
                    perm.displayName ||
                    perm.name}
                </span>
                <label className="toggle-switch">
                  <input
                    type="checkbox"
                    checked={isEnabled}
                    onChange={(e) => handleToggle(perm.name, e.target.checked)}
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
