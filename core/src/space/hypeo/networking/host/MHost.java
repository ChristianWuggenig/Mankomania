package space.hypeo.networking.host;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import space.hypeo.networking.IHostConnector;
import space.hypeo.networking.IPlayerConnector;
import space.hypeo.networking.PlayerInfo;
import space.hypeo.networking.packages.PingRequest;
import space.hypeo.networking.packages.PingResponse;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class MHost implements IPlayerConnector, IHostConnector {

    private com.esotericsoftware.kryonet.Server server;
    private HashMap<String, PlayerInfo> players;

    private static final int PORT_NO = 25454;
    private final int MAX_PLAYER = 5;

    private class ServerListener extends Listener {

        /**
         * If client has connected
         * @param connection
         */
        @Override
        public void connected(Connection connection) {
            super.connected(connection);

            if( players.size() >= MAX_PLAYER ) {
                // game is full
                // send message to client: you can not join game, game is full
                return;
            }

            PlayerInfo newPlayer = new PlayerInfo(connection);

            players.put(newPlayer.getAddress(), newPlayer);
        }

        /**
         * If client has disconnected
         * @param connection
         */
        @Override
        public void disconnected(Connection connection) {
            super.disconnected(connection);

            PlayerInfo leavingPlayer = new PlayerInfo(connection);

            players.remove(leavingPlayer);
        }

        /**
         * If has received a package from client
         * @param connection
         * @param object
         */
        @Override
        public void received(Connection connection, Object object) {
            super.received(connection, object);

            if( object instanceof PingRequest ) {
                PingRequest pingRequest = (PingRequest)object;
                PingResponse pingResponse = new PingResponse(pingRequest.getTime());
                connection.sendTCP(pingResponse);
            }
        }
    }

    public static int getPortNo() {
        return PORT_NO;
    }

    @Override
    public void advertiseGame() {

    }

    @Override
    public boolean startGame() {
        return false;
    }

    @Override
    public void startServer() {
        server = new Server();
        server.start();

        try {
            server.bind(PORT_NO);
        } catch (IOException e) {
            e.printStackTrace();
        }

        server.addListener(new ServerListener());

        Kryo kryo = server.getKryo();
        kryo.register(PingRequest.class);
        kryo.register(PingResponse.class);

        // TODO: create a lobby: see each player in players
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
        return players;
    }
}
