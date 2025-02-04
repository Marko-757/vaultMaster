import React from "react";
import "./login.css";
import logo from '../Assets/vaultmaster_logo.png';
import { useNavigate } from "react-router-dom"; // Import useNavigate from react-router-dom

export const Login = () => {
  const navigate = useNavigate(); // Initialize useNavigate

  // Handle login button click
  const handleLogin = (e) => {
    e.preventDefault(); // Prevent form submission (if needed)
    
    // Perform login logic here (e.g., validate credentials)
    console.log("Logging in...");

    // Redirect to the Personal Password Manager page
    navigate("/personal-pw-manager");
  };

  return (
    <div className="login-container">
      {/* Left Side Login */}
      <div className="login-left">
        <div className="login-form">
          <h1>Login</h1>
          <form>
            {/* Email textbox */}
            <div className="email-input">
              <input type="email" placeholder="Enter email" />
            </div>

            {/* Password textbox */}
            <div className="password-input">
              <input type="password" placeholder="Enter password" />
            </div>

            {/* Forgot Password */}
            <a href="/forgotPassword" className="forgot-password">
              Forgot Password?
            </a>

            {/* Login Button */}
            <button
              type="submit"
              className="login-button"
              onClick={handleLogin} // Add onClick handler
            >
              Login
            </button>
          </form>

          {/* Signup */}
          <div className="signup">
            Don't have an account? <a href="signup">Sign up here</a>
          </div>
        </div>
      </div>

      {/* Right Side Login */}
      <div className="login-right">
        <img src={logo} alt="" className="loginLogo" />
        <p className="vault-master-right-side">VaultMaster</p>
        <p className="protection-section">"Seamless Action, Unbreakable Protection."</p>
      </div>
    </div>
  );
};

export default Login;