import './App.css';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { Login } from './pages/login';
import { Signup } from './pages/signup';
import { ForgotPassword } from './pages/forgotPassword';
import {PersonalPwManager} from './pages/personal_pw_manager';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path='signup' element={<Signup />} />
        <Route path='login' element={<Login />} />
        <Route path='forgotPassword' element={<ForgotPassword />} />
        <Route path='personal-pw-manager' element={<PersonalPwManager />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;