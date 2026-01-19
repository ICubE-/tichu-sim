import React, {useEffect, useState} from 'react';
import {useAxios} from './useAxios.jsx';
import {useAuth} from './useAuth.jsx';

const HomePage = () => {
  const api = useAxios();
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setLoading(true);
    fetchRooms().finally(() => setLoading(false));
  }, []);

  const fetchRooms = async () => {
    try {
      const response = await api.get('/rooms');
      setRooms(response.data);
    } catch (error) {
      console.error('Failed to get rooms:', error);
    }
  };

  return (
    <div style={{padding: '20px', width: '100%', maxWidth: '1000px'}}>
      <div style={{
        display: 'flex', 
        justifyContent: 'space-between', 
        alignItems: 'center', 
        marginTop: '30px',
        marginBottom: '10px'
      }}>
        <h2 style={{margin: 0}}>Rooms List</h2>
        <button onClick={fetchRooms} style={{cursor: 'pointer'}}>Refresh</button>
      </div>

      {loading && rooms.length === 0 ? (
        <p>Loading...</p>
      ) : (
        <table border="1" style={{width: '100%', borderCollapse: 'collapse', marginTop: '10px'}}>
          <thead>
          <tr style={{backgroundColor: '#f2f2f2'}}>
            <th style={{padding: '10px'}}>ID</th>
            <th style={{padding: '10px'}}>Name</th>
            <th style={{padding: '10px'}}>Status</th>
            <th style={{padding: '10px'}}>Players</th>
          </tr>
          </thead>
          <tbody>
          {rooms.length > 0 ? (
            rooms.map((room) => (
              <tr key={room.id} style={{textAlign: 'center'}}>
                <td style={{padding: '10px'}}>{room.id}</td>
                <td style={{padding: '10px'}}>{room.name || `방 ${room.id}`}</td>
                <td style={{padding: '10px'}}>{room.isGamePlaying ? '게임 진행 중' : '대기 중'}</td>
                <td style={{padding: '10px'}}>{room.memberCount} / 4</td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="4" style={{padding: '20px', textAlign: 'center'}}>
                현재 생성된 방이 없습니다.
              </td>
            </tr>
          )}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default HomePage;
