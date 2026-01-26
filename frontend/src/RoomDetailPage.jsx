import React, {useEffect, useState} from 'react';
import {useParams, useNavigate} from 'react-router-dom';
import {useRoom} from "./useRoom.jsx";
import {useStomp} from "./useStomp.jsx"
import './RoomDetailPage.css';
import {useAxios} from "./useAxios.jsx";

const RoomDetailPage = () => {
  const {roomId} = useParams();
  const navigate = useNavigate();
  const {fetchMyRoom, enterRoom, leaveRoom, fetchRoom} = useRoom();
  const [room, setRoom] = useState(null);
  const [loading, setLoading] = useState(true);
  const stomp = useStomp();
  const api = useAxios();
  const [chatMessages, setChatMessages] = useState([]);
  const [chatInput, setChatInput] = useState('');

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

  const handleMemberChange = (memberMessage) => {
    setRoom((prevRoom) => {
      if (!prevRoom) {
        return prevRoom;
      }

      let members = [...(prevRoom.members || [])];
      if (memberMessage.type === 'ENTER') {
        if (!members.find(m => m.id === memberMessage.id)) {
          members.push({ id: memberMessage.id, name: memberMessage.name });
        }
      } else if (memberMessage.type === 'LEAVE') {
        members = members.filter(m => m.id !== memberMessage.id);
      }

      return { ...prevRoom, members: members };
    });
  };

  const handleReceiveChatMessage = (chatMessage) => {
    setChatMessages((prev) => [...prev, chatMessage]);
  };

  const handleSendChatMessage = () => {
    if (chatInput.trim() === '') {
      return;
    }

    stomp.publish(`/app/rooms/${roomId}/chat`, {
      message: chatInput
    });

    setChatInput('');
  };

  const handleKeyPressOnChatInput = (e) => {
    if (e.key === 'Enter') {
      handleSendChatMessage();
    }
  };

  useEffect(() => {
    stomp.subscribe(`/topic/rooms/${roomId}/members`, handleMemberChange);
    stomp.subscribe(`/topic/rooms/${roomId}/chat`, handleReceiveChatMessage);
    api.get('/auth/issue/web-socket-token')
      .then(response => response.data.token)
      .then(token => stomp.connect(token));
    return stomp.disconnect;
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
            {chatMessages.length === 0 ? (
              <p className="chat-placeholder">No messages yet.</p>
            ) : (
              chatMessages.map((msg, index) => (
                <div key={index} className="chat-message">
                  <strong>{room.members?.find(m => m.id === msg.userId)?.name || 'Unknown'}:</strong> {msg.message}
                </div>
              ))
            )}
          </div>
          <div className="chat-input-area">
            <input
              type="text"
              placeholder="Enter a message..."
              value={chatInput}
              onChange={(e) => setChatInput(e.target.value)}
              onKeyDown={handleKeyPressOnChatInput}
            />
            <button onClick={handleSendChatMessage}>Send</button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RoomDetailPage;
