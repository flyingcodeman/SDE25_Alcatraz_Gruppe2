package client;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class ClientApplication {


    public static void main(String[] args) throws RemoteException, AlreadyBoundException {
        PlayerServer playerServer = new PlayerServerImpl();
        initClientRMI(args[0], playerServer);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Enter a number (1: register, 2: leave, 3: startGame, 4: exit):");
            int command = scanner.nextInt();

            switch (command) {
                case 1:
                    PlayerServer otherPlayer = getRMIPlayer(1);
                    otherPlayer.helloWorld();
                    break;
                case 2:
                    leave();
                    break;
                case 3:
                    startGame();
                    break;
                case 4:
                    System.out.println("Exiting...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid number. Please try again.");
            }
        }
    }


    private static void register() {
    }

    private static void startGame() {
    }

    private static void leave() {
    }

    private static void initClientRMI(String myId, PlayerServer playerServer) throws RemoteException, AlreadyBoundException {
        Registry registry;
        try {
            registry = LocateRegistry.createRegistry(1099);
        } catch (RemoteException ex) {
            try {
                registry = LocateRegistry.getRegistry(1099);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("myID: "+myId);
        registry.rebind("player" + myId, playerServer);
        System.out.println("player " + myId + " successfully started.\n");
    }

    private static PlayerServer getRMIPlayer(int playerId) throws RemoteException {
        // Check if clients from server list are reachable
        PlayerServer playerOp = null;
        try {
            playerOp = (PlayerServer) Naming.lookup("rmi://localhost:1099/player" + playerId);
            System.out.println("Player_" + playerId + " up and running");

        } catch (NotBoundException e) {
            System.out.println("Player " + playerId + " not reachable");
        } catch (MalformedURLException | RemoteException e) {
            //e.printStackTrace();
        }
        return playerOp;
    }




}
