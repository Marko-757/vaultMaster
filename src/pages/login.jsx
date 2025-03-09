import React, { useState } from "react";
import "./login.css";
import { useNavigate } from "react-router-dom";
import { login } from "../api/authService"; 

export const Login = () => {
  const navigate = useNavigate();

  // Form state
  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });

  // Success & error messages
  const [successMessage, setSuccessMessage] = useState("");
  const [generalError, setGeneralError] = useState("");
  const [emailError, setEmailError] = useState("");
  const [passwordError, setPasswordError] = useState("");

  // Handle input changes & reset errors dynamically
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });

    // Clear previous errors when the user starts typing
    setSuccessMessage("");
    setGeneralError("");
    setEmailError("");
    setPasswordError("");
  };

  const handleLogin = async (e) => {
    e.preventDefault();

    if (!formData.email || !formData.password) {
      setGeneralError("Both fields are required.");
      return;
    }

    try {
      const response = await login(formData.email, formData.password); 

      if (response) {
        setSuccessMessage("Login successful!");
        setGeneralError("");

        // Clear form fields after success
        setFormData({ email: "", password: "" });

        // Navigate to 2fa
        navigate("/2fa");
      } else {
        setGeneralError("Login failed. Please try again.");
      }
    } catch (error) {
      console.error("Login error:", error);
      setGeneralError("An error occurred. Please try again.");
    }
  };

  return (
    <div className="login-left">
      <div className="login-form">
        <h1>Login</h1>

        {/* Success & Error Messages */}
        {successMessage && <p className="success-message">{successMessage}</p>}
        {generalError && <p className="error-message">{generalError}</p>}
        {emailError && <p className="error-message">{emailError}</p>}
        {passwordError && <p className="error-message">{passwordError}</p>}

        <form onSubmit={handleLogin}>
          <div className="email-input">
            <input
              type="email"
              name="email"
              placeholder="Enter email"
              value={formData.email}
              onChange={handleChange}
              required
            />
          </div>

          <div className="password-input">
            <input
              type="password"
              name="password"
              placeholder="Enter password"
              value={formData.password}
              onChange={handleChange}
              required
            />
          </div>

          <p>
            <span className="forgot-password" onClick={() => navigate("/forgotPassword")}>
              Forgot Password?
            </span>
          </p>

          <button type="submit" className="login-button">
            Login
          </button>
        </form>

        {/* "Sign up" link remains under the login button */}
        <div className="signup">
          Don't have an account?{" "}
          <span className="nav-link" onClick={() => navigate("/auth/signup")}>
            Sign up here
          </span>
        </div>
      </div>
    </div>
  );
};

export default Login;
