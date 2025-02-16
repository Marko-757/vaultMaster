import React, { useState } from "react";
import "./profileSettings.css";
import defaultProfilePic from "../Assets/defaultProfileImage.png";

const ProfileSettings = () => {
  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    phoneNumber: "",
    password: "",
    profilePicture: defaultProfilePic,
  });

  // handle input changes
  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  // handle profile picture upload
  const handleProfilePictureUpload = (e) => {
    const file = e.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = () => {
        setFormData({ ...formData, profilePicture: reader.result });
      };
      reader.readAsDataURL(file);
    }
  };

  // handle form submission
  const handleSubmit = (e) => {
    e.preventDefault();
    alert("Profile updated successfully!");
    // *************************************ADD API CALL HERE********************************************
  };

  return (
    <div className="profile-settings-container">
      <button className="back-button">⬅</button>

      <div className="profile-content">
        {/* Profile Picture Section */}
        <div className="profile-section">
          <label htmlFor="profilePictureUpload" className="profile-picture-label">
            <img
              src={formData.profilePicture}
              alt="Profile"
              className="profile-picture"
            />
            <p>Set Profile Picture</p>
          </label>
          <input
            type="file"
            id="profilePictureUpload"
            accept="image/*"
            onChange={handleProfilePictureUpload}
            style={{ display: "none" }}
          />
        </div>

        {/* Profile Settings Form */}
        <form className="settings-form" onSubmit={handleSubmit}>
          <h2>Profile Settings</h2>

          <div className="form-group">
            <label>First Name:</label>
            <input
              type="text"
              name="firstName"
              value={formData.firstName}
              onChange={handleChange}
            />
          </div>

          <div className="form-group">
            <label>Last Name:</label>
            <input
              type="text"
              name="lastName"
              value={formData.lastName}
              onChange={handleChange}
            />
          </div>

          <div className="form-group">
            <label>Email:</label>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
            />
          </div>

          <div className="form-group">
            <label>Phone Number:</label>
            <input
              type="tel"
              name="phoneNumber"
              value={formData.phoneNumber}
              onChange={handleChange}
            />
          </div>

          <div className="form-group">
            <label>Password:</label>
            <input
              type="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
            />
          </div>

          <button type="submit" className="save-button">Save Changes</button>
        </form>
      </div>
    </div>
  );
};

export default ProfileSettings;
