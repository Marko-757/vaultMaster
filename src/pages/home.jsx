import React, { useState, useRef, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./home.css";
import logo from "../Assets/vaultmaster_logo.png";
import profileIcon from "../Assets/defaultProfileImage.png";


export const Home = () => {
  const navigate = useNavigate(); 

  //Profile Dropdown Menu (Top Right)
    const [dropdownOpen, setDropdownOpen] = useState(false);
    const dropdownRef = useRef(null);
  
    const toggleDropdown = () => {
      setDropdownOpen(!dropdownOpen);
    };
  
    const handleLogout = () => {
      console.log("Logging out..."); 
      navigate("/auth/login"); 
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
    <div className="home-page-container">
      <img src={logo} alt="VaultMaster Logo" className="logo" />

      <h1>Welcome to VaultMaster</h1>


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
            <button onClick={() => navigate("/settings")}>Profile Settings</button>
            <button onClick={handleLogout}>Log Out</button>
          </div>
        )}
      </div>


      {/* Personal Button */}
      <button
        className="personal-button"
        onClick={() => navigate("/personal")}
      >
        Personal
      </button>

      {/* Teams Button */}
      <button
        className="teams-button"
        onClick={() => navigate("/team")}
      >
        Teams
      </button>
    </div>
  );
};

export default Home;
