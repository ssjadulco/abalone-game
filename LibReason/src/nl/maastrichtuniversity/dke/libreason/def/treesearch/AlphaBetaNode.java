package nl.maastrichtuniversity.dke.libreason.def.treesearch;

/**
 * An alpha-beta node is a special case of a minimax node which provides
 * additional information about the best value for max (alpha) and the best
 * value for min (beta) along its' path. These values can be used for pruning.
 * 
 * The alpha-beta bookkeeping is expected to be done in the corresponding search
 * class. If you implement this class, you just need to provide the getters and
 * setters!
 * 
 * @author Daniel Mescheder
 * 
 */
public interface AlphaBetaNode extends MinimaxNode
{
	/**
	 * Beta is the best value for min along this node's path.
	 * 
	 * @param val
	 *            the new beta value
	 */
	public void setBeta(double val);

	/**
	 * 
	 * Beta is the best value for min along this node's path.
	 * 
	 * @return the value of beta
	 */
	public double getBeta();

	/**
	 * 
	 * Alpha is the best value for min along this node's path.
	 * 
	 * @param val
	 *            the new alpha value
	 */
	public void setAlpha(double val);

	/**
	 * 
	 * Alpha is the best value for min along this node's path.
	 * 
	 * @return the value of alpha
	 */
	public double getAlpha();
}
