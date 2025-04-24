import React, { useEffect, useState } from "react";
import {
  getRolesForTeam,
  assignItemPermissions,
  getGlobalPermissions,
  getItemPermissionsForRole,
  removeItemPermission,
} from "../api/teamRoleService";
import "./teamFilePermissions.css";

const itemPermissionNames = [
  "FILE_VIEW",
  "FILE_UPLOAD",
  "FILE_DELETE",
  "DOWNLOAD_TEAM_FILE",
  "MOVE_TEAM_FILE",
];

const itemPermissionLabels = {
  FILE_VIEW: "View File",
  FILE_UPLOAD: "Upload File",
  FILE_DELETE: "Delete File",
  DOWNLOAD_TEAM_FILE: "Download File",
  MOVE_TEAM_FILE: "Move File",
};

const TeamFilePermissions = ({ selectedTeamId, fileId }) => {
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
      const res = await getGlobalPermissions();
      const filtered = res.data.filter((p) =>
        itemPermissionNames.includes(p.name)
      );
      setPermissions(filtered);
    } catch (err) {
      console.error("Failed to load permissions", err);
    }
  };

  const handleToggle = async (permissionName, enabled) => {
    if (!selectedRoleId || !fileId) return;

    const payload = {
      roleId: selectedRoleId,
      itemId: fileId,
      itemType: "file",
      permissions: [permissionName],
      teamId: selectedTeamId,
    };

    try {
      if (enabled) {
        await assignItemPermissions(payload);
        setAssignedPermissions((prev) => [
          ...new Set([...prev, permissionName]),
        ]);
      } else {
        await removeItemPermission(payload);
        setAssignedPermissions((prev) =>
          prev.filter((perm) => perm !== permissionName)
        );
      }
    } catch (err) {
      console.error("Failed to update item permission:", err);
    }
  };

  const handleRoleChange = async (e) => {
    const roleId = e.target.value;
    setSelectedRoleId(roleId);
    try {
      const res = await getItemPermissionsForRole({
        roleId,
        itemId: fileId,
        itemType: "file",
      });
      const permissionNames = res.data.map((p) => p.name);
      setAssignedPermissions(permissionNames);
    } catch (err) {
      console.error("Failed to load item permissions:", err);
      setAssignedPermissions([]);
    }
  };

  return (
    <div className="file-permission-container">
      <h4>Assign File Permissions to Role</h4>
      <p className="permissions-note">
        *Individual File Permissions Override Folder Permissions
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
                  {itemPermissionLabels[perm.name] || perm.name}
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

export default TeamFilePermissions;
