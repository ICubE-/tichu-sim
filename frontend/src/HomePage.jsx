import React from 'react';
import {useAxios} from './useAxios.jsx';

const HomePage = () => {
  const api = useAxios();

  const handleGetData = async () => {
    const response = await api.get('/auth/me');
    alert(`Hello, ${response.data}!`);
  };

  return (
    <div>
      <button onClick={handleGetData}>Say hello</button>
    </div>
  );
};

export default HomePage;
