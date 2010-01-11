package nl.maastrichtuniversity.dke.libreason.def.treesearch;

/**
 * An interface for a node in a minimax tree. The most important method such a
 * node provides is the expand method inherited from the {@link SearchNode}
 * interface. This child interface adds an additional value property to the node
 * which represents the quality of a node in the minimax search tree.
 * 
 * @author Daniel Mescheder
 * @see SearchNode
 */
public interface MinimaxNode extends SearchNode
{
	/**
	 * Setter for the value property. The value of a node is an estimate of
	 * it's quality in the search tree.
	 * @param val the new value.
	 */
	public void setValue(double val);
	/**
	 * Getter for the value property. The value of a node is an estimate of
	 * it's quality in the search tree.
	 * @returns the value
	 */
	public double getValue();
}
