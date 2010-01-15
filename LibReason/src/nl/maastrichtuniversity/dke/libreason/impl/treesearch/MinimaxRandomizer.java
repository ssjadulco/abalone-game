package nl.maastrichtuniversity.dke.libreason.impl.treesearch;

import nl.maastrichtuniversity.dke.libreason.def.SearchProblem;
import nl.maastrichtuniversity.dke.libreason.def.heuristic.Evaluator;
import nl.maastrichtuniversity.dke.libreason.def.treesearch.AbstractMinimaxSearch;
import nl.maastrichtuniversity.dke.libreason.def.treesearch.AlphaBetaNode;
import nl.maastrichtuniversity.dke.libreason.def.treesearch.DepthLimitedSearch;
import nl.maastrichtuniversity.dke.libreason.def.treesearch.MinimaxNode;
import nl.maastrichtuniversity.dke.libreason.def.treesearch.MinimaxProblem;
import nl.maastrichtuniversity.dke.libreason.def.treesearch.SearchNode;
import nl.maastrichtuniversity.dke.libreason.impl.treesearch.queues.DepthFirstQueue;

/**
 * TODO documentation
 */
public class MinimaxRandomizer<N extends MinimaxNode> implements DepthLimitedSearch<N>
{
	// The sub strategy that is used
	private AbstractMinimaxSearch<N> searchStrategy;

	public MinimaxRandomizer(AbstractMinimaxSearch<N> searchStrategy)
	{
		this.searchStrategy = searchStrategy;
	}

	@Override
	public int getDepthLimit()
	{
		return searchStrategy.getDepthLimit();
	}

	@Override
	public void setDepthLimit(int limit)
	{
		searchStrategy.setDepthLimit(limit);
	}

	@Override
	public SearchProblem getProblem()
	{
		return searchStrategy.getProblem();
	}

	@Override
	public N search(N initial) throws InterruptedException
	{
		return randomizedMax(initial);
	}

	public N randomizedMax(N node) throws InterruptedException
	{
		if (Thread.interrupted())
		{
			// For thread safe programming we check whether there was an
			// interrupt
			throw new InterruptedException();
		}
		// We store the maximal child in v:
		N v = null;
		// We store the second best child in w:
		N w = null;
		for (SearchNode n : node.expand())
		{
			// For every successor node
			// We know that the search node should be of kind N, thus
			// the following cast should work:
			N current = (N) n;

			if (searchStrategy.expandMinNode(current))
			{
				// The test told us, that we should indeed expand
				// this successor. So we get the minimal child node of
				// this node.
				MinimaxNode min = searchStrategy.minNode(current);

				if (min == null)
				{
					// current should have been recognized as an endnode,...
					// but it hasn't.
					// However we KNOW that it is an endnode (as no further
					// actions were generated) so we simply assign a value and
					// return.
					current.setValue(((MinimaxProblem) getProblem()).getFinalStateValue(current.getState()));
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
				w = v;
				v = current;
			}
			else if (w == null || w.getValue() < current.getValue())
			{
				w = current;
			}

			if (!searchStrategy.continueAfterMinNode(current))
			{
				// The test told us, that we should not continue searching
				// and prune the rest of the children of the current node.
				// Instead the best node found so far will be returned.
				
				gamble(v,w);

			}
			// It turned out that we should continue searching, so we loop
			// again.
		}
		// The loop finished and we investigated on all children of the
		// current node. We know now that the maximal child is in v.
		// So we return it.
		return gamble(v,w);
	}
	
	private N gamble(N node1, N node2)
	{
		if (node2 == null)
		{
			return node1;
		}
		else
		{
			// TODO: This is kind of magic number and horrifying...
			if (Math.random() < 0.7 + (node1.getValue() - node2.getValue()))
			{
				return node1;
			}
			else
			{
				return node2;
			}
		}
	}

}
