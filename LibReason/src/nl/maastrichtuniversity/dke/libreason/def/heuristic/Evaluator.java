package nl.maastrichtuniversity.dke.libreason.def.heuristic;

import nl.maastrichtuniversity.dke.libreason.def.SearchState;

public interface Evaluator<E extends Comparable<E>>
{
	public E eval(SearchState state);
}
