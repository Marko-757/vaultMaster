import React, { useRef, useState } from "react";
import "./2fa.css";
import { useNavigate } from "react-router-dom";

export const TwoFA = () => {
  const navigate = useNavigate();
  const [codes, setCodes] = useState(["", "", "", "", "", ""]); // State to store the 6-digit code
  const inputRefs = useRef([]); // Refs for the input boxes

  const handleSubmit = (event) => {
    event.preventDefault();
    const code = codes.join(""); // Combine the codes into a single string
    console.log("Submitted code:", code);
    // Add your submission logic here
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

  return (
    <div className="verification-form twofa-container">
      <div className="twofa-form">
        <h1>2-Factor Authentication</h1>

        <form onSubmit={handleSubmit} className="content-area">
        <h3>Welcome Back!</h3>
          <h4>Verify Login Code</h4>
          

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
                  ref={(el) => (inputRefs.current[index] = el)} // Assign ref to each input
                  maxLength={1} // Limit to one character
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

        <a href="/login" className="back-to-login">
          Back to Login
        </a>
      </div>
    </div>
  );
};

export default TwoFA;