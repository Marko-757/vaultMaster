import React, { useState } from "react";
import "./signup.css";
import { useNavigate } from "react-router-dom";
import { FaEye, FaEyeSlash, FaCheckCircle } from "react-icons/fa";
import bcrypt from "bcryptjs";

export const Signup = () => {
  const navigate = useNavigate();

  // Form state
  const [formData, setFormData] = useState({
    fullName: "",
    email: "",
    emailVerify: "",
    phone: "",
    password: "",
    passwordVerify: "",
  });

  // Error & success states
  const [generalError, setGeneralError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [nameError, setNameError] = useState("");
  const [emailError, setEmailError] = useState("");
  const [passwordError, setPasswordError] = useState("");

  // Password validation
  const [showPassword, setShowPassword] = useState(false);
  const [showPasswordVerify, setShowPasswordVerify] = useState(false);
  const [passwordFocused, setPasswordFocused] = useState(false);
  const [passwordCriteria, setPasswordCriteria] = useState({
    minLength: false,
    upperLowerCase: false,
    containsNumber: false,
    containsSpecial: false,
  });

  // Handle input changes & clear errors dynamically
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
    setGeneralError("");
    setSuccessMessage("");
    setNameError("");
    setEmailError("");
    setPasswordError("");

    if (name === "password") {
      setPasswordCriteria({
        minLength: value.length >= 12,
        upperLowerCase: /[a-z]/.test(value) && /[A-Z]/.test(value),
        containsNumber: /\d/.test(value),
        containsSpecial: /[!@#$%^&*(),.?":{}|<>]/.test(value),
      });
    }
  };

  // Auto-format phone number input
  const handlePhoneChange = (e) => {
    let value = e.target.value.replace(/\D/g, "");
    if (value.length > 10) value = value.slice(0, 10);

    let formattedNumber = value;
    if (value.length > 6) {
      formattedNumber = `(${value.slice(0, 3)}) ${value.slice(3, 6)}-${value.slice(6)}`;
    } else if (value.length > 3) {
      formattedNumber = `(${value.slice(0, 3)}) ${value.slice(3)}`;
    }

    setFormData({ ...formData, phone: formattedNumber });
  };

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

  const handleSignup = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;

    // Hash password before sending to backend
    const saltRounds = 10;
    const hashedPassword = await bcrypt.hash(formData.password, saltRounds);

    // Prepare user data
    const userData = {
      fullName: formData.fullName,
      email: formData.email,
      phoneNumber: formData.phone,
      passwordHash: hashedPassword,
    };

    try {
      const response = await fetch("http://localhost:8080/api/auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(userData),
      });

      const data = await response.text();
      if (response.ok) {
        setSuccessMessage("Sign-up Successful!");
        setGeneralError("");

        // Clear form & reset validation states
        setFormData({
          fullName: "",
          email: "",
          emailVerify: "",
          phone: "",
          password: "",
          passwordVerify: "",
        });

        setPasswordCriteria({
          minLength: false,
          upperLowerCase: false,
          containsNumber: false,
          containsSpecial: false,
        });

      } else {
        setGeneralError("Registration failed: " + data);
      }
    } catch (error) {
      console.error("Signup error:", error);
      setGeneralError("An error occurred. Please try again.");
    }
  };

  return (
    <div className="signup-left">
      <div className="signup-form">
        <h1>Sign Up</h1>
        {successMessage && <p className="success-message">{successMessage}</p>}
        {generalError && <p className="error-message">{generalError}</p>}

        <form onSubmit={handleSignup}>
          <div className="fullname-input">
            <input
              type="text"
              name="fullName"
              placeholder="Full Name"
              value={formData.fullName}
              onChange={handleChange}
              required
            />
            {nameError && <span className="error-message-inline">{nameError}</span>}
          </div>

          <div className="email-input">
            <input type="email" name="email" placeholder="Enter Email" value={formData.email} onChange={handleChange} required />
          </div>

          <div className="emailVerify-input">
            <input type="email" name="emailVerify" placeholder="Verify Email" value={formData.emailVerify} onChange={handleChange} required />
            {emailError && <span className="error-message-inline">{emailError}</span>}
          </div>

          <div className="phone-input">
            <input type="text" name="phone" placeholder="Phone Number" value={formData.phone} onChange={handlePhoneChange} required />
          </div>

          <div className="password-input">
            <div className="password-field">
              <input
                type={showPassword ? "text" : "password"}
                name="password"
                placeholder="Create Password"
                value={formData.password}
                onChange={handleChange}
                onFocus={() => setPasswordFocused(true)}
                onBlur={() => setPasswordFocused(false)}
                required
              />
              <span className="toggle-password" onClick={() => setShowPassword(!showPassword)}>
                {showPassword ? <FaEyeSlash /> : <FaEye />}
              </span>
            </div>
          </div>

          <div className="passwordVerify-input">
            <div className="password-field">
              <input
                type={showPasswordVerify ? "text" : "password"}
                name="passwordVerify"
                placeholder="Verify Password"
                value={formData.passwordVerify}
                onChange={handleChange}
                required
              />
              <span className="toggle-password" onClick={() => setShowPasswordVerify(!showPasswordVerify)}>
                {showPasswordVerify ? <FaEyeSlash /> : <FaEye />}
              </span>
            </div>
            {passwordError && <span className="error-message-inline">{passwordError}</span>}
          </div>

          <button type="submit" className="signup-button">Sign Up</button>
        </form>

        <div className="login">
          Already signed up?{" "}
          <span className="nav-link" onClick={() => navigate("/auth/login")}>
            Login here
          </span>
        </div>
      </div>
    </div>
  );
};

export default Signup;
