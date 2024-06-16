package classes;
import interfaces.IUserChat;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class UserChat extends UnicastRemoteObject implements IUserChat{
    private String name;

    public UserChat(String name) throws RemoteException{
        this.name = name;
    }
    
    @Override
    public void deliverMsg(String senderName, String msg) throws RemoteException{
        System.out.println(senderName + ": " + msg);
    }
 
    public String getUsrName(){
        return this.name;
    }
}
