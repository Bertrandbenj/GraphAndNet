package project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static java.util.stream.Collectors.toList;
/**
 * 
 * To find a minimum Chinese postman route we must walk
 * along each edge at least once and in addition we must also
 * walk along the least pairings of odd vertices on one extra
 * occasion.
 * An algorithm for finding an optimal Chinese postman route is:
 * 
 **/
public class GogolXL implements Car{
	
	public class Pair {
		Square[] sqs;
		public Pair(Square ... s){
			sqs = s;
		}
		
		public Square _1(){ return sqs[0]; }
		
		public Square _2(){ return sqs[1]; }
		
		@Override
		public boolean equals(Object o){
			if(o==null)
				return false;
			Pair p = (Pair) o;
			return sqs[0].equals(p.sqs[0]) && sqs[1].equals(p.sqs[1]) || 
					sqs[1].equals(p.sqs[0]) && sqs[0].equals(p.sqs[1]);
		}
	}

	
	public GogolXL(){
		
	}
	static City CITY;
	
	
	@Override
	public void driveThrough(City c, Square startingPoint, String file) {
		System.out.println(this.getClass().getName()+" driveThrough");
		CITY=c;
		CITY.toDot(true, true, true, false, false, "GogolXL", null);

		List<Path> pairings = 
		
/**
 * Step 1 : List all odd vertices.
 */
			CITY.oddVertices()
			
/**
 * Step 2 : List all possible pairings of odd vertices.
 */
			.flatMap(s1 -> CITY.oddVertices() 	// take another stream of the same list 
								.filter(s2 -> !s2.equals(s1)) 	// remove duplicate edge
								.map(s2-> new Pair(s2,s1)))		// map each element to the other
			.distinct()		 // we remove duplicate using equals function of Pair
			
			
/**
 * Step 3 : For each pairing find the edges that connect the vertices with the minimum weight.
 * 
 * Pair to Path transformation 
 */
			.map( pair -> shortestPath(pair._1(), pair._2())  )
			
/**
 * Step 4 : Find the pairings such that the sum of the weights is minimised.
 * 
 */
			// sorted by size, get smaller paths first and hope the accumulation will give us a smaller sum of path 
			// we should consider a better sorting maybe ? 
			// this doesn't give us an optimal solution but at most one single vertice alone (a valid Pairings)
			.sorted( (p1,p2) -> new Integer(p1.size()).compareTo(p2.size()))	
			
			.collect(Collectors.reducing(
							new ArrayList<Path>(), 		// 
							p -> Arrays.asList(p),		// as list for aggregation
							(pathList,p2) -> { 			// mapper's arguments
					 			Path elem = p2.get(0); 	// because its a Stream (since its iterative, the second argument is always the single element's list )
					 			
					 			// Determine if the element can be added (path must not contains its vertices)
					 			boolean isValid	= pathList
					 					.stream()
					 					.noneMatch( p -> 	p.contains(elem.head()) 
					 									|| 	p.contains(elem.tail()) );
					 			System.out.println("Step 4  : adding "+p2+ " isValid?"+isValid+ "   to "+pathList);
					 			
					 			if(isValid){
					 				pathList.add(elem);
					 			}
					 			return pathList;
					 		}));
/**
 * Step 5 : On the original graph add the edges that have been found in Step 4.
 */
		
		CITY.setExtra(pairings.stream() // We convert paths into virtual edges and add it to the city 
								.flatMap(path ->  Stream.of(
										new Street(path.tail(), path.head(), path.toString()),
										new Street(path.head(), path.tail(), path.toString())))
								.collect(toList())
					);


/**
 * Step 6 : The length of an optimal Chinese postman route is the sum of all the edges added to the total found in Step 4.
 * 
 * Step 7 : A route corresponding to this minimum weight can then be easily found
 */
		new GogolL().driveThrough(CITY, CITY.startingNode(), "XLL");
		
	}
	
	
	/**
	 * This shortest path is a Dijkstra implementation it keeps in memory the
	 * last "layer" of nodes visited
	 * 
	 * Note that the collect(Collectors.toList()) appears in the loop, it means
	 * we "materialize" each layers which allows us to test the progress made
	 * 
	 * @param s1
	 *            a Square to visit
	 * @param s2
	 *            another Square to visit
	 * @return the shortest path found between the nodes
	 */
	public Path shortestPath(Square s1, Square s2) {
		Path current = new Path();
		
		List<Path> searchPaths = CITY.adjacentStreet().get(s1)
				.stream()
				.flatMap(st -> children(current, s1))
				.collect(Collectors.toList());
		//System.out.println("searchPath " + searchPaths + "  "+searchPaths.stream().noneMatch(p -> p.head().equals(s2)) );
		
		// Iterate layer per layer and stop when the last Vertice of the path is the one searched
		while (searchPaths.stream().noneMatch(p -> p.head().equals(s2)) ) {
			
			searchPaths = searchPaths
					.stream()
					.flatMap(p -> children(p, null))
					.distinct()			// use Path.equals() to filter on head
					.collect(Collectors.toList());
			//System.out.println("while searchPath " + searchPaths);
			//sleep();
		}

		Path res = searchPaths.stream().filter(p -> p.head().equals(s2)).findAny().get();
		
		System.out.println("Step 3 :  "+res+"  between "+s1.name+" and "+s2.name);
		
		return res;
	}

	/**
	 * Transform one path into all the paths that can be taken from there 
	 * @param path
	 * @param square optional, default  : path.head()
	 * @return All the paths 
	 */
	public  Stream<Path> children(Path path, Square square) {
		Square current = square!=null?square:path.head();
		
		return CITY.adjacentStreet()  		// Map<Square,List<Street>>
					.get(current)			// List<Street> 
					.stream()				// Stream<Street> 
					// 	we filter 
					.filter(st -> !path.contains(st.sq2))
					.map(st -> path.drive(st))
					;
	}

}
