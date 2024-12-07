import React from "react";
import "./forgotPassword.css";

const ForgotPassword = () => {
  return (
    <div className="forgot-password-container">
      <div className="forgot-password-form">
        <h1>Forgot Password</h1>
        <p>Enter your email address below, and we'll send you instructions to reset your password.</p>
        
        <form>
          {/* Email textbox */}
          <div className="email-input">
            <input type="email" placeholder="Enter your email" required />
          </div>
          
          {/* Submit Button */}
          <button type="submit" className="submit-button">
            Send Reset Link
          </button>
        </form>
        
        <a href="/login" className="back-to-login">Back to Login</a>
      </div>
    </div>
  );
};

export default ForgotPassword;
