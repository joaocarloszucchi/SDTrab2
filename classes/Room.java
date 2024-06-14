package classes;

import java.util.HashMap;
import java.util.Map;

import interfaces.IRoomChat;
import interfaces.IUserChat;

public class Room implements IRoomChat {
    private Map<String, IUserChat> userList;

    // Constructor to initialize the userList as an empty HashMap
    public Room() {
        this.userList = new HashMap<String, IUserChat>();
    }

    @Override
    public void sendMsg(String usrName, String msg) {
        // Implementation needed
    }

    @Override
    public void joinRoom(String usrName, IUserChat user) {
        // Implementation needed
    }

    @Override
    public void leaveRoom(String usrName) {
        // Implementation needed
    }

    @Override
    public void closeRoom() {
        // Implementation needed
    }

    @Override
    public String getRoomName() {
        // Implementation needed
        return null;
    }
}
