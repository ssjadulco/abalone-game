package abalone.gui;

import java.util.HashMap;
import java.util.List;

import abalone.adt.KeyValuePair;
import abalone.gamestate.GameState;
import abalone.model.*;


import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QBrush;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QGraphicsScene;
import com.trolltech.qt.gui.QGraphicsView;
import com.trolltech.qt.gui.QLinearGradient;

/**
 * A widget that draws an abalone board corresponding to a
 * GameState.
 *
 */
public class BoardWidget extends QGraphicsView
{
	public Signal1<Move> move = new Signal1<Move>();
	
	// The game state that is being watched
	private GameState state;
	// The count of activated marbles
	private int activeCount = 0;
	// The marble radius
	private final static double r = 15;
	
	private MarbleLine marLine;

	/**
	 * Creates a new Board Widget
	 * The board that is drawn is taken from the state
	 * @param state the relevant game state
	 */
	public BoardWidget(GameState state)
	{
		this.state = state;
		
		// We initialize colors for all the participating players
		// There might be a nicer way for this than the static method
		GameNodeEllipse.playerColors = new HashMap<Player, QColor>();
		GameNodeEllipse.playerColors.put(state.getPlayers().get(0), QColor.red);
		GameNodeEllipse.playerColors.put(state.getPlayers().get(1), QColor.blue);
		
		// get the scene
		QGraphicsScene scene = prepareScene();
		// draw the background
		QLinearGradient gradient = new QLinearGradient(0, 1000, 0, 0);
		QBrush brush = new QBrush(gradient);
		setBackgroundBrush(brush);

		// we don't want no scrollbars, dude!
		setHorizontalScrollBarPolicy(Qt.ScrollBarPolicy.ScrollBarAlwaysOff);
		setVerticalScrollBarPolicy(Qt.ScrollBarPolicy.ScrollBarAlwaysOff);
		
		// tell the widget to use the scene
		this.setScene(scene);
		
		// we initialize with an autogenerated size (which currently still sucks)
		//setBaseSize(sizeHint());
		
		marLine = new MarbleLine();
	}
	
	public void updateBoard()
	{
		this.setScene(prepareScene());
	}

	/**
	 * Prepare the scene
	 * a.k.a. do the graphical stuff 
	 * @return a scene that contains the nice looking board
	 */
	protected QGraphicsScene prepareScene()
	{
		// Initialize some objects
		QGraphicsScene scene = new QGraphicsScene();
		Board board = state.getBoard();
		QPointF center = new QPointF(this.width() / 2, this.height() / 2);
		
		// We take a path from the board
		// It might be possible to use a different one to
		// change the orientation
		List<KeyValuePair<Direction, Node>> path = board.getEquiPaths().get(0);
		for (KeyValuePair<Direction, Node> step : path)
		{
			// iterate through all the nodes on the path
			
			if (Direction.RIGHT.equals(step.getKey()))
			{
				center.setX(center.x() + 2 * r);
			}
			else if (Direction.LEFT.equals(step.getKey()))
			{
				center.setX(center.x() - 2 * r);
			}
			else if (Direction.UPPER_LEFT.equals(step.getKey()))
			{
				center.setY(center.y() - Math.sqrt(3) * r);
				center.setX(center.x() - r);
			}
			else if (Direction.UPPER_RIGHT.equals(step.getKey()))
			{
				center.setY(center.y() - Math.sqrt(3) * r);
				center.setX(center.x() + r);
			}
			else if (Direction.DOWN_LEFT.equals(step.getKey()))
			{
				center.setY(center.y() + Math.sqrt(3) * r);
				center.setX(center.x() - r);
			}
			else if (Direction.DOWN_RIGHT.equals(step.getKey()))
			{
				center.setY(center.y() + Math.sqrt(3) * r);
				center.setX(center.x() + r);
			}

			GameNodeEllipse ellipse = new GameNodeEllipse(step.getValue(),center.x() - r, center.y() - r, 2 * r, 2 * r);
			ellipse.clicked.connect(this, "nodeClicked(GameNodeEllipse)");
			scene.addItem(ellipse);

		}
		
		// We now want to draw some arrows
		double angle = 0;
		
		for(Direction d : Direction.LEFT)
		{
			// Iterate through all directions
			QPointF p = new QPointF(this.width() / 2.0-Math.cos(angle)*11.0*r,this.height() / 2.0-Math.sin(angle)*11.0*r);
			
			// the ellipses are just placeholders for the arrows
			Arrow marker = new Arrow(p.x(),p.y(),angle,d);
			marker.clicked.connect(this, "arrowClicked(Direction)");
			scene.addItem(marker);
			
			// continue further around the board
			angle+=Math.PI/3.0;
		}

		return scene;
	}
	
	/**
	 * "Slot" (Qt-Slang for "Event handler") for GameNodeEllipse click events
	 * @param ellipse the ellipse that raised the event
	 */
	public void nodeClicked(GameNodeEllipse ellipse)
	{
		if(ellipse.isActivated())
		{
			if(marLine.checkRemove(ellipse.getNode()))
			{
				ellipse.deactivate();
			}
		}
		else
		{
			if(!state.getCurrentPlayer().equals(ellipse.getNode().getMarbleOwner()))
			{
				// We also don't want to select marlbes
				// that the current player does not own
				return;
			}
			if(marLine.checkAdd(ellipse.getNode()))
			{
				ellipse.activate();
			}
		}
	}
	
	public void arrowClicked(Direction direction)
	{
		Move set = new Move();
		set.setMarbleLine(marLine);
		set.setDirection(direction);
		move.emit(set);
	}

	public static double getRadius()
	{
		return r;
	}
}
