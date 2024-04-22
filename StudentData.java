import java.util.List;

public class StudentData {
    private int studentId;
    private String selectedDestinations;
    private String finalDestination;

    public StudentData(int studentId, String selectedDestinations, String finalDestination) {
        this.studentId = studentId;
        this.selectedDestinations = selectedDestinations;
        this.finalDestination = finalDestination;
    }

    public int getStudentId() {
        return studentId;
    }

    public String getSelectedDestinations() {
        return selectedDestinations;
    }

    public String getFinalDestination() {
        return finalDestination;
    }
}