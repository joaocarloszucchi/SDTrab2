import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class UserChat extends UnicastRemoteObject implements IUserChat {
    private String name;
    private IServerChat server;
    ArrayList<String> roomList;

    DefaultListModel<String> roomListModel;
    JList<String> rooms;
    JButton joinRoomButton;
    JButton createRoomButton;
    JButton refreshButton;

    public UserChat(String name) throws RemoteException {
        this.name = name;
        try{
            server = (IServerChat) Naming.lookup("//localhost:2020/Servidor");
        } catch(Exception e){
            e.printStackTrace();
        }
        
        initializeGUI();
    }
    
    @Override
    public void deliverMsg(String senderName, String msg) throws RemoteException {
        System.out.println(senderName + ": " + msg);
    }

    public String getUsrName() {
        return this.name;
    }

    public void initializeGUI() {
        try {
            JFrame frame = new JFrame("Chat Client: " + name);
            frame.setSize(400, 300);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);

            roomListModel = new DefaultListModel<>();
            rooms = new JList<>(roomListModel);
            JScrollPane roomScrollPane = new JScrollPane(rooms);

            joinRoomButton = new JButton("Join Room");
            createRoomButton = new JButton("Create Room");
            refreshButton = new JButton("Refresh");

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(joinRoomButton);
            buttonPanel.add(createRoomButton);
            buttonPanel.add(refreshButton);

            frame.add(roomScrollPane, BorderLayout.CENTER);
            frame.add(buttonPanel, BorderLayout.SOUTH);

            joinRoomButton.addActionListener(e -> joinRoom(server, rooms.getSelectedValue()));
            createRoomButton.addActionListener(e -> createRoom(server));
            refreshButton.addActionListener(e -> loadRooms(server, roomListModel));

            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    // Optionally handle window close event
                }
            });

            loadRooms(server, roomListModel);
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRooms(IServerChat server, DefaultListModel<String> roomListModel) {
        try {
            roomList = server.getRooms();
            roomListModel.clear();
            for (String room : roomList) {
                roomListModel.addElement(room);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void joinRoom(IServerChat server, String selectedRoom) {
        if (selectedRoom != null) {
            try {
                IRoomChat room = (IRoomChat) Naming.lookup("//localhost:2020/" + selectedRoom);
                room.joinRoom(name, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void createRoom(IServerChat server) {
        String newRoomName = JOptionPane.showInputDialog("Enter room name:");
        if (newRoomName != null && !newRoomName.trim().isEmpty() && !roomList.contains(newRoomName)) {
            try {
                server.createRoom(newRoomName);
                loadRooms(server, roomListModel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new UserChat(args[0]);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }
}
