import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import MainLayout from "../layouts/MainLayout";
import userService from "../../services/user.service";
import { useAuth } from "../../contexts/AuthProvider";
import RoleEditor from "../users/RoleEditor";
import Button from "../common/Button";
import Modal from "../common/Modal";
import toast from "react-hot-toast";

const User = () => {
  const { username } = useParams();
  const navigate = useNavigate();
  const { user: currentUser } = useAuth();
  const isAdmin = currentUser?.roles?.includes("ROLE_ADMIN");
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);

  useEffect(() => {
    const fetchUserDetails = async () => {
      try {
        setLoading(true);
        setError(null);
        console.log('Fetching user details for:', username);
        
        const result = await userService.getUserByUsername(username);
        console.log('User details result:', result);

        if (result.success && result.data?.data) {
          console.log('Setting user data:', result.data.data);
          setUser(result.data.data);
        } else {
          console.error('Error in response:', result);
          setError(result.message);
        }
      } catch (err) {
        console.error('Error in fetchUserDetails:', err);
        setError(err.response?.data?.data?.message || "Failed to fetch user details");
      } finally {
        setLoading(false);
      }
    };

    if (username && isAdmin) {
      fetchUserDetails();
    }
  }, [username, isAdmin]);

  const handleRolesUpdated = (newRoles) => {
    setUser(prev => ({
      ...prev,
      roles: newRoles
    }));
    setIsEditing(false);
  };

  const handleDelete = async () => {
    try {
      setIsDeleting(true);
      const result = await userService.deleteUser(username);
      
      if (result.success) {
        toast.success('User deleted successfully');
        navigate('/dashboard');
      } else {
        toast.error(result.message);
      }
    } catch (err) {
      console.error('Error deleting user:', err);
      toast.error(err.response?.data?.data?.message || 'Failed to delete user');
    } finally {
      setIsDeleting(false);
      setShowDeleteModal(false);
    }
  };

  if (!isAdmin) {
    return (
      <MainLayout>
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="bg-white shadow-sm rounded-lg mt-8 p-6">
            <div className="text-center">
              <div className="text-red-600 text-xl font-semibold mb-2">
                Access Denied
              </div>
              <p className="text-gray-600">
                You don't have permission to access this page.
              </p>
            </div>
          </div>
        </div>
      </MainLayout>
    );
  }

  return (
    <MainLayout>
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between py-6">
          <h1 className="text-3xl font-bold text-gray-900">User Details</h1>
          <Button
            onClick={() => navigate(-1)}
            variant="secondary"
          >
            Back
          </Button>
        </div>

        <main>
          {loading ? (
            <div className="text-center py-4">Loading...</div>
          ) : error ? (
            <div className="bg-white shadow sm:rounded-lg p-6">
              <div className="text-red-600 text-center">{error}</div>
            </div>
          ) : user ? (
            <div className="bg-white shadow overflow-hidden sm:rounded-lg">
              <div className="px-4 py-5 sm:px-6">
                <div>
                  <h3 className="text-lg leading-6 font-medium text-gray-900">
                    Account Information
                  </h3>
                  <p className="mt-1 max-w-2xl text-sm text-gray-500">
                    Personal details and account settings.
                  </p>
                </div>
              </div>

              <div className="border-t border-gray-200">
                <dl>
                  <div className="bg-gray-50 px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
                    <dt className="text-sm font-medium text-gray-500">Username</dt>
                    <dd className="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">
                      {user.username}
                    </dd>
                  </div>
                  <div className="bg-white px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
                    <dt className="text-sm font-medium text-gray-500">Email</dt>
                    <dd className="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">
                      {user.email}
                    </dd>
                  </div>
                  <div className="bg-gray-50 px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
                    <dt className="text-sm font-medium text-gray-500">Roles</dt>
                    <dd className="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">
                      {isEditing ? (
                        <RoleEditor
                          user={user}
                          onRolesUpdated={handleRolesUpdated}
                          onCancel={() => setIsEditing(false)}
                        />
                      ) : (
                        <div className="space-x-2">
                          {user.roles?.map((role) => (
                            <span
                              key={role}
                              className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800"
                            >
                              {role.replace('ROLE_', '')}
                            </span>
                          ))}
                        </div>
                      )}
                    </dd>
                  </div>
                </dl>
              </div>

              {/* Action Buttons */}
              {user.username !== currentUser.username && (
                <div className="px-4 py-5 sm:px-6 border-t border-gray-200">
                  <div className="flex justify-end space-x-3">
                    <Button
                      onClick={() => setIsEditing(!isEditing)}
                      variant="primary"
                      disabled={isEditing}
                    >
                      Edit Roles
                    </Button>
                    <Button
                      onClick={() => setShowDeleteModal(true)}
                      disabled={isDeleting}
                      loading={isDeleting}
                      variant="danger"
                    >
                      Delete User
                    </Button>
                  </div>
                </div>
              )}
            </div>
          ) : null}
        </main>
      </div>

      <Modal
        isOpen={showDeleteModal}
        onClose={() => setShowDeleteModal(false)}
        onConfirm={handleDelete}
        title="Delete User"
        message={`Are you sure you want to delete user "${user?.username}"? This action cannot be undone.`}
        confirmText="Delete"
        cancelText="Cancel"
      />
    </MainLayout>
  );
};

export default User; 