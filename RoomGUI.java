import javax.swing.*;
import classes.UserChat;
import java.awt.*;
import java.awt.event.*;
import java.rmi.RemoteException;

import interfaces.IUserChat;
import interfaces.IRoomChat;

public class RoomGUI extends JFrame {
    private UserChat user;
    private IRoomChat room;
    private DefaultListModel<String> messageListModel;
    private JList<String> messageList;
    private JTextField messageField;
    private JButton sendButton;

    public RoomGUI(UserChat user, IRoomChat room) throws RemoteException{
        this.user = user;
        this.room = room;

        setTitle("Chat Room: " + room.getRoomName());
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        messageListModel = new DefaultListModel<>();
        messageList = new JList<>(messageListModel);
        JScrollPane messageScrollPane = new JScrollPane(messageList);

        messageField = new JTextField(30);
        sendButton = new JButton("Send");

        JPanel inputPanel = new JPanel();
        inputPanel.add(messageField);
        inputPanel.add(sendButton);

        add(messageScrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());

        // Listen for window close event to leave the room
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                leaveRoom();
            }
        });
    }

    private void sendMessage() {
        String msg = messageField.getText().trim();
        if (!msg.isEmpty()) {
            try {
                room.sendMsg(user.getUsrName(), msg);
                messageListModel.addElement(user.getUsrName() + ": " + msg);
                messageField.setText("");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void leaveRoom() {
        try {
            room.leaveRoom(user.getUsrName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
