package server;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

//import com.sun.xml.internal.ws.resources.SenderMessages;

import at.falb.games.alcatraz.api.Player;
import server.spread.messagetypes.DeRegisterMessage;
import server.spread.messagetypes.RegisterMessage;
import server.spread.messagetypes.StartGameMessage;
import server.spread.messagetypes.UpdateLobbyMessage;
import spread.AdvancedMessageListener;
import spread.SpreadConnection;
import spread.SpreadException;
import spread.SpreadGroup;
import spread.SpreadMessage;

public class SpreadService implements AdvancedMessageListener, Serializable{
	SpreadConnection connection;
	SpreadGroup group;
	String serviceName;
	String myPrivateId;
	
	public SpreadService(String serviceName) throws UnknownHostException, SpreadException {
		this.serviceName = serviceName;
		this.connection = new SpreadConnection();
		this.connection.connect(InetAddress.getByName("localhost"), 0, serviceName, true, true);
		this.connection.add(this);
		myPrivateId = this.connection.getPrivateGroup().toString();
		
		group = new SpreadGroup();
		group.join(connection, "group");
	}

	public void registerPlayer(String playerName) throws SpreadException{
		SpreadMessage message = new SpreadMessage();
		message.setObject(new RegisterMessage(playerName));
		message.setType((short) 1); // sync
		message.addGroup(group);
		message.setSafe();
		message.setSelfDiscard(true);
		connection.multicast(message);
	}

	public void deRegisterPlayer(Player player) throws SpreadException{
		SpreadMessage message = new SpreadMessage();
		message.setObject(new DeRegisterMessage(player));
		message.setType((short) 1); // sync
		message.addGroup(group);
		message.setSafe();
		message.setSelfDiscard(true);
		connection.multicast(message);
	}

	public void sendLobby(Lobby<Player> playerList) throws SpreadException{
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
					MainServer.registerPlayer( newPlayerName);
				}else if(messageData instanceof  DeRegisterMessage){
					// deregister
					Player playToRemove = ( (DeRegisterMessage) messageData).getPlayerToRemove();
					MainServer.deRegisterPlayer( playToRemove);
				}else if(messageData instanceof StartGameMessage){
					//startGame
					MainServer.gameStarted = true;
					MainServer.players = ( (StartGameMessage) messageData).getFinalLobby();
				}else if(messageData instanceof UpdateLobbyMessage){

					MainServer.players = ((UpdateLobbyMessage) messageData).getUpdateLobby();
					System.out.println("SPREAD output from UPDATELOBBY " + this.serviceName + " "  + MainServer.players);
				}

			System.out.println("SPREAD output from " + this.serviceName + "regularMsg" + MainServer.players);
		}else {
			System.out.println("SPREAD output from " + this.serviceName + "regularMsg" + "ERROR while receiving Spread MSG!!!");
		}
	}

	public static byte[] serializeList(List<? extends Serializable> objectList) {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
			 ObjectOutputStream oos = new ObjectOutputStream(bos)) {

			// Serialize the list of objects
			oos.writeObject(objectList);

			// Return the byte array
			return bos.toByteArray();

		} catch (IOException e) {
			e.printStackTrace();
			// Handle the exception as needed
			return null;
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
