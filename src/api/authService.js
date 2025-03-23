import bcrypt from "bcryptjs";

const BASE_URL = "http://localhost:8080/api/auth";

// Sign-up
export const signup = async (userData) => {
  try {
    const hashedPassword = await bcrypt.hash(userData.password, 10);
    const userDataWithHash = { ...userData, password: hashedPassword };

    const response = await fetch(`${BASE_URL}/register`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(userDataWithHash),
    });

    if (!response.ok) {
      const errorMessage = await response.text();
      throw new Error(errorMessage || "Signup failed.");
    }

    return await response.text();
  } catch (error) {
    console.error("Signup error:", error.message);
    return null;
  }
};

// Login
export const login = async (email, password) => {
  try {
    const response = await fetch(`${BASE_URL}/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, password }),
      credentials: "include",
    });

    if (!response.ok) {
      const errorMessage = await response.text();
      throw new Error(errorMessage || "Invalid email or password.");
    }

    return await response.text();
  } catch (error) {
    console.error("Login error:", error.message);
    return null;
  }
};

// Logout
export const logout = async () => {
  try {
    await fetch(`${BASE_URL}/logout`, {
      method: "POST",
      credentials: "include",
    });
  } catch (error) {
    console.error("Logout error:", error.message);
  }
};

// Check Authentication
export const checkAuth = async () => {
  try {
    const response = await fetch("http://localhost:8080/api/auth/me", {
      credentials: "include", // Sends the cookie
    });

    if (!response.ok) {
      return null;
    }

    return await response.json(); 
  } catch (error) {
    console.error("Auth check error:", error);
    return null;
  }
};

export const getProfile = async () => {
  try {
    const response = await fetch("http://localhost:8080/api/users/profile", {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include", 
    });

    if (!response.ok) {
      throw new Error("Failed to fetch profile");
    }

    return await response.json(); 
  } catch (error) {
    console.error("Error fetching profile:", error.message);
    throw error;
  }
};

export const updateProfile = async (profileData) => {
  try {
    const token = localStorage.getItem("jwtToken"); 
    const response = await fetch("http://localhost:8080/api/users/profile", {
      method: "PUT",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify(profileData), 
    });

    if (!response.ok) {
      throw new Error("Failed to update profile");
    }

    return await response.json(); 
  } catch (error) {
    console.error("Error updating profile:", error.message);
    throw error;
  }
};
