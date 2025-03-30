import React, { useState, useRef, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./team_pw_manager.css";
import profileIcon from "../Assets/defaultProfileImage.png";
import MyMembers from "../components/myMembers";
import ManageRoles from "../components/manageRoles";
import PasswordAndFileManagement from "../components/passwordAndFileManagement";
import TeamCreationForm from "../components/teamCreationForm";
import {
  getAllTeamsForUser,
  createTeam,
  updateTeamName,
  deleteTeam,
} from "../api/teamService";

const TeamPwManager = () => {
  const navigate = useNavigate();
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const dropdownRef = useRef(null);
  const [teams, setTeams] = useState([]);
  const [selectedTeam, setSelectedTeam] = useState(null);
  const [selectedOption, setSelectedOption] = useState(null);
  const [showTeamCreationModal, setShowTeamCreationModal] = useState(false);

  const [isRenameModalOpen, setIsRenameModalOpen] = useState(false);
  const [newTeamName, setNewTeamName] = useState("");
  const [teamToRename, setTeamToRename] = useState(null);

  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [teamToDelete, setTeamToDelete] = useState(null);

  // Toggle dropdown
  const toggleDropdown = () => setDropdownOpen(!dropdownOpen);

  const handleLogout = () => {
    console.log("Logging out...");
    navigate("/auth/login");
  };

  useEffect(() => {
    const fetchTeams = async () => {
      try {
        console.log("Fetching teams...");
        const fetchedTeams = await getAllTeamsForUser();
        console.log("Fetched teams:", fetchedTeams);
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
      const newTeam = await createTeam(teamName);
      setTeams((prevTeams) => [...prevTeams, newTeam]);
      setSelectedTeam(newTeam);
    } catch (error) {
      console.error("Error creating team:", error);
    }
  };

  // Handle renaming a team
  const openRenameModal = (team) => {
    setTeamToRename(team);
    setNewTeamName(team.teamName);
    setIsRenameModalOpen(true);
  };

  const handleRenameTeam = async () => {
    if (!newTeamName.trim()) {
      alert("Please enter a new team name!");
      return;
    }

    try {
      const updatedTeam = await updateTeamName(
        teamToRename.teamId,
        newTeamName
      );
      setTeams((prevTeams) =>
        prevTeams.map((team) =>
          team.teamId === updatedTeam.teamId ? updatedTeam : team
        )
      );
      setIsRenameModalOpen(false);
      setNewTeamName("");
    } catch (error) {
      console.error("Error renaming team:", error);
      alert("Failed to rename team.");
    }
  };

  // Handle deleting a team
  const openDeleteModal = (team) => {
    setTeamToDelete(team);
    setIsDeleteModalOpen(true);
  };

  const handleDeleteTeam = async () => {
    try {
      await deleteTeam(teamToDelete.teamId);
      setTeams((prevTeams) =>
        prevTeams.filter((team) => team.teamId !== teamToDelete.teamId)
      );
      setIsDeleteModalOpen(false);
      setTeamToDelete(null);
    } catch (error) {
      console.error("Error deleting team:", error);
      alert("Failed to delete team.");
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
                className={`accordion-collapse collapse ${
                  teams.length > 0 ? "show" : ""
                }`}
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
                            className={`btn btn-primary flex-grow-1 ${
                              selectedTeam?.teamId === team.teamId
                                ? "active-folder"
                                : ""
                            }`}
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
                              <button
                                className="dropdown-item"
                                onClick={() => openRenameModal(team)}
                              >
                                Rename
                              </button>
                            </li>
                            <li>
                              <button
                                className="dropdown-item"
                                onClick={() => openDeleteModal(team)}
                              >
                                Delete
                              </button>
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
                  <div>No memberships available.</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Main Content Area */}
      <div className="main-content">
        {selectedOption === "members" ? (
          <MyMembers
            selectedTeam={selectedTeam}
            onBack={() => setSelectedOption(null)}
          />
        ) : selectedOption === "roles" ? (
          <ManageRoles
            selectedTeam={selectedTeam}
            onBack={() => setSelectedOption(null)}
          />
        ) : selectedOption === "password" ? (
          <PasswordAndFileManagement
            selectedTeam={selectedTeam}
            onBack={() => setSelectedOption(null)}
          />
        ) : (
          <div>
            <h1 className="banner">
              {selectedTeam ? selectedTeam.teamName : "No team selected"}
            </h1>
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

      {/* Team Creation Form */}
      <TeamCreationForm
        isVisible={showTeamCreationModal}
        onClose={closeTeamCreationModal}
        onCreateTeam={handleCreateTeam}
      />

      {/* Rename Team Modal */}
      {isRenameModalOpen && (
        <div className="overlay">
          <div className="modal">
            <div className="modal-content">
              <h2>Rename Team</h2>
              <form
                onSubmit={(e) => {
                  e.preventDefault();
                  handleRenameTeam();
                }}
              >
                <div className="input-container">
                  <input
                    type="text"
                    value={newTeamName}
                    onChange={(e) => setNewTeamName(e.target.value)}
                    placeholder="Enter new team name"
                    className="team-input"
                    autoFocus
                  />
                </div>
                <div className="modal-buttons">
                  <button type="submit" className="confirm-button">
                    Rename
                  </button>
                  <button
                    type="button"
                    className="cancel-button"
                    onClick={() => setIsRenameModalOpen(false)}
                  >
                    Cancel
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}

      {/* Delete Team Confirmation Modal */}
      {isDeleteModalOpen && (
        <div className="overlay">
          <div className="modal">
            <div className="modal-content">
              <h2>Are you sure you want to delete this team?</h2>
              <div className="modal-buttons">
                <button
                  type="button"
                  className="confirm-button"
                  onClick={handleDeleteTeam}
                >
                  Yes, Delete
                </button>
                <button
                  type="button"
                  className="cancel-button"
                  onClick={() => setIsDeleteModalOpen(false)}
                >
                  Cancel
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default TeamPwManager;
