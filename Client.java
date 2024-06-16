import javax.swing.*;
import classes.UserChat;
import java.awt.*;
import java.awt.event.*;
import java.rmi.Naming;
import java.util.ArrayList;
import interfaces.IRoomChat;
import interfaces.IServerChat;

public class Client extends JFrame {
    private IServerChat server;
    private UserChat user;
    private DefaultListModel<String> roomListModel;
    private JList<String> roomList;
    private JButton joinRoomButton;
    private JButton createRoomButton;

    public Client(String userName) {
        try {
            server = (IServerChat) Naming.lookup("//localhost:2020/Servidor");
            user = new UserChat(userName);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        setTitle("Chat Client: " + userName);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        roomListModel = new DefaultListModel<>();
        roomList = new JList<>(roomListModel);
        JScrollPane roomScrollPane = new JScrollPane(roomList);

        joinRoomButton = new JButton("Join Room");
        createRoomButton = new JButton("Create Room");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(joinRoomButton);
        buttonPanel.add(createRoomButton);

        add(roomScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        joinRoomButton.addActionListener(e -> joinRoom());
        createRoomButton.addActionListener(e -> createRoom());

        // Listen for window close event to leave the room
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                return;
            }
        });

        loadRooms();
    }

    private void loadRooms() {
        try {
            ArrayList<String> rooms = server.getRooms();
            roomListModel.clear();
            for (String room : rooms) {
                roomListModel.addElement(room);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void joinRoom() {
        String selectedRoom = roomList.getSelectedValue();
        System.out.println("selected is " + selectedRoom);
        if (selectedRoom != null) {
            try {
                IRoomChat room = (IRoomChat) Naming.lookup("//localhost:2020/" + selectedRoom);
                room.joinRoom(user.getUsrName(), user);
                new RoomGUI(user, room).setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void createRoom() {
        String newRoomName = JOptionPane.showInputDialog(this, "Enter room name:");
        if (newRoomName != null && !newRoomName.trim().isEmpty()) {
            try {
                server.createRoom(newRoomName);
                /*
                IRoomChat room = (IRoomChat) Naming.lookup("//localhost:2020/" + newRoomName);
                room.joinRoom(user.getUsrName(), user);
                new RoomGUI(user, room).setVisible(true);
                */
                loadRooms();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Client(args[0]).setVisible(true);
        });
    }
}
