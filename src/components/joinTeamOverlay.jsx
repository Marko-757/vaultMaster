import React, { useState } from "react";
import "./joinTeamOverlay.css"; // Assuming you already have basic modal styling
import { joinTeamWithCode } from "../api/teamMembershipService"; // <-- You will need to create this small API if not already made

const JoinTeamOverlay = ({ isOpen, onClose }) => {
  const [joinCode, setJoinCode] = useState("");
  const [message, setMessage] = useState("");

  const handleJoinTeam = async () => {
    if (!joinCode.trim()) {
      alert("Please enter a valid team code.");
      return;
    }

    try {
      const response = await joinTeamWithCode(joinCode);
      setMessage("Successfully joined the team!");
      setTimeout(() => {
        setMessage("");
        setJoinCode("");
        onClose();
      }, 1500);
    } catch (error) {
      console.error(error);
      setMessage("Failed to join team. Please check your code and try again.");
    }
  };

  if (!isOpen) return null;

  return (
    <div className="overlay">
      <div className="overlay-content">
        <h2>Join a Team</h2>
        <input
          type="text"
          placeholder="Enter Team Code"
          value={joinCode}
          onChange={(e) => setJoinCode(e.target.value)}
          className="input-field"
        />
        <div className="button-group">
          <button onClick={handleJoinTeam} className="join-button">
            Join
          </button>
          <button onClick={onClose} className="cancel-button">
            Cancel
          </button>
        </div>
        {message && <p className="status-message">{message}</p>}
      </div>
    </div>
  );
};

export default JoinTeamOverlay;
