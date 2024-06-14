package interfaces;

public interface IRoomChat extends java.rmi.Remote {
    // user calls it remotelly to send a message 
    public void sendMsg(String usrName, String msg);     
    
    // user calls it remotelly to join a room
    public void joinRoom(String usrName, IUserChat user);   
    
    // user calls it remotelly to join a room
    public void leaveRoom(String usrName);                  
    
    // user calls it remotelly to leave a room
    public void closeRoom();                                
    
    // server calls it locally to close a room
    public String getRoomName();
}