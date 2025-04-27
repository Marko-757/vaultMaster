import React from "react";
import "./sidebar.css";

function Sidebar({ title, items, onSelectItem, onAddItem, onDeleteItem }) {
  return (
    <div className="sidebar-container">
      <h3>{title}</h3>
      <ul className="sidebar-list">
        <li
          className="all-items"
          onClick={() => onSelectItem(null)}
        >
          All {title}
        </li>
        {items.map((item) => (
          <li key={item.id} className="sidebar-item">
            <span className="folder-name" onClick={() => onSelectItem(item)}>
              {item.name}
            </span>
            <button
              className="delete-button"
              onClick={() => onDeleteItem(item.id)}
              title="Delete Folder"
            >
              🗑️
            </button>
          </li>
        ))}
      </ul>
      <button className="add-button" onClick={onAddItem} title="Add Folder">
        +
      </button>
    </div>
  );
}

export default Sidebar;
