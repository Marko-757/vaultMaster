import logo from './logo.svg';
import './App.css';
import Login from './pages/login';
import Signup from './pages/signup';
import { Outlet } from 'react-router-dom';
import ForgotPassword from './pages/forgotPassword';
import PersonalPWManager from './pages/personal_pw_manager';

function App() {
  return (
    <div>
      <PersonalPWManager />
    </div>
  );
}

export default App;