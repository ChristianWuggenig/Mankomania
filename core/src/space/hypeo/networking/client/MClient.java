package space.hypeo.networking.client;

import com.esotericsoftware.kryo.Kryo;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;
import java.util.HashMap;

import space.hypeo.networking.IClientConnector;
import space.hypeo.networking.IPlayerConnector;
import space.hypeo.networking.PlayerInfo;
import space.hypeo.networking.host.MHost;
import space.hypeo.networking.packages.PingRequest;
import space.hypeo.networking.packages.PingResponse;

public class MClient implements IPlayerConnector, IClientConnector {

    private com.esotericsoftware.kryonet.Client client;

    private final int TIMEOUT_MS = 5000;

    private PlayerInfo hostInfo = null;

    private long lastPingRequest = 0;

    private class ClientListener extends Listener {

        @Override
        public void connected(Connection connection) {
            super.connected(connection);

            hostInfo = new PlayerInfo();

            hostInfo.address = connection.getRemoteAddressTCP().getAddress().toString();
            hostInfo.hostName = connection.getRemoteAddressTCP().getHostName();
            hostInfo.port = connection.getRemoteAddressTCP().getPort();
        }

        @Override
        public void disconnected(Connection connection) {
            super.disconnected(connection);
            hostInfo = null;
        }

        @Override
        public void received(Connection connection, Object object) {
            super.received(connection, object);

            if( object instanceof PingResponse) {
                PingResponse pingResponse = (PingResponse) object;
                System.out.println("Ping time [ms] = " + (lastPingRequest - pingResponse.getTime()));
            }
        }
    }

    @Override
    public boolean joinGame(String playerID) {
        return false;
    }

    @Override
    public void startClient() {
        client = new Client();
        client.start();

        try {
            // TODO: connect to which host-address?
            client.connect(TIMEOUT_MS, "127.0.0.1", MHost.getPortNo());
        } catch (IOException e) {
            e.printStackTrace();
        }

        client.addListener(new ClientListener());

        Kryo kryo = client.getKryo();
        kryo.register(PingRequest.class);
        kryo.register(PingResponse.class);

        // TODO: create a lobby: see each player in players + who is host

        PingRequest pingRequest = new PingRequest();
        lastPingRequest = pingRequest.getTime();

        client.sendTCP(pingRequest);

        while( true ) {
            // wait for response
        }
    }

    @Override
    public void changeBalance(String playerID, int amount) {

    }

    @Override
    public void movePlayer(String playerID, int position) {

    }

    @Override
    public void endTurn() {

    }

    @Override
    public int getPlayerBalance(String playerID) {
        return 0;
    }

    @Override
    public int getPlayerPosition(String playerID) {
        return 0;
    }

    @Override
    public String getCurrentPlayerID() {
        return null;
    }

    @Override
    public HashMap<String, PlayerInfo> registeredPlayers() {
        return null;
    }
}