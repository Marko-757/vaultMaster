import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { FaUserCircle } from "react-icons/fa";
import "./passwordAndFileManagement.css";

const PasswordAndFileManagement = ({ selectedTeam, onBack }) => {
  const navigate = useNavigate();
  const [items, setItems] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [newItem, setNewItem] = useState({ type: "password", name: "", value: "" });

  // Toggle password visibility
  const toggleVisibility = (id) => {
    setItems(items.map((item) =>
      item.id === id ? { ...item, hidden: !item.hidden } : item
    ));
  };

  // Delete item with confirmation
  const deleteItem = (id) => {
    if (window.confirm("Are you sure you want to delete this item?")) {
      setItems(items.filter((item) => item.id !== id));
    }
  };

  // Handle adding new passwords or files
  const addNewItem = () => {
    if (newItem.name.trim() === "") {
      alert("Please enter a name.");
      return;
    }

    const itemToAdd = {
      id: items.length + 1,
      type: newItem.type,
      name: newItem.name,
      value: newItem.type === "password" ? newItem.value : "",
      hidden: true,
    };

    setItems([...items, itemToAdd]);
    setShowModal(false);
    setNewItem({ type: "password", name: "", value: "" });
  };

  return (
    <div className="pafm-container">
      {/* Banner Row: Back Button, Banner, and Profile Button */}
      <div className="banner-wrapper">
        <button className="back-button" onClick={onBack}>←</button>
        <h2 className="section-banner">{selectedTeam} - Password and File Management</h2>
        <button className="profile-button" onClick={() => navigate("/settings")}>
          <FaUserCircle />
        </button>
      </div>

      <div className="pafm-items-list">
        {items.length === 0 ? (
          <p className="pafm-empty-message">
            No passwords or files available. Add one using the button below.
          </p>
        ) : (
          items.map((item) => (
            <div key={item.id} className="pafm-item-card">
              <span className="pafm-item-name">{item.name}</span>

              <div className="pafm-item-actions">
                {item.type === "password" ? (
                  <>
                    <span className="pafm-password">
                      {item.hidden ? "●●●●●●●●" : item.value}
                    </span>
                    <button className="pafm-icon-button" onClick={() => toggleVisibility(item.id)}>
                      👁️
                    </button>
                  </>
                ) : (
                  <button className="pafm-icon-button">⬇️</button>
                )}
                <button className="pafm-icon-button pafm-delete" onClick={() => deleteItem(item.id)}>
                  🗑️
                </button>
              </div>
            </div>
          ))
        )}
      </div>

      <button className="pafm-add-button" onClick={() => setShowModal(true)}>Add</button>

      {showModal && (
        <div className="pafm-modal">
          <div className="pafm-modal-content">
            <h3>Add</h3>
            <label>Type:</label>
            <select
              value={newItem.type}
              onChange={(e) => setNewItem({ ...newItem, type: e.target.value })}
            >
              <option value="password">Password</option>
              <option value="file">File</option>
            </select>

            <label>Name:</label>
            <input
              type="text"
              placeholder="Enter name"
              value={newItem.name}
              onChange={(e) => setNewItem({ ...newItem, name: e.target.value })}
            />

            {newItem.type === "password" && (
              <>
                <label>Password:</label>
                <input
                  type="text"
                  placeholder="Enter password"
                  value={newItem.value}
                  onChange={(e) => setNewItem({ ...newItem, value: e.target.value })}
                />
              </>
            )}

            <div className="pafm-modal-buttons">
              <button className="pafm-confirm-button" onClick={addNewItem}>Add</button>
              <button className="pafm-cancel-button" onClick={() => setShowModal(false)}>Cancel</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default PasswordAndFileManagement;