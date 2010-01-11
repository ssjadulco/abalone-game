package nl.maastrichtuniversity.dke.libreason.genetics.selection;

import nl.maastrichtuniversity.dke.libreason.genetics.GeneticIndividual;
import nl.maastrichtuniversity.dke.libreason.genetics.GeneticPopulation;

public class ElitistSelection implements GeneticSelection
{
	public GeneticPopulation select(GeneticPopulation pop, int size)
	{
		GeneticPopulation selected = new GeneticPopulation();
		for(int i = 0; i< size; i++)
		{
			GeneticIndividual max = pop.getFittest();
			selected.add(max);
			pop.remove(max);
			
		}
		return selected;
	}
}
