import React from "react";
import "./signup.css";
import logo from '../Assets/vaultmaster_logo.png';
import { useNavigate, Link } from 'react-router-dom';

const Signup = () => {
  return (
    <div className="signup-container">
      
      {/* Left Side Signup */}
      <div className="signup-left">
        <div className="signup-form">
          <h1>Signup</h1>
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
            <a href="Signup" className="forgot-password">
              Forgot Password?
            </a>
            
            {/* Signup Button */}
            <button type="submit" className="signup-button">
              Signup
            </button>
          
          </form>
          
          {/* Login */}
          <div className="login">
            Already have an account? <a href="#">Login</a>
          </div>
        </div>
      </div>
      
      {/* Right Side Signup */}
      <div className="signup-right">
        {/* <div className="protection-section"> */}
          <img src={logo} alt="" className="signuplogo"/>
          <p className="vault-master-right-side">VaultMaster</p>
          <p className="protection-section">"Seamless Action, Unbreakable Protection."</p>
        {/* </div> */}
      </div>
    
    </div>
  );
};

export default Signup;
