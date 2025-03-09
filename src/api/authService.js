import bcrypt from "bcryptjs";
const BASE_URL = "http://localhost:8080/api/auth";

//Sign-up

export const signup = async (userData) => {
    try {
        // ✅ Hash the password before sending
        const hashedPassword = await bcrypt.hash(userData.password, 10);
        const userDataWithHash = { ...userData, password: hashedPassword };

        const response = await fetch(`${BASE_URL}/register`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(userDataWithHash),
        });

        if (!response.ok) {
            throw new Error("Signup failed.");
        }
        return await response.json();
    } catch (error) {
        console.error("Signup error:", error);
        return null;
    }
};



//Login
export const login = async (email, password) => {
    try {
        const response = await fetch(`${BASE_URL}/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email }),
        });

        if (!response.ok) {
            throw new Error("Invalid email or password.");
        }

        const data = await response.json();

        // Compare input password with stored hashed password
        const isMatch = await bcrypt.compare(password, data.user.passwordHash);
        if (!isMatch) {
            throw new Error("Invalid email or password.");
        }

        // Store token & user data if authentication succeeds
        localStorage.setItem("jwtToken", data.token);
        localStorage.setItem("user", JSON.stringify(data.user));

        return data;
    } catch (error) {
        console.error("Login error:", error);
        return null;
    }
};


//Logout
export const logout = () => {
    localStorage.removeItem("jwtToken");
    localStorage.removeItem("user");
    console.log("User logged out.");
};