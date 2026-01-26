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
    const dest = '/api/ws' + destination;
    if (subscriptions.current[dest]?.subscription) {
      subscriptions.current[dest]?.subscription.unsubscribe();
    }
    subscriptions.current[dest] = {
      callback: callback,
      subscription: !client.active ?
        null :
        client.subscribe(
          dest,
          (message) => {
            callback(JSON.parse(message.body))
          }
        )
    };
  };

  const unsubscribe = (destination) => {
    const dest = '/api/ws' + destination;
    subscriptions.current[dest]?.subscription.unsubscribe();
    delete subscriptions.current[dest];
  };

  const publish = (destination, message) => {
    client.publish({
      destination: '/api/ws' + destination,
      body: JSON.stringify(message)
    });
  };

  return {client, connect, disconnect, subscribe, publish};
};
