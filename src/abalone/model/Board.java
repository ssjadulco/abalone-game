package abalone.model;

import java.util.LinkedList;
import java.util.List;

/**
 * A class representing a board state in the game of abalone
 *
 * The board is represented as a graph with interconnected nodes.
 */
public class Board
{
	/**
	 * The central node of the board.
	 */
	private Node centralNode;
	private List<Node> nodes;
	
	public Node getCentralNode()
	{
		return centralNode;
	}

	public List<Node> getNodes()
	{
		return nodes;
	}

	public Board()
	{
		centralNode = new Node();
		nodes = new LinkedList<Node>();
		nodes.add(centralNode);
	}



	public void printBoard()
	{
		Node leftNode = furthestNode(Direction.UPPER_LEFT);
		while (leftNode != null)
		{
			Node node = leftNode;
			while (node != null)
			{
				System.out.print("0");
				node=node.getNeighbour(Direction.RIGHT);
			}
			leftNode = (leftNode.getNeighbour(Direction.DOWN_LEFT)==null)?leftNode.getNeighbour(Direction.DOWN_RIGHT):leftNode.getNeighbour(Direction.DOWN_LEFT);
			System.out.println();
		}
	}

	public Node furthestNode(Direction inDirection)
	{
		Node node = centralNode;
		
		while (node.getNeighbour(inDirection) != null)
		{
			node = node.getNeighbour(inDirection);
		}
		return node;
	}
}
