import React, { useEffect, useState } from "react";
import {
  getRolesForTeam,
  assignItemPermissions,
  getGlobalPermissions,
  getItemPermissionsForRole,
  getFolderPermissionsForRole,
  removeItemPermission,
} from "../api/teamRoleService";
import "./teamPasswordPermissions.css";

const itemPermissionNames = [
  "PASSWORD_VIEW",
  "PASSWORD_EDIT",
  "PASSWORD_DELETE",
  "MOVE_TEAM_PASSWORD",
];

const itemPermissionLabels = {
  PASSWORD_VIEW: "View Password",
  PASSWORD_EDIT: "Edit Password",
  PASSWORD_DELETE: "Delete Password",
  MOVE_TEAM_PASSWORD: "Move Password",
};

const TeamPasswordPermissions = ({ selectedTeamId, passwordId, folderId }) => {
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
      const filtered = response.data.filter((p) =>
        itemPermissionNames.includes(p.name)
      );
      setPermissions(filtered);
    } catch (err) {
      console.error("Failed to load global permissions", err);
    }
  };

  const handleToggle = async (permissionName, enabled) => {
    if (!selectedRoleId || !passwordId) return;

    const payload = {
      roleId: selectedRoleId,
      itemId: passwordId,
      itemType: "password",
      permissions: [permissionName],
      teamId: selectedTeamId,
    };

    try {
      if (enabled) {
        await assignItemPermissions(payload);
        setAssignedPermissions((prev) => [...new Set([...prev, permissionName])]);
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
      const [itemRes, folderRes, globalRes] = await Promise.all([
        getItemPermissionsForRole({ roleId, itemId: passwordId, itemType: "password" }),
        folderId
          ? getFolderPermissionsForRole({ roleId, folderId, folderType: "password" })
          : Promise.resolve({ data: [] }),
        getGlobalPermissions(),
      ]);

      const itemPerms = itemRes.data.map((p) => p.permissionName || p.name);
      const folderPerms = folderRes.data.map((p) => p.permissionName || p.name);
      const globalPerms = globalRes.data.map((p) => p.name);

      const combined = new Set(itemPerms);

      if (globalPerms.includes("MANAGE_TEAM_PASSWORDS")) {
        combined.add("PASSWORD_VIEW");
        combined.add("PASSWORD_EDIT");
        combined.add("PASSWORD_DELETE");
        combined.add("MOVE_TEAM_PASSWORD");
      }

      folderPerms.forEach((perm) => combined.add(perm));
      setAssignedPermissions([...combined]);
    } catch (err) {
      console.error("Failed to load permissions", err);
      setAssignedPermissions([]);
    }
  };

  return (
    <div className="password-permission-container">
      <h4>Assign Password Permissions to Role</h4>
      <p className="permissions-note">
        *Individual Password Permissions Override Folder Permissions
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
              <div key={perm.id || perm.name} className="permission-toggle">
                <span className="perm-label">
                  {itemPermissionLabels[perm.name] || perm.displayName || perm.name}
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

export default TeamPasswordPermissions;
