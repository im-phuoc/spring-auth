import axios from "./axios";

class AuthService {
  async login(username, password) {
    try {
      const response = await axios.post("/auth/login", {
        username,
        password,
      });

      const loginData = response.data;
      
      if (loginData?.token) {
        localStorage.setItem("token", loginData.token);
        return {
          success: true,
          data: loginData
        };
      } else {
        return {
          success: false,
          message: loginData?.data?.message || "Invalid response format from server"
        };
      }
    } catch (error) {
      console.error("Login error:", error);
      return {
        success: false,
        message: error.data?.message || "Login failed"
      };
    }
  }

  async register(username, email, password) {
    try {
      const response = await axios.post("/auth/register", {
        username,
        email,
        password,
      });

      return {
        success: true,
        data: response.data
      };
    } catch (error) {
      return {
        success: false,
        message: error.data?.message || "Registration failed"
      };
    }
  }

  logout() {
    try {
      localStorage.removeItem("token");
      return {
        success: true,
        message: "Logout successful",
      };
    } catch (error) {
      return {
        success: false,
        message: error.data?.message || "Logout failed",
      };
    }
  }

  async getUserInfo() {
    try {
      const token = localStorage.getItem("token");
      if (!token) {
        throw new Error("No token found");
      }

      const response = await axios.get("/users/profile", {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });

      return {
        success: true,
        data: response.data
      };
    } catch (error) {
      return {
        success: false,
        message: error.data?.message || "Failed to get user info"
      };
    }
  }

  getCurrentToken() {
    try {
      const token = localStorage.getItem("token");
      return {
        success: true,
        data: token,
      };
    } catch (error) {
      return {
        success: false,
        message: error.data?.message || "Failed to get token",
      };
    }
  }
}

export default new AuthService();
