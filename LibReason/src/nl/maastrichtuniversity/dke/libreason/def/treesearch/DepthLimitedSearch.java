package nl.maastrichtuniversity.dke.libreason.def.treesearch;

/**
 * Like the normal depth-first search, depth-limited search is a uninformed
 * search. It works exactly like depth-first search, but avoids its drawbacks
 * regarding completeness by imposing a maximum limit on the depth of the
 * search. Even if the search could still expand a vertex beyond that depth, it
 * will not do so and thereby it will not follow infinitely deep paths or get
 * stuck in cycles. Therefore depth-limited search will find a solution if it is
 * within the depth limit, which guarantees at least completeness on all graphs.
 * (<a href="http://en.wikipedia.org/wiki/Depth-limited_search">Wikipedia</a>)
 * 
 * @author Daniel Mescheder
 * 
 * @param <N>
 *            the SearchNode class the tree consists of
 */
public interface DepthLimitedSearch<N extends SearchNode> extends TreeSearch<N>
{
	/**
	 * Sets the depth limit up to which this search runs.
	 * 
	 * @param limit
	 *            the new depth limit
	 */
	public void setDepthLimit(int limit);

	/**
	 * Returns the depth limit up to which this search runs.
	 * 
	 * @return the depth limit
	 */
	public int getDepthLimit();
}
