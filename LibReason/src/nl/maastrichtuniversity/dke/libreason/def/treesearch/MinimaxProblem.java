package nl.maastrichtuniversity.dke.libreason.def.treesearch;

import nl.maastrichtuniversity.dke.libreason.def.SearchProblem;
import nl.maastrichtuniversity.dke.libreason.def.SearchState;

/**
 * A search problem in a minimax environment. This environment is characterized
 * by a game like, competitive character with two participants.
 * One of the players tries to maximise the game outcome while the other tries to 
 * minimize it.
 * 
 * @author Daniel Mescheder
 * 
 */
public interface MinimaxProblem extends SearchProblem
{
	/**
	 * Return the value of a given endsituation from max's perspective.
	 * An example would be: one if max wins, negative one if max loses.
	 * @param state the state that is investigated
	 * @return a value indicating the desirability of the state for max.
	 */
	public int getFinalStateValue(SearchState state);

	/**
	 * A special function that indicates how desirable it is for max
	 * to have a never ending game
	 * @return
	 */
	public double repetitionValue();

}
