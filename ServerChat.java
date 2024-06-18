import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import java.awt.*;

public class ServerChat extends UnicastRemoteObject implements IServerChat {
    private ArrayList<String> roomList;
    private Map<String, IRoomChat> rooms;
    private String ip;
    private String port;

    private DefaultListModel<String> roomListModel;
    private JList<String> roomListUI;
    private JButton refreshButton;
    private JButton closeRoomButton;

    public ServerChat(String ip, String port) throws RemoteException {
        this.roomList = new ArrayList<>();
        this.rooms = new HashMap<>();
        this.ip = ip;
        this.port = port;
        initializeServer();
        initializeGUI();
    }

    private void initializeServer() {
        try {
            LocateRegistry.createRegistry(2020);
            Naming.rebind("//" + ip + ":" + port + "/" + "Servidor", this); //"//localhost:2020/Servidor" 
            System.out.println("Server chat running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized ArrayList<String> getRooms() throws RemoteException {
        System.out.println("Returning room list");
        return this.roomList;
    }

    @Override
    public synchronized void createRoom(String roomName) throws RemoteException {
        System.out.println("Creating room " + roomName);
        if (!roomList.contains(roomName)) {
            IRoomChat newRoom;
            try {
                newRoom = new RoomChat(roomName);
                rooms.put(roomName, newRoom);
                roomList.add(roomName);
                Naming.rebind("//" + ip + ":" + port + "/" + roomName, newRoom);// "//localhost:2020/"
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
                Naming.unbind("//" + ip + ":" + port + "/" + roomName); //"//localhost:2020/"
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Closed room " + roomName);
        }
    }

    private void initializeGUI() {
        JFrame frame = new JFrame("Chat Server");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        roomListModel = new DefaultListModel<>();
        roomListUI = new JList<>(roomListModel);
        JScrollPane roomScrollPane = new JScrollPane(roomListUI);

        refreshButton = new JButton("Refresh");
        closeRoomButton = new JButton("Close Room");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeRoomButton);

        frame.add(roomScrollPane, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> loadRooms());
        closeRoomButton.addActionListener(e -> getSelectedRoom());

        frame.setVisible(true);

        loadRooms();
    }

    private void loadRooms() {
        try {
            ArrayList<String> rooms = getRooms();
            roomListModel.clear();
            for (String room : rooms) {
                roomListModel.addElement(room);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getSelectedRoom() {
        String selectedRoom = roomListUI.getSelectedValue();
        if (selectedRoom != null) {
            System.out.println("Selected Room: " + selectedRoom);
            try {
                closeRoom(selectedRoom);
                loadRooms();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "No room selected", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new ServerChat(args[0], args[1]);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }
}
