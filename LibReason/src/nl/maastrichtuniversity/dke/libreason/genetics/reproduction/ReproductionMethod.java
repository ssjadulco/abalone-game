package nl.maastrichtuniversity.dke.libreason.genetics.reproduction;

import nl.maastrichtuniversity.dke.libreason.genetics.GeneticPopulation;

public interface ReproductionMethod
{
	public void setPopulation(GeneticPopulation pop);
	public int getResultSize();
	public GeneticPopulation getResult();
}
