import { createContext, useContext, useState, useEffect } from "react";
import authService from "../services/auth.service";

const AuthContext = createContext(null);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [loading, setLoading] = useState(true);
  const [user, setUser] = useState(null);
  const [error, setError] = useState(null);

  // Check authentication status on app load
  useEffect(() => {
    const checkAuth = async () => {
      try {
        setError(null);
        const token = localStorage.getItem("token");
        
        if (token) {
          const result = await authService.getUserInfo();
          if (result.success) {
            setUser(result.data);
            setIsAuthenticated(true);
          } else {
            // Only remove token if it's an authentication error
            if (result.message.includes("401") || result.message.includes("unauthorized")) {
              localStorage.removeItem("token");
              setIsAuthenticated(false);
              setUser(null);
            }
          }
        } else {
          setIsAuthenticated(false);
          setUser(null);
        }
      } catch (error) {
        console.error("Auth check error:", error);
        setError(error.message || "Authentication check failed");
      } finally {
        setLoading(false);
      }
    };

    checkAuth();
  }, []);

  const login = async (username, password) => {
    try {
      setLoading(true);
      setError(null);
      const result = await authService.login(username, password);
      
      if (result.success && result.data) {
        const userData = {
          username: result.data.username,
          email: result.data.email,
          roles: result.data.roles
        };
        setUser(userData);
        setIsAuthenticated(true);
        return { success: true, data: result.data };
      } else {
        const errorMessage = result.message || "Login failed";
        setError(errorMessage);
        return { success: false, message: errorMessage };
      }
    } catch (error) {
      const errorMessage = error.message || "Login failed";
      setError(errorMessage);
      return { success: false, message: errorMessage };
    } finally {
      setLoading(false);
    }
  };

  const logout = async () => {
    try {
      setLoading(true);
      setError(null);
      const result = await authService.logout();
      
      if (result.success) {
        setUser(null);
        setIsAuthenticated(false);
      }
      return result;
    } catch (error) {
      setError(error.message || "Logout failed");
      return { success: false, message: error.message };
    } finally {
      setLoading(false);
    }
  };

  const register = async (username, email, password) => {
    try {
      setLoading(true);
      setError(null);
      const result = await authService.register(username, email, password);
      return result;
    } catch (error) {
      setError(error.message || "Registration failed");
      return { success: false, message: error.message };
    } finally {
      setLoading(false);
    }
  };

  const value = {
    isAuthenticated,
    loading,
    user,
    error,
    login,
    logout,
    register
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
