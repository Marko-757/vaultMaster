import React, { useState } from "react";
import { sendTeamInvitation } from "../api/teamInvitationService";
import "./sendInviteForm.css";

const SendInviteForm = ({ selectedTeamId, onInviteSent }) => {
  const [email, setEmail] = useState("");
  const [statusMessage, setStatusMessage] = useState("");
  const [sending, setSending] = useState(false);

  const handleInvite = async (e) => {
    e.preventDefault();
    if (!email.trim()) return;

    try {
      setSending(true);
      setStatusMessage("");
      await sendTeamInvitation(selectedTeamId, email);
      setStatusMessage(`Invitation sent to ${email}`);
      setEmail("");
      if (onInviteSent) onInviteSent();
    } catch (error) {
      console.error("Failed to send invitation:", error);
      setStatusMessage("Failed to send invitation.");
    } finally {
      setSending(false);
    }
  };

  return (
    <div className="send-invite-form">
      <h3>Invite a Member</h3>
      <form onSubmit={handleInvite}>
        <input
          type="email"
          placeholder="Enter user's email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <button type="submit" disabled={sending}>
          {sending ? "Sending..." : "Send Invite"}
        </button>
      </form>
      {statusMessage && <p className="status-message">{statusMessage}</p>}
    </div>
  );
};

export default SendInviteForm;
