import React from "react";
import "./fileList.css";

const FileList = ({ passwords, onSelectPassword }) => {
  return (
    <div className="password-name-list">
      {passwords.length === 0 ? (
        <div className="no-passwords">No Passwords Found</div>
      ) : (
        passwords.map((password) => (
          <button 
            key={password.id} 
            className="password-name-button" 
            onClick={() => onSelectPassword(password)}
          >
            {password.name}
          </button>
        ))
      )}
    </div>
  );
};

export default FileList;
