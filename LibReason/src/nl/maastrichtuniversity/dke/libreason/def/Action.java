package nl.maastrichtuniversity.dke.libreason.def;

import java.io.Serializable;

/**
 * The Action class represents a possible action in the search space that leads
 * from one state to another.
 * The Action class is not intended to be directly instantiated, therefore it is
 * abstract. To use it, derive an action class suited for your domain from it.
 * 
 * @author Daniel Mescheder
 * 
 */
public abstract class Action implements Serializable
{
	private static final long serialVersionUID = -8835650567166127605L;
	// Optional: the action can have a name in order to identify it.
	private String name;
	// The cost to perform a particular action. By default one.
	private double cost;

	/**
	 * Default constructor. Sets the name of the action to the default value of
	 * "action" and the cost to the default value of one.
	 */
	public Action()
	{
		this("action");
	}

	/**
	 * Constructor. Sets the name of the action to the given value and the cost
	 * to the default value of one.
	 * 
	 * @param name
	 *            The name of the action with which it can be identified.
	 */
	public Action(String name)
	{
		setName(name);
		setCost(1);
	}

	/**
	 * Accesses the name of the action.
	 * 
	 * @return the name used for identification of this action.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Prints out the action in a custom format.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return name + "[" + cost + "]";
	}

	/**
	 * Set the name of the action to a new value. The name can be used to
	 * identify the action but is not important for the search process.
	 * 
	 * @param name
	 *            the new name of the action
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Sets the cost value of this action. The cost is what it takes to perform
	 * this action. Note that a value of 0 means, that an agent does not care
	 * about how many actions he takes to the goal as they are all for free
	 * anyway. By default the cost of an action is one.
	 * 
	 * @param cost
	 *            the new cost value.
	 */
	public void setCost(double cost)
	{
		this.cost = cost;
	}

	/**
	 * Returns the cost of the current action.
	 * 
	 * @return
	 */
	public double getCost()
	{
		return cost;
	}

}
