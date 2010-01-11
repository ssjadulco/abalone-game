package nl.maastrichtuniversity.dke.libreason.impl.treesearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import nl.maastrichtuniversity.dke.libreason.def.Action;
import nl.maastrichtuniversity.dke.libreason.def.SearchState;
import nl.maastrichtuniversity.dke.libreason.def.treesearch.AlphaBetaNode;
import nl.maastrichtuniversity.dke.libreason.def.treesearch.MinimaxNode;
import nl.maastrichtuniversity.dke.libreason.def.treesearch.SearchNode;

/**
 * A helper class that implements the basic features of a typical minimax node
 * including capabilities for alpha beta pruning.
 * 
 * @author Daniel Mescheder
 */
public abstract class AbstractMinimaxNode extends AbstractSearchNode implements MinimaxNode, AlphaBetaNode
{
	private static final long serialVersionUID = 2645178826744705933L;
	private double value, alpha, beta;

	public AbstractMinimaxNode(SearchState s, SearchNode parent, Action a)
	{
		super(s, parent, a);
	}

	public AbstractMinimaxNode(SearchState s)
	{
		super(s);
	}

	@Override
	public double getValue()
	{
		return value;
	}

	@Override
	public void setValue(double val)
	{
		this.value = val;
	}

	@Override
	public double getAlpha()
	{
		return alpha;
	}

	@Override
	public double getBeta()
	{
		return beta;
	}

	@Override
	public void setAlpha(double val)
	{
		this.alpha = val;
	}

	@Override
	public void setBeta(double val)
	{
		this.beta = val;
	}

}
