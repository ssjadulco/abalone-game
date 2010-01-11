package nl.maastrichtuniversity.dke.libreason.def.treesearch;

import java.util.List;
import java.util.Queue;

import nl.maastrichtuniversity.dke.libreason.def.Action;
import nl.maastrichtuniversity.dke.libreason.def.SearchState;



public interface SearchNode
{

	/**
	 * Gets the search state this node refers to
	 * 
	 * @return a SearchState object
	 */
	public SearchState getState();

	/**
	 * Expands this node. The return value is a queue of nodes that can be
	 * reached from this node by taking appropriate actions.
	 * The implementation of the queue determines the search strategy.
	 * 
	 * @return an Queue of SearchNode objects which are children of this
	 *         node.
	 */
	public Queue<SearchNode> expand();

	/**
	 * Gets the price to get from the start node to this node
	 * 
	 * @return a double representing the price
	 */
	public double getPathCost();

	/**
	 * Generates a list of actions that lead to this node. This method can be
	 * used to trace the path to a goal node after a search was finished.
	 * 
	 * @return an ArrayList of Action objects
	 */
	public List<Action> getPath();

	public Action getAction();

	public SearchNode getParent();

	public int getDepth();

}