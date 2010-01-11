package nl.maastrichtuniversity.dke.libreason.def.treesearch;

/**
 * Iterative deepening depth-first search (IDDFS) is a state space search
 * strategy in which a depth-limited search is run repeatedly, increasing the
 * depth limit with each iteration until it reaches the depth of the shallowest
 * goal state or exceeds a time limit. On each iteration, IDDFS visits the nodes
 * in the search tree in the same order as depth-first search, but the
 * cumulative order in which nodes are first visited, assuming no pruning, is
 * effectively breadth-first.
 * (<a href="http://en.wikipedia.org/wiki/Iterative_deepening_depth-first_search">Wikipedia</a>)
 * 
 * The iterative deepening search is supposed to work with a search strategy which is
 * required to be depth limited. The iterative deepening search can then iteratively
 * restart the search strategy with an increasing limit value.
 * @author Daniel Mescheder
 * 
 * @param <N>
 */
public interface IterativeDeepeningSearch<N extends SearchNode> extends TreeSearch<N>
{
	/**
	 * Sets the limit which constraints the runtime of this search.
	 * @param time the new time limit
	 */
	public void setTimeLimit(long time);

	/**
	 * Returns the limit which constraints the runtime of this search.
	 * @returns the time limit
	 */
	public long getTimeLimit();
	
	public DepthLimitedSearch<N> getSearchStrategy();
	
	public void setSearchStrategy(DepthLimitedSearch<N> search);
}
