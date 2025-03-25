import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthProvider';

const MainLayout = ({ children }) => {
  const { isAuthenticated, user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/');
  };

  return (
    <div className="fixed inset-0 flex flex-col bg-gray-50">
      {/* Navigation */}
      <nav className="flex-none h-16 bg-white shadow-md">
        <div className="h-full max-w-7xl mx-auto px-4">
          <div className="flex justify-between items-center h-full">
            <div className="flex items-center">
              <Link to="/" className="text-xl font-bold text-gray-800">
                My App
              </Link>
              <div className="hidden sm:ml-6 sm:flex sm:space-x-8">
                <Link
                  to="/"
                  className="text-gray-700 hover:text-gray-900 px-1 text-sm font-medium"
                >
                  Home
                </Link>
                {isAuthenticated && (
                  <>
                    <Link
                      to="/dashboard"
                      className="text-gray-700 hover:text-gray-900 px-1 text-sm font-medium"
                    >
                      Dashboard
                    </Link>
                    <Link
                      to="/profile"
                      className="text-gray-700 hover:text-gray-900 px-1 text-sm font-medium"
                    >
                      Profile
                    </Link>
                  </>
                )}
              </div>
            </div>

            <div className="flex items-center space-x-4">
              {isAuthenticated ? (
                <>
                  <span className="text-gray-700">Welcome, {user?.username}</span>
                  <button
                    onClick={handleLogout}
                    className="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-md text-sm font-medium"
                  >
                    Logout
                  </button>
                </>
              ) : (
                <>
                  <Link
                    to="/login"
                    className="text-gray-700 hover:text-gray-900 px-3 py-2 rounded-md text-sm font-medium"
                  >
                    Login
                  </Link>
                  <Link
                    to="/register"
                    className="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-md text-sm font-medium"
                  >
                    Register
                  </Link>
                </>
              )}
            </div>
          </div>
        </div>
      </nav>

      {/* Main Content */}
      <main className="flex-1 flex items-center justify-center">
        {children}
      </main>

      {/* Footer */}
      <footer className="flex-none h-16 bg-white shadow-md">
        <div className="h-full max-w-7xl mx-auto px-4 flex items-center justify-center">
          <p className="text-gray-500 text-sm">
            © 2024 My App. All rights reserved.
          </p>
        </div>
      </footer>
    </div>
  );
};

export default MainLayout;
