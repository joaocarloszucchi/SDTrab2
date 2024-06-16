import javax.swing.*;
import java.awt.*;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import classes.ServerChat;
import java.util.ArrayList;

public class Server extends JFrame {
    private ServerChat server;
    private DefaultListModel<String> roomListModel;
    private JList<String> roomList;
    private JButton refreshButton;

    public Server() {
        try {
            LocateRegistry.createRegistry(2020);
            server = new ServerChat();
            Naming.rebind("//localhost:2020/Servidor", server);
            System.out.println("Server chat running...");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        setTitle("Chat Server");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        roomListModel = new DefaultListModel<>();
        roomList = new JList<>(roomListModel);
        JScrollPane roomScrollPane = new JScrollPane(roomList);

        refreshButton = new JButton("Refresh");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);

        add(roomScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> loadRooms());

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Server().setVisible(true);
        });
    }
}
