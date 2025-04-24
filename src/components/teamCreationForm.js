import React, { useState } from "react";
import "./teamCreationForm.css";

const TeamCreationForm = ({ onClose, onCreateTeam }) => {
  const [teamName, setTeamName] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    if (teamName.trim()) {
      onCreateTeam(teamName.trim());
      setTeamName("");
      onClose();
    } else {
      alert("Please enter a team name!");
    }
  };

  return (
    <>
      <h2>Create a New Team</h2>
      <form onSubmit={handleSubmit}>
        <div className="input-container">
          <input
            type="text"
            value={teamName}
            onChange={(e) => setTeamName(e.target.value)}
            placeholder="Enter team name"
            className="team-input"
            autoFocus
          />
        </div>
        <div className="modal-buttons">
          <button type="submit" className="confirm-button">
            Create Team
          </button>
          <button
            type="button"
            className="cancel-button"
            onClick={onClose}
          >
            Cancel
          </button>
        </div>
      </form>
    </>
  );
};

export default TeamCreationForm;
