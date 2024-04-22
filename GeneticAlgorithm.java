import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GeneticAlgorithm {
    private static final int POPULATION_SIZE = 50;
    private static final int MAX_GENERATIONS = 100;
    private static final int TOURNAMENT_SIZE = 3;
    private static final double MUTATION_RATE = 0.01;

    public static String findBestDestination(StudentPreferences preferences) {
        List<Chromosome> population = initializePopulation(preferences);
        for (int i = 0; i < MAX_GENERATIONS; i++) {
            List<Chromosome> nextGeneration = new ArrayList<>();
            for (int j = 0; j < POPULATION_SIZE; j++) {
                Chromosome parent1 = selectParent(population);
                Chromosome parent2 = selectParent(population);
                Chromosome child = crossover(parent1, parent2);
                mutate(child);
                nextGeneration.add(child);
            }
            population = nextGeneration;
        }
        Chromosome bestChromosome = getBestChromosome(population);
        return bestChromosome.getDestinations().get(0); // returning only first destination
    }


    private static List<Chromosome> initializePopulation(StudentPreferences preferences) {
        List<Chromosome> population = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            List<String> destinations = new ArrayList<>(preferences.getSelectedDestinations());
            Collections.shuffle(destinations);
            int fitness = calculateFitness(destinations, preferences); // place for fit calc
            population.add(new Chromosome(destinations, fitness));
        }
        return population;
    }

    private static Chromosome selectParent(List<Chromosome> population) {
        Collections.shuffle(population);
        List<Chromosome> tournament = population.subList(0, TOURNAMENT_SIZE);
        return getBestChromosome(tournament);
    }

    private static Chromosome crossover(Chromosome parent1, Chromosome parent2) {
        List<String> destinations1 = parent1.getDestinations();
        List<String> destinations2 = parent2.getDestinations();

        // creating a child chromosome by crossover operation 
        int crossoverPoint = Math.min(destinations1.size(), destinations2.size()) / 2;
        List<String> childDestinations = new ArrayList<>();
        childDestinations.addAll(destinations1.subList(0, crossoverPoint));
        childDestinations.addAll(destinations2.subList(crossoverPoint, destinations2.size()));

        // calculate   fitness
        int fitness = calculateFitness(childDestinations, null); //  null as its not needed 

        return new Chromosome(childDestinations, fitness);
    }

    private static void mutate(Chromosome chromosome) {
        if (Math.random() < MUTATION_RATE) {
            List<String> destinations = chromosome.getDestinations();
            Collections.swap(destinations, (int) (Math.random() * destinations.size()), (int) (Math.random() * destinations.size()));
        }
    }

    private static Chromosome getBestChromosome(List<Chromosome> population) {
        Chromosome bestChromosome = population.get(0);
        for (Chromosome chromosome : population) {
            if (chromosome.getFitness() > bestChromosome.getFitness()) {
                bestChromosome = chromosome;
            }
        }
        return bestChromosome;
    }

    private static int calculateFitness(List<String> destinations, StudentPreferences preferences) {
        int fitness = 0;
        for (int i = 0; i < destinations.size(); i++) {
            String destination = destinations.get(i);
            int position = i + 1; // positionn of the destination in the student's preferences

            // cost based on guidelines
            int cost;
            if (position == 1) {
                cost = 0; // no cost if first choice of student
            } else {
                cost = (position - 1) * (position - 1); // (i-1)^2 if he obtains his first choice
            }

            //  if destination choice was not his choice
            if (!preferences.getSelectedDestinations().contains(destination)) {
                cost = 10 * destinations.size() * destinations.size(); // 10 * Nd^2 if stident is assigned to a destination he did not choose
            }

            fitness += cost;
        }
        return fitness;
    }
}
