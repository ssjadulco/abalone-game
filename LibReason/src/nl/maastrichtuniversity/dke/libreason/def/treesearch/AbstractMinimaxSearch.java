package nl.maastrichtuniversity.dke.libreason.def.treesearch;

import nl.maastrichtuniversity.dke.libreason.def.SearchProblem;
import nl.maastrichtuniversity.dke.libreason.def.heuristic.Evaluator;

/**
 * This is an abstract class describing a generic minimax search. It includes
 * the normal recursive search features - only preprocessing and postprocessing
 * of nodes when they are investigated is not included. By implementing the
 * abstract methods it is possible to introduce advanced features as pruning or
 * hashing.
 * 
 * A minimax search is always depth first. As this introduces the problem of
 * incompleteness, it is by default depth limited. If this causes problems,
 * maybe the {@link IDTreeSearch} might solve the issue.
 * 
 * @author Daniel Mescheder
 * 
 * @param <N>
 *            the class of the nodes.
 */
public abstract class AbstractMinimaxSearch<N extends MinimaxNode> implements DepthLimitedSearch<N>
{
	// The problem that describes the domain.
	private MinimaxProblem problem;
	// The evaluator that gets used when the depth limit is reached
	private Evaluator<Double> evaluator;
	// The limit at which the search is cancelled.
	private int depthLimit;

	/**
	 * Creates a new abstract minimax search for a given problem description.
	 * 
	 * @param problem
	 *            The problem description of the search domain.
	 * @param eval
	 *            An evaluation function that is used when the depth limit is
	 *            reached
	 * @param depthLimit
	 *            The number of levels on which this search will investigate.
	 *            The higher the value, the deeper it searches. Keep in mind,
	 *            that the time needed to search normally increases
	 *            exponentially with this value.
	 */
	public AbstractMinimaxSearch(MinimaxProblem problem, Evaluator<Double> eval, int depthLimit)
	{
		this.problem = problem;
		this.evaluator = eval;
		this.depthLimit = depthLimit;
	}

	/**
	 * This will start a minimax search on a given start node. The result will
	 * be from the perspective of max as a starting player the most preferable
	 * subsequent node. To get the action that led to the result, call <code>
	 * 	search(startNode).getAction();
	 * </code>
	 * 
	 * @see nl.maastrichtuniversity.dke.libreason.def.treesearch.TreeSearch#search(nl.maastrichtuniversity.dke.libreason.def.treesearch.SearchNode)
	 */
	@Override
	public N search(N startNode) throws InterruptedException
	{
		// We start the search from max's perspective.
		// n will hold the node subsequent to startNode
		// which is to be preferred by the starting player.
		return maxNode(startNode);
	}

	/**
	 * Recursively finds the maximal child of the passed node.
	 * 
	 * @param node
	 *            the node to investigate on
	 * @return the maximal child of the node.
	 * @throws InterruptedException
	 *             if the thread was interrupted
	 */
	@SuppressWarnings("unchecked")
	protected N maxNode(N node) throws InterruptedException
	{
		if (Thread.interrupted())
		{
			// For thread safe programming we check whether there was an
			// interrupt
			throw new InterruptedException();
		}
		// We store the maximal child in v:
		N v = null;
		for (SearchNode n : node.expand())
		{
			// For every successor node
			// We know that the search node should be of kind N, thus
			// the following cast should work:
			N current = (N) n;

			if (expandMaxNode(current))
			{
				// The test told us, that we should indeed expand
				// this successor. So we get the minimal child node of
				// this node.
				MinimaxNode min = minNode(current);

				if (min == null)
				{
					// current should have been recognized as an endnode,...
					// but it hasn't.
					// However we KNOW that it is an endnode (as no further
					// actions were generated) so we simply assign a value and
					// return.

					current.setValue(problem.getFinalStateValue(current.getState()));
				}
				else
				{
					// We now know, that the value of the current node equals
					// the value of the minimal child (as it was min who did
					// the corresponding move)
					current.setValue(min.getValue());
				}
			}

			if (v == null || v.getValue() < current.getValue())
			{
				// Either we haven't found any node at all yet, or we found
				// one who will eventually lead to a higher outcome.
				// Anyway: store it in v!
				v = current;
			}

			if (!continueAfterMaxNode(current))
			{
				// The test told us, that we should not continue searching
				// and prune the rest of the children of the current node.
				// Instead the best node found so far will be returned.
				return v;
			}
			// It turned out that we should continue searching, so we loop
			// again.
		}
		// The loop finished and we investigated on all children of the
		// current node. We know now that the maximal child is in v.
		// So we return it.
		return v;
	}

	/**
	 * Recursively finds the minimal child of the passed node
	 * 
	 * @param node
	 *            the node to investigate on
	 * 
	 * @return the minimal child of the node.
	 * @throws InterruptedException
	 *             if the thread was interrupted
	 */
	@SuppressWarnings("unchecked")
	protected N minNode(N node) throws InterruptedException
	{

		if (Thread.interrupted())
		{
			// For thread safe programming we check whether there was an
			// interrupt
			throw new InterruptedException();
		}
		// We store the minimal child in v:
		N v = null;
		for (SearchNode n : node.expand())
		{
			// For every successor node
			// We know that the search node should be of kind N, thus
			// the following cast should work:
			N current = (N) n;

			if (expandMinNode(current))
			{
				// The test told us, that we should indeed expand
				// this successor. So we get the maximal child node of
				// this node.
				MinimaxNode max = maxNode(current);

				if (max == null)
				{
					// current should have been recognized as an endnode,...
					// but it hasn't.
					// However we KNOW that it is an endnode (as no further
					// actions were generated) so we simply assign a value and
					// return.

					current.setValue(problem.getFinalStateValue(current.getState()));
				}
				else
				{
					// We now know, that the value of the current node equals
					// the value of the maximal child (as it was max who did
					// the corresponding move)
					current.setValue(max.getValue());
				}
			}

			if (v == null || v.getValue() > current.getValue())
			{
				// Either we haven't found any node at all yet, or we found
				// one who will eventually lead to a lower (and thus more
				// preferable
				// for min) outcome.
				// Anyway: store it in v!
				v = current;
			}

			if (!continueAfterMinNode(current))
			{
				// The test told us, that we should not continue searching
				// and prune the rest of the children of the current node.
				// Instead the best node found so far will be returned.
				return v;
			}
			// It turned out that we should continue searching, so we loop
			// again.
		}
		// The loop finished and we investigated on all children of the
		// current node. We know now that the minimal child is in v.
		// So we return it.
		return v;
	}

	/**
	 * Test a given node in the min routine on whether or not the search should
	 * go deeper into this node.
	 * 
	 * @param node
	 * @return true if the search can be canceled
	 */
	public abstract boolean expandMinNode(N node);

	/**
	 * Test - after having investigated a given node in the min routine -
	 * whether or not the search can be canceled and the currently found best
	 * option can be returned.
	 * 
	 * @param node
	 * @return true if the search can be canceled
	 */
	public abstract boolean continueAfterMinNode(N node);

	/**
	 * Test a given node in the max routine on whether or not the search should
	 * go deeper into this node.
	 * 
	 * @param node
	 * @return true if the search can be canceled
	 */
	public abstract boolean expandMaxNode(N node);

	/**
	 * Test - after having investigated a given node in the max routine -
	 * whether or not the search can be canceled and the currently found best
	 * option can be returned.
	 * 
	 * @param node
	 * @return true if the search can be canceled
	 */
	public abstract boolean continueAfterMaxNode(N node);

	/**
	 * @see nl.maastrichtuniversity.dke.libreason.def.treesearch.TreeSearch#getProblem()
	 */
	@Override
	public SearchProblem getProblem()
	{
		return problem;
	}

	/**
	 * Gets the evaluator which is currently used to assess the quality of a
	 * state at the depth limit.
	 */
	public Evaluator<Double> getEvaluator()
	{
		return evaluator;
	}

	/**
	 * Sets the evaluator which is used to assess the quality of a state at the
	 * depth limit.
	 */
	public void setEvaluator(Evaluator<Double> eval)
	{
		this.evaluator = eval;
	}

	/**
	 * @see nl.maastrichtuniversity.dke.libreason.def.treesearch.DepthLimitedSearch#getDepthLimit()
	 */
	@Override
	public int getDepthLimit()
	{
		return this.depthLimit;
	}

	/**
	 * @see nl.maastrichtuniversity.dke.libreason.def.treesearch.DepthLimitedSearch#getDepthLimit()
	 */
	@Override
	public void setDepthLimit(int limit)
	{
		this.depthLimit = limit;
	}

}
