import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ClientUI {
    private JFrame frame;
    private JTextField idField;
    private JCheckBox[] destinationCheckBoxes;
    private DefaultTableModel tableModel;

    private static final String[] COUNTRIES = {
            "Australia", "Brazil", "Canada", "China", "France", "Germany", "India", "Italy", "Japan", "Mexico"
    };

    public ClientUI(ObjectOutputStream outputStream, ObjectInputStream inputStream) {
        createIdAndDestinationsFrame(outputStream, inputStream);
    }

    private void createIdAndDestinationsFrame(ObjectOutputStream outputStream, ObjectInputStream inputStream) {
        frame = new JFrame("Enter Student ID and Choose Destinations");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(860, 600);
        frame.setLayout(new BorderLayout());

        JPanel idPanel = new JPanel();
        JLabel idLabel = new JLabel("Enter Student ID:");
        idField = new JTextField(10);
        idPanel.add(idLabel);
        idPanel.add(idField);

        JPanel destinationsPanel = new JPanel();
        destinationsPanel.setLayout(new GridLayout(0, 1));
        JLabel destinationsLabel = new JLabel("Choose Destinations (Max 5):");
        destinationsPanel.add(destinationsLabel);

        destinationCheckBoxes = new JCheckBox[COUNTRIES.length];
        for (int i = 0; i < COUNTRIES.length; i++) {
            destinationCheckBoxes[i] = new JCheckBox(COUNTRIES[i]);
            destinationsPanel.add(destinationCheckBoxes[i]);
        }

        JButton submitButton = new JButton("Submit");
        submitButton.setBackground(new Color(52, 152, 219)); //  background blue
        submitButton.setForeground(Color.WHITE); //  text color white
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //  if student ID is empty
                if (idField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Student ID cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // if student ID is valid 8-digit int
                int studentId;
                try {
                    studentId = Integer.parseInt(idField.getText());
                    if (idField.getText().length() != 8) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Student ID must be 8-digit integer.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                //  if no destinations selected or more than 5 selected
                List<String> selectedDestinations = new ArrayList<>();
                for (JCheckBox checkBox : destinationCheckBoxes) {
                    if (checkBox.isSelected()) {
                        selectedDestinations.add(checkBox.getText());
                    }
                }
                if (selectedDestinations.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please select at least 1 destination.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (selectedDestinations.size() > 5) {
                    JOptionPane.showMessageDialog(frame, "Please select up to 5 destinations.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // handle the submission
                submitPreferences(studentId, selectedDestinations, outputStream, inputStream);

                // clear all the fields
                idField.setText("");
                for (JCheckBox checkBox : destinationCheckBoxes) {
                    checkBox.setSelected(false);
                }
            }
        });


        // table of displaying student data
        String[] columnNames = {"Student ID", "Selected Destinations", "Final Destination"};
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // disabling cell editing
            }
        };
        JScrollPane scrollPane = new JScrollPane(table);

        JButton deleteAllButton = new JButton("Delete All");
        deleteAllButton.setBackground(new Color(231, 76, 60)); // backgreound red
        deleteAllButton.setForeground(Color.WHITE); // text color white
        deleteAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                idField.setText("");
                for (JCheckBox checkBox : destinationCheckBoxes) {
                    checkBox.setSelected(false);
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submitButton);
        buttonPanel.add(deleteAllButton);

        frame.add(idPanel, BorderLayout.NORTH);
        frame.add(destinationsPanel, BorderLayout.WEST);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private void submitPreferences(int studentId, List<String> selectedDestinations, ObjectOutputStream outputStream, ObjectInputStream inputStream) {
    	 if (isStudentIdExist(studentId)) {
    	        JOptionPane.showMessageDialog(frame, "Student ID already exists in table.", "Error", JOptionPane.ERROR_MESSAGE);
    	        return;
    	    }

        if (selectedDestinations.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please select at least 1 destination.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        } else if (selectedDestinations.size() > 5) {
            JOptionPane.showMessageDialog(frame, "Please select up to 5 destinations.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            System.out.println("Sending student preferences to server...");
            outputStream.writeObject(new StudentPreferences(studentId, selectedDestinations));
            System.out.println("Preferences sent.");

            System.out.println("Waiting for best destination from server...");
            List<String> bestDestinations = (List<String>) inputStream.readObject(); // changing cast to List<String>

            // updating table for displaying best destination
            String bestDestination = bestDestinations.get(0); // first destination from list
            tableModel.addRow(new Object[]{studentId, selectedDestinations, bestDestination});
            JOptionPane.showMessageDialog(frame, "Data submitted successfully.", "Info", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
    private boolean isStudentIdExist(int studentId) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if ((int) tableModel.getValueAt(i, 0) == studentId) {
                return true;
            }
        }
        return false;
    }

}
