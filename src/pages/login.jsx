import React from "react";
import "./login.css";
import logo from '../Assets/vaultmaster_logo.png';
import { useNavigate, Link } from 'react-router-dom';

const Login = () => {
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
            <a href="#" className="forgot-password">
              Forgot Password?
            </a>
            
            {/* Login Button */}
            <button type="submit" className="login-button">
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
        {/* <div className="protection-section"> */}
          <img src={logo} alt="" className="loginLogo"/>
          <p className="vault-master-right-side">VaultMaster</p>
          <p className="protection-section">"Seamless Action, Unbreakable Protection."</p>
        {/* </div> */}
      </div>
    
    </div>
  );
};

export default Login;
