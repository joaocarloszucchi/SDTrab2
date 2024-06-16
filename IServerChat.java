import java.rmi.RemoteException;
import java.util.ArrayList;

public interface IServerChat extends java.rmi.Remote {
    // user calls it remotelly to and it returns all the avaliable rooms
    public ArrayList<String> getRooms() throws RemoteException;

    // user calls it remotelly to create a new room
    public void createRoom(String roomName) throws RemoteException;
}