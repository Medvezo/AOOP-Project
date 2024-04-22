import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final ObjectInputStream inputStream;
    private final ObjectOutputStream outputStream;

    public ClientHandler(Socket socket, ObjectInputStream inputStream, ObjectOutputStream outputStream) {
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("ClientHandlerThread");
        try {
            while (true) {
                // reading student choices from client
                StudentPreferences preferences = (StudentPreferences) inputStream.readObject();
                System.out.println("Received student preferences for student ID: " + preferences.getStudentId());

                // processin choices using genetic algorithm
                String bestDestination = GeneticAlgorithm.findBestDestination(preferences);

                // updating data
                Server.updateStudentData(preferences.getStudentId(), bestDestination, bestDestination);

                // send message to student that choice is processed
                outputStream.writeObject("PreferencesProcessed");

                // waiting for display of best destination
                inputStream.readObject();

                // displaying data
                Server.displayStudentData();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
