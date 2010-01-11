package nl.maastrichtuniversity.dke.libreason.impl.treesearch;

import nl.maastrichtuniversity.dke.libreason.def.treesearch.AbstractMinimaxSearch;
import nl.maastrichtuniversity.dke.libreason.def.treesearch.AlphaBetaNode;
import nl.maastrichtuniversity.dke.libreason.def.treesearch.MinimaxProblem;

/**
 * Alpha-beta pruning is a search algorithm which seeks to reduce the number of
 * nodes that are evaluated in the search tree by the minimax algorithm. It is a
 * search with adversary algorithm used commonly for machine playing of
 * two-player games (Tic-tac-toe, Chess, Go, etc.). It stops completely
 * evaluating a move when at least one possibility has been found that proves
 * the move to be worse than a previously examined move. Such moves need not be
 * evaluated further. Alpha-beta pruning is a sound optimization in that it does
 * not change the result of the algorithm it optimizes. 
 * (<a href="http://en.wikipedia.org/wiki/Alpha_beta_pruning">Wikipedia</a>)
 * 
 * This AlphaBetaSearch is implemented according to the decorator design
 * pattern: You can use it as a wrapper around your standard minimax search and
 * it will again constitute a minimax search.
 * 
 * @author Daniel Mescheder
 * 
 * @param <N>
 *            the AlphaBetaNode class that is used for the tree.
 */
public class AlphaBetaSearch<N extends AlphaBetaNode> extends AbstractMinimaxSearch<N>
{
	// The sub strategy that is used by the alpha beta decorator
	private AbstractMinimaxSearch<N> searchStrategy;

	/**
	 * Creates a new AlphaBeta decorator.
	 * 
	 * @param searchStrategy
	 *            the referenced search strategy on which alpha beta pruning is
	 *            applied
	 */
	public AlphaBetaSearch(AbstractMinimaxSearch<N> searchStrategy)
	{
		// We know that our search strategy is a minimax search, so we can
		// simply use its' problem definition, evaluator and depth limit.
		super((MinimaxProblem) searchStrategy.getProblem(),searchStrategy.getEvaluator(),searchStrategy.getDepthLimit());
		this.searchStrategy = searchStrategy;
	}

	@Override
	public N search(N startNode) throws InterruptedException
	{
		// The interrupt is simply passed on
		startNode.setAlpha(Double.NEGATIVE_INFINITY);
		startNode.setBeta(Double.POSITIVE_INFINITY);
		return super.search(startNode);
	}

	@Override
	public boolean continueAfterMaxNode(N node)
	{
		AlphaBetaNode parent = (AlphaBetaNode) node.getParent();
		// First of all we get the current value of beta.
		// This value is stored in the parent of "node"
		double beta = parent.getBeta();

		if (node.getValue() >= beta)
		{
			// beta (the best choice for min on the path)
			// is smaller than the value of the investigated node.
			// That means, max's choice is at least as good as the current
			// value, but min has a better alternative somewhere else.
			// Thus min would never choose the parent node!
			// Therefore it does not make any sense to further
			// look at any more siblings.

			return false;
		}
		else
		{
			// There is no reason to cancel the search.
			// Anyway, we have to do some bookkeeping and update the
			// value of alpha. Alpha is the best option for max on
			// the path:
			double alpha = parent.getAlpha();
			parent.setAlpha(Math.max(alpha, node.getValue()));
		}

		// We had no reason to cancel the search, but maybe the referenced
		// strategy
		// has, so we let it decide:
		return searchStrategy.continueAfterMaxNode(node);
	}

	@Override
	public boolean continueAfterMinNode(N node)
	{
		AlphaBetaNode parent = (AlphaBetaNode) node.getParent();
		// First of all we get the current value of alpha.
		// This value is stored in the parent of "node"
		double alpha = parent.getAlpha();

		if (node.getValue() <= alpha)
		{
			// alpha (the best choice for max on the path)
			// is greater than the value of the investigated node.
			// That means, min's choice is at most worth the current
			// value, but max has a better alternative somewhere else.
			// Thus max would never choose the parent node!
			// Therefore it does not make any sense to further
			// look at any more siblings.

			return false;
		}
		else
		{
			// There is no reason to cancel the search.
			// Anyway, we have to do some bookkeeping and update the
			// value of beta. Beta is the best option for min on
			// the path:
			double beta = parent.getBeta();
			parent.setAlpha(Math.min(beta, node.getValue()));
		}

		// We had no reason to cancel the search, but maybe the referenced
		// strategy
		// has, so we let it decide:
		return searchStrategy.continueAfterMinNode(node);
	}

	/**
	 * Alpha-Beta pruning does not affect whether a node is expanded or not.
	 * This class therefore just calls the search strategy to answer this
	 * question.
	 * 
	 * However, we make sure, that the node being expanded knows about the
	 * current alpha and beta values.
	 * 
	 * @see nl.maastrichtuniversity.dke.libreason.def.treesearch.AbstractMinimaxSearch#expandMaxNode(nl.maastrichtuniversity.dke.libreason.def.treesearch.MinimaxNode)
	 */
	@Override
	public boolean expandMaxNode(N node)
	{
		return searchStrategy.expandMaxNode(node);
	}

	/**
	 * Alpha-Beta pruning does not affect whether a node is expanded or not.
	 * This class therefore just calls the search strategy to answer this
	 * question.
	 * 
	 * However, we make sure, that the node being expanded knows about the
	 * current alpha and beta values.
	 * 
	 * @see nl.maastrichtuniversity.dke.libreason.def.treesearch.AbstractMinimaxSearch#expandMaxNode(nl.maastrichtuniversity.dke.libreason.def.treesearch.MinimaxNode)
	 */
	@Override
	public boolean expandMinNode(N node)
	{
		return searchStrategy.expandMinNode(node);
	}

	protected void passOnAlphaBeta(N node)
	{
		AlphaBetaNode parent = (AlphaBetaNode) node.getParent();

		node.setAlpha(parent.getAlpha());
		node.setBeta(parent.getBeta());
	}

}
