package nl.maastrichtuniversity.dke.libreason.impl.treesearch;

import java.util.HashMap;
import java.util.Map;

import nl.maastrichtuniversity.dke.libreason.def.hashing.Hashable;
import nl.maastrichtuniversity.dke.libreason.def.heuristic.Evaluator;
import nl.maastrichtuniversity.dke.libreason.def.treesearch.AbstractMinimaxSearch;
import nl.maastrichtuniversity.dke.libreason.def.treesearch.MinimaxNode;
import nl.maastrichtuniversity.dke.libreason.def.treesearch.MinimaxProblem;

/**
 * TODO: Documentation
 * 
 * @author Daniel Mescheder
 * 
 */
public class HashingMinimaxSearch<N extends MinimaxNode & Hashable> extends AbstractMinimaxSearch<N>
{
	protected static class TableEntry
	{
		public static final TableEntry OPEN = new TableEntry(0, 0);
		private int precision;
		private Double value;

		public TableEntry(int precision, double value)
		{
			this.precision = precision;
			this.value = value;
		}

		public Double getValue()
		{
			return value;
		}

		public int getPrecision()
		{
			return precision;
		}
	}

	private static final long serialVersionUID = 5354076006129704855L;
	private AbstractMinimaxSearch<N> searchStrategy;
	private Map<Long, TableEntry> table;

	public HashingMinimaxSearch(AbstractMinimaxSearch<N> searchStrategy)
	{
		super((MinimaxProblem) searchStrategy.getProblem());
		this.searchStrategy = searchStrategy;
		this.table = new HashMap<Long, TableEntry>(200);
	}

	@Override
	public boolean continueAfterMinNode(N node)
	{
		putHash(node);
		return searchStrategy.continueAfterMinNode(node);
	}

	@Override
	public boolean continueAfterMaxNode(N node)
	{
		putHash(node);
		return searchStrategy.continueAfterMaxNode(node);
	}

	@Override
	public boolean expandMinNode(N node)
	{
		if (inHashTable(node))
		{
			return false;
		}
		if (searchStrategy.expandMinNode(node))
		{
//			table.put(node.getHash(), TableEntry.OPEN);
			return true;
		}
		return false;
	}

	@Override
	public boolean expandMaxNode(N node)
	{
		if (inHashTable(node))
		{
			return false;
		}
		if (searchStrategy.expandMaxNode(node))
		{
//			table.put(node.getHash(), TableEntry.OPEN);
			return true;
		}
		return false;
	}

	protected boolean inHashTable(N node)
	{
		TableEntry e = table.get(node.getHash());
		if (e != null)
		{
			if (e == TableEntry.OPEN)
			{
				node.setValue(((MinimaxProblem) getProblem()).repetitionValue());
				return true;
			}
			else if (e.getPrecision() >= (getDepthLimit() - node.getDepth()))
			{
				node.setValue(e.getValue());
				return true;
			}
		}
		return false;
	}

	protected void putHash(N n)
	{
		table.put(n.getHash(), new TableEntry(getDepthLimit() - n.getDepth(), n.getValue()));
	}

	protected Map<Long, TableEntry> getHashTable()
	{
		return table;
	}

	/**
	 * Gets the evaluator which is currently used to assess the quality of a
	 * state at the depth limit.
	 */
	@Override
	public Evaluator<Double> getEvaluator()
	{
		return searchStrategy.getEvaluator();
	}

	/**
	 * Sets the evaluator which is used to assess the quality of a state at the
	 * depth limit.
	 */
	@Override
	public void setEvaluator(Evaluator<Double> eval)
	{
		searchStrategy.setEvaluator(eval);
	}

	/**
	 * @see nl.maastrichtuniversity.dke.libreason.def.treesearch.DepthLimitedSearch#getDepthLimit()
	 */
	@Override
	public int getDepthLimit()
	{
		return searchStrategy.getDepthLimit();
	}

	/**
	 * @see nl.maastrichtuniversity.dke.libreason.def.treesearch.DepthLimitedSearch#getDepthLimit()
	 */
	@Override
	public void setDepthLimit(int limit)
	{
		searchStrategy.setDepthLimit(limit);
	}

	@Override
	public void initSearch(N startNode)
	{
		searchStrategy.initSearch(startNode);
	}

}
