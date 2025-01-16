import logo from './logo.svg';
import './App.css';
import Login from './Pages/login';
import Signup from './Pages/signup';
import { Outlet } from 'react-router-dom';
import ForgotPassword from './Pages/forgotPassword';
import PersonalPWManager from './Pages/personal_pw_manager';

function App() {
  return (
    <div>
      <PersonalPWManager />
    </div>
  );
}

export default App;
