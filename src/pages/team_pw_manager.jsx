import React, { useState } from "react";
import "./team_pw_manager.css";

const TeamPwManager = () => {
  const [teams, setTeams] = useState(["My Company 1", "My Company 2"]);
  const [memberships, setMemberships] = useState(["My Membership 1", "My Membership 2"]);
  const [selectedTeam, setSelectedTeam] = useState(teams[0]);
  // Track which view is active. Null for the default view.
  const [selectedOption, setSelectedOption] = useState(null);

  // Dummy employee data for "My Members"
  const [employees] = useState([
    {
      id: 1,
      firstName: "John",
      lastName: "Doe",
      email: "john.doe@example.com",
      phone: "123-456-7890",
      roles: "Manager",
      photo: "https://via.placeholder.com/50",
    },
    {
      id: 2,
      firstName: "Jane",
      lastName: "Smith",
      email: "jane.smith@example.com",
      phone: "987-654-3210",
      roles: "Developer",
      photo: "https://via.placeholder.com/50",
    },
  ]);

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

  // Render a horizontal row for each member
  const renderMemberRow = (member) => (
    <div className="member-row" key={member.id}>
      <img
        src={member.photo}
        alt={`${member.firstName} ${member.lastName}`}
        className="member-photo"
      />
      <div className="member-name">
        {member.firstName} {member.lastName}
      </div>
      <div className="member-email">{member.email}</div>
      <div className="member-phone">{member.phone}</div>
      <div className="member-roles">{member.roles}</div>
    </div>
  );

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
            <button className="add-button" onClick={addTeam}>
              +
            </button>
          </div>
        </div>

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
            <button className="add-button" onClick={addMembership}>
              +
            </button>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="main-content">
        {selectedOption === "members" ? (
          // "My Members" view
          <div className="members-view">
            <div className="banner-container">
              <div className="back-button-wrapper">
                <button
                  className="back-button"
                  onClick={() => setSelectedOption(null)}
                >
                  Back
                </button>
              </div>
              <h1 className="main-banner">My Members</h1>
              <div className="spacer"></div>
            </div>
            <div className="members-roster">
              {employees.map((employee) => renderMemberRow(employee))}
            </div>
          </div>
        ) : (
          // Default view with team header and options
          <div>
            <h1>{selectedTeam}</h1>
            <div className="options-container">
              <button className="option-button">
                Password and File Management
              </button>
              <button
                className="option-button"
                onClick={() => setSelectedOption("members")}
              >
                My Members
              </button>
              <button className="option-button">Manage Roles</button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default TeamPwManager;
