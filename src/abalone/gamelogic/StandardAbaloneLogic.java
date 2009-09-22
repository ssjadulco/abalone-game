package abalone.gamelogic;

import java.util.ArrayList;
import java.util.List;

import abalone.adt.KeyValuePair;
import abalone.gamestate.GameState;
import abalone.model.Board;
import abalone.model.Direction;
import abalone.model.Move;
import abalone.model.Node;
import abalone.model.Player;

public class StandardAbaloneLogic implements GameLogic
{

	@Override
	public Board initBoard()
	{
		Board b = new Board();
		// Create the graph recursively:
		createNode(b, b.getCentralNode(), 4);

		// add equi-paths:
		for (Direction d : Direction.UPPER_LEFT)
		{
			b.addPath(createPath(b.getCentralNode(), d));
		}
		return b;
	}

	private List<KeyValuePair<Direction, Node>> createPath(Node centralNode, Direction startDirection)
	{
		List<KeyValuePair<Direction, Node>> path = new ArrayList<KeyValuePair<Direction, Node>>();

		path.add(new KeyValuePair<Direction, Node>(null, centralNode));
		Direction currentDir = startDirection;
		Node currentNode = centralNode;
		for (int i = 1; i <= 5; i++)
		{
			// Iterate through all the 5 rings of the board
			currentNode.getNeighbour(currentDir);
			path.add(new KeyValuePair<Direction, Node>(currentDir, currentNode));
			currentDir = currentDir.getNextCW();

			for (Direction d : currentDir)
			{
				if (d.equals(startDirection.getNextCW()))
				{
					for (int j = 0; j < i - 1; j++)
					{
						currentNode.getNeighbour(currentDir);
						path.add(new KeyValuePair<Direction, Node>(currentDir, currentNode));
					}
				}
				else
				{
					for (int j = 0; j < i; j++)
					{
						currentNode.getNeighbour(currentDir);
						path.add(new KeyValuePair<Direction, Node>(currentDir, currentNode));
					}
				}
			}

			currentDir = startDirection;

		}

		return path;
	}

	@Override
	public GameState initState(Board board, List<Player> players)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void applyMove(GameState state, Move move)
	{
		for (Node n : move.getNodes())
		{
			// TODO check whether n is in state
			// TODO check whether move is legal

			n.getNeighbour(move.getDirection()).setMarbleOwner(n.getMarbleOwner());
			n.setMarbleOwner(null);
			// TODO this won't really work, because marbles that are moved
			// within one move can overwrite each other
		}
	}

	/**
	 * Recursively adds nodes to the board graph
	 * 
	 * @param node
	 *            the node that is currently handled
	 * @param level
	 *            level-counter counting backwards from the center - recursion
	 *            will terminate when level is zero.
	 */
	private void createNode(Board b, Node node, int level)
	{
		// TODO: this might need to be improved
		if (level <= 0)
		{
			// Terminate recursion
			return;
		}

		Direction startDirection = Direction.UPPER_LEFT;
		for (Direction d : startDirection)
		{
			// We're going clockwise looking into all directions.
			// The direction we're currently looking at is d
			// od is the opposite direction.
			Direction od = d.getOpposite();

			// We look at the nodes neighbour in the investigated direction
			Node neighbour = node.getNeighbour(d);
			if (neighbour == null)
			{
				// The neighbour is null, so it has not been created yet
				// thus, we now create it and add it to the nodes list.
				neighbour = node.addNeighbour(d);
				b.getNodes().add(neighbour);
			}

			// We still have to link the neighbour with the current node...
			neighbour.setNeighbour(od, node);

			// Now we want to look at the nodes next to the investigated
			// neighbour...
			Node prev = node.getNeighbour(d.getNextCCW());
			Node next = node.getNeighbour(d.getNextCW());
			if (prev != null)
			{
				// there is a node next to our neighbour node
				// in counterclockwise direction... we interconnect
				// those two.
				neighbour.setNeighbour(od.getNextCW(), prev);
				prev.setNeighbour(d.getNextCW(), neighbour);
			}
			if (next != null)
			{
				// there is a node next to our neighbour node
				// in clockwise direction... we interconnect
				// those two.
				neighbour.setNeighbour(od.getNextCCW(), next);
				next.setNeighbour(d.getNextCCW(), neighbour);
			}

			// now we go deeper into the recursion tree, further investigating
			// on
			// the neighbour node.
			createNode(b, neighbour, level - 1);
		}
	}

}
