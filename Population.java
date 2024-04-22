import java.util.ArrayList;
import java.util.List;

public class Population {
    private List<Chromosome> chromosomes;

    public Population() {
        chromosomes = new ArrayList<>();
    }

    public List<Chromosome> getChromosomes() {
        return chromosomes;
    }

    public void setChromosomes(List<Chromosome> chromosomes) {
        this.chromosomes = chromosomes;
    }

    public void addChromosome(Chromosome chromosome) {
        chromosomes.add(chromosome);
    }
}
