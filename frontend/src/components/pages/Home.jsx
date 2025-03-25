import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthProvider';
import MainLayout from '../layouts/MainLayout';

const Home = () => {
  const { isAuthenticated } = useAuth();

  return (
    <MainLayout>
      <div className="w-full h-full flex items-center justify-center">
        <div className="max-w-xl mx-auto text-center px-4">
          {isAuthenticated ? (
            <h1 className="text-4xl font-bold text-gray-900 mb-4">
              Welcome to Homepage
            </h1>
          ) : (
            <h1 className="text-4xl font-bold text-gray-900 mb-4">
              You are authenticated
            </h1>
          )}
          <p className="text-lg text-gray-600 mb-8">
            A secure authentication system built with Spring Boot and React
          </p>
          {!isAuthenticated && (
            <div className="flex flex-row items-center justify-center gap-4">
              <Link
                to="/login"
                className="bg-blue-600 hover:bg-blue-700 text-white px-6 py-2 rounded-lg text-base font-medium transition-colors"
              >
                Get Started
              </Link>
              <Link
                to="/register"
                className="bg-white hover:bg-gray-50 text-gray-800 border border-gray-300 px-6 py-2 rounded-lg text-base font-medium transition-colors"
              >
                Create Account
              </Link>
            </div>
          )}
        </div>
      </div>
    </MainLayout>
  );
};

export default Home; 