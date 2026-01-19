import React from 'react';
import {BrowserRouter as Router, Routes, Route, Navigate} from 'react-router-dom';
import {AuthProvider, useAuth} from './useAuth.jsx';
import LoginPage from './LoginPage';
import NavBar from "./NavBar";
import HomePage from './HomePage';
import './App.css';

const ProtectedRoute = ({children}) => {
  const {ready: authReady, accessToken} = useAuth();

  if (!authReady) {
    return <div>Authenticating...</div>;
  }
  if (!accessToken) {
    return <Navigate to="/login" replace/>;
  }

  return children;
};

function App() {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          <Route path="/login" element={<LoginPage/>}/>

          <Route
            path="/"
            element={
              <ProtectedRoute>
                <NavBar/>
                <HomePage/>
              </ProtectedRoute>
            }
          />

          <Route path="*" element={<Navigate to="/" replace/>}/>
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
