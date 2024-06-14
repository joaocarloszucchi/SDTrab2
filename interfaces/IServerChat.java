package interfaces;

import java.util.ArrayList;

public interface IServerChat extends java.rmi.Remote {
    // user calls it remotelly to and it returns all the avaliable rooms
    public ArrayList<String> getRooms();

    // user calls it remotelly to create a new room
    public void createRoom(String roomName);
}