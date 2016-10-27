package project;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class City {
	private ArrayList<Street> streets = new ArrayList<Street>();

	public ArrayList<Street> getStreets() {
		return streets;
	}

	private ArrayList<Square> squares = new ArrayList<Square>();

	public City(ArrayList<Street> sts, ArrayList<Square> sqs) {
		streets = sts;
		squares = sqs;
	}

	/**
	 * Adjacency lists 
	 * <pre>
	 * Collectors.toMap does 3 things with 3 params:
	 * 1 - map the key of the resulting map: Street -> Street.square1
	 * 2 - map the value of the resulting map: Street -> List<Street.square2>
	 * 	(we make it a list because of the merging operation)
	 * 3 - merging rule : if two keys in the map are identical
	 *  then we merge the value according to this rule: 
	 *  (l1, l2) -> return l1 concat l2;
	 * </pre>
	 * @return a key value map of Square -> List<Square>
	 */
	Map<Square, List<Square>> adjacentSquare() {
		return streets.stream().
				collect(Collectors.toMap(st -> st.sq1
						,st -> Arrays.asList(st.getSq2())
						,(list1, list2) -> Stream.concat(list1.stream(), list2.stream()).collect(Collectors.toList())
				));
	}
	
	/**
	 * Adjacency lists of street
	 * <pre>
	 * Same as adjacentSquare
	 * </pre>
	 * @return a key value map of Square -> List<Street>
	 */
	Map<Square, List<Street>> adjacentStreet() {
		return streets.stream().
				collect(Collectors.toMap(st -> st.sq1
						,st -> Arrays.asList(st)
						,(list1, list2) -> Stream.concat(list1.stream(), list2.stream()).collect(Collectors.toList())
				));
	}

	/**
	 * print a dot representation of the graph for visualization
	 */
	void toDot(boolean printStreetName, boolean printSquareName, boolean printMark, boolean printDoubleArrow) {
		String res = "digraph {\n";
		if(printDoubleArrow)
			res+="edge [dir=\"both\"];\n";
		// -------- Nodes description
		res += squares.stream().map(sq -> {
			String square = "\t" + sq.cleanName() + " [label=\"";
			if(printMark)
				square += sq.mark==""?"":(sq.mark + "\\n");
			if(printSquareName)
				square += sq.name ;
			square +=  "\"];";
			return square;
		}  )
				.collect(Collectors.joining("\n"));

		// -------- Edges
		res += "\n\n";
		res += streets.stream()
				
				.map(st -> {
					String street= "\t" + st.sq1.cleanName() + " -> " + st.sq2.cleanName();
					street += " [label=\"";
					if(printMark)
						street += st.mark==""?"":(st.mark + "\\n");
					if(printStreetName)
						street += st.name ;
					street+= "\"];";
					return street;
				})
				.collect(Collectors.joining("\n"));

		// --------- Reverse Edges (using edge[dir=both])
		// res+="\n";
		// res+=streets.stream()
		// .map(st -> st.sq2.cleanName()+" -> "+st.sq1.cleanName()+";")
		// .collect(Collectors.joining("\n"));
		res += "\n}";
		System.out.println(res);
		try(  PrintWriter out = new PrintWriter( "filename.dot" )  ){
		    out.println( res );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
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

		return streets.stream().flatMap(street -> Stream.of(new Square[] { street.sq1, street.sq2 }))
				.collect(Collectors.toMap(s -> s, s -> 1, Integer::sum)).entrySet().stream()
				.filter(ent -> (ent.getValue().longValue() % 2) == 1).count();
	}

	/**
	 * Streets are un-oriented edges as we assume we can drive them both way.
	 * yet they have square1 and square2 members making the graph directed.
	 * in order to truly have bi-directional Edges we may need the reverse edges.
	 * foreach [square1,square2,streetName] we produce an additional [square2,square1,streetName] 
	 */
	void addReverseEdges() {
		List<Street> reverse = streets
				.stream()
				.map(st -> new Street(st.sq2, st.sq1, st.name))
				.collect(Collectors.toList());
		streets.addAll(reverse);
	}

	Square startingNode() {
		if (isEulerien()) {
			return squares.get(0);
		} else {
			return streets.stream().flatMap(street -> Stream.of(new Square[] { street.sq1, street.sq2 }))
					.collect(Collectors.toMap(s -> s, s -> 1, Integer::sum)).entrySet().stream()
					.filter(ent -> (ent.getValue().longValue() % 2) == 1).findFirst().get().getKey();
		}
	}

	boolean isEulerien() {
		long impairs = nbDegreImpair();
		return impairs == 0 || impairs == 2;
	}

	long nbDegreImpair2() {
		HashMap<Square, Long> map = new HashMap<Square, Long>(squares.size());
		// map.entrySet()
		for (Street s : streets) {
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
		}
		// System.out.println(map.size());
		// System.out.println(map.entrySet().stream().map(s ->
		// s.toString()).collect(Collectors.joining("\n\t")));
		return map.entrySet().stream().filter(ent -> (ent.getValue().longValue() % 2) == 1).count();
	}

}
