package server;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

//import com.sun.xml.internal.ws.resources.SenderMessages;

import at.falb.games.alcatraz.api.Player;
import interfaces.Constants;
import server.spread.messagetypes.DeRegisterMessage;
import server.spread.messagetypes.RegisterMessage;
import server.spread.messagetypes.StartGameMessage;
import server.spread.messagetypes.UpdateLobbyMessage;
import spread.AdvancedMessageListener;
import spread.SpreadConnection;
import spread.SpreadException;
import spread.SpreadGroup;
import spread.SpreadMessage;

public class SpreadService implements Constants, AdvancedMessageListener, Serializable{
	SpreadConnection connection;
	SpreadGroup group;
	String serviceName;
	String myPrivateId;
	
	public SpreadService(String serviceName) throws UnknownHostException, SpreadException {
		this.serviceName = serviceName;
		this.connection = new SpreadConnection();
		this.connection.connect(InetAddress.getByName(SPREAD_DEMON), 0, serviceName, true, true);
		this.connection.add(this);
		myPrivateId = this.connection.getPrivateGroup().toString();
		
		group = new SpreadGroup();
		group.join(connection, "group");
	}

	public void registerPlayer(String playerName, String playerIP) throws SpreadException{
		SpreadMessage message = new SpreadMessage();
		message.setObject(new RegisterMessage(playerName, playerIP));
		message.setType((short) 1); // sync
		message.addGroup(group);
		message.setSafe();
		message.setSelfDiscard(true);
		connection.multicast(message);
	}

	public void deRegisterPlayer(AlcatrazPlayer player) throws SpreadException{
		SpreadMessage message = new SpreadMessage();
		message.setObject(new DeRegisterMessage(player));
		message.setType((short) 1); // sync
		message.addGroup(group);
		message.setSafe();
		message.setSelfDiscard(true);
		connection.multicast(message);
	}

	public void sendLobby(Lobby<AlcatrazPlayer> playerList) throws SpreadException{
		SpreadMessage message = new SpreadMessage();
		System.out.println("Log in sendPlayerList: " + playerList);
        message.setObject(new UpdateLobbyMessage(playerList));
        message.setType((short) 1); // sync
        message.addGroup(group);
        message.setSafe();
		message.setSelfDiscard(true);
        connection.multicast(message);
	}

	public void startGame() throws  SpreadException{
		SpreadMessage message = new SpreadMessage();
		System.out.println("Log in sendPlayerList: " + " game started.");
		message.setObject(new StartGameMessage(MainServer.players));
		message.setType((short) 1); // sync
		message.addGroup(group);
		message.setSafe();
		message.setSelfDiscard(true);
		connection.multicast(message);
	}
	
	@Override
	public void membershipMessageReceived(SpreadMessage msg) {
		if(msg.getMembershipInfo().isCausedByJoin()){
			System.out.println(msg.getMembershipInfo().getJoined());

				if(msg.getMembershipInfo().getJoined().toString().equals(myPrivateId)){
					System.out.println("this is my own message");
					return;
				}

			try {
				sendLobby(MainServer.players);
			} catch (SpreadException e) {
				throw new RuntimeException(e);
			}
		}
		//CustomMsg customMsg = (CustomMsg) convertByteArrayToObject(arg0.getData());
		System.out.println("SPREAD output from membershiptMessage" + this.serviceName);
	}

	@Override
	public void regularMessageReceived(SpreadMessage msg) {

		if(deserializeList(msg.getData()) != null){
				Object messageData = deserializeList(msg.getData());
				if(messageData instanceof RegisterMessage){
					String newPlayerName = ( (RegisterMessage) messageData).getPlayerName();
					String newPlayerIP = ( (RegisterMessage) messageData).getPlayerIP();
					MainServer.registerPlayer( newPlayerName, newPlayerIP);
				}else if(messageData instanceof  DeRegisterMessage){
					// deregister
					AlcatrazPlayer playToRemove = ( (DeRegisterMessage) messageData).getPlayerToRemove();
					MainServer.deRegisterPlayer( playToRemove);
				}else if(messageData instanceof StartGameMessage){
					//startGame
					MainServer.players = ( (StartGameMessage) messageData).getFinalLobby();
					List<AlcatrazPlayer> gameStartResponse= MainServer.setGameStart();
					if(gameStartResponse.isEmpty()){
						System.out.println("Game was not started!");
					}else{
						System.out.println("Started Game!");
					}
				}else if(messageData instanceof UpdateLobbyMessage){

					MainServer.players = ((UpdateLobbyMessage) messageData).getUpdateLobby();
					System.out.println("SPREAD output from UPDATELOBBY " + this.serviceName + " "  + MainServer.players);
				}

			System.out.println("SPREAD output from " + this.serviceName + "regularMsg" + MainServer.players);
		}else {
			System.out.println("SPREAD output from " + this.serviceName + "regularMsg" + "ERROR while receiving Spread MSG!!!");
		}
	}


	@SuppressWarnings("unchecked")
	public static Object deserializeList(byte[] data) {
		try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
			 ObjectInputStream ois = new ObjectInputStream(bis)) {

			// Deserialize the byte array to a list of objects
			return (Object) ois.readObject();

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			// Handle the exception as needed
			return null;
		}
	}
}
