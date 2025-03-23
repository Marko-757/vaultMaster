import React, { useRef, useState } from "react";
import "./2fa.css";
import { useNavigate } from "react-router-dom";
import { verifyOtp } from "../api/2faService";

export const TwoFA = () => {
  const navigate = useNavigate();
  const [codes, setCodes] = useState(["", "", "", "", "", ""]); // State to store the 6-digit code
  const inputRefs = useRef([]); // Refs for the input boxes

  const handleSubmit = async (event) => {
    event.preventDefault();
    const code = codes.join("").trim();

    try {
      const response = await fetch(
        `http://localhost:8080/api/2fa/verify?otp=${code}`, // You might also need `userId` here depending on backend
        {
          method: "POST",
          credentials: "include",
        }
      );

      if (!response.ok) {
        const errorText = await response.text();
        alert(errorText || "Invalid OTP. Try again.");
        return;
      }

      const result = await response.text();
      console.log("OTP verified:", result);
      // Redirect to the password manager or wherever you want
      navigate("/personal");
    } catch (error) {
      console.error("OTP verification error:", error);
      alert("Something went wrong verifying the OTP.");
    }
  };

  const handleInputChange = (index, value) => {
    // Only allow numbers and limit to one character
    if (/^\d*$/.test(value) && value.length <= 1) {
      const newCodes = [...codes];
      newCodes[index] = value;
      setCodes(newCodes);

      // Move to the next input if a number is entered
      if (value && index < 5) {
        inputRefs.current[index + 1].focus();
      }
    }
  };

  const handleKeyDown = (index, event) => {
    // Move to the previous input on backspace
    if (event.key === "Backspace" && !codes[index] && index > 0) {
      inputRefs.current[index - 1].focus();
    }
  };

  const handlePaste = (event) => {
    event.preventDefault();
    const pasteData = event.clipboardData.getData("text").trim(); // Get pasted data
    if (/^\d{6}$/.test(pasteData)) {
      // Check if pasted data is a 6-digit number
      const newCodes = pasteData.split(""); // Split into individual digits
      setCodes(newCodes); // Update state with the new codes

      // Focus on the last input box after pasting
      inputRefs.current[5].focus();
    }
  };

  return (
    <div className="verification-form">
      <div className="twofa-container">
        <div className="twofa-form">
          <h1>2-Factor Authentication</h1>

          <form onSubmit={handleSubmit} className="content-area">
            <h3>Welcome Back!</h3>
            <h4>Enter Verification Code</h4>

            <fieldset className="number-code">
              <legend>Security Code</legend>
              <div>
                {codes.map((code, index) => (
                  <input
                    key={index}
                    name="code"
                    className="code-input"
                    type="text"
                    value={code}
                    onChange={(e) => handleInputChange(index, e.target.value)}
                    onKeyDown={(e) => handleKeyDown(index, e)}
                    onPaste={handlePaste}
                    ref={(el) => (inputRefs.current[index] = el)}
                    maxLength={1}
                    required
                  />
                ))}
              </div>
            </fieldset>
            <p>
              <a href="#">Resend Code</a>
            </p>
            <input type="submit" value="Submit" className="submit-button" />
          </form>

          <a href="/auth/login" className="back-to-login">
            Back to Login
          </a>
        </div>
      </div>
    </div>
  );
};

export default TwoFA;
