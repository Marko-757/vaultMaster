import React, { useEffect, useState } from "react";
import { getPasswordsForRole, decryptTeamPassword } from "../api/teamPWFileService";
import "./rolePasswordViewer.css";
import { FaEye, FaEyeSlash, FaRegCopy } from "react-icons/fa";

const RolePasswordViewer = ({ roleId }) => {
  const [passwords, setPasswords] = useState([]);
  const [visiblePasswords, setVisiblePasswords] = useState({});

  useEffect(() => {
    const fetchPasswords = async () => {
      try {
        const res = await getPasswordsForRole(roleId);
        setPasswords(res.data);
      } catch (err) {
        console.error("Failed to fetch role passwords:", err);
      }
    };

    if (roleId) fetchPasswords();
  }, [roleId]);

  const toggleVisibility = async (passwordId) => {
    setVisiblePasswords((prev) => {
      const isVisible = prev[passwordId]?.visible;
      return {
        ...prev,
        [passwordId]: {
          visible: !isVisible,
          value: isVisible ? "" : prev[passwordId]?.value || null,
        },
      };
    });

    if (!visiblePasswords[passwordId]?.value) {
      try {
        const res = await decryptTeamPassword(passwordId);
        setVisiblePasswords((prev) => ({
          ...prev,
          [passwordId]: {
            visible: true,
            value: res.data,
          },
        }));
      } catch (err) {
        console.error("Decryption failed:", err);
        setVisiblePasswords((prev) => ({
          ...prev,
          [passwordId]: {
            visible: true,
            value: "[Unable to decrypt]",
          },
        }));
      }
    }
  };

  const handleCopy = (text) => {
    navigator.clipboard.writeText(text).then(() => {
      alert("Password copied to clipboard!");
    });
  };

  return (
    <div className="role-password-viewer">
      <h2>Passwords You Can View</h2>
      {passwords.length === 0 ? (
        <p>No passwords assigned to your role.</p>
      ) : (
        passwords.map((pw) => {
          const isVisible = visiblePasswords[pw.teamPasswordId]?.visible;
          const decrypted = visiblePasswords[pw.teamPasswordId]?.value || "••••••••";

          return (
            <div key={pw.teamPasswordId} className="role-password-entry">
              <h4>{pw.accountName}</h4>
              <p><strong>Username:</strong> {pw.username}</p>
              <p><strong>Website:</strong> {pw.website || "—"}</p>
              <p className="password-row">
                <strong>Password:</strong> {isVisible ? decrypted : "••••••••"}
                <span
                  className="icon-button"
                  title={isVisible ? "Hide password" : "Show password"}
                  onClick={() => toggleVisibility(pw.teamPasswordId)}
                >
                  {isVisible ? <FaEyeSlash /> : <FaEye />}
                </span>
                {isVisible && decrypted !== "[Unable to decrypt]" && (
                  <span
                    className="icon-button"
                    title="Copy password"
                    onClick={() => handleCopy(decrypted)}
                  >
                    <FaRegCopy />
                  </span>
                )}
              </p>
            </div>
          );
        })
      )}
    </div>
  );
};

export default RolePasswordViewer;