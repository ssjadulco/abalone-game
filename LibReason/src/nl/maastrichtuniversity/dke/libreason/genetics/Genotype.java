package nl.maastrichtuniversity.dke.libreason.genetics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class Genotype extends ArrayList<Gene> implements Serializable
{
	private static final long serialVersionUID = 4110730467197626434L;

	public Genotype()
	{
		super();
	}

	public Genotype(Collection<? extends Gene> c)
	{
		super(c);
	}

	public Genotype(int initialCapacity)
	{
		super(initialCapacity);
	}

	public String toString()
	{
		String str = "";

		for (Gene g : this)
		{
			str += g.toString() + " ";
		}

		return str;
	}
}
