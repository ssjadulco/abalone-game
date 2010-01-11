package nl.maastrichtuniversity.dke.libreason.impl.treesearch;

import nl.maastrichtuniversity.dke.libreason.def.SearchState;
import nl.maastrichtuniversity.dke.libreason.def.heuristic.Evaluator;
import nl.maastrichtuniversity.dke.libreason.def.treesearch.AbstractMinimaxSearch;
import nl.maastrichtuniversity.dke.libreason.def.treesearch.DepthLimitedSearch;
import nl.maastrichtuniversity.dke.libreason.def.treesearch.MinimaxNode;
import nl.maastrichtuniversity.dke.libreason.def.treesearch.MinimaxProblem;

/**
 * A Depth Limited Minimax search. This class extends the generic
 * {@link AbstractMinimaxSearch} and extends it with a preprocessing test that
 * cancels the search at a certain depth. If the depth limit is reached and no
 * final state was found, an evaluation function is used to estimate the quality
 * of the state.
 * 
 * @author Daniel Mescheder
 * @param <N>
 *            the class of the nodes.
 */
public class DLMinimax<N extends MinimaxNode> extends AbstractMinimaxSearch<N> implements DepthLimitedSearch<N>
{

	/**
	 * A simple dummy evaluator that is used in case the user did not provide
	 * any evaluator - i.e. there is no domain knowledge available.
	 * This evaluator will simply evaluate every state to zero.
	 * 
	 * @author Daniel Mescheder
	 * 
	 */
	private static class DummyEvaluator implements Evaluator<Double>
	{
		@Override
		public Double eval(SearchState state)
		{
			return 0d;
		}
	}

	/**
	 * Create a new Depth Limited Minimax search.
	 * 
	 * @param problem
	 *            The problem definition of the search domain.
	 * @param eval
	 *            An evaluation function that is used when the depth limit is
	 *            reached
	 * @param depthLimit
	 *            The number of levels on which this search will investigate.
	 *            The higher the value, the deeper it searches. Keep in mind,
	 *            that the time needed to search normally increases
	 *            exponentially with this value.
	 */
	public DLMinimax(MinimaxProblem problem, Evaluator<Double> eval, int depthLimit)
	{
		super(problem, eval, depthLimit);
	}
	
	/**
	 * Create a new Depth Limited Minimax search without any domain knowledge.
	 * A standard evaluator will be used that yields zero for every state.
	 * If that is not desired, use 
	 * <code>
	 * 	DLMinimax(MinimaxProblem problem, Evaluator<Double> eval, int depthLimit)
	 * </code>
	 * instead.
	 * 
	 * @param problem
	 *            The problem definition of the search domain.
	 * @param depthLimit
	 *            The number of levels on which this search will investigate.
	 *            The higher the value, the deeper it searches. Keep in mind,
	 *            that the time needed to search normally increases
	 *            exponentially with this value.
	 */
	public DLMinimax(MinimaxProblem problem, int depthLimit)
	{
		this(problem, new DummyEvaluator(), depthLimit);
	}

	/**
	 * In the normal depth limited minimax search, a search is always continued
	 * until a final node or the depth limit is reached.
	 * 
	 * @param node
	 *            the Node that is investigated
	 * @return always true
	 * @see nl.maastrichtuniversity.dke.libreason.def.treesearch.AbstractMinimaxSearch#continueAfterMaxNode(nl.maastrichtuniversity.dke.libreason.def.treesearch.MinimaxNode)
	 */
	@Override
	public boolean continueAfterMaxNode(N node)
	{
		return true;
	}

	/**
	 * In the normal depth limited minimax search, a search is always continued
	 * until a final node or the depth limit is reached.
	 * 
	 * @param node
	 *            the Node that is investigated
	 * @return always true
	 * @see nl.maastrichtuniversity.dke.libreason.def.treesearch.AbstractMinimaxSearch#continueAfterMinNode(nl.maastrichtuniversity.dke.libreason.def.treesearch.MinimaxNode)
	 */
	@Override
	public boolean continueAfterMinNode(N node)
	{
		return true;
	}

	/**
	 * The node shall <u>not</u> be expanded if:
	 * <ul>
	 * <li>The depth limit is reached</li>
	 * <li>An endnode is reached</li>
	 * </ul>
	 * In the first case we let the evaluator evaluate the quality of the state,
	 * in the second case the quality value is taken from the domain represented
	 * by the problem definition.
	 * 
	 * @param node
	 *            the Node that is investigated
	 * @return true if the above criteria do not apply
	 * @see nl.maastrichtuniversity.dke.libreason.def.treesearch.AbstractMinimaxSearch#expandMaxNode(nl.maastrichtuniversity.dke.libreason.def.treesearch.MinimaxNode)
	 */
	@Override
	public boolean expandMaxNode(N node)
	{
		return expandNode(node);
	}

	/**
	 * The node shall <u>not</u> be expanded if:
	 * <ul>
	 * <li>The depth limit is reached</li>
	 * <li>An endnode is reached</li>
	 * </ul>
	 * In the first case we let the evaluator evaluate the quality of the state,
	 * in the second case the quality value is taken from the domain represented
	 * by the problem definition.
	 * 
	 * @param node
	 *            the Node that is investigated
	 * @return true if the above criteria do not apply
	 * @see nl.maastrichtuniversity.dke.libreason.def.treesearch.AbstractMinimaxSearch#expandMinNode(nl.maastrichtuniversity.dke.libreason.def.treesearch.MinimaxNode)
	 */
	@Override
	public boolean expandMinNode(N node)
	{
		return expandNode(node);
	}

	/**
	 * The node shall <u>not</u> be expanded if:
	 * <ul>
	 * <li>The depth limit is reached</li>
	 * <li>An endnode is reached</li>
	 * </ul>
	 * In the first case we let the evaluator evaluate the quality of the state,
	 * in the second case the quality value is taken from the domain represented
	 * by the problem definition.
	 * 
	 * @param node
	 *            the Node that is investigated
	 * @return true if the above criteria do not apply
	 */
	protected boolean expandNode(N node)
	{
		// We know that we've got a minimax domain, so this cast is no problem:
		MinimaxProblem problem = (MinimaxProblem) getProblem();
		if (problem.goalTest(node.getState()))
		{
			// Voila, we really found a goal state!!
			// So here's the plan: get the value of the final
			// state (win for min/win for max/draw/...?)
			// and return false to indicate we don't want to go
			// any deeper:
			node.setValue(problem.getFinalStateValue(node.getState()));
			return false;
		}

		if (node.getDepth() >= getDepthLimit())
		{
			// As the node under investigation is located at the
			// depth limit (or deeper) we do not want to go on
			// searching deeper.
			// Instead we get an estimate of the state quality
			// from the evaluator and return false.
			node.setValue(getEvaluator().eval(node.getState()));
			return false;
		}

		// If none of the above cases applied, we see no reason not to go
		// on searching:
		return true;
	}

}
