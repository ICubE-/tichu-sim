import React, {useState} from 'react';
import {useAuth} from './useAuth.jsx';

const LoginPage = () => {
  const {login} = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    const response = await fetch('/api/auth/login', {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify({email, password}),
    });

    if (response.ok) {
      const data = await response.json();
      login(data.token);
    } else {
      alert('Login failed');
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input type="email" onChange={(e) => setEmail(e.target.value)} placeholder="Email"/>
      <input type="password" onChange={(e) => setPassword(e.target.value)} placeholder="Password"/>
      <button type="submit">로그인</button>
    </form>
  );
};

export default LoginPage;
