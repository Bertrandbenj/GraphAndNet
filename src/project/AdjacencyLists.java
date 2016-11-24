package project;

import java.util.HashMap;
import java.util.List;


public class AdjacencyLists extends HashMap<Square, List<Street>>{

	private static final long serialVersionUID = 6320421095052457514L;

	public AdjacencyLists (City c){
		super(c.adjacentStreet());
	}
	
	public AdjacencyLists (AdjacencyLists in){
		super(in);
	}
	
	/**
	 * The resulting adjacency of driving in a street
	 * @param st the street to remove from the mapping 
	 * @return a new Adjacency Map without the given street 
	 */
	public AdjacencyLists driveIn(Street st){
		AdjacencyLists res = new AdjacencyLists(this);
		res.get(st.getSq1()).removeIf(x -> st.name == x.name);
		res.get(st.getSq2()).removeIf(x -> st.name == x.name);
		return res;
	}

}
