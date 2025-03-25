import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "../../contexts/AuthProvider";

const AdminRoute = () => {
  const { user, loading } = useAuth();

  if (loading) {
    return (
      <div className="flex justify-center items-center h-screen">
        Loading...
      </div>
    );
  }

  // Check if user has ROLE_ADMIN role
  const isAdmin = user?.roles?.includes("ROLE_ADMIN");

  if (!isAdmin) {
    return <Navigate to="/profile" replace />;
  }

  return <Outlet />;
};

export default AdminRoute; 