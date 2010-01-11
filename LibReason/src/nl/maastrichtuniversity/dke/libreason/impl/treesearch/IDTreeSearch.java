package nl.maastrichtuniversity.dke.libreason.impl.treesearch;

import java.io.IOException;

import nl.maastrichtuniversity.dke.libreason.def.SearchProblem;
import nl.maastrichtuniversity.dke.libreason.def.treesearch.DepthLimitedSearch;
import nl.maastrichtuniversity.dke.libreason.def.treesearch.IterativeDeepeningSearch;
import nl.maastrichtuniversity.dke.libreason.def.treesearch.SearchNode;

/**
 * A class to execute an iterative deepening search given a depth limited search
 * strategy. The search stops when the time runs out.
 * 
 * @author Daniel Mescheder
 * 
 */
public class IDTreeSearch<N extends SearchNode> implements IterativeDeepeningSearch<N>
{
	
	private class Searcher implements Runnable
	{
		private N result = null;
		private N node;
		
		public Searcher(N node)
		{
			this.node = node;
		}
		
		@Override
		public void run()
		{
			boolean keepRunning = true;
			searchStrategy.setDepthLimit(0);
			while (keepRunning)
			{
				try
				{
					searchStrategy.setDepthLimit(searchStrategy.getDepthLimit() + 1);
					result = searchStrategy.search(node);
				}
				catch (InterruptedException e)
				{
					keepRunning = false;
				}
			}				
		}
		
		public N getResult()
		{
			return result;
		}
	}
		
	// The point at which the search is canceled
	private long timeLimit;
	// The underlying (depth limited) search strategy which is executed
	private DepthLimitedSearch<N> searchStrategy;

	/**
	 * Creates a new Iterative Deepening Treesearch. You have to provide an
	 * underlying depth limited search strategy. The problem description and
	 * domain knowledge (if available) will be taken from this referenced search
	 * class.
	 * Furthermore you have to provide a time limit at which this search will be
	 * canceled.
	 * 
	 * @param searchStrategy
	 * @param timeLimit
	 */
	public IDTreeSearch(DepthLimitedSearch<N> searchStrategy, long timeLimit)
	{
		this.searchStrategy = searchStrategy;
		this.timeLimit = timeLimit;
	}

	/**
	 * Executes the search starting from a given start node. During the search,
	 * the underlying search strategy will be called iteratively.
	 * 
	 * @param node
	 *            the startnode of the search
	 * @return the goal node or null if nothing was found
	 */
	@Override
	public N search(N node) throws InterruptedException
	{
		// Initialize the searcher runnable with the start node
		Searcher searcher = new Searcher(node);	
		// Create a new thread and let the searcher run in that thread
		Thread searchThread = new Thread(searcher);
		searchThread.start();
		// Wait till the time has elapsed and then interrupt the search
		Thread.sleep(timeLimit);
		searchThread.interrupt();
		
		
		return searcher.getResult();
	}

	@Override
	public long getTimeLimit()
	{
		return this.timeLimit;
	}

	@Override
	public void setTimeLimit(long time)
	{
		this.timeLimit = time;
	}

	@Override
	public DepthLimitedSearch<N> getSearchStrategy()
	{
		return searchStrategy;
	}

	@Override
	public void setSearchStrategy(DepthLimitedSearch<N> search)
	{
		this.searchStrategy = search;
	}

	@Override
	public SearchProblem getProblem()
	{
		return searchStrategy.getProblem();
	}
}
