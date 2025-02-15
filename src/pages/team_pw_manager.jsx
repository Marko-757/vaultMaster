import React, { useState } from "react";
import "./team_pw_manager.css";

const TeamPwManager = () => {
  const [teams, setTeams] = useState(["My Company 1", "My Company 2"]);
  const [memberships, setMemberships] = useState(["My Membership 1", "My Membership 2"]);
  const [selectedTeam, setSelectedTeam] = useState(teams[0]);

  // add a new team
  const addTeam = () => {
    const newTeam = window.prompt("Enter new team name:");
    if (newTeam) {
      setTeams([...teams, newTeam]);
    }
  };

  // add a new membership
  const addMembership = () => {
    const newMembership = window.prompt("Enter new membership name:");
    if (newMembership) {
      setMemberships([...memberships, newMembership]);
    }
  };

  // delete a team
  const deleteTeam = (team) => {
    if (window.confirm(`Are you sure you want to delete "${team}"?`)) {
      setTeams(teams.filter((t) => t !== team));
      if (selectedTeam === team) {
        setSelectedTeam(teams.length > 1 ? teams[0] : null);
      }
    }
  };

  // delete a membership
  const deleteMembership = (membership) => {
    if (window.confirm(`Are you sure you want to delete "${membership}"?`)) {
      setMemberships(memberships.filter((m) => m !== membership));
    }
  };

  return (
    <div className="teams-container">
      {/* Sidebar */}
      <div className="sidebar">
        <div className="sidebar-section">
          <h2>My Teams</h2>
          {teams.map((team, index) => (
            <div key={index} className="team-item">
              <button
                className={`team-button ${selectedTeam === team ? "active" : ""}`}
                onClick={() => setSelectedTeam(team)}
              >
                <span className="team-name">{team}</span>
                <span
                  className="delete-icon"
                  onClick={(e) => {
                    e.stopPropagation();
                    deleteTeam(team);
                  }}
                >
                  🗑️
                </span>
              </button>
            </div>
          ))}
          <div className="add-button-container">
            <button className="add-button" onClick={addTeam}>+</button>
          </div>
        </div>

        {/* Divider Line */}
        <hr className="divider" />

        <div className="sidebar-section">
          <h2>My Memberships</h2>
          {memberships.map((membership, index) => (
            <div key={index} className="team-item">
              <button className="membership-button">
                <span className="team-name">{membership}</span>
                <span
                  className="delete-icon"
                  onClick={(e) => {
                    e.stopPropagation();
                    deleteMembership(membership);
                  }}
                >
                  🗑️
                </span>
              </button>
            </div>
          ))}
          <div className="add-button-container">
            <button className="add-button" onClick={addMembership}>+</button>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="main-content">
        <h1>{selectedTeam}</h1>
        <div className="options-container">
          <button className="option-button">Password and File Management</button>
          <button className="option-button">My Members</button>
          <button className="option-button">Manage Roles</button>
        </div>
      </div>
    </div>
  );
};

export default TeamPwManager;
