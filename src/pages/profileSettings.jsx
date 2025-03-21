import React, { useState, useEffect } from "react";
import "./profileSettings.css";
import { getProfile, updateProfile } from "../api/authService"; // Import the getProfile function
import axios from "axios";

const ProfileSettings = () => {
  const [userProfile, setUserProfile] = useState({
    fullName: '',
    email: '',
    phoneNumber: '',
    profilePicture: '',  // If you want to show a profile picture
  });

  // State for modal visibility
  const [isModalOpen, setIsModalOpen] = useState(false);

  // State for password fields
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Fetch profile data when component mounts
  useEffect(() => {
    // Fetch profile data when the component mounts
    getProfile()
      .then((data) => {
        if (data) {
          setUserProfile(data);
        } else {
          setError('Failed to fetch profile data.');
        }
        setLoading(false);
      })
      .catch((err) => {
        setError(err.message);
        setLoading(false);
      });
  }, []);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setUserProfile((prevProfile) => ({
      ...prevProfile,
      [name]: value,
    }));
  };

  const handleSave = async (e) => {
    e.preventDefault();
    try {
      const updatedProfile = await updateProfile(userProfile); // Send the updated profile to the backend
      if (updatedProfile) {
        alert("Profile updated successfully!");
      } else {
        setError("Failed to update profile.");
      }
    } catch (err) {
      setError(err.message);
    }
  };

  if (loading) {
    return (
      <div className="loading-screen">
        <span>Loading...</span> {/* Keep existing UI loading style */}
      </div>
    );
  }

  if (error) {
    return (
      <div className="error-message">
        <span>Error: {error}</span>
      </div>
    );
  }

  // Modal for changing password
  const openModal = () => setIsModalOpen(true);
  const closeModal = () => setIsModalOpen(false);


  // Handle password change
  const handlePasswordChange = async () => {
    if (newPassword !== confirmPassword) {
      alert('Passwords do not match');
      return;
    }

    // Send password change request to backend
    const response = await fetch('/api/users/change-password', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        currentPassword,
        newPassword,
      }),
    });

    if (response.ok) {
      alert('Password changed successfully!');
      closeModal();
    } else {
      alert('Error changing password');
    }
  };

  return (

    <div className="profile-settings-container">
      <div className="profile-content">
        <div className="profile-section">
        <h2>Your Profile</h2>
        <form className="settings-form" onSubmit={handleSave}>
        <div className="form-group">
          <label>Full Name:</label>
          <input
            type="text"
            name="fullName"
            value={userProfile.fullName}
            onChange={handleInputChange}
            required
          />
          </div>
          <div className="form-group">
          <label>Email:</label>
          <input
            type="email"
            name="email"
            value={userProfile.email}
            onChange={handleInputChange}
            required
          />
          </div>
          <div className="form-group">
          <label>Phone Number:</label>
          <input
            type="text"
            name="phoneNumber"
            value={userProfile.phoneNumber}
            onChange={handleInputChange}
            required
          />
          </div>
          </form>
        </div>

        {/* Password Section */}
        <div>
          <button className="change-password-button" onClick={openModal}>
            Change Password
          </button>
        <button type="submit" className="save-button">
          Save Changes
        </button>
        </div>
      </div>

      {/* Modal for Changing Password */}
      <div className={`modal-overlay ${isModalOpen ? 'active' : ''}`} onClick={closeModal}>
        <div className="modal-content" onClick={(e) => e.stopPropagation()}>
          <h2>Change Your Password</h2>
          <div className="form-group">
            <label>Current Password</label>
            <input
              type="password"
              value={currentPassword}
              onChange={(e) => setCurrentPassword(e.target.value)}
              placeholder="Enter your current password"
            />
          </div>
          <div className="form-group">
            <label>New Password</label>
            <input
              type="password"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              placeholder="Enter your new password"
            />
          </div>
          <div className="form-group">
            <label>Confirm New Password</label>
            <input
              type="password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              placeholder="Confirm your new password"
            />
          </div>
          <div>
            <button className="save-button" onClick={handlePasswordChange}>Confirm</button>
            <button className="cancel-button" onClick={closeModal}>Cancel</button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProfileSettings;
