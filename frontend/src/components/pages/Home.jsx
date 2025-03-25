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
              You are not logged in
            </h1>
          )}
        </div>
      </div>
    </MainLayout>
  );
};

export default Home; 