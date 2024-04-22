import java.util.List;

public class Chromosome {
    private List<String> destinations;
    private int fitness;

    public Chromosome(List<String> destinations, int fitness) {
        this.destinations = destinations;
        this.fitness = fitness;
    }

    public List<String> getDestinations() {
        return destinations;
    }

    public int getFitness() {
        return fitness;
    }
}
