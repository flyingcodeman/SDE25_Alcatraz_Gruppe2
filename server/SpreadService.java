package server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.sun.xml.internal.ws.resources.SenderMessages;

import spread.AdvancedMessageListener;
import spread.SpreadConnection;
import spread.SpreadException;
import spread.SpreadGroup;
import spread.SpreadMessage;

public class SpreadService implements AdvancedMessageListener{
	SpreadConnection connection;
	SpreadGroup group;
	String serviceName;
	
	public SpreadService(String serviceName) throws UnknownHostException, SpreadException {
		this.serviceName = serviceName;
		this.connection = new SpreadConnection();
		this.connection.connect(InetAddress.getByName("localhost"), 0, serviceName, true, true);
		this.connection.add(this);
		
		group = new SpreadGroup();
		group.join(connection, "group");
	}

	public void sendMsg(CustomMsg msg) throws SpreadException{
		SpreadMessage message = new SpreadMessage();
        message.setObject(msg);
        message.setType((short) 1); // sync
        message.addGroup(group);
        message.setSafe();
        connection.multicast(message);
	}
	
	@Override
	public void membershipMessageReceived(SpreadMessage arg0) {
		try {
			CustomMsg customMsg = (CustomMsg) convertByteArrayToObject(arg0.getData());
			System.out.println("SPREAD output from" + this.serviceName + ": "+customMsg.getData());
		} catch (SpreadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	@Override
	public void regularMessageReceived(SpreadMessage arg0) {
		
		try {
			CustomMsg customMsg = (CustomMsg) convertByteArrayToObject(arg0.getData());
			System.out.println("SPREAD output from " + this.serviceName + ": "+customMsg.getData());
		} catch (SpreadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	private Object convertByteArrayToObject(byte[] data) throws SpreadException {
        ByteArrayInputStream var1 = new ByteArrayInputStream(data);

        ObjectInputStream var2;
        try {
            var2 = new ObjectInputStream(var1);
        } catch (IOException var8) {
            throw new SpreadException("ObjectInputStream(): " + var8);
        }

        Object var3;
        try {
            var3 = var2.readObject();
        } catch (ClassNotFoundException | IOException var6) {
            throw new SpreadException("readObject(): " + var6);
        }

        try {
            var2.close();
            var1.close();
            return var3;
        } catch (IOException var5) {
            throw new SpreadException("close/close(): " + var5);
        }
    }
}
