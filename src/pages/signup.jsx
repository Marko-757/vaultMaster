import React from "react";
import "./signup.css";
import logo from '../Assets/vaultmaster_logo.png';
import { useNavigate } from "react-router";

export const Signup = () => {
  const navigate = useNavigate;

  return (
    <div className="signup-container">
      
      {/* Left Side Signup */}
      <div className="signup-left">
        <div className="signup-form">
          <h1>Signup</h1>
          <form>
            
            {/* Full Name textbox */}
            <div className="fullname-input">
              <input type="fullname" placeholder="Enter first & last name" />
            </div>

            {/* Email textbox */}
            <div className="email-input">
              <input type="email" placeholder="Enter email" />
            </div>

            {/* Email Verification textbox*/}
            <div className="emailVerify-input">
              <input type="emailVerify" placeholder="Verify email" />
            </div>

             {/* Phone Number textbox */}
             <div className="phone-input">
              <input type="phone" placeholder="Enter phone number: (123) 456-789" />
            </div>
            
            {/* Password textbox */}
            <div className="password-input">
              <input type="password" placeholder="Enter password" />
            </div>

            {/* Password Verification textbox */}
            <div className="passwordVerify-input">
              <input type="passwordVerify" placeholder="Verify password" />
            </div>

            {/* Forgot Password */}
            <a href="/forgotPassword" className="forgot-password">
              Forgot Password?
            </a>
            
            {/* Signup Button */}
            <button type="submit" className="signup-button">
              Signup
            </button>
          
          </form>
          
          {/* Login */}
          <div className="login">
            Already have an account? <a href="login">Login</a>
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
