package classes;
import java.util.ArrayList;
import interfaces.IServerChat;

public class Server implements IServerChat {
    private ArrayList<String> roomList;

    public Server(){
        this.roomList = new ArrayList<String>();
    }

    @Override
    public ArrayList<String> getRooms() {
        return this.roomList;
    }

    @Override
    public void createRoom(String roomName) {
        
    }
}
