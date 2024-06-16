import java.util.HashMap;
import java.util.Map;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RoomChat extends UnicastRemoteObject implements IRoomChat {
    private String roomName;
    private Map<String, IUserChat> userList;

    public RoomChat(String roomName) throws RemoteException {
        this.roomName = roomName;
        this.userList = new HashMap<>();
    }

    @Override
    public synchronized void sendMsg(String usrName, String msg) throws RemoteException{
        for (IUserChat user : userList.values()) {
            user.deliverMsg(usrName, msg);
        }
    }

    @Override
    public synchronized void joinRoom(String usrName, IUserChat user) throws RemoteException{
        userList.put(usrName, user);
        sendMsg("SYSTEM ", usrName + " has joined the room.");
    }

    @Override
    public synchronized void leaveRoom(String usrName) throws RemoteException{
        if (userList.remove(usrName) != null) {
            sendMsg("SYSTEM ", usrName + " has left the room.");
        }
    }

    @Override
    public synchronized void closeRoom() throws RemoteException{
        sendMsg("SYSTEM ", " Room closed by server.");
        userList.clear();
    }

    @Override
    public String getRoomName(){
        return roomName;
    }
}
