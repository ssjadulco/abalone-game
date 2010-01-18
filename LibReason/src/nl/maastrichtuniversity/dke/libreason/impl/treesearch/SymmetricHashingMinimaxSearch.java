package nl.maastrichtuniversity.dke.libreason.impl.treesearch;

import nl.maastrichtuniversity.dke.libreason.def.hashing.SymmetryHashable;
import nl.maastrichtuniversity.dke.libreason.def.treesearch.AbstractMinimaxSearch;
import nl.maastrichtuniversity.dke.libreason.def.treesearch.MinimaxNode;

/**
 * TODO: Documentation
 * 
 * @author Daniel Mescheder
 * 
 */
public class SymmetricHashingMinimaxSearch<N extends MinimaxNode & SymmetryHashable> extends HashingMinimaxSearch<N>
{	
	public SymmetricHashingMinimaxSearch(AbstractMinimaxSearch<N> searchStrategy)
	{
		super(searchStrategy);
	}
	
	@Override
	protected void putHash(N n)
	{
		getHashTable().put(n.getHash(), new TableEntry(getDepthLimit() - n.getDepth(),n.getValue()));
		
		for(long hash : n.getSymmetryHashes())
		{
			getHashTable().put(hash, new TableEntry(getDepthLimit() - n.getDepth(),n.getValue()));
		}
	}
}
