package project;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GogolS implements Gogol{
	public GogolS(){
	}

	/**
	 * The main loop can't be parallelized easily
	 * 
	 * Complexity = 
	 *   Complexity of {@link City#adjacentStreet()}
	 * + O(2*E)
	 * 
	 * <pre>
	 * adjM <- Adjacency Map<Square, List<Street>> {@link City#adjacentStreet()}
	 * current <- startingPoint
	 * do
	 * 		path.add(current) 
	 * 		adjL <- adjM.get(current)
	 * 		street <- adjL.removeFirst
	 * 		path.add(street)
	 * 
	 * 		if adjL is empty then
	 * 			adjM.remove(adjL)
	 * 		fi
	 * 	
	 * 		current <- street.OtherSquare
	 * while adjM not empty
	 * <pre>
	 */
	@Override
	public void driveThrough(City city, Square current, String file) {
		System.out.println("Driving Through the city : \n");
		
		Map<Square, List<Street>> adjM = city.adjacentStreet();
		//System.out.println("adj.size:"+adj.size() );
		
		LinkedList<GraphObj> path = new LinkedList<GraphObj>();
		
		int step=0;
		do{
			path.add(current);
			List<Street> adjL = adjM.get(current);
			Street street = adjL.remove(0);
			path.add(street);
			
			if(adjL.isEmpty()){
				adjM.remove(current);
			}
			current=street.sq2;
			
			System.out.println("\tGoing to "+current.name +" by "+street.name);
			 // used for printing out the graph
			street.mark("step " + step++);
		}while(!adjM.isEmpty());
		//System.out.println(path.stream().map(s->s.mark+" "+s.name).collect(Collectors.joining("\n")) );
		
		city.toDot(false,true, true, true, true, "GogolS_"+file,null);
	}
}
