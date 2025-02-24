import React, { useState } from "react";
import ManageRoles from "../components/manageRoles";
import MyMembers from "../components/myMembers";
import PasswordAndFileManagement from "../components/passwordAndFileManagement";
import "./team_pw_manager.css";

const TeamPwManager = () => {
  const [teams, setTeams] = useState([
    "My Company 1",
    "My Company 2",
    "My Company 3",
    "My Company 4",
  ]);
  const [memberships, setMemberships] = useState([
    "My Membership 1",
    "My Membership 2",
    "My Membership 3",
    "My Membership 4",
  ]);
  const [selectedTeam, setSelectedTeam] = useState(teams[0]);
  const [selectedOption, setSelectedOption] = useState(null);

  const [expandedTeams, setExpandedTeams] = useState(false);
  const [expandedMemberships, setExpandedMemberships] = useState(false);

  const [showTeamInput, setShowTeamInput] = useState(false);
  const [newTeamName, setNewTeamName] = useState("");

  const [showMembershipInput, setShowMembershipInput] = useState(false);
  const [newMembershipName, setNewMembershipName] = useState("");

  const previewCount = 3;
  const displayedTeams = expandedTeams ? teams : teams.slice(0, previewCount);
  const displayedMemberships = expandedMemberships
    ? memberships
    : memberships.slice(0, previewCount);

  return (
    <div className="teams-container">
      {/* Left Sidebar */}
      <div className="sidebar">
        <div className="sidebar-heading">
          <div className="sidebar-heading-top">Teams</div>
          <div className="sidebar-heading-bottom">Passwords & Files</div>
        </div>
        <hr className="divider" />

        {/* Teams Section */}
        <div className="sidebar-section">
          <div className="section-header">
            <h2>My Teams</h2>
            <button
              className="section-add-button"
              onClick={() => setShowTeamInput(true)}
            >
              +
            </button>
          </div>
          <div
            className={`teams-list-container ${expandedTeams ? "scrollable" : ""}`}
          >
            {displayedTeams.map((team, index) => (
              <div key={index} className="team-item">
                <button
                  className={`team-button ${
                    selectedTeam === team ? "active" : ""
                  }`}
                  onClick={() => {
                    setSelectedTeam(team);
                    setSelectedOption(null);
                  }}
                >
                  <span className="team-name">{team}</span>
                </button>
              </div>
            ))}
            {showTeamInput && (
              <div className="team-item">
                <input
                  type="text"
                  className="team-input"
                  value={newTeamName}
                  onChange={(e) => setNewTeamName(e.target.value)}
                  onKeyDown={(e) => {
                    if (e.key === "Enter" && newTeamName.trim() !== "") {
                      setTeams([...teams, newTeamName.trim()]);
                      setNewTeamName("");
                      setShowTeamInput(false);
                    }
                  }}
                  autoFocus
                  placeholder="Enter new team name"
                />
              </div>
            )}
          </div>
          {teams.length > previewCount && (
            <button
              className="see-toggle-button"
              onClick={() => setExpandedTeams(!expandedTeams)}
            >
              {expandedTeams ? "See Less" : "See More"}
            </button>
          )}
        </div>

        <hr className="divider" />

        {/* Memberships Section */}
        <div className="sidebar-section">
          <div className="section-header">
            <h2>My Memberships</h2>
            <button
              className="section-add-button"
              onClick={() => setShowMembershipInput(true)}
            >
              +
            </button>
          </div>
          <div
            className={`memberships-list-container ${
              expandedMemberships ? "scrollable" : ""
            }`}
          >
            {displayedMemberships.map((membership, index) => (
              <div key={index} className="team-item">
                <button className="membership-button">
                  <span className="team-name">{membership}</span>
                </button>
              </div>
            ))}
            {showMembershipInput && (
              <div className="team-item">
                <input
                  type="text"
                  className="membership-input"
                  value={newMembershipName}
                  onChange={(e) => setNewMembershipName(e.target.value)}
                  onKeyDown={(e) => {
                    if (e.key === "Enter" && newMembershipName.trim() !== "") {
                      setMemberships([...memberships, newMembershipName.trim()]);
                      setNewMembershipName("");
                      setShowMembershipInput(false);
                    }
                  }}
                  autoFocus
                  placeholder="Enter new membership name"
                />
              </div>
            )}
          </div>
          {memberships.length > previewCount && (
            <button
              className="see-toggle-button"
              onClick={() => setExpandedMemberships(!expandedMemberships)}
            >
              {expandedMemberships ? "See Less" : "See More"}
            </button>
          )}
        </div>
      </div>

      {/* Main Content Area */}
      <div className="main-content">
        {selectedOption === "members" ? (
          <MyMembers selectedTeam={selectedTeam} onBack={() => setSelectedOption(null)} />
        ) : selectedOption === "roles" ? (
          <ManageRoles selectedTeam={selectedTeam} onBack={() => setSelectedOption(null)} />
        ) : selectedOption === "password" ? (
          <PasswordAndFileManagement selectedTeam={selectedTeam} onBack={() => setSelectedOption(null)} />
        ) : (
          <div>
            <h1 className="banner">{selectedTeam}</h1>
            <div className="options-container">
              <button
                className="option-button"
                onClick={() => setSelectedOption("password")}
              >
                Password and File Management
              </button>
              <button
                className="option-button"
                onClick={() => setSelectedOption("members")}
              >
                My Members
              </button>
              <button
                className="option-button"
                onClick={() => setSelectedOption("roles")}
              >
                Manage Roles
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default TeamPwManager;