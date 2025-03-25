import { useState, useEffect } from "react";
import { useAuth } from "../../contexts/AuthProvider";
import { useNavigate } from "react-router-dom";
import MainLayout from "../layouts/MainLayout";
import userService from "../../services/user.service";

const Dashboard = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const isAdmin = user?.roles?.includes("ROLE_ADMIN");
  const [users, setUsers] = useState([]);
  const [pagination, setPagination] = useState({
    pageNumber: 0,
    pageSize: 10,
    totalPages: 0,
    totalElements: 0,
    lastPage: false
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchUsers = async (page = 0) => {
    try {
      setLoading(true);
      setError(null);
      console.log('Fetching users for page:', page);
      
      const result = await userService.getUsers(page, pagination.pageSize);
      console.log('Fetch users result:', result);

      if (result.success && result.data?.data) {
        const { content, pageNumber, pageSize, totalPages, totalElements, lastPage } = result.data.data;
        console.log('Setting users:', content);
        setUsers(content);
        setPagination({
          pageNumber,
          pageSize,
          totalPages,
          totalElements,
          lastPage
        });
      } else {
        console.error('Error in response:', result);
        setError(result.message || "Failed to fetch users");
      }
    } catch (err) {
      console.error('Error in fetchUsers:', err);
      setError(err.message || "Failed to fetch users");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (isAdmin) {
      fetchUsers();
    }
  }, [isAdmin]);

  const handlePageChange = (newPage) => {
    if (newPage >= 0 && newPage < pagination.totalPages) {
      fetchUsers(newPage);
    }
  };

  const handleUserClick = (username) => {
    navigate(`/users/${username}`);
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
        <header className="py-6">
          <h1 className="text-3xl font-bold text-gray-900">User Management</h1>
        </header>

        <main>
          {loading ? (
            <div className="text-center py-4">Loading...</div>
          ) : error ? (
            <div className="text-red-600 text-center py-4">{error}</div>
          ) : (
            <div className="bg-white shadow overflow-hidden sm:rounded-lg">
              <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-gray-200">
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Username
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Email
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Roles
                      </th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-gray-200">
                    {users.map((user, index) => (
                      <tr 
                        key={index}
                        onClick={() => handleUserClick(user.username)}
                        className="cursor-pointer hover:bg-gray-50"
                      >
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                          {user.username}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                          {user.email}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                          {user.roles.join(", ")}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              {/* Pagination */}
              <div className="bg-white px-4 py-3 flex items-center justify-between border-t border-gray-200 sm:px-6">
                <div className="flex-1 flex justify-between items-center">
                  <div>
                    <p className="text-sm text-gray-700">
                      Showing{" "}
                      <span className="font-medium">
                        {users.length > 0 ? pagination.pageNumber * pagination.pageSize + 1 : 0}
                      </span>{" "}
                      to{" "}
                      <span className="font-medium">
                        {Math.min(
                          (pagination.pageNumber + 1) * pagination.pageSize,
                          pagination.totalElements
                        )}
                      </span>{" "}
                      of{" "}
                      <span className="font-medium">{pagination.totalElements}</span>{" "}
                      results
                    </p>
                  </div>
                  <div>
                    <nav className="relative z-0 inline-flex rounded-md shadow-sm -space-x-px">
                      <button
                        onClick={() => handlePageChange(pagination.pageNumber - 1)}
                        disabled={pagination.pageNumber === 0}
                        className={`relative inline-flex items-center px-2 py-2 rounded-l-md border border-gray-300 bg-white text-sm font-medium ${
                          pagination.pageNumber === 0
                            ? "text-gray-300 cursor-not-allowed"
                            : "text-gray-500 hover:bg-gray-50"
                        }`}
                      >
                        Previous
                      </button>
                      <span className="relative inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-sm font-medium text-gray-700">
                        Page {pagination.pageNumber + 1} of {pagination.totalPages}
                      </span>
                      <button
                        onClick={() => handlePageChange(pagination.pageNumber + 1)}
                        disabled={pagination.lastPage}
                        className={`relative inline-flex items-center px-2 py-2 rounded-r-md border border-gray-300 bg-white text-sm font-medium ${
                          pagination.lastPage
                            ? "text-gray-300 cursor-not-allowed"
                            : "text-gray-500 hover:bg-gray-50"
                        }`}
                      >
                        Next
                      </button>
                    </nav>
                  </div>
                </div>
              </div>
            </div>
          )}
        </main>
      </div>
    </MainLayout>
  );
};

export default Dashboard;
