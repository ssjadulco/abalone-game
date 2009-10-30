package abalone.model;

import java.util.HashMap;
import java.util.Map;

/**
 * A node in the board graph
 * 
 */
public class Node
{

	// The list of neighbours of the current node,
	// ordered by direction
	private Map<Direction, Node> neighbourList;
	private String name;
	
	public Node(String name)
	{
		neighbourList = new HashMap<Direction, Node>();
		this.name = name;
	}
	
	public Node()
	{
		this("un");
	}
	
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @param position
	 *            Direction
	 * @param newNode
	 *            the new node
	 */
	public void setNeighbour(Direction position, Node aNode)
	{
		neighbourList.put(position, aNode);
	}

	/**
	 * @param position
	 *            Direction
	 * @return Node, the newly created node
	 */
	public Node addNeighbour(Direction position)
	{
		Node n = new Node();
		neighbourList.put(position, n);
		return n;
	}

	/**
	 * @param position
	 *            Direction
	 */
	public Node getNeighbour(Direction position)
	{
		return neighbourList.get(position);
	}
	
	@Override
	public String toString()
	{
		return name;
	}
}
