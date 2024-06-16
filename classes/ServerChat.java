package classes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import interfaces.IRoomChat;
import interfaces.IServerChat;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServerChat extends UnicastRemoteObject implements IServerChat {
    private ArrayList<String> roomList;
    private Map<String, IRoomChat> rooms;

    public ServerChat() throws RemoteException{
        this.roomList = new ArrayList<>();
        this.rooms = new HashMap<>();
    }

    @Override
    public ArrayList<String> getRooms() throws RemoteException{
        System.out.println("Returning room list");
        return this.roomList;
    }

    @Override
    public synchronized void createRoom(String roomName) throws RemoteException{
        System.out.println("Creating room " + roomName);
        if (!roomList.contains(roomName)) {
            IRoomChat newRoom;
            try {
                newRoom = new RoomChat(roomName);
                rooms.put(roomName, newRoom);
                roomList.add(roomName);
                Naming.rebind("//localhost:2020/" + roomName, newRoom);
                System.out.println("Added room " + roomName);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (java.net.MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized IRoomChat getRoom(String roomName) throws RemoteException {
        System.out.println("Returning room " + roomName);
        return rooms.get(roomName);
    }

    public synchronized void closeRoom(String roomName) throws RemoteException {
        System.out.println("Closing room " + roomName);
        IRoomChat room = rooms.get(roomName);
        if (room != null) {
            room.closeRoom();
            rooms.remove(roomName);
            roomList.remove(roomName);
            try {
                Naming.unbind("//localhost:2020/" + roomName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Closed room " + roomName);
        }
    }
}
