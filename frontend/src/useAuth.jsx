import React, {createContext, useState, useContext, useEffect} from 'react';

const AuthContext = createContext(null);

export const AuthProvider = ({children}) => {
  const [ready, setReady] = useState(false);
  const [accessToken, setAccessToken] = useState(null);

  const login = (token) => {
    setAccessToken(token);
    setReady(true);
  };

  const logout = () => {
    setAccessToken(null);
    setReady(true);
  };

  const refresh = async () => {
    try {
      const response = await fetch('/api/auth/refresh', {
        method: 'POST',
        credentials: 'include',
      });

      if (response.ok) {
        const data = await response.json();
        setAccessToken(data.accessToken);
      } else {
        logout();
      }
    } catch (error) {
      console.log("Failed token refreshing:", error);
      logout();
    } finally {
      setReady(true);
    }
  };

  // Refresh tokens when window is refreshed
  useEffect(() => {
    refresh().then();
  }, []);

  return (
    <AuthContext.Provider value={{ready, accessToken, login, logout, refresh}}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
