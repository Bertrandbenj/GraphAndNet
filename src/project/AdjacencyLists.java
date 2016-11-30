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
		AdjacencyLists res = (AdjacencyLists) this.clone();
		
		//System.out.println("AVANT"+this.get(st.getSq1())+" "+res);
		res.get(st.getSq1()).remove(st);
		if(res.get(st.sq1).size()==0)
			res.remove(st.sq1);
		//System.out.println("APRES"+this.get(st.getSq1()));		
		return res;
	}

}
