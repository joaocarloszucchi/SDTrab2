package interfaces;

import java.rmi.RemoteException;

public interface IUserChat extends java.rmi.Remote {
    // server calls it remotelly to send a message
    public void deliverMsg(String senderName, String msg) throws RemoteException;
}
