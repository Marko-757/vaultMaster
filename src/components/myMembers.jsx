import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./myMembers.css";
import axios from "axios";
import { FaEllipsisV } from "react-icons/fa";
import Toast from "bootstrap/js/dist/toast";

const MyMembers = ({ selectedTeam, onBack }) => {
  const navigate = useNavigate();
  const [members, setMembers] = useState([]);
  const [roles, setRoles] = useState([]);
  const [selectedMember, setSelectedMember] = useState(null);
  const [selectedRoleId, setSelectedRoleId] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [inviteEmail, setInviteEmail] = useState("");

  useEffect(() => {
    const fetchMembers = async () => {
      if (!selectedTeam?.teamId) return;
      try {
        const response = await axios.get(
          `/api/teams/members/team/${selectedTeam.teamId}/profiles`,
          { withCredentials: true }
        );
        setMembers(response.data);
      } catch (error) {
        console.error("Error fetching members:", error);
      }
    };
    fetchMembers();
  }, [selectedTeam]);

  const openMemberModal = async (member) => {
    setSelectedMember(member);
    setSelectedRoleId(member.roleId || ""); // fallback to blank if null

    try {
      const res = await axios.get(`/api/roles/team/${selectedTeam.teamId}`, {
        withCredentials: true,
      });
      setRoles(res.data);
    } catch (error) {
      console.error("Failed to load roles:", error);
    }

    setIsModalOpen(true);
  };

  const closeModal = () => {
    setIsModalOpen(false);
    setSelectedMember(null);
    setSelectedRoleId(null);
  };

  const showToast = (message, delay = 2500) => {
    const toastId = `toast-${Date.now()}`;
    const toastHTML = `
      <div id="${toastId}" class="toast text-bg-primary fade" role="alert" aria-live="assertive" aria-atomic="true" style="min-width: 250px;">
        <div class="d-flex">
          <div class="toast-body">${message}</div>
          <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
        </div>
      </div>
    `;
    const container = document.getElementById("toast-container");
    if (!container) return;
    container.insertAdjacentHTML("beforeend", toastHTML);
    const toastEl = document.getElementById(toastId);
    const toast = new Toast(toastEl, { delay, autohide: true });
    toast.show();
    toastEl.addEventListener("hidden.bs.toast", () => toastEl.remove());
  };

  const handleRoleChange = async () => {
    if (!selectedRoleId || !selectedMember) return;
    console.log("Selected member for role change:", selectedMember);
    try {
      await axios.put(
        `/api/roles/assign?teamId=${selectedTeam.teamId}&userId=${selectedMember.userId}&roleId=${selectedRoleId}`,
        {},
        { withCredentials: true }
      );
      
      showToast("Role updated successfully!");
      setMembers((prev) =>
        prev.map((m) =>
          m.userId === selectedMember.userId
            ? { ...m, roleId: selectedRoleId, roleName: getRoleName(selectedRoleId) }
            : m
        )
      );
      closeModal();
    } catch (error) {
      console.error("Failed to assign role:", error);
      showToast("Failed to update role", 3000);
    }
  };

  const getRoleName = (roleId) => roles.find((r) => r.roleId === roleId)?.roleName || "Unknown";

  const handleSendInvite = async () => {
    if (!inviteEmail.trim()) return;
    try {
      await axios.post(
        "/api/team-invitations/send",
        { teamId: selectedTeam.teamId, email: inviteEmail },
        { withCredentials: true }
      );
      setInviteEmail("");
      showToast("Invite Sent!");
    } catch (error) {
      console.error("Error sending invite:", error);
      showToast("Failed to send invite", 3000);
    }
  };

  return (
    <div className="members-view">
      <div className="members-header">
        <button className="back-button" onClick={onBack}>
          ←
        </button>
        <h2 className="members-title">
          {selectedTeam?.teamName || "Team"} Members
        </h2>
      </div>

      <div className="members-roster">
        {members.length > 0 ? (
          <div className="members-table">
            <div className="members-header">
              <div className="header-photo">Photo</div>
              <div className="header-name">Name</div>
              <div className="header-email">Email</div>
              <div className="header-phone">Phone</div>
              <div className="header-role">Role</div>
              <div className="header-actions"></div>
            </div>
            {members.map((member, index) => (
              <div className="member-row" key={index}>
                <div className="member-photo-container">
                  <img
                    src={member.profilePicture || "https://via.placeholder.com/50"}
                    alt={member.fullName}
                    className="member-photo"
                  />
                </div>
                <div className="member-name">
                  {member.fullName}
                  {member.isOwner && <span className="owner-badge">Owner</span>}
                </div>
                <div className="member-email">{member.email}</div>
                <div className="member-phone">{member.phoneNumber}</div>
                <div className="member-roles">
                  {member.roleName || "Member"}
                </div>
                <div className="member-actions">
                  <button
                    className="ellipsis-button"
                    onClick={() => openMemberModal(member)}
                  >
                    <FaEllipsisV />
                  </button>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <p className="empty-message">No members found in this team.</p>
        )}
      </div>

      {/* Modal */}
      {isModalOpen && selectedMember && (
        <div className="member-modal-overlay">
          <div className="member-modal">
            <div className="member-modal-header">
              <h3>Change Role</h3>
              <button className="close-modal" onClick={closeModal}>
                ×
              </button>
            </div>
            <div className="member-modal-content">
              <div className="member-details">
                <p><strong>Name:</strong> {selectedMember.fullName}</p>
                <p><strong>Email:</strong> {selectedMember.email}</p>
                <p><strong>Current Role:</strong> {selectedMember.roleName || "None"}</p>
                <label htmlFor="roleSelect"><strong>New Role:</strong></label>
                <select
                  id="roleSelect"
                  className="form-select mt-1 mb-3"
                  value={selectedRoleId}
                  onChange={(e) => setSelectedRoleId(e.target.value)}
                >
                  <option value="">-- Select Role --</option>
                  {roles.map((role) => (
                    <option key={role.roleId} value={role.roleId}>
                      {role.roleName}
                    </option>
                  ))}
                </select>
                <div className="modal-buttons">
                  <button className="btn btn-primary me-2" onClick={handleRoleChange}>
                    Save
                  </button>
                  <button className="btn btn-secondary" onClick={closeModal}>
                    Cancel
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Invite Form */}
      <div className="invite-form-footer">
        <form
          className="invite-form"
          onSubmit={(e) => {
            e.preventDefault();
            handleSendInvite();
          }}
        >
          <input
            type="email"
            placeholder="Enter email to invite"
            value={inviteEmail}
            onChange={(e) => setInviteEmail(e.target.value)}
            className="invite-input"
            required
          />
          <button type="submit" className="invite-button">
            Send Invite
          </button>
        </form>
      </div>

      <div
        id="toast-container"
        className="position-fixed bottom-0 end-0 p-3"
        style={{ zIndex: 9999 }}
      ></div>
    </div>
  );
};

export default MyMembers;
