import React, {useEffect, useState, useCallback} from 'react';
import {useParams, useNavigate} from 'react-router-dom';
import {useStomp} from "./useStomp.jsx";
import {useAuth} from "./useAuth.jsx";
import {useAxios} from "./useAxios.jsx";
import './TichuPage.css';

const TichuPage = (roomId, stomp, chatMessages) => {
  const {user} = useAuth();
  const api = useAxios();

  const [gameState, setGameState] = useState({
    rule: null,
    players: [], // {id, name, index, cardCount, tichuDeclaration, exitOrder, passed}
    scoresHistory: [],
    hand: [], // My cards
    roundStatus: null,
    wish: null,
    phaseStatus: null,
    turn: null,
    lastTrick: null, // {playerIndex, type, cards}
  });

  const [selectedCards, setSelectedCards] = useState([]);

  const handleTichuMessage = useCallback((message) => {
    console.log('Tichu Message:', message);
    switch (message.type) {
      case 'START':
        setGameState(prev => ({
          ...prev,
          players: message.players.map((p, i) => ({
            ...p,
            index: i,
            cardCount: 0,
            tichuDeclaration: null,
            exitOrder: 0,
            passed: false
          })),
        }));
        break;
      case 'GET':
        setGameState(prev => ({
          ...prev,
          rule: message.rule,
          players: message.players.map((p, i) => ({
            ...p,
            index: i,
            cardCount: 0,
            tichuDeclaration: null,
            exitOrder: 0,
            passed: false
          })),
          scoresHistory: message.scoresHistory,
          hand: message.myHand,
          roundStatus: message.roundStatus,
          wish: message.wish,
          phaseStatus: message.phaseStatus,
          turn: message.turn,
          lastTrick: message.lastTrick,
        }));
        break;
      case 'INIT_FIRST_DRAWS':
        setGameState(prev => ({
          ...prev,
          hand: message.cards,
          players: prev.players.map(p => p.id === user.id ? {...p, cardCount: message.cards.length} : p)
        }));
        break;
      case 'ADD_SECOND_DRAWS':
        setGameState(prev => ({
          ...prev,
          hand: [...prev.hand, ...message.cards],
          players: prev.players.map(p => p.id === user.id ? {
            ...p,
            cardCount: prev.hand.length + message.cards.length
          } : p)
        }));
        break;
      case 'PHASE_START':
        setGameState(prev => ({
          ...prev,
          turnIndex: message.firstPlayerIndex,
          phase: 'PLAYING'
        }));
        break;
      case 'PLAY_TRICK':
      case 'PLAY_BOMB':
        setGameState(prev => {
          const playerIndex = prev.players.findIndex(p => p.id === message.playTrick?.playerId || p.id === message.playBomb?.playerId);
          const newPlayers = [...prev.players];
          if (playerIndex !== -1) {
            newPlayers[playerIndex] = {
              ...newPlayers[playerIndex],
              cardCount: newPlayers[playerIndex].cardCount - (message.playTrick?.trick.cards.length || message.playBomb?.bomb.cards.length || 0)
            };
          }
          return {
            ...prev,
            trick: message.playTrick || message.playBomb,
            turnIndex: (prev.turnIndex + 1) % 4,
            players: newPlayers
          };
        });
        break;
      case 'PASS':
        setGameState(prev => ({
          ...prev,
          turnIndex: (prev.turnIndex + 1) % 4,
          players: prev.players.map(p => p.id === message.playerId ? {...p, passed: true} : p)
        }));
        break;
      default:
        break;
    }
  }, [user.id]);

  useEffect(() => {
    if (!user) {
      return;
    }

    const destination = `/user/${user.id}/queue/game/tichu`;
    stomp.subscribe(destination, handleTichuMessage);
  }, [roomId, handleTichuMessage, user]);

  const toggleCardSelection = (card) => {
    setSelectedCards(prev => {
      if (prev.find(c => c.id === card.id)) {
        return prev.filter(c => c.id !== card.id);
      } else {
        return [...prev, card];
      }
    });
  };

  const handlePlayTrick = () => {
    if (selectedCards.length === 0) return;
    stomp.publish(`/app/rooms/${roomId}/game/tichu/play-trick`, {
      cards: selectedCards.map(c => c.id)
    });
    setSelectedCards([]);
  };

  const handlePass = () => {
    stomp.publish(`/app/rooms/${roomId}/game/tichu/pass`, {});
  };

  const myIndex = gameState.players.findIndex(p => p.id === user.id);
  const getPlayerAt = (offset) => {
    if (myIndex === -1) return null;
    return gameState.players[(myIndex + offset) % 4];
  };

  const renderCard = (card, isSelectable = true) => (
    <div
      key={card.id}
      className={`card ${selectedCards.find(c => c.id === card.id) ? 'selected' : ''} suit-${card.suit}`}
      onClick={() => isSelectable && toggleCardSelection(card)}
    >
      <div className="card-top">
        <span>{card.rank}</span>
        <span>{card.suit}</span>
      </div>
      <div className="card-bottom">
        <span>{card.rank}</span>
        <span>{card.suit}</span>
      </div>
    </div>
  );

  const renderPlayer = (p, position) => {
    if (!p) return null;
    const isMyTurn = gameState.turnIndex === p.index;
    return (
      <div className={`player-section player-${position} ${isMyTurn ? 'active-turn' : ''}`}>
        <div className="player-info">
          <div className="player-name">{p.name}</div>
          <div className="card-count">{p.cardCount} Cards</div>
          {p.declaration && <div className="declaration">{p.declaration}</div>}
          {p.passed && <div className="status-pass">PASS</div>}
        </div>
        {position !== 'bottom' && (
          <div className="hand">
            {Array.from({length: p.cardCount}).map((_, i) => (
              <div key={i} className="card back"/>
            ))}
          </div>
        )}
      </div>
    );
  };

  return (
    <div className="tichu-game-container">
      <div className="game-board">
        {/* Top Player (Partner) */}
        {renderPlayer(getPlayerAt(2), 'top')}

        {/* Left Player */}
        {renderPlayer(getPlayerAt(1), 'left')}

        {/* Right Player */}
        {renderPlayer(getPlayerAt(3), 'right')}

        {/* Trick Area */}
        <div className="trick-area">
          {gameState.trick ? (
            <>
              <div className="played-by">Played
                by: {gameState.players.find(p => p.id === (gameState.trick.playerId))?.name}</div>
              <div className="played-cards">
                {(gameState.trick.trick?.cards || gameState.trick.bomb?.cards || []).map(c => renderCard(c, false))}
              </div>
            </>
          ) : (
            <div className="trick-placeholder">Trick Area</div>
          )}
        </div>

        {/* Bottom Player (Me) */}
        <div className="player-section player-bottom">
          <div className="player-info">
            <div className="player-name">{user.name} (ME)</div>
            <div className="card-count">{gameState.hand.length} Cards</div>
          </div>
          <div className="hand">
            {gameState.hand.map(card => renderCard(card))}
          </div>
        </div>
      </div>

      <div className="controls">
        <button
          className="btn-game btn-trick"
          onClick={handlePlayTrick}
          disabled={selectedCards.length === 0 || gameState.turnIndex !== myIndex}
        >
          Play Trick
        </button>
        <button
          className="btn-game btn-pass"
          onClick={handlePass}
          disabled={gameState.turnIndex !== myIndex}
        >
          Pass
        </button>
      </div>
    </div>
  );
};

export default TichuPage;
