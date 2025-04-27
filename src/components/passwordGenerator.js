import React from "react";

function PasswordGenerator({ onGenerate }) {
  const generatePassword = () => {
    const length = 12;
    const chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()_+";
    let password = "";
    for (let i = 0; i < length; i++) {
      password += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    onGenerate(password);
  };

  return (
    <div className="password-generator">
      <button onClick={generatePassword}>Generate Strong Password</button>
    </div>
  );
}

export default PasswordGenerator;
