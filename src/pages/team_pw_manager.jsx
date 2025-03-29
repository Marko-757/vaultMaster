import React, { useState, useRef, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./team_pw_manager.css";
import profileIcon from "../Assets/defaultProfileImage.png";
import MyMembers from "../components/myMembers";
import ManageRoles from "../components/manageRoles";
import PasswordAndFileManagement from "../components/passwordAndFileManagement";
import TeamCreationForm from "../components/teamCreationForm";
import { getAllTeamsForUser, createTeam } from "../api/teamService";

const TeamPwManager = () => {
  const navigate = useNavigate();
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const dropdownRef = useRef(null);
  const [teams, setTeams] = useState([]);
  const [selectedTeam, setSelectedTeam] = useState(null);
  const [selectedOption, setSelectedOption] = useState(null);
  const [showTeamCreationModal, setShowTeamCreationModal] = useState(false);

  // Toggle dropdown
  const toggleDropdown = () => setDropdownOpen(!dropdownOpen);

  const handleLogout = () => {
    console.log("Logging out...");
    navigate("/auth/login");
  };

  // Fetch teams from the backend
  useEffect(() => {
    const fetchTeams = async () => {
      try {
        const fetchedTeams = await getAllTeamsForUser(); // Use the service function to get teams
        setTeams(fetchedTeams);
      } catch (error) {
        console.error("Error fetching teams:", error);
      }
    };

    fetchTeams();
  }, []);

  // Show and hide the team creation modal
  const openTeamCreationModal = () => {
    setShowTeamCreationModal(true);
  };

  const closeTeamCreationModal = () => {
    setShowTeamCreationModal(false);
  };

  // Handle team creation
  const handleCreateTeam = async (teamName) => {
    try {
      const newTeam = await createTeam(teamName); // Use the service function to create a team
      setTeams((prevTeams) => [...prevTeams, newTeam]);
      setSelectedTeam(newTeam); // Automatically select the new team
    } catch (error) {
      console.error("Error creating team:", error);
    }
  };

  // Close dropdown when clicking outside
  useEffect(() => {
    function handleClickOutside(event) {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setDropdownOpen(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  return (
    <div className="teams-container d-flex">
      {/* Home Button */}
      <button className="home-button" onClick={() => navigate("/home")}>
        🏠︎
      </button>

      {/* Profile Button with Dropdown */}
      <div className="profile-container" ref={dropdownRef}>
        <img
          src={profileIcon}
          alt="Profile"
          className="profile-icon"
          onClick={toggleDropdown}
        />
        {dropdownOpen && (
          <div className="profile-dropdown">
            <button onClick={() => navigate("/settings")}>
              Profile Settings
            </button>
            <button onClick={handleLogout}>Log Out</button>
          </div>
        )}
      </div>

      {/* Left Sidebar with Accordion */}
      <div className="left-column">
        <div className="sidebar-heading">
          <div className="sidebar-heading-top">Teams</div>
          <div className="sidebar-heading-bottom">Passwords & Files</div>
        </div>
        <hr className="divider" />

        {/* All View Buttons (Teams & Memberships) */}
        <div className="all-view-buttons">
          <button
            className={`btn btn-outline-primary mb-2 w-100 ${selectedOption === "teams" ? "active-folder" : ""}`}
            onClick={() => {
              setSelectedOption("teams");
            }}
          >
            All Teams
          </button>
          <button
            className={`btn btn-outline-primary mb-2 w-100 ${selectedOption === "memberships" ? "active-folder" : ""}`}
            onClick={() => {
              setSelectedOption("memberships");
            }}
          >
            All Memberships
          </button>
        </div>

        {/* Teams Accordion */}
        <div className="accordion-wrapper">
          <div className="accordion" id="teamsAccordion">
            <div className="accordion-item">
              <h2 className="accordion-header" id="headingTeams">
                <button
                  className="accordion-button"
                  type="button"
                  data-bs-toggle="collapse"
                  data-bs-target="#collapseTeams"
                  aria-expanded="true"
                  aria-controls="collapseTeams"
                >
                  Teams
                </button>
              </h2>
              <div
                id="collapseTeams"
                className={`accordion-collapse collapse ${teams.length > 0 ? "show" : ""}`}
                aria-labelledby="headingTeams"
                data-bs-parent="#teamsAccordion"
              >
                <div className="accordion-body scrollable-accordion">
                  {teams.length === 0 ? (
                    <div>No teams available. Create a new team!</div>
                  ) : (
                    teams.map((team) => (
                      <div key={team.teamId} className="team-item">
                        <div className="btn-group w-100 mb-2">
                          <button
                            type="button"
                            className={`btn btn-primary flex-grow-1 ${selectedTeam?.teamId === team.teamId ? "active-folder" : ""}`}
                            onClick={() => setSelectedTeam(team)}
                          >
                            {team.teamName}
                          </button>

                          <button
                            type="button"
                            className="btn btn-primary dropdown-toggle dropdown-toggle-split flex-shrink-0"
                            data-bs-toggle="dropdown"
                            aria-expanded="false"
                          >
                            <span className="visually-hidden">
                              Toggle Dropdown
                            </span>
                          </button>
                          <ul className="dropdown-menu">
                            <li>
                              <button className="dropdown-item">Rename</button>
                            </li>
                            <li>
                              <button className="dropdown-item">Delete</button>
                            </li>
                          </ul>
                        </div>
                      </div>
                    ))
                  )}
                  <button
                    className="btn btn-success w-100"
                    onClick={openTeamCreationModal}
                  >
                    + Add Team
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Memberships Accordion */}
        <div className="accordion-wrapper">
          <div className="accordion" id="membershipsAccordion">
            <div className="accordion-item">
              <h2 className="accordion-header" id="headingMemberships">
                <button
                  className="accordion-button"
                  type="button"
                  data-bs-toggle="collapse"
                  data-bs-target="#collapseMemberships"
                  aria-expanded="true"
                  aria-controls="collapseMemberships"
                >
                  Memberships
                </button>
              </h2>
              <div
                id="collapseMemberships"
                className="accordion-collapse collapse"
                aria-labelledby="headingMemberships"
                data-bs-parent="#membershipsAccordion"
              >
                <div className="accordion-body scrollable-accordion">
                  {/* Insert your memberships data here */}
                  <div>No memberships available.</div>
                  {/* Placeholder for memberships */}
                </div>
              </div>
            </div>
          </div>
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
            <h1 className="banner">{selectedTeam ? selectedTeam.teamName : "No team selected"}</h1>
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

      {/* Team Creation Modal */}
      <TeamCreationForm
        isVisible={showTeamCreationModal}
        onClose={closeTeamCreationModal}
        onCreateTeam={handleCreateTeam}
      />
    </div>
  );
};

export default TeamPwManager;
