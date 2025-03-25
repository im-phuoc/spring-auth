import axios from "./axios";

class UserService {
  async getUsers(page = 0, size = 10) {
    try {
      console.log('Fetching users with params:', { page, size });
      const response = await axios.get(`/users?page=${page}&size=${size}`);
      console.log('Users API response:', response);
      
      if (response && response.data) {
        return {
          success: true,
          data: response
        };
      } else {
        console.error('Invalid response structure:', response);
        return {
          success: false,
          message: response?.data?.data?.message || "Invalid response format from server"
        };
      }
    } catch (error) {
      console.error('Error fetching users:', error);
      return {
        success: false,
        message: error.data?.message || "Failed to fetch users"
      };
    }
  }

  async getUserByUsername(username) {
    try {
      console.log('Fetching user details for username:', username);
      const response = await axios.get(`/users/${username}`);
      console.log('User details API response:', response);

      if (response && response.data) {
        return {
          success: true,
          data: response
        };
      } else {
        console.error('Invalid response structure:', response);
        return {
          success: false,
          message: response?.data?.data?.message || "Invalid response format from server"
        };
      }
    } catch (error) {
      console.error('Error fetching user details:', error);
      return {
        success: false,
        message: error.data?.message || "Failed to fetch user details"
      };
    }
  }

  async updateUserRoles(username, roles) {
    try {
      console.log('Updating roles for user:', username, 'with roles:', roles);
      const response = await axios.put(`/users/${username}`, { roles });
      console.log('Update roles API response:', response);

      if (response && response.data) {
        return {
          success: true,
          data: response.data
        };
      } else {
        console.error('Invalid response structure:', response);
        return {
          success: false,
          message: response?.data?.data?.message || "Invalid response format from server"
        };
      }
    } catch (error) {
      console.error('Error updating user roles:', error);
      return {
        success: false,
        message: error.data?.message || "Failed to update user roles"
      };
    }
  }

  async deleteUser(username) {
    try {
      console.log('Deleting user:', username);
      const response = await axios.delete(`/users/${username}`);
      console.log('Delete user API response:', response);

      if (response && response.data) {
        return {
          success: true,
          data: response.data
        };
      } else {
        console.error('Invalid response structure:', response);
        return {
          success: false,
          message: response?.data?.data?.message || "Invalid response format from server"
        };
      }
    } catch (error) {
      console.error('Error deleting user:', error);
      return {
        success: false,
        message: error.data?.message || "Failed to delete user"
      };
    }
  }
}

export default new UserService(); 