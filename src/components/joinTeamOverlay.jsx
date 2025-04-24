import React, { useState } from "react";

const JoinTeamOverlay = ({ onClose, onJoin }) => {
  const [inviteCode, setInviteCode] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    onJoin(inviteCode);
    onClose();
  };

  return (
    <div className="overlay">
      <div className="overlay-content">
        <h3>Join a Team</h3>
        <form onSubmit={handleSubmit}>
          <input
            type="text"
            value={inviteCode}
            onChange={(e) => setInviteCode(e.target.value)}
            placeholder="Enter 6-digit code"
            required
          />
          <button type="submit">Join</button>
          <button type="button" onClick={onClose}>Cancel</button>
        </form>
      </div>
    </div>
  );
};

export default JoinTeamOverlay;
