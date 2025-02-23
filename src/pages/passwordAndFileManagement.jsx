import React, { useState } from "react";
import "./passwordAndFileManagement.css";

const PasswordAndFileManagement = () => {
  const [items, setItems] = useState([]);

  const [showModal, setShowModal] = useState(false);
  const [newItem, setNewItem] = useState({ type: "password", name: "", value: "" });

  // Toggle password visibility
  const toggleVisibility = (id) => {
    setItems(items.map((item) =>
      item.id === id ? { ...item, hidden: !item.hidden } : item
    ));
  };

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
    <div className="password-file-container">
      <div className="header">Password and File Management</div>

      <div className="items-list">
        {items.length === 0 ? (
          <p className="empty-message">No passwords or files available. Add one using the button below.</p>
        ) : (
          items.map((item) => (
            <div key={item.id} className="item-card">
              <span className="item-name">{item.name}</span>

              <div className="item-actions">
                {item.type === "password" ? (
                  <>
                    <span className="password">{item.hidden ? "●●●●●●●●" : item.value}</span>
                    <button className="icon-button" onClick={() => toggleVisibility(item.id)}>👁️</button>
                  </>
                ) : (
                  <button className="icon-button">⬇️</button>
                )}

                <button className="icon-button delete" onClick={() => deleteItem(item.id)}>🗑️</button>
              </div>
            </div>
          ))
        )}
      </div>

      <button className="add-button" onClick={() => setShowModal(true)}>Add</button>


      {showModal && (
        <div className="modal">
          <div className="modal-content">
            <h3>Add</h3>
            <label>Type:</label>
            <select value={newItem.type} onChange={(e) => setNewItem({ ...newItem, type: e.target.value })}>
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

            <div className="modal-buttons">
              <button className="confirm-button" onClick={addNewItem}>Add</button>
              <button className="cancel-button" onClick={() => setShowModal(false)}>Cancel</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default PasswordAndFileManagement;
