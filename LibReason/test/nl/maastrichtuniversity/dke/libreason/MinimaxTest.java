package nl.maastrichtuniversity.dke.libreason;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import nl.maastrichtuniversity.dke.libreason.def.Action;
import nl.maastrichtuniversity.dke.libreason.def.SearchState;
import nl.maastrichtuniversity.dke.libreason.def.hashing.Hashable;
import nl.maastrichtuniversity.dke.libreason.def.treesearch.AbstractMinimaxSearch;
import nl.maastrichtuniversity.dke.libreason.def.treesearch.IterativeDeepeningSearch;
import nl.maastrichtuniversity.dke.libreason.def.treesearch.MinimaxProblem;
import nl.maastrichtuniversity.dke.libreason.def.treesearch.SearchNode;
import nl.maastrichtuniversity.dke.libreason.impl.treesearch.AbstractMinimaxNode;
import nl.maastrichtuniversity.dke.libreason.impl.treesearch.AlphaBetaSearch;
import nl.maastrichtuniversity.dke.libreason.impl.treesearch.DLMinimax;
import nl.maastrichtuniversity.dke.libreason.impl.treesearch.HashingMinimaxSearch;
import nl.maastrichtuniversity.dke.libreason.impl.treesearch.IDTreeSearch;
import nl.maastrichtuniversity.dke.libreason.impl.treesearch.queues.DepthFirstQueue;

import org.junit.Before;
import org.junit.Test;

public class MinimaxTest
{
	private class TicTacToeState implements SearchState, Hashable
	{
		private static final long serialVersionUID = 5571562440467675249L;
		public int[][] field;
		public int currPlayer;
		public int winner = -1;
		public int emptyFields = 9;
		public long hash = 0;

		private TicTacToeState()
		{
			field = new int[3][3];
			for (int i = 0; i < 3; i++)
			{
				for (int j = 0; j < 3; j++)
				{
					field[i][j] = -1;
				}
			}
			currPlayer = 0;
		}

		@Override
		public boolean equalState(SearchState state)
		{
			return state.equals(this);
		}

		@Override
		public TicTacToeState clone()
		{
			TicTacToeState s2 = new TicTacToeState();
			s2.field = new int[3][];
			for (int i = 0; i < 3; i++)
			{
				s2.field[i] = Arrays.copyOf(field[i], 3);
			}
			s2.currPlayer = currPlayer;
			s2.winner = winner;
			s2.hash = hash;
			return s2;
		}

		public void apply(SetMarker a)
		{
			hash ^= hashTable[a.x][a.y][field[a.x][a.y] + 1];
			hash ^= hashTable[a.x][a.y][a.player + 1];
			field[a.x][a.y] = a.player;
			emptyFields--;
			currPlayer = (currPlayer + 1) % 2;
		}

		@Override
		public String toString()
		{
			return Arrays.deepToString(field);
		}

		@Override
		public long getHash()
		{
			return hash;
		}

		public void initHash()
		{
			for (int i = 0; i < 3; i++)
			{
				for (int j = 0; j < 3; j++)
				{
					hash ^= hashTable[i][j][field[i][j] + 1];
				}
			}
		}
	}

	private class SetMarker extends Action
	{
		private static final long serialVersionUID = 3054832980273624489L;
		public int x, y, player;

		public SetMarker(int x, int y, int player)
		{
			this.x = x;
			this.y = y;
			this.player = player;
		}
	}

	private class TicTacToeProblem implements MinimaxProblem
	{
		private static final long serialVersionUID = 7343546706051419423L;
		private int startPlayer;

		public TicTacToeProblem(int startPlayer)
		{
			this.startPlayer = startPlayer;
		}

		@Override
		public List<Action> generateActions(SearchState state)
		{
			TicTacToeState s = (TicTacToeState) state;
			List<Action> actions = new LinkedList<Action>();
			for (int i = 0; i < 3; i++)
			{
				for (int j = 0; j < 3; j++)
				{
					if (s.field[i][j] == -1)
					{
						// there's nothing on this field yet,...
						actions.add(new SetMarker(i, j, s.currPlayer));
					}
				}
			}
			return actions;
		}

		@Override
		public boolean goalTest(SearchState state)
		{
			TicTacToeState s = (TicTacToeState) state;
			int p;
			if ((p = check(s, 1, 1, 1, 0)) != -1 || (p = check(s, 1, 1, 0, 1)) != -1 || (p = check(s, 1, 1, 1, 1)) != -1 || (p = check(s, 1, 1, 1, -1)) != -1
					|| (p = check(s, 0, 1, 0, 1)) != -1 || (p = check(s, 1, 0, 1, 0)) != -1 || (p = check(s, 2, 1, 0, 1)) != -1
					|| (p = check(s, 1, 2, 1, 0)) != -1)
			{
				s.winner = p;
				return true;
			}
			else if (s.emptyFields == 0)
			{
				return true;
			}
			return false;

		}

		public int check(TicTacToeState s, int x, int y, int dx, int dy)
		{
			int p = s.field[x][y];

			if (p == -1)
			{
				return -1;
			}
			else if (p == s.field[x + dx][y + dy] && p == s.field[x - dx][y - dy])
			{
				return p;
			}
			return -1;
		}

		@Override
		public int getFinalStateValue(SearchState state)
		{
			TicTacToeState s = (TicTacToeState) state;
			if (s.winner == -1)
			{
				return 0;
			}
			else if (s.winner == startPlayer)
			{
				return 1;
			}
			else
			{
				return -1;
			}
		}

		@Override
		public double repetitionValue()
		{
			return 0;
		}

	}

	private class TicTacToeNode extends AbstractMinimaxNode implements Hashable
	{
		private static final long serialVersionUID = 6445077830952143869L;

		public TicTacToeNode(SearchState s, SearchNode parent, Action a)
		{
			super(s, parent, a);
		}

		public TicTacToeNode(SearchState s)
		{
			super(s);
		}

		@Override
		public Queue<SearchNode> expand()
		{
			TicTacToeState s = (TicTacToeState) getState();
			Queue<SearchNode> successors = new DepthFirstQueue();
			for (Action action : problem.generateActions(getState()))
			{
				TicTacToeState s2 = s.clone();
				SetMarker a = (SetMarker) action;
				s2.apply(a);
				SearchNode newNode = new TicTacToeNode(s2, this, a);
				successors.add(newNode);
			}

			return successors;
		}

		@Override
		public long getHash()
		{
			return ((Hashable) getState()).getHash();
		}

	}

	private TicTacToeProblem problem;
	private TicTacToeState initialState;
	private TicTacToeState winnableState;
	private long[][][] hashTable = new long[3][3][3];
	private Random random = new Random();

	@Before
	public void setUp() throws Exception
	{
		problem = new TicTacToeProblem(0);
		initialState = new TicTacToeState();
		winnableState = new TicTacToeState();

		winnableState.field[0][0] = 0;
		winnableState.field[1][1] = 0;
		winnableState.field[0][2] = 1;
		winnableState.field[2][0] = 1;

		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				for (int k = 0; k < 3; k++)
				{
					hashTable[i][j][k] = random.nextLong();
				}
			}
		}

		initialState.initHash();
		winnableState.initHash();

	}

	@Test
	public void depthLimitedMinimaxTest01() throws InterruptedException
	{
		AbstractMinimaxSearch<TicTacToeNode> search = new DLMinimax<TicTacToeNode>(problem, 9);
		TicTacToeNode initialNode = new TicTacToeNode(initialState);

		TicTacToeNode n = search.search(initialNode);

		assertEquals(n.getValue(), 0.0, 0);
		assertEquals(n.getParent(), initialNode);
	}

	@Test
	public void depthLimitedMinimaxTest02() throws InterruptedException
	{
		AbstractMinimaxSearch<TicTacToeNode> search = new DLMinimax<TicTacToeNode>(problem, 1);
		TicTacToeNode initialNode = new TicTacToeNode(winnableState);

		TicTacToeNode n = search.search(initialNode);

		assertEquals(n.getValue(), 1d, 0);
		assertEquals(n.getParent(), initialNode);
		SetMarker a = (SetMarker) n.getAction();
		assertEquals(a.x, 2);
		assertEquals(a.y, 2);
		assertEquals(a.player, 0);
	}

	@Test
	public void depthLimitedMinimaxTest03() throws InterruptedException
	{
		AbstractMinimaxSearch<TicTacToeNode> search = new DLMinimax<TicTacToeNode>(problem, 5);
		TicTacToeNode initialNode = new TicTacToeNode(winnableState);

		TicTacToeNode n = search.search(initialNode);

		assertEquals(n.getValue(), 1d, 0);
		assertEquals(n.getParent(), initialNode);
	}

	@Test
	public void alphaBetaTest01() throws InterruptedException
	{
		AbstractMinimaxSearch<TicTacToeNode> search = new AlphaBetaSearch<TicTacToeNode>(new DLMinimax<TicTacToeNode>(problem, 9));
		TicTacToeNode initialNode = new TicTacToeNode(initialState);

		TicTacToeNode n = search.search(initialNode);

		assertEquals(n.getValue(), 0.0, 0);
		assertEquals(n.getParent(), initialNode);
	}

	@Test
	public void alphaBetaTest02() throws InterruptedException
	{
		AbstractMinimaxSearch<TicTacToeNode> search = new AlphaBetaSearch<TicTacToeNode>(new DLMinimax<TicTacToeNode>(problem, 1));
		TicTacToeNode initialNode = new TicTacToeNode(winnableState);

		TicTacToeNode n = search.search(initialNode);

		assertEquals(n.getValue(), 1d, 0);
		assertEquals(n.getParent(), initialNode);
		SetMarker a = (SetMarker) n.getAction();
		assertEquals(a.x, 2);
		assertEquals(a.y, 2);
		assertEquals(a.player, 0);
	}

	@Test
	public void alphaBetaTest03() throws InterruptedException
	{
		AbstractMinimaxSearch<TicTacToeNode> search = new AlphaBetaSearch<TicTacToeNode>(new DLMinimax<TicTacToeNode>(problem, 5));
		TicTacToeNode initialNode = new TicTacToeNode(winnableState);

		TicTacToeNode n = search.search(initialNode);

		assertEquals(n.getValue(), 1d, 0);
		assertEquals(n.getParent(), initialNode);
	}

	@Test
	public void hashingTest01() throws InterruptedException
	{
		AbstractMinimaxSearch<TicTacToeNode> search = new HashingMinimaxSearch<TicTacToeNode>(new DLMinimax<TicTacToeNode>(problem, 9));
		TicTacToeNode initialNode = new TicTacToeNode(initialState);

		TicTacToeNode n = search.search(initialNode);

		assertEquals(n.getValue(), 0.0, 0);
		assertEquals(n.getParent(), initialNode);
	}

	@Test
	public void hashingTest02() throws InterruptedException
	{
		AbstractMinimaxSearch<TicTacToeNode> search = new HashingMinimaxSearch<TicTacToeNode>(new DLMinimax<TicTacToeNode>(problem, 1));
		TicTacToeNode initialNode = new TicTacToeNode(winnableState);

		TicTacToeNode n = search.search(initialNode);

		assertEquals(n.getValue(), 1d, 0);
		assertEquals(n.getParent(), initialNode);
		SetMarker a = (SetMarker) n.getAction();
		assertEquals(a.x, 2);
		assertEquals(a.y, 2);
		assertEquals(a.player, 0);
	}

	@Test
	public void hashingTest03() throws InterruptedException
	{
		AbstractMinimaxSearch<TicTacToeNode> search = new HashingMinimaxSearch<TicTacToeNode>(new DLMinimax<TicTacToeNode>(problem, 5));
		TicTacToeNode initialNode = new TicTacToeNode(winnableState);

		TicTacToeNode n = search.search(initialNode);

		assertEquals(n.getValue(), 1d, 0);
		assertEquals(n.getParent(), initialNode);
	}

	@Test
	public void alphaBetaHashingTest01() throws InterruptedException
	{
		AbstractMinimaxSearch<TicTacToeNode> search = new AlphaBetaSearch<TicTacToeNode>(new HashingMinimaxSearch<TicTacToeNode>(new DLMinimax<TicTacToeNode>(
				problem, 9)));
		TicTacToeNode initialNode = new TicTacToeNode(initialState);

		TicTacToeNode n = search.search(initialNode);

		assertEquals(n.getValue(), 0.0, 0);
		assertEquals(n.getParent(), initialNode);
	}

	@Test
	public void alphaBetaHashingTest02() throws InterruptedException
	{
		AbstractMinimaxSearch<TicTacToeNode> search = new AlphaBetaSearch<TicTacToeNode>(new HashingMinimaxSearch<TicTacToeNode>(new DLMinimax<TicTacToeNode>(
				problem, 1)));
		TicTacToeNode initialNode = new TicTacToeNode(winnableState);
		TicTacToeNode n = search.search(initialNode);

		assertEquals(n.getValue(), 1d, 0);
		assertEquals(n.getParent(), initialNode);
		SetMarker a = (SetMarker) n.getAction();
		assertEquals(a.x, 2);
		assertEquals(a.y, 2);
		assertEquals(a.player, 0);
	}

	@Test
	public void alphaBetaHashingTest03() throws InterruptedException
	{
		AbstractMinimaxSearch<TicTacToeNode> search = new AlphaBetaSearch<TicTacToeNode>(new HashingMinimaxSearch<TicTacToeNode>(new DLMinimax<TicTacToeNode>(
				problem, 9)));

		TicTacToeNode initialNode = new TicTacToeNode(winnableState);

		TicTacToeNode n = search.search(initialNode);

		assertEquals(n.getValue(), 1d, 0);
		assertEquals(n.getParent(), initialNode);
	}

	@Test
	public void hashingAlphaBetaTest01() throws InterruptedException
	{
		AbstractMinimaxSearch<TicTacToeNode> search = new HashingMinimaxSearch<TicTacToeNode>(new AlphaBetaSearch<TicTacToeNode>(new DLMinimax<TicTacToeNode>(
				problem, 9)));
		TicTacToeNode initialNode = new TicTacToeNode(initialState);

		TicTacToeNode n = search.search(initialNode);

		assertEquals(n.getValue(), 0.0, 0);
		assertEquals(n.getParent(), initialNode);
	}

	@Test
	public void iterativeDeepeningTest01() throws InterruptedException
	{
		IterativeDeepeningSearch<TicTacToeNode> search = new IDTreeSearch<TicTacToeNode>(new DLMinimax<TicTacToeNode>(problem, 0), 5000);
		TicTacToeNode initialNode = new TicTacToeNode(initialState);

		TicTacToeNode n = search.search(initialNode);

		assertEquals(n.getValue(), 0.0, 0);
		assertEquals(n.getParent(), initialNode);
	}

	@Test
	public void iterativeDeepeningTest02() throws InterruptedException
	{
		IterativeDeepeningSearch<TicTacToeNode> search = new IDTreeSearch<TicTacToeNode>(new DLMinimax<TicTacToeNode>(problem, 0), 5000);
		TicTacToeNode initialNode = new TicTacToeNode(winnableState);

		TicTacToeNode n = search.search(initialNode);

		assertEquals(n.getValue(), 1d, 0);
		assertEquals(n.getParent(), initialNode);
	}

	@Test
	public void combinedTest01() throws InterruptedException
	{
		IterativeDeepeningSearch<TicTacToeNode> search = new IDTreeSearch<TicTacToeNode>(new HashingMinimaxSearch<TicTacToeNode>(
				new AlphaBetaSearch<TicTacToeNode>(new DLMinimax<TicTacToeNode>(problem, 0))), 5000);
		TicTacToeNode initialNode = new TicTacToeNode(winnableState);

		TicTacToeNode n = search.search(initialNode);

		assertEquals(n.getValue(), 1d, 0);
		assertEquals(n.getParent(), initialNode);
	}

}
