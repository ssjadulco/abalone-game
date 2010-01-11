package nl.maastrichtuniversity.dke.libreason;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import nl.maastrichtuniversity.dke.libreason.def.Action;
import nl.maastrichtuniversity.dke.libreason.def.SearchProblem;
import nl.maastrichtuniversity.dke.libreason.def.SearchState;
import nl.maastrichtuniversity.dke.libreason.def.treesearch.IterativeDeepeningSearch;
import nl.maastrichtuniversity.dke.libreason.def.treesearch.SearchNode;
import nl.maastrichtuniversity.dke.libreason.def.treesearch.TreeSearch;
import nl.maastrichtuniversity.dke.libreason.impl.treesearch.AbstractMinimaxNode;
import nl.maastrichtuniversity.dke.libreason.impl.treesearch.DLTreeSearch;
import nl.maastrichtuniversity.dke.libreason.impl.treesearch.IDTreeSearch;
import nl.maastrichtuniversity.dke.libreason.impl.treesearch.queues.DepthFirstQueue;

import org.junit.Before;
import org.junit.Test;

public class TreeSearchTest
{
	private class QueensState implements SearchState
	{
		private static final long serialVersionUID = 5571562440467675249L;
		public List<Integer> queens;

		private QueensState()
		{
		}

		public QueensState(int n)
		{
			this.queens = new ArrayList<Integer>();
		}

		@Override
		public boolean equalState(SearchState state)
		{
			return state.equals(this);
		}

		@Override
		public QueensState clone()
		{
			QueensState s2 = new QueensState();
			s2.queens = new ArrayList<Integer>(this.queens);
			return s2;
		}

		public void apply(AddQueen a)
		{
			queens.add(a.position);
		}

		@Override
		public String toString()
		{
			String ret = "[";
			for (Integer i : queens)
			{
				ret += i + " ";
			}
			return ret + "]";
		}
	}

	private class AddQueen extends Action
	{
		private static final long serialVersionUID = 3054832980273624489L;
		public int position;

		public AddQueen(int position)
		{
			this.position = position;
		}
	}

	private class QueensProblem implements SearchProblem
	{
		private static final long serialVersionUID = 7343546706051419423L;
		public int n;

		public QueensProblem(int n)
		{
			this.n = n;
		}

		@Override
		public List<Action> generateActions(SearchState state)
		{
			QueensState s = (QueensState) state;
			if (s.queens.size() >= n)
			{
				return new ArrayList<Action>();
			}
			List<Action> actions = new ArrayList<Action>();
			for (int i = 0; i < n; i++)
			{
				actions.add(new AddQueen(i));
			}
			return actions;
		}

		@Override
		public boolean goalTest(SearchState state)
		{
			QueensState s = (QueensState) state;
			if (s.queens.size() < n)
			{
				return false;
			}
			else
			{
				for (int i = 0; i < n; i++)
				{
					for (int j = i + 1; j < n; j++)
					{
						if (s.queens.get(i) == s.queens.get(j))
						{
							return false;
							// same row
						}
						if (Math.abs(s.queens.get(i) - s.queens.get(j)) == (j - i))
						{
							return false;
							// diagonal
						}
					}
				}
			}
			// nothing applyed - so it's a solution
			return true;
		}

	}

	private class QueensNode extends AbstractMinimaxNode
	{
		private static final long serialVersionUID = 6445077830952143869L;

		public QueensNode(SearchState s, SearchNode parent, Action a)
		{
			super(s, parent, a);
		}

		public QueensNode(SearchState s)
		{
			super(s);
		}

		@Override
		public Queue<SearchNode> expand()
		{
			QueensState s = (QueensState) getState();
			Queue<SearchNode> successors = new DepthFirstQueue();
			for (Action action : problem.generateActions(getState()))
			{
				QueensState s2 = s.clone();
				AddQueen a = (AddQueen) action;
				s2.apply(a);
				SearchNode newNode = new QueensNode(s2, this, a);
				successors.add(newNode);
			}

			return successors;
		}

	}

	private QueensState initialState;
	private QueensProblem problem;
	private int queens = 5;

	@Before
	public void setUp() throws Exception
	{
		initialState = new QueensState(queens);
		problem = new QueensProblem(queens);
	}

	@Test
	public void depthLimitedSearch() throws InterruptedException
	{
		TreeSearch<QueensNode> search = new DLTreeSearch<QueensNode>(problem, queens);
		QueensNode initialNode = new QueensNode(initialState);
		QueensNode n = search.search(initialNode);

		assertTrue(problem.goalTest(n.getState()));
	}

	@Test
	public void iterativeDeepeningSearch() throws InterruptedException
	{
		// We expect it to find something within 8 sec
		IterativeDeepeningSearch<QueensNode> search = new IDTreeSearch<QueensNode>(new DLTreeSearch<QueensNode>(problem), 5000);
		QueensNode initialNode = new QueensNode(initialState);
		QueensNode n = search.search(initialNode);

		assertTrue(problem.goalTest(n.getState()));
	}

}
