package nl.maastrichtuniversity.dke.libreason.impl.treesearch;

import java.util.Collection;
import java.util.Queue;

import nl.maastrichtuniversity.dke.libreason.def.SearchProblem;
import nl.maastrichtuniversity.dke.libreason.def.treesearch.DepthLimitedSearch;
import nl.maastrichtuniversity.dke.libreason.def.treesearch.SearchNode;



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
public class DLTreeSearch<N extends SearchNode> implements DepthLimitedSearch<N>
{

	private static final long serialVersionUID = 1933371370782723235L;
	protected int depthLimit;
	// A search problem
	private SearchProblem problem;

	/**
	 * Executes the search starting from a given start node
	 * 
	 * @param node
	 *            the startnode of the search
	 * @return the goal node or null if nothing was found
	 */
	@SuppressWarnings("unchecked")
	@Override
	public N search(N node) throws InterruptedException
	{
		// Don't get fooled by a start node that is at the same time a goal node:
		if (problem.goalTest(node.getState()))
		{
			// the goal test matches - a goal has been found, return it!
			return node;
		}
		if (this.breakTest(node))
		{
			// Also we're not fooled by a search that may not even be started:
			return null;
		}
		
		Queue<N> fringe = (Queue<N>) node.expand();
		while (!fringe.isEmpty())
		{
			// choose a leaf node
			node = fringe.remove();
			
			// as long as there are still nodes that can be expanded
			if (problem.goalTest(node.getState()))
			{
				// the goal test matches - a goal has been found, return it!
				return node;
			}
			if (this.breakTest(node))
			{
				// the break test matches - we should not search any further
				// here. Just remove this node from the fringe and go on!
				fringe.remove(node);
				continue;
			}
			if(Thread.interrupted())
			{
				throw new InterruptedException();
			}
			
			// expand the chosen node and add the result to the fringe
			fringe.addAll((Collection<N>) node.expand());
		}
		return null;
	}
	
	/**
	 * 
	 * TODO JavaDoc
	 * @return
	 */
	public SearchProblem getProblem()
	{
		return problem;
	}
	
	public boolean breakTest(N node)
	{
		return node.getDepth() >= depthLimit;
	}

	public DLTreeSearch(SearchProblem t, int limit)
	{
		this.problem = t;
		this.depthLimit = limit;
	}

	public DLTreeSearch(SearchProblem problem)
	{
		this(problem,0);
	}

	@Override
	public int getDepthLimit()
	{
		return this.depthLimit;
	}

	@Override
	public void setDepthLimit(int limit)
	{
		this.depthLimit = limit;
	}
}
