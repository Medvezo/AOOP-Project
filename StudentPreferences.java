import java.io.Serializable;
import java.util.List;

public class StudentPreferences implements Serializable {
    private int studentId;
    private List<String> selectedDestinations;

    public StudentPreferences(int studentId, List<String> selectedDestinations) {
        this.studentId = studentId;
        this.selectedDestinations = selectedDestinations;
    }

    public int getStudentId() {
        return studentId;
    }

    public List<String> getSelectedDestinations() {
        return selectedDestinations;
    }
}
