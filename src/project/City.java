package project;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import project.City.Node;

import static java.util.stream.Collectors.*;
/**
 * <pre>
 * A City is a graph composed by Streets and Squares 
 * A Street has 2 Square as extremities and cars can drive in both way 
 * 
 * Graph[V,E] == City[Squares,Streets]
 * 
 * The graph is not oriented but it is possible to obtain the oriented graph using 
 * asDirectedStreet : 
 * 	City[Squares,Streets] -> City[Squares,2*Streets]
 * <pre>
 * @author ben
 *
 */
public class City {
	
	public class Node {
		
		Street s;
		String mark;

	}

	private Street[] streetsL;
	private Street[] streetsL2;
	private Square[] squaresL;
	
	public City(List<Street> sts, List<Square> sqs, boolean asDirectedGraph) {
		streetsL = sts.toArray(new Street[sts.size()]);
		squaresL = sqs.toArray(new Square[sqs.size()]);
		if(asDirectedGraph)
		streetsL2 = Stream.of(streetsL)
			.map(st -> new Street(st.sq2, st.sq1, st.name))
			.collect(Collectors.toList())
			.toArray(new Street[streetsL.length]);
	}

	public Stream<Street> singleStreet(){
		List<Street>  other = Arrays.asList(streetsL2);
		return Stream.of(streetsL).map(s->{
			
			Street t = other.stream().filter(s1->s.name==s1.name).collect(toList()).get(0);
			s.mark="" +s.mark+ "\n"+t.mark;
			return s;
		});
		 
	}

	/**
	 * Adjacency lists
	 * 
	 * <pre>
	 * Collectors.toMap does 3 things with 3 params:
	 *"st -> st.sq1" : 
	 *	map the key of the resulting map
	 *
	 *"street -> Stream.of(street.sq2)" : 
	 *	map the value of the resulting map. same as before 
	 * 	but as a list because of the merging operation
	 * 
	 *"(l1, l2) -> Stream.concat(l1, l2)" :
	 * 	merging rule if two keys in the map are identical 
	 *  
	 * Complexity :the first step is a mapping E -> E
	 *  	the main complexity here is the one of the "aggregate by key"
	 *  	which depend on the implementation used. 
	 *  
	 * Not that this operation can be parallelized
	 * </pre>
	 * 
	 * @return a key value map of Square -> List of Square
	 */
	Map<Square, Stream<Square>> adjacentSquare() {
		return getStreets().collect(
				toMap(
						steet -> steet.sq1, 					// key mapping
						street -> Stream.of(street.sq2), 	// value mapping 
						(l1, l2) -> Stream.concat(l1, l2) 		// merging
				));
	}

	/**
	 * Adjacency lists of street
	 * 
	 * <pre>
	 * Same as adjacentSquare
	 * </pre>
	 * 
	 * @return a key value map of Square -> List of Street
	 */
	Map<Square, List<Street>> adjacentStreet() {
		return getStreets().collect(
				toMap(
						st -> st.sq1, 
						st -> Stream.of(st).collect(toList()), 
						(list1, list2) -> Stream.concat(list1.stream(),list2.stream()).collect(toList())
				));
	}
	


	/**
	 * <pre>
	 * write Doc/fileOut.dot, a representation of the graph in dot format 
	 * execute "bash -c /usr/bin/dot -Tpng Doc/fileOut.dot -o Doc/fileOut.png"
	 * <pre>
	 * @param printStreetName
	 * @param printSquareName
	 * @param printMark
	 * @param printDoubleArrow
	 * @param fileOut
	 * @return
	 */
	String toDot(boolean allArcs, boolean printStreetName, boolean printSquareName, boolean printMark, boolean printDoubleArrow,
			String fileOut, Square current) {
		
		String res = "digraph {\n";
		if (printDoubleArrow)
			res += "edge [dir=\"both\"];\n";
		// -------- Nodes description
		res += getSquares().map(sq -> {
			String square = "\t" + sq.cleanName() + " [label=\"";
			if (printMark)
				square += sq.mark == "" ? "" : (sq.mark + "\\n");
			if (printSquareName)
				square += sq.name;
			square += "\"";
			if(sq.equals(current)){
				square += ",color=\"red\"";
			}
			square += "];";
			return square;
		}).collect(joining("\n"));

		// -------- Edges
		res += "\n\n";

		res += ((allArcs)?getStreets():singleStreet())
				.map(st -> {
					String edge = "\t" + st.sq1.cleanName() + " -> " + st.sq2.cleanName();
					edge += " [label=\"";
					if (printMark)
						edge += st.mark;
					if (printStreetName)
						edge += st.name;
					edge += st.pos+ "\"];";
					return edge;
				}).collect(joining("\n"));
		res += "\n}";
		System.out.println("Creating output graphviz file: Doc/" + fileOut + ".dot");

		// output the dot File 
		try (PrintWriter out = new PrintWriter("Doc/" + fileOut + ".dot")) {

			out.println(res);
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		// fail safe attempt to use dot and produce an image output 
		Project.run("/usr/bin/dot -Tpng Doc/" + fileOut + ".dot -o Doc/" + fileOut + ".png");
		return res;
	}

	/**
	 * Java 8 style
	 * 
	 * <pre>
	 * 1 - flatMap : return the collection of 
	 * each node's occurrence by iterating each vector
	 * [Street[Square,Square,name],Street[...],...]
	 * 		-> [[Square,Square],[Square,Square]] (map)
	 * 		-> [Square,Square,Square,Square]     (flattening)
	 * 2 - Collect and group By
	 * [Square,Square,Square]
	 * 		-> [N1,1],[N1,1],[N2,1]  associate 1 to each node
	 * 		-> [N1,2],[N2,1]  aggregate by key
	 * 4 - entrySet - get as a list of key -> value
	 * 5 - filter - filter only odd numbers
	 * 6 - count - count and return
	 * </pre>
	 * 
	 * <pre>
	 * The collect is equivalent to 
	 *  .collect(Collectors.groupingBy(Function.identity(), Collectors.summingInt(e -> 1)));
	 * </pre>
	 * 
	 * @return the number of Square having an odd number of adjacent street
	 */
	long nbDegreImpair() {	
		return getStreets().flatMap(street -> Stream.of( street.sq1, street.sq2 ))
				.collect(toMap(s -> s, s -> 1, Integer::sum))
				.entrySet().stream()
				.filter(ent -> (ent.getValue().longValue() % 2) == 1)
				.count();
	}

	Square startingNode() {
		if (isEulerien()) {
			return getSquares().findAny().get();
		} else {
			return getStreets()
					.flatMap(street -> Stream.of( street.sq1, street.sq2 ))
					.collect(toMap(s -> s, s -> 1, Integer::sum))
					.entrySet()
					.stream()
					.filter(ent -> (ent.getValue().longValue() % 2) == 1)
					.map(ent -> ent.getKey())
					.findFirst()
					.get()
				;
		}
	}

	boolean isEulerien() {
		long odd = nbDegreImpair();
		boolean res = odd == 0 || odd == 2; 
		System.out.println("the Graph is Eulerien : "+res);
		return res;
	}

	/**
	 * classic java style 
	 * @return the number of edges having odd incidence
	 */
	long nbDegreImpair2() {
		HashMap<Square, Long> map = new HashMap<Square, Long>((int) getSquares().count());
		
		getStreets().forEach(s -> {
			if (map.containsKey(s.sq1)) {
				map.put(s.sq1, map.get(s.sq1).longValue() + 1);
			} else {
				map.put(s.sq1, 1L);
			}

			if (map.containsKey(s.sq2)) {
				map.put(s.sq2, map.get(s.sq2).longValue() + 1);
			} else {
				map.put(s.sq2, 1L);
			}
		});
		
		return map.entrySet().stream().filter(ent -> (ent.getValue().longValue() % 2) == 1).count();
	}
	
	public Stream<Street> getStreets() {
		return  Stream.concat(Stream.of(streetsL),Stream.of(streetsL2));
	}
	
	/**
	 * Streets are un-oriented edges as we assume we can drive them both way.
	 * yet they have square1 and square2 members making the graph directed. in
	 * order to truly have bi-directional Edges we may need the reverse edges.
	 * foreach [square1,square2,streetName] we produce an additional
	 * [square2,square1,streetName]
	 * 
	 * Graph(V,E) -> Graph(V,2*E)
	 * undirected -> directed graph
	 * 
	 * @return a collection of Street 
	 */
	private Stream<Street> asDirectedStreet(){
		return Stream.concat(getStreets(), getStreets().map(st -> new Street(st.sq2, st.sq1, st.name)));
	}
	
	public Stream<Square> getSquares(){
		return Stream.of(squaresL);
	}

}
