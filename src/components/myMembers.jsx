import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./myMembers.css";
import axios from "axios";
import { FaEllipsisV } from "react-icons/fa";
import Toast from "bootstrap/js/dist/toast";

const MyMembers = ({ selectedTeam, onBack }) => {
  const navigate = useNavigate();
  const [members, setMembers] = useState([]);
  const [selectedMember, setSelectedMember] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [inviteEmail, setInviteEmail] = useState("");

  useEffect(() => {
    const fetchMembers = async () => {
      if (!selectedTeam?.teamId) return;
      try {
        const response = await axios.get(
          `http://localhost:8080/api/teams/members/team/${selectedTeam.teamId}/profiles`,
          { withCredentials: true }
        );
        setMembers(response.data);
      } catch (error) {
        console.error("Error fetching members:", error);
      }
    };
    fetchMembers();
  }, [selectedTeam]);

  const openMemberModal = (member) => {
    setSelectedMember(member);
    setIsModalOpen(true);
  };

  const closeModal = () => setIsModalOpen(false);

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

  const handleSendInvite = async () => {
    if (!inviteEmail.trim()) return;
    try {
      await axios.post(
        "http://localhost:8080/api/team-invitations/send",
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
                    src={
                      member.profilePicture || "https://via.placeholder.com/50"
                    }
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

      {isModalOpen && selectedMember && (
        <div className="member-modal-overlay">
          <div className="member-modal">
            <div className="member-modal-header">
              <h3>Member Details</h3>
              <button className="close-modal" onClick={closeModal}>
                ×
              </button>
            </div>
            <div className="member-modal-content">
              <div className="member-photo-container">
                <img
                  src={
                    selectedMember.profilePicture ||
                    "https://via.placeholder.com/100"
                  }
                  alt={selectedMember.fullName}
                  className="member-photo-large"
                />
              </div>
              <div className="member-details">
                <p>
                  <strong>Name:</strong> {selectedMember.fullName}
                </p>
                <p>
                  <strong>Email:</strong> {selectedMember.email}
                </p>
                {selectedMember.phoneNumber && (
                  <p>
                    <strong>Phone:</strong> {selectedMember.phoneNumber}
                  </p>
                )}
                <p>
                  <strong>Role:</strong> {selectedMember.roleName || "Member"}
                </p>
                {selectedMember.isOwner && (
                  <p>
                    <strong>Status:</strong>{" "}
                    <span className="owner-badge">Team Owner</span>
                  </p>
                )}
              </div>
            </div>
          </div>
        </div>
      )}

      {/* 🔹 Sticky Invite Form Footer */}
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

      {/* 🔹 Bootstrap Toast Container */}
      <div
        id="toast-container"
        className="position-fixed bottom-0 end-0 p-3"
        style={{ zIndex: 9999 }}
      ></div>
    </div>
  );
};

export default MyMembers;
