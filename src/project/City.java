package project;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import static java.util.stream.Collectors.*;

public class City {
	

	public Stream<Street> getStreets() {
		return  Stream.of(streetsL);
	}
	
	public Stream<Square> getSquares(){
		return Stream.of(squaresL);
	}

	private Street[] streetsL;
	private Square[] squaresL;
	
	public City(List<Street> sts, List<Square> sqs) {
		streetsL = sts.toArray(new Street[sts.size()]);
		squaresL = sqs.toArray(new Square[sqs.size()]);
	}


	/**
	 * Adjacency lists
	 * 
	 * <pre>
	 * Collectors.toMap does 3 things with 3 params:
	 * 1 - map the key of the resulting map: Street -> Street.square1
	 * 2 - map the value of the resulting map: Street -> List<Street.square2>
	 * 	(we make it a list because of the merging operation)
	 * 3 - merging rule : if two keys in the map are identical
	 *  then we merge the value according to this rule: 
	 *  (l1, l2) -> return l1 concat l2;
	 * </pre>
	 * 
	 * @return a key value map of Square -> List<Square>
	 */
	Map<Square, Stream<Square>> adjacentSquare() {
		return getStreets().collect(toMap(
				st -> st.sq1, 									// key mapping
				st -> Stream.of(st.getSq2()), 					// value mapping 
				(list1, list2) -> Stream.concat(list1, list2) 	// merging
				));
	}

	/**
	 * Adjacency lists of street
	 * 
	 * <pre>
	 * Same as adjacentSquare
	 * </pre>
	 * 
	 * @return a key value map of Square -> List<Street>
	 */
	Map<Square, List<Street>> adjacentStreet() {
		return getStreets().collect(
				toMap(
						st -> st.sq1, 
						st -> Stream.of(st).collect(toList()), 
						(list1, list2) -> Stream.concat(list1.stream(),list2.stream()).collect(toList())
						)
				);
	}

	/**
	 * print a dot representation of the graph for visualization
	 */
	String toDot(boolean printStreetName, boolean printSquareName, boolean printMark, boolean printDoubleArrow,
			String fileOut) {
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
			square += "\"];";
			return square;
		}).collect(joining("\n"));

		// -------- Edges
		res += "\n\n";
		res += getStreets()

				.map(st -> {
					String street = "\t" + st.sq1.cleanName() + " -> " + st.sq2.cleanName();
					street += " [label=\"";
					if (printMark)
						street += st.mark == "" ? "" : (st.mark + "\\n");
					if (printStreetName)
						street += st.name;
					street += "\"];";
					return street;
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

	/**
	 * Streets are un-oriented edges as we assume we can drive them both way.
	 * yet they have square1 and square2 members making the graph directed. in
	 * order to truly have bi-directional Edges we may need the reverse edges.
	 * foreach [square1,square2,streetName] we produce an additional
	 * [square2,square1,streetName]
	 */
	void addReverseEdges() {
		
		List<Street> l = Stream.concat(getStreets(), getStreets().map(st -> new Street(st.sq2, st.sq1, st.name)))
				.collect(toList());
		streetsL = l.toArray(new Street[l.size()]);
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
		long impairs = nbDegreImpair();
		return impairs == 0 || impairs == 2;
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
	

}
