import React, { useState, useEffect } from "react";
import "./profileSettings.css";
import { getProfile, updateProfile } from "../api/authService";
import axios from "axios";
import defaultProfileImage from "../Assets/defaultProfileImage.png";
import { useNavigate } from "react-router-dom";

const ProfileSettings = () => {
  const [userProfile, setUserProfile] = useState({
    fullName: "",
    email: "",
    phoneNumber: "",
    profilePicture: defaultProfileImage,
  });

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const navigate = useNavigate();

  const handleGoBack = () => {
    navigate(-1); // navigates to the previous page in history
  };

  useEffect(() => {
    getProfile()
      .then((data) => {
        if (data) {
          setUserProfile(data);
        } else {
          setError("Failed to fetch profile data.");
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
      const updatedProfile = await updateProfile(userProfile);
      if (updatedProfile) {
        alert("Profile updated successfully!");
      } else {
        setError("Failed to update profile.");
      }
    } catch (err) {
      setError(err.message);
    }
  };

  const openModal = () => setIsModalOpen(true);
  const closeModal = () => setIsModalOpen(false);

  const handlePasswordChange = async () => {
    if (newPassword !== confirmPassword) {
      alert("Passwords do not match");
      return;
    }

    const response = await fetch("/api/users/change-password", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        currentPassword,
        newPassword,
      }),
    });

    if (response.ok) {
      alert("Password changed successfully!");
      closeModal();
    } else {
      alert("Error changing password");
    }
  };

  if (loading) {
    return <div className="loading-screen">Loading...</div>;
  }

  if (error) {
    return <div className="error-message">Error: {error}</div>;
  }

  return (
    <div className="container py-5">
      <button className="btn btn-outline-secondary mb-3" onClick={handleGoBack}>
        ← Back
      </button>

      <h2 className="mb-4">User Settings</h2>
      <form onSubmit={handleSave}>
        <div className="row mb-4">
          <div className="col-md-6">
            <h4>Profile Picture</h4>
            <div className="mb-3">
              <img
                src={userProfile.profilePicture}
                alt="Profile"
                className="img-thumbnail mb-2"
                style={{ maxWidth: "150px" }}
              />
              <input
                className="form-control"
                type="file"
                id="profilePicture"
                disabled
              />
            </div>
          </div>
          <div className="col-md-6">
            <h4>Personal Information</h4>
            <div className="mb-3">
              <label htmlFor="fullName" className="form-label">
                Full Name
              </label>
              <input
                type="text"
                className="form-control"
                id="fullName"
                name="fullName"
                value={userProfile.fullName}
                onChange={handleInputChange}
              />
            </div>
            <div className="mb-3">
              <label htmlFor="email" className="form-label">
                Email address
              </label>
              <input
                type="email"
                className="form-control"
                id="email"
                name="email"
                value={userProfile.email}
                onChange={handleInputChange}
              />
            </div>
            <div className="mb-3">
              <label htmlFor="phoneNumber" className="form-label">
                Phone Number
              </label>
              <input
                type="tel"
                className="form-control"
                id="phoneNumber"
                name="phoneNumber"
                value={userProfile.phoneNumber}
                onChange={handleInputChange}
              />
            </div>
          </div>
        </div>

        <div className="d-grid gap-2 d-md-flex justify-content-md-end">
          <button type="submit" className="btn btn-primary">
            Save Changes
          </button>
        </div>
      </form>

      {/* Modal */}
      <div
        className={`modal-overlay ${isModalOpen ? "active" : ""}`}
        onClick={closeModal}
      >
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
            <button className="save-button" onClick={handlePasswordChange}>
              Confirm
            </button>
            <button className="cancel-button" onClick={closeModal}>
              Cancel
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProfileSettings;
