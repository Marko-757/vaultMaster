import React from "react";
import "./login.css";
import { useNavigate } from "react-router-dom"; 

export const Login = () => {
  const navigate = useNavigate();

  const handleLogin = (e) => {
    e.preventDefault();
    console.log("Logging in...");
    navigate("/2fa"); // Navigate to 2FA page after login
  };

  return (
    <div className="login-left">
      <div className="login-form">
        <h1>Login</h1>
        <form>
          <div className="email-input">
            <input type="email" placeholder="Enter email" />
          </div>
          <div className="password-input">
            <input type="password" placeholder="Enter password" />
          </div>
          <p>
            <span className="forgot-password" onClick={() => navigate("/forgotPassword")}>
              Forgot Password?
            </span>
          </p>
          <button type="submit" className="login-button" onClick={handleLogin}>
            Login
          </button>
        </form>
        <div className="signup">
          Don't have an account?{" "}
          <span className="nav-link" onClick={() => navigate("/auth/signup")}>
            <p>Sign up here</p>
          </span>
        </div>
      </div>
    </div>
  );
};

export default Login;
