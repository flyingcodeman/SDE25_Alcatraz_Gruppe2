/*

package at.falb.fh.vtsys.Backup_NumberService.;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class NumberServiceImpl extends UnicastRemoteObject implements NumberService {
    private static int sharedNumber = 0;

    public NumberServiceImpl() throws RemoteException {
        // No need to initialize sharedNumber here
    }

    @Override
    public synchronized void updateNumber(int value) throws RemoteException {
        // Update the shared number in a synchronized manner to ensure thread safety
        sharedNumber += value;
    }

    @Override
    public synchronized int getNumber() throws RemoteException {
        // Return the current value of the shared number
        return sharedNumber;
    }
}

*/