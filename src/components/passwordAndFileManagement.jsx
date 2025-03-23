import React, { useRef, useState } from "react";
import "./passwordAndFileManagement.css";

const PasswordAndFileManagement = () => {
  const [items, setItems] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [newItem, setNewItem] = useState({ type: "password", name: "", value: "" });
  const fileInputRef = useRef(null);
  const [selectedFileName, setSelectedFileName] = useState("");

  const toggleVisibility = (id) => {
    setItems(items.map(item =>
      item.id === id ? { ...item, hidden: !item.hidden } : item
    ));
  };

  const deleteItem = (id) => {
    if (window.confirm("Are you sure you want to delete this item?")) {
      setItems(items.filter(item => item.id !== id));
    }
  };

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setSelectedFileName(file.name);
      setNewItem({ ...newItem, name: file.name, file });
    }
  };

  const addNewItem = () => {
    if (newItem.type === "password") {
      if (!newItem.name || !newItem.value) {
        alert("Please enter a name and password.");
        return;
      }
    } else {
      if (!newItem.file) {
        alert("Please choose a file.");
        return;
      }
    }

    const itemToAdd = {
      id: items.length + 1,
      type: newItem.type,
      name: newItem.name,
      value: newItem.value || "",
      file: newItem.file || null,
      hidden: true,
    };

    setItems([...items, itemToAdd]);
    setShowModal(false);
    setNewItem({ type: "password", name: "", value: "" });
    setSelectedFileName("");
  };

  const downloadFile = (item) => {
    if (!item.file) return;

    const blobUrl = URL.createObjectURL(item.file);
    const link = document.createElement("a");
    link.href = blobUrl;
    link.download = item.name;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(blobUrl);
  };

  return (
    <div className="password-file-container">
      <div className="header">Password and File Management</div>

      <div className="items-list">
        {items.length === 0 ? (
          <p className="empty-message">No passwords or files available. Add one using the button below.</p>
        ) : (
          items.map(item => (
            <div key={item.id} className="item-card">
              <div className="item-info">
                <span className="item-name">{item.name}</span>
                <span className="item-label">{item.type === "password" ? "Password" : "File"}</span>
              </div>

              <div className="item-actions">
                {item.type === "password" ? (
                  <>
                    <span className="password">
                      {item.hidden ? "●●●●●●●●" : item.value}
                    </span>
                    <button className="icon-button" onClick={() => toggleVisibility(item.id)}>👁️</button>
                  </>
                ) : (
                  <button className="icon-button" onClick={() => downloadFile(item)}>⬇️</button>
                )}
                <button className="icon-button delete" onClick={() => deleteItem(item.id)}>🗑️</button>
              </div>
            </div>
          ))
        )}
      </div>

      <button className="add-button" onClick={() => setShowModal(true)}>+</button>

      {showModal && (
        <div className="modal">
          <div className="modal-content">
            <h3>Add Password or File</h3>

            <label>Type:</label>
            <select
              value={newItem.type}
              onChange={(e) => {
                const type = e.target.value;
                setNewItem({ type, name: "", value: "", file: null });
                setSelectedFileName("");
              }}
            >
              <option value="password">Password</option>
              <option value="file">File</option>
            </select>

            {newItem.type === "password" && (
              <>
                <label>Name:</label>
                <input
                  type="text"
                  placeholder="Enter name"
                  value={newItem.name}
                  onChange={(e) => setNewItem({ ...newItem, name: e.target.value })}
                />
                <label>Password:</label>
                <input
                  type="text"
                  placeholder="Enter password"
                  value={newItem.value}
                  onChange={(e) => setNewItem({ ...newItem, value: e.target.value })}
                />
              </>
            )}

            {newItem.type === "file" && (
              <>
                <label>Choose File:</label>
                <button
                  className="file-picker-button"
                  onClick={() => fileInputRef.current.click()}
                >
                  {selectedFileName ? selectedFileName : "Browse..."}
                </button>
                <input
                  type="file"
                  ref={fileInputRef}
                  style={{ display: "none" }}
                  onChange={handleFileChange}
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
