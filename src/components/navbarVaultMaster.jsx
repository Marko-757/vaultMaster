import React from "react";
import { Navbar, Container, Nav, NavDropdown } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import vaultLogo from "../Assets/vaultmaster_logo.png";
import profileIcon from "../Assets/defaultProfileImage.png";
import "./navbarVaultMaster.css";

const NavbarVaultMaster = ({ onLogout }) => {
  const navigate = useNavigate();

  return (
    <Navbar bg="light" expand="lg" className="shadow-sm mb-3">
      <Container fluid>
        <Navbar.Brand href="/home">
          <img
            src={vaultLogo}
            alt="VaultMaster Logo"
            width="30"
            height="30"
            className="d-inline-block align-text-top me-2"
          />
          VaultMaster
        </Navbar.Brand>
        <Nav className="ms-auto ms-4">
          <div style={{ position: "relative" }}>
            <NavDropdown
              title={
                <img
                  src={profileIcon}
                  alt="Profile"
                  width="40"
                  height="40"
                  style={{
                    borderRadius: "50%",
                    objectFit: "cover",
                    border: "1px solid #ccc",
                    padding: "2px",
                    backgroundColor: "#f8f9fa",
                  }}
                />
              }
              id="profile-dropdown"
              align="end"
              menuVariant="light"
            >
              <NavDropdown.Item onClick={() => navigate("/settings")}>
                Profile Settings
              </NavDropdown.Item>
              <NavDropdown.Divider />
              <NavDropdown.Item onClick={onLogout}>Log Out</NavDropdown.Item>
            </NavDropdown>
          </div>
        </Nav>
      </Container>
    </Navbar>
  );
};

export default NavbarVaultMaster;
