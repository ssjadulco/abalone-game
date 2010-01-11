package nl.maastrichtuniversity.dke.libreason.def.treesearch;

import nl.maastrichtuniversity.dke.libreason.def.SearchProblem;





/**
 * A class to execute a basic tree searchtest. If it gets a properly defined search
 * problem and a start node, it will start searching for a goal node. The
 * implementations of these methods are very basic. This class is rather meant
 * to be extended by more intelligent approaches.
 * 
 * Some ideas for this class are taken from "Artificial Intelligence - A Modern Approach" (Russel, Norvig).
 * 
 * @author Daniel Mescheder
 * 
 */
public interface TreeSearch<N extends SearchNode>
{
	public N search(N initial) throws InterruptedException;
	public SearchProblem getProblem();
}
