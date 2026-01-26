import React, {useEffect, useState} from 'react';
import {useParams, useNavigate} from 'react-router-dom';
import {useRoom} from "./useRoom.jsx";
import './RoomDetailPage.css';

const RoomDetailPage = () => {
  const {roomId} = useParams();
  const navigate = useNavigate();
  const {fetchMyRoom, enterRoom, leaveRoom, fetchRoom} = useRoom();
  const [room, setRoom] = useState(null);
  const [loading, setLoading] = useState(true);

  const handleLeave = async () => {
    try {
      await leaveRoom(roomId);
      navigate('/');
    } catch (error) {
      console.error('Failed to leave room:', error);
    }
  };

  useEffect(() => {
    const init = async () => {
      setLoading(true);
      let myRoom;
      try {
        myRoom = await fetchMyRoom();
      } catch (error) {
        console.error('Failed to fetch my room:', error);
        return;
      }
      if (myRoom === null) {
        try {
          await enterRoom(roomId);
        } catch (error) {
          console.error('Failed to enter room:', error);
          navigate('/');
          return;
        }
      } else if (String(myRoom.id) !== String(roomId)) {
        alert('You are already in another room. Please leave the current room first.');
        navigate(`/${myRoom.id}`);
        return;
      }

      try {
        setRoom(await fetchRoom(roomId));
        setLoading(false);
      } catch (error) {
        console.error('Failed to fetch room detail:', error);
      }
    };

    init().then();
  }, [roomId]);

  if (loading || room === null) {
    return <div style={{padding: '20px'}}>Loading...</div>;
  }

  return (
    <div className="room-detail-container">
      <div className="room-detail-header">
        <h2>[{room.id}] {room.name}</h2>
        <button onClick={handleLeave} className="leave-button">Leave</button>
      </div>

      <div className="room-content">
        <div className="room-info-section">
          <div className="info-card">
            <h3>Players ({room.members?.length || 0} / 4)</h3>
            <ul className="member-list">
              {room.members?.map((member) => (
                <li key={member.id} className="member-item">
                  {member.name}
                </li>
              ))}
            </ul>
          </div>

          <div className="info-card">
            <h3>Rules</h3>
            <div className="rule-box">
              <p>TODO</p>
              {<pre>{JSON.stringify(room.gameRule, null, 2)}</pre>}
            </div>
          </div>
        </div>

        <div className="chat-section">
          <div className="chat-header">
            <strong>Chat</strong>
          </div>
          <div className="chat-messages">
            <p className="chat-placeholder">
              TODO
            </p>
          </div>
          <div className="chat-input-area">
            <input
              type="text"
              placeholder="Enter a message..."
              disabled
            />
            <button disabled>Send</button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RoomDetailPage;
