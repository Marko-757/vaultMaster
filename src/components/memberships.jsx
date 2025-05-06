import React, { useEffect, useState } from "react";
import {
  redeemInvitationCode,
  getMyTeamMemberships,
  getPasswordsVisibleToRole,
} from "../api/teamMembershipService";
import TeamPasswordInformation from "./teamPasswordInformation";
import "./myMemberships.css";

const MyMemberships = () => {
  const [code, setCode] = useState("");
  const [memberships, setMemberships] = useState([]);
  const [selectedMembership, setSelectedMembership] = useState(null);
  const [visiblePasswords, setVisiblePasswords] = useState([]);

  const fetchMemberships = async () => {
    try {
      const res = await getMyTeamMemberships();
      setMemberships(res.data);
    } catch (err) {
      console.error("Failed to load memberships:", err);
    }
  };

  useEffect(() => {
    fetchMemberships();
  }, []);

  const handleRedeem = async () => {
    try {
      await redeemInvitationCode(code);
      setCode("");
      fetchMemberships(); // refresh list
    } catch (err) {
      alert("Failed to redeem invitation code.");
      console.error(err);
    }
  };

  const handleSelectMembership = async (membership) => {
    setSelectedMembership(membership);
    try {
      const res = await getPasswordsVisibleToRole(membership.roleId);
      setVisiblePasswords(res.data);
    } catch (err) {
      console.error("Failed to load passwords:", err);
    }
  };

  return (
    <div className="memberships-container">
      <h2>Join a Team</h2>
      <input
        type="text"
        value={code}
        onChange={(e) => setCode(e.target.value)}
        placeholder="Enter invitation code"
      />
      <button onClick={handleRedeem}>Join Team</button>

      <h2>My Team Memberships</h2>
      <ul className="membership-list">
        {memberships.map((m) => (
          <li
            key={m.teamId}
            className={selectedMembership?.teamId === m.teamId ? "selected" : ""}
            onClick={() => handleSelectMembership(m)}
          >
            <strong>{m.teamName}</strong> — Role: <em>{m.roleName}</em>
          </li>
        ))}
      </ul>

      {selectedMembership && (
        <>
          <h3>Passwords Visible to {selectedMembership.roleName}</h3>
          {visiblePasswords.map((pw) => (
            <TeamPasswordInformation
              key={pw.teamPasswordId}
              password={pw}
              decryptedPassword="••••••••"
              showPassword={false}
              canEdit={false}
            />
          ))}
        </>
      )}
    </div>
  );
};

export default MyMemberships;