import React from "react";
import { useNavigate } from "react-router-dom";
import { FaUserCircle } from "react-icons/fa";
import "./myMembers.css";

const MyMembers = ({
  selectedTeam,
  employees = [
    {
      id: 1,
      firstName: "John",
      lastName: "Doe",
      email: "john.doe@example.com",
      phone: "123-456-7890",
      roles: "Manager",
      photo: "https://via.placeholder.com/50",
    },
    {
      id: 2,
      firstName: "Jane",
      lastName: "Smith",
      email: "jane.smith@example.com",
      phone: "987-654-3210",
      roles: "Developer",
      photo: "https://via.placeholder.com/50",
    },
  ],
  onBack,
}) => {
  const navigate = useNavigate();

  return (
    <div className="members-view">
      {/* Banner Row: Back Button, Banner, and Profile Button */}
      <div className="banner-wrapper">
        <button className="back-button" onClick={onBack}>
          ←
        </button>
        <h2 className="section-banner">
          {selectedTeam} - My Members
        </h2>
        <button
          className="profile-button"
          onClick={() => navigate("/settings")}
        >
          <FaUserCircle />
        </button>
      </div>

      <div className="members-roster">
        {employees.length > 0 ? (
          employees.map((employee) => (
            <div className="member-row" key={employee.id}>
              <img
                src={employee.photo}
                alt={`${employee.firstName} ${employee.lastName}`}
                className="member-photo"
              />
              <div className="member-name">
                {employee.firstName} {employee.lastName}
              </div>
              <div className="member-email">{employee.email}</div>
              <div className="member-phone">{employee.phone}</div>
              <div className="member-roles">{employee.roles}</div>
            </div>
          ))
        ) : (
          <p className="empty-message">No members found.</p>
        )}
      </div>
    </div>
  );
};

export default MyMembers;
