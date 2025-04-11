import React, { useEffect, useState } from "react";
import {
  createRole,
  getRolesForTeam,
  getTeamPasswords,
  getTeamFiles,
  assignRolePasswordAccess,
  assignRoleFileAccess,
} from "../api/teamRoleService";
import "./manageRoles.css";

const ManageRoles = ({ selectedTeamId }) => {
  const [roles, setRoles] = useState([]);
  const [newRoleName, setNewRoleName] = useState("");
  const [expandedRoleId, setExpandedRoleId] = useState(null);
  const [teamPasswords, setTeamPasswords] = useState([]);
  const [teamFiles, setTeamFiles] = useState([]);

  useEffect(() => {
    if (selectedTeamId) {
      loadRoles();
      loadTeamData();
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

  const loadTeamData = async () => {
    try {
      const [passwordRes, fileRes] = await Promise.all([
        getTeamPasswords(selectedTeamId),
        getTeamFiles(selectedTeamId),
      ]);
      setTeamPasswords(passwordRes.data);
      setTeamFiles(fileRes.data);
    } catch (err) {
      console.error("Failed to load team passwords/files", err);
    }
  };

  const handleAddRole = async () => {
    if (!newRoleName.trim()) return;

    try {
      await createRole(selectedTeamId, newRoleName);
      setNewRoleName("");
      await loadRoles();
    } catch (err) {
      console.error("Failed to create role", err);
    }
  };

  const handleToggleRole = (roleId) => {
    setExpandedRoleId(expandedRoleId === roleId ? null : roleId);
  };

  const handlePermissionChange = async (roleId, type, id, checked) => {
    try {
      if (type === "password") {
        const updatedIds = checked
          ? [...roles.find((r) => r.roleId === roleId)?.passwordIds || [], id]
          : roles.find((r) => r.roleId === roleId)?.passwordIds?.filter((pid) => pid !== id) || [];
        await assignRolePasswordAccess(roleId, updatedIds);
      } else if (type === "file") {
        const updatedIds = checked
          ? [...roles.find((r) => r.roleId === roleId)?.fileIds || [], id]
          : roles.find((r) => r.roleId === roleId)?.fileIds?.filter((fid) => fid !== id) || [];
        await assignRoleFileAccess(roleId, updatedIds);
      }
    } catch (err) {
      console.error(`Failed to update ${type} access`, err);
    }
  };

  return (
    <div className="manage-roles">
      <h2>Manage Roles</h2>

      {roles.map((role) => (
        <div key={role.roleId} className="role-item">
          <div className="role-header" onClick={() => handleToggleRole(role.roleId)}>
            <span>{role.roleName}</span>
            <span>{expandedRoleId === role.roleId ? "▲" : "▼"}</span>
          </div>

          {expandedRoleId === role.roleId && (
            <div className="role-permissions">
              <strong>Can View:</strong>

              <div className="permissions-section">
                <div>
                  <p>Passwords:</p>
                  {teamPasswords.map((pw) => (
                    <div key={pw.id}>
                      <label>
                        <input
                          type="checkbox"
                          onChange={(e) =>
                            handlePermissionChange(role.roleId, "password", pw.id, e.target.checked)
                          }
                        />
                        {pw.name}
                      </label>
                    </div>
                  ))}
                </div>

                <div>
                  <p>Files:</p>
                  {teamFiles.map((file) => (
                    <div key={file.id}>
                      <label>
                        <input
                          type="checkbox"
                          onChange={(e) =>
                            handlePermissionChange(role.roleId, "file", file.id, e.target.checked)
                          }
                        />
                        {file.original_filename}
                      </label>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          )}
        </div>
      ))}

      <div className="add-role-container">
        <input
          type="text"
          placeholder="Role Name"
          value={newRoleName}
          onChange={(e) => setNewRoleName(e.target.value)}
        />
        <button className="add-role-btn" onClick={handleAddRole}>
          Add Role
        </button>
      </div>
    </div>
  );
};

export default ManageRoles;
