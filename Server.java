import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static List<StudentData> studentDataList = new ArrayList<>();
    private static DefaultTableModel tableModel;

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(5000);
            System.out.println("Server running...");

            // Create the table
            JFrame frame = new JFrame("Student Data");
            JTable table = new JTable();
            tableModel = new DefaultTableModel();
            tableModel.addColumn("Student ID");
            tableModel.addColumn("Selected Destinations");
            tableModel.addColumn("Final Destination");
            table.setModel(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);
            frame.add(scrollPane, BorderLayout.CENTER);
            frame.setSize(800, 600);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected");

                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

                // handling client in a separate thread
                new Thread(new ClientHandler(socket, inputStream, outputStream)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updateStudentData(int studentId, String selectedDestinations, String finalDestination) {
        studentDataList.add(new StudentData(studentId, selectedDestinations, finalDestination));
        displayStudentData();
    }

    public static void displayStudentData() {
        // clear table
        tableModel.setRowCount(0);

        // add student data to table
        for (StudentData studentData : studentDataList) {
            tableModel.addRow(new Object[]{studentData.getStudentId(), studentData.getSelectedDestinations(), studentData.getFinalDestination()});
        }
    }
}
