package at.falb.fh.vtsys;

public interface HelloInterface extends java.rmi.Remote {
    String sayHello() throws java.rmi.RemoteException;
}
