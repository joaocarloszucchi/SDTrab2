import java.rmi.RemoteException;

public interface IRoomChat extends java.rmi.Remote {
    // user calls it remotelly to send a message 
    public void sendMsg(String usrName, String msg) throws RemoteException;     
    
    // user calls it remotelly to join a room
    public void joinRoom(String usrName, IUserChat user) throws RemoteException;   
    
    // user calls it remotelly to join a room
    public void leaveRoom(String usrName) throws RemoteException;                  
    
    // user calls it remotelly to leave a room
    public void closeRoom() throws RemoteException;                                
    
    // server calls it locally to close a room
    public String getRoomName() throws RemoteException;
}