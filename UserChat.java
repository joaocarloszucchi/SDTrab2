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

    private JFrame mainFrame;
    private JFrame roomFrame;
    private JTextArea chatArea;
    private JTextField chatInput;
    private JButton sendButton;
    private JButton exitButton;

    private IRoomChat currentRoom;

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
        if (chatArea != null){
            if (isRoomClosed(msg)){
                synchronized (this) {
                    if (currentRoom != null) {
                        String closedRoomName = currentRoom.getRoomName();
                        roomList.remove(closedRoomName);
                        roomListModel.removeElement(closedRoomName);
                    }
                    currentRoom = null;
                    roomFrame.setVisible(false);
                    mainFrame.setVisible(true);
                }
            }
            else{
                chatArea.append(senderName + ": " + msg + "\n");
            }
        }
    }

    private boolean isRoomClosed(String msg){
        return msg.endsWith(" Room closed by server.");
    }

    public String getUsrName() {
        return this.name;
    }

    public void initializeGUI() {
        try {
            mainFrame = new JFrame("Chat Client: " + name);
            mainFrame.setSize(400, 300);
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setLocationRelativeTo(null);

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

            mainFrame.add(roomScrollPane, BorderLayout.CENTER);
            mainFrame.add(buttonPanel, BorderLayout.SOUTH);

            joinRoomButton.addActionListener(e -> joinRoom(server, rooms.getSelectedValue()));
            createRoomButton.addActionListener(e -> createRoom(server));
            refreshButton.addActionListener(e -> loadRooms(server, roomListModel));

            mainFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    leaveRoom();
                    return;
                }
            });

            loadRooms(server, roomListModel);
            mainFrame.setVisible(true);
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
                currentRoom  = (IRoomChat) Naming.lookup("//localhost:2020/" + selectedRoom);
                currentRoom.joinRoom(name, this);
                showRoomFrame(selectedRoom);
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

    private void showRoomFrame(String roomName) {
        if (roomFrame == null) {
            roomFrame = new JFrame("Chat Room: " + roomName);
            roomFrame.setSize(500, 400);
            roomFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            roomFrame.setLocationRelativeTo(null);

            chatArea = new JTextArea();
            chatArea.setEditable(false);
            JScrollPane chatScrollPane = new JScrollPane(chatArea);

            chatInput = new JTextField(30);
            sendButton = new JButton("Send");
            exitButton = new JButton("Exit");

            JPanel inputPanel = new JPanel();
            inputPanel.add(chatInput);
            inputPanel.add(sendButton);
            inputPanel.add(exitButton);

            roomFrame.add(chatScrollPane, BorderLayout.CENTER);
            roomFrame.add(inputPanel, BorderLayout.SOUTH);

            sendButton.addActionListener(e -> sendMessage());

            exitButton.addActionListener(e -> leaveRoom());

            roomFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    leaveRoom();
                }
            });
        }

        roomFrame.setTitle("Chat Room: " + roomName);
        chatArea.setText("");
        mainFrame.setVisible(false);
        roomFrame.setVisible(true);
    }

    private void sendMessage() {
        String message = chatInput.getText().trim();
        if (!message.isEmpty()) {
            try {
                currentRoom.sendMsg(name, message);
                chatInput.setText("");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void leaveRoom() {
        if (currentRoom != null) {
            try {
                currentRoom.leaveRoom(name);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                currentRoom = null;
                roomFrame.setVisible(false);
                mainFrame.setVisible(true);
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
