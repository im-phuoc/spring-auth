import axios from "axios";

const baseURL = import.meta.env.VITE_BACKEND_URL;
console.log("API URL:", baseURL); // For debugging

const axiosInstance = axios.create({
  baseURL,
  headers: {
    "Content-Type": "application/json",
  },
  timeout: 10000, // timeout sau 10s
});

// Add a request interceptor
axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    console.log("Request:", config); // For debugging
    return config;
  },
  (error) => Promise.reject(error),
);

const formatValidationErrors = (data) => {
  // Nếu có message trực tiếp, return message đó
  if (data.message) {
    return data.message;
  }

  // Nếu có các validation errors
  const errors = Object.entries(data)
    .map(([field, message]) => `${message}`)
    .join(", ");

  return errors || "Validation failed";
};

// Add a response interceptor
axiosInstance.interceptors.response.use(
  (response) => {
    console.log("Response:", response); // For debugging
    return response.data;
  },
  (error) => {
    if (error.response) {
      const errorData = error.response.data.data;
      return Promise.reject({
        message: formatValidationErrors(errorData),
        status: error.response.status,
        data: errorData,
      });
    } else if (error.request) {
      // Request was made but no response
      return Promise.reject({
        message: "No response from server",
        status: 503,
        data: null,
      });
    } else {
      // Something happened in setting up the request
      return Promise.reject({
        message: error.message || "Request failed",
        status: 500,
        data: null,
      });
    }
  },
);

export default axiosInstance;
