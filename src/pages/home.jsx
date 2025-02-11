import React from "react";
import { useNavigate } from "react-router-dom";
import "./home.css";
import logo from "../Assets/vaultmaster_logo.png";

export const Home = () => {
  const navigate = useNavigate(); 

  return (
    <div className="home-page-container">
      <img src={logo} alt="VaultMaster Logo" className="logo" />

      <h1>Welcome to VaultMaster</h1>

      {/* Personal Button */}
      <button
        className="personal-button"
        onClick={() => navigate("/personal-pw-manager")}
      >
        Personal
      </button>

      {/* Teams Button */}
      <button
        className="teams-button"
        onClick={() => navigate("/teams-pw-manager")}
      >
        Teams
      </button>
    </div>
  );
};

export default Home;
