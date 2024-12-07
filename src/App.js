import logo from './logo.svg';
import './App.css';
import Login from './Pages/login';
import Signup from './Pages/signup';
import { Outlet } from 'react-router-dom';
import ForgotPassword from './Pages/forgotPassword';

function App() {
  return (
    <div>
      <Signup />
    </div>
  );
}

export default App;
