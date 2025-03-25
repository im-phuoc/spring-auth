import { Toaster } from "react-hot-toast";
import "./App.css";
import AppRouter from "./components/routing/AppRouter";
import { AuthProvider } from "./contexts/AuthProvider";

function App() {
  return (
    <AuthProvider>
      <Toaster position="top-right" />
      <AppRouter />
    </AuthProvider>
  );
}

export default App;
