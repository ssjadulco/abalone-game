package nl.maastrichtuniversity.dke.libreason.genetics;

public interface FitnessEvaluator
{
	GeneticPopulation eval(GeneticPopulation pop) throws InterruptedException;
}
