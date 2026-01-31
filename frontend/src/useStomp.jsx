import {Client} from "@stomp/stompjs";
import {useMemo, useRef} from "react";

export const useStomp = () => {
  const subscriptions = useRef({});

  const client = useMemo(() => new Client({
    brokerURL: `${window.location.origin.replace('http', 'ws')}/api/ws`,
    reconnectDelay: 1000,
    heartbeatIncoming: 0,
    heartbeatOutgoing: 0,
    debug: (str) => console.debug(str),
    onStompError: (frame) => {
      console.error('Broker reported error: ' + frame.headers['message']);
    },
  }), []);

  client.onConnect = (frame) => {
    for (const [dest, sub] of Object.entries(subscriptions.current)) {
      sub.subscription = client.subscribe(
        dest,
        (message) => {
          sub.callback(JSON.parse(message.body))
        }
      )
    }
  }

  const connect = (token) => {
    client.connectHeaders.Authorization = `Bearer ${token}`;
    if (!client.active) {
      client.activate();
    }
  }

  const disconnect = () => {
    client.deactivate().then();
  };

  const subscribe = (destination, callback) => {
    if (subscriptions.current[destination]?.subscription) {
      subscriptions.current[destination]?.subscription.unsubscribe();
    }
    subscriptions.current[destination] = {
      callback: callback,
      subscription: !client.active ?
        null :
        client.subscribe(
          destination,
          (message) => {
            callback(JSON.parse(message.body))
          }
        )
    };
  };

  const unsubscribe = (destination) => {
    subscriptions.current[destination]?.subscription.unsubscribe();
    delete subscriptions.current[destination];
  };

  const publish = (destination, message) => {
    client.publish({
      destination: destination,
      body: JSON.stringify(message)
    });
  };

  return {client, connect, disconnect, subscribe, publish};
};
