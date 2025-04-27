import React from "react";
import { Outlet } from "react-router-dom";
import "./auth.css"; 
import logo from '../Assets/vaultmaster_logo.png';

export const AuthLayout = () => {
  return (
    <div className="auth-container">
      {/* Left Side - Changes between Login & Signup */}
      <div className="auth-left">
        <Outlet />
      </div>

      {/* Right Side - Stays the same */}
      <div className="auth-right">
        <div className="right-content">
          <img src={logo} alt="VaultMaster Logo" className="authLogo" />
          <p className="vault-master-right-side">VaultMaster</p>
          <p className="protection-section">"Seamless Action, Unbreakable Protection."</p>
        </div>
      </div>
    </div>
  );
};

export default AuthLayout;
