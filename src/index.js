import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import reportWebVitals from './reportWebVitals';
import {
  createBrowserRouter,
  RouterProvider,
  Route,
  Navigate,
} from 'react-router-dom';
import AuthLayout from './pages/authLayout'; // Shared layout for Login/Signup
import Login from './pages/login';
import Signup from './pages/signup';
import ForgotPassword from './pages/forgotPassword';
import TwoFA from './pages/2fa';
import PersonalPwManager from './pages/personal_pw_manager';
import Home from './pages/home';
import App from './App';

// Define the router with correct paths
const router = createBrowserRouter([
  {
    path: "/",
    element: <Navigate to="/auth/login" replace />, // Redirect to login
  },
  {
    path: "/auth", // Authentication pages under `/auth`
    element: <AuthLayout />, // Shared layout for login/signup
    children: [
      { path: "login", element: <Login /> },
      { path: "signup", element: <Signup /> },
    ],
  },
  {
    path: "/forgotPassword",
    element: <ForgotPassword />,
  },
  {
    path: "/2fa",
    element: <TwoFA />,
  },
  {
    path: "/personal-pw-manager",
    element: <PersonalPwManager />,
  },
  {
    path: "/home",
    element: <Home />,
  },
  {
    path: "*",
    element: <h1>404 - Page Not Found</h1>, // Handles unknown routes
  },
]);

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <RouterProvider router={router} />
  </React.StrictMode>
);

reportWebVitals();
