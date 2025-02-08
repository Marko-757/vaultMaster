import React, { useState } from "react";
import "./signup.css";
import { useNavigate } from "react-router-dom"; 
import { FaEye, FaEyeSlash, FaCheckCircle } from "react-icons/fa";

export const Signup = () => {
  const navigate = useNavigate();

  // State for form fields
  const [formData, setFormData] = useState({
    fullName: "",
    email: "",
    emailVerify: "",
    phone: "",
    password: "",
    passwordVerify: "",
  });

  // State for errors
  const [nameError, setNameError] = useState("");
  const [emailError, setEmailError] = useState("");
  const [passwordError, setPasswordError] = useState("");
  const [generalError, setGeneralError] = useState("");

  // State for password visibility & focus
  const [showPassword, setShowPassword] = useState(false);
  const [showPasswordVerify, setShowPasswordVerify] = useState(false);
  const [passwordFocused, setPasswordFocused] = useState(false);

  // Password strength indicators
  const passwordCriteria = {
    minLength: formData.password.length >= 12,
    upperLowerCase: /[a-z]/.test(formData.password) && /[A-Z]/.test(formData.password),
    containsNumber: /\d/.test(formData.password),
    containsSpecial: /[!@#$%^&*(),.?":{}|<>]/.test(formData.password),
  };

  // Function to handle input changes
  const handleChange = (e) => {
    const { name, value } = e.target;

    // Auto-format phone number
    if (name === "phone") {
      setFormData({ ...formData, phone: formatPhoneNumber(value) });
    } else {
      setFormData({ ...formData, [name]: value });
    }

    // Clear errors as user types
    if (name === "fullName") setNameError("");
    if (name === "email" || name === "emailVerify") setEmailError("");
    if (name === "password" || name === "passwordVerify") setPasswordError("");
    setGeneralError("");
  };

  // Format phone number as user types
  const formatPhoneNumber = (value) => {
    let cleaned = value.replace(/\D/g, ""); // Remove non-numeric characters
    let formatted = "";

    if (cleaned.length > 0) formatted = `(${cleaned.substring(0, 3)}`;
    if (cleaned.length > 3) formatted += `) ${cleaned.substring(3, 6)}`;
    if (cleaned.length > 6) formatted += `-${cleaned.substring(6, 10)}`;

    return formatted;
  };

  // Function to validate form before submission
  const validateForm = () => {
    if (
      !formData.fullName ||
      !formData.email ||
      !formData.emailVerify ||
      !formData.phone ||
      !formData.password ||
      !formData.passwordVerify
    ) {
      setGeneralError("All fields are required.");
      return false;
    }
    if (!formData.fullName.includes(" ")) {
      setNameError("Please enter both first and last name.");
      return false;
    }
    if (formData.email !== formData.emailVerify) {
      setEmailError("Emails do not match.");
      return false;
    }
    if (formData.password !== formData.passwordVerify) {
      setPasswordError("Passwords do not match.");
      return false;
    }
    if (formData.phone.length < 14) {
      setGeneralError("Invalid phone number format.");
      return false;
    }
    if (
      !passwordCriteria.minLength ||
      !passwordCriteria.upperLowerCase ||
      !passwordCriteria.containsNumber ||
      !passwordCriteria.containsSpecial
    ) {
      setPasswordError("Password does not meet all security requirements.");
      return false;
    }
    return true;
  };

  // Function to handle form submission
  const handleSignup = (e) => {
    e.preventDefault();

    // Validate form
    if (!validateForm()) return;

    console.log("Signing up...");
    navigate("/auth/login"); // Redirect to login after successful signup
  };

  return (
    <div className="signup-left">
      <div className="signup-form">
        <h1>Sign Up</h1>
        {generalError && <p className="error-message">{generalError}</p>}
        <form>
          <div className="fullname-input">
            <input
              type="text"
              name="fullName"
              placeholder="Full Name"
              value={formData.fullName}
              onChange={handleChange}
            />
            {nameError && <span className="error-message-inline">{nameError}</span>}
          </div>
          <div className="email-input">
            <input
              type="email"
              name="email"
              placeholder="Enter email"
              value={formData.email}
              onChange={handleChange}
            />
          </div>
          <div className="emailVerify-input">
            <input
              type="email"
              name="emailVerify"
              placeholder="Verify email"
              value={formData.emailVerify}
              onChange={handleChange}
            />
            {emailError && <span className="error-message-inline">{emailError}</span>}
          </div>
          <div className="phone-input">
            <input
              type="text"
              name="phone"
              placeholder="Phone Number"
              value={formData.phone}
              onChange={handleChange}
            />
          </div>
          <div className="password-input">
            <div className="password-field">
              <input
                type={showPassword ? "text" : "password"}
                name="password"
                placeholder="Create password"
                value={formData.password}
                onChange={handleChange}
                onFocus={() => setPasswordFocused(true)}
                onBlur={() => setPasswordFocused(false)}
              />
              <span className="toggle-password" onClick={() => setShowPassword(!showPassword)}>
                {showPassword ? <FaEyeSlash /> : <FaEye />}
              </span>
            </div>
            {passwordFocused && (
              <div className="password-criteria">
                <p className={passwordCriteria.minLength ? "valid" : "invalid"}>
                  <FaCheckCircle /> At least 12 characters
                </p>
                <p className={passwordCriteria.upperLowerCase ? "valid" : "invalid"}>
                  <FaCheckCircle /> Uppercase & lowercase letters
                </p>
                <p className={passwordCriteria.containsNumber ? "valid" : "invalid"}>
                  <FaCheckCircle /> Contains a number
                </p>
                <p className={passwordCriteria.containsSpecial ? "valid" : "invalid"}>
                  <FaCheckCircle /> Contains a special character (!@#$%^&*)
                </p>
              </div>
            )}
          </div>
          <div className="passwordVerify-input">
            <div className="password-field">
              <input
                type={showPasswordVerify ? "text" : "password"}
                name="passwordVerify"
                placeholder="Verify password"
                value={formData.passwordVerify}
                onChange={handleChange}
              />
              <span className="toggle-password" onClick={() => setShowPasswordVerify(!showPasswordVerify)}>
                {showPasswordVerify ? <FaEyeSlash /> : <FaEye />}
              </span>
            </div>
            {passwordError && <span className="error-message-inline">{passwordError}</span>}
          </div>
          <button type="submit" className="signup-button" onClick={handleSignup}>
            Sign Up
          </button>
        </form>
        <div className="login">
          Already have an account?{" "}
          <span className="nav-link" onClick={() => navigate("/auth/login")}>
            Login here
          </span>
        </div>
      </div>
    </div>
  );
};

export default Signup;
