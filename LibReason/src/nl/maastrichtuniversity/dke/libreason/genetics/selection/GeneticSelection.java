package nl.maastrichtuniversity.dke.libreason.genetics.selection;

import nl.maastrichtuniversity.dke.libreason.genetics.GeneticPopulation;

public interface GeneticSelection
{
	public GeneticPopulation select(GeneticPopulation p,int size);
}
