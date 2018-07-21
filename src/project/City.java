package project;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

/**
 * <pre>
 * A City is a graph composed by Streets and Squares A Street has 2 Square as
 * extremities and cars can drive in both way
 * 
 * Graph[V,E] == City[Squares,Streets]
 * 
 * The graph is not oriented but it is possible to obtain the oriented graph
 * using {@link City#oriented()}
 * 
 * </pre>
 * 
 * @author ben
 *
 */
public class City {

	static City singleton;

	/**
	 * The streets objects as parsed from the input file
	 */
	private Street[] streetsL;
	/**
	 * The streets objects representing the opposed arcs of the graph
	 */
	private Street[] streetsL2;

	/**
	 * The list of Squares as parsed from the input file
	 */
	private Square[] squares;

	public City(List<Street> sts, List<Square> sqs) {

		squares = sqs.toArray(new Square[sqs.size()]);

		streetsL = sts.toArray(new Street[sts.size()]);

		// we add the reverse edges in a separate array to distinguish
		streetsL2 = Stream.of(streetsL).map(st -> new Street(st.sq2, st.sq1, st.name)).collect(Collectors.toList())
				.toArray(new Street[streetsL.length]);

		singleton = this;
	}

	public Street reverseEdge(Street t) {

		Street trev = new Street(t.sq2, t.sq1, t.name);
		// System.out.println(t +" "+trev+"\n"+ Stream.of(streetsL).filter(st ->
		// st.name.equals(t.name)).map(x->x.toString()).collect(Collectors.joining("\n")));
		return Stream.of(streetsL2).filter(s -> s.equals(trev)).findFirst().orElse(null);

	}

	/**
	 * give the incidence of each square
	 * 
	 * @return a mapping representing the incidence of each square
	 */
	public Map<Square, Integer> verticesDegree() {
		return unOriented().flatMap(street -> Stream.of(street.sq1, street.sq2))
				.collect(toMap(s -> s, s -> 1, Integer::sum));
	}

	/**
	 * aggregate marking of the two arcs into one street object. used for display
	 * only
	 * 
	 * @return a list of undirected streets
	 */
	public Stream<Street> unOriented() {
		return Stream.of(streetsL);
	}

	BinaryOperator<List<Street>> huhu = (l1, l2) -> Stream.concat(l1.stream(), l2.stream()).collect(toList());

	/**
	 * Adjacency lists of street
	 * 
	 * <pre>
	 * Collectors.toMap does 3 things with 3 params:
	 * 
	 *"st -> st.sq1" : map the key of the resulting map
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
	 * Note that this operation can be parallelized
	 * </pre>
	 * 
	 * 
	 * @return a key value map of Square -> List of Street
	 */
	Map<Square, List<Street>> adjacentStreet() {
		return oriented().collect(Collectors.toMap(//
				Street::getSq1, //
				st -> Stream.of(st).collect(toList()), //
				huhu //
		));
	}

	List<Street> oposingArcs(List<Street> l) {
		return oriented().filter(st -> l.contains(new Street(st.sq2, st.sq1, st.name))).collect(Collectors.toList());
	}

	/**
	 * <pre>
	 * write Doc/fileOut.dot, a representation of the graph in dot format execute
	 * "bash -c /usr/bin/dot -Tpng Doc/fileOut.dot -o Doc/fileOut.png"
	 * 
	 * <pre>
	 * 
	 * @param printStreetName
	 * @param printSquareName
	 * @param printMark
	 * @param printDoubleArrow
	 * @param fileOut
	 * @param path
	 * @return the digraph as a string
	 */
	String toDot(boolean allArcs, boolean printStreetName, boolean printSquareName, boolean printMark,
			boolean printDoubleArrow, String fileOut, Square current, LinkedList<Street> path) {
		if (!Project.printDot)
			return "";

		String res = "digraph {\n";
		if (printDoubleArrow && !allArcs)
			res += "edge [dir=\"both\"];\n";

		// -------- Nodes description
		res += getSquares().map(sq -> sq.toDot(printMark, printSquareName, sq == current)).collect(joining("\n"));
		res += "\n\n";

		// -------- Edges description
		res += ((allArcs) ? oriented() : unOriented()).map(st -> st.toDot(printStreetName, printMark, path))
				.collect(joining("\n"));
		res += "\n}";

		// Output the dot File
		try (PrintWriter out = new PrintWriter("doc/dot/" + fileOut + ".dot")) {
			out.println(res);
			out.close();
			System.out.println("Created output graphviz file: doc/dot/" + fileOut + ".dot");
		} catch (Exception e) {
			e.printStackTrace();
		}

		// fail safe attempt to use dot and produce an image output
		Project.run("/usr/bin/dot -Tpng doc/dot/" + fileOut + ".dot -o doc/png/" + fileOut + ".png");
		return res;
	}

	/**
	 * Steps of execution in ordered sequence.
	 * 
	 * @return a map of execution steps
	 */
	Map<Integer, String> getOrderedSteps() {
		return oriented().filter(s -> s.step != null).sorted((s1, s2) -> s1.step.compareTo(s2.step))
				.collect(toMap(s -> s.step, s -> s.name, (x, y) -> x));
	}

	/**
	 * Choose a starting Node : any if it is Eulerien or an odd one if it is not
	 * 
	 * @return the Starting Square
	 */
	public Square startingNode() {
		Square res = isEulerien() ? getSquares().findAny().get() : oddVertices().findAny().get();
		System.out.println("Choosing starting node " + res);
		return res;
	}

	/**
	 * <pre>
	 * Tell whether or not this graph admit an Eulerian path using the property that
	 * says : "For the existence of Eulerian trails it is necessary that zero or two
	 * vertices have an odd degree"
	 * 
	 * @return true if and only if the Graph represented by this City object admit
	 *         an Eulerian trail
	 * 
	 *         <pre>
	 */
	boolean isEulerien() {
		long odd = oddVertices().count();
		boolean res = odd == 0 || odd == 2;
		System.out.println("the Graph is Eulerien : " + res);
		return res;
	}

	/**
	 * @return A stream of all verticed having an odd number of edges
	 */
	public Stream<Square> oddVertices() {
		return verticesDegree().entrySet() // Set<Entry<Square,Integer>>
				.stream() // Stream<Entry<Square,Integer>>
				// Filter the odd numbers only
				.filter(ent -> (ent.getValue().longValue() % 2) == 1)
				// return only the Square and forget the Integer
				.map(ent -> ent.getKey());
	}

	public void sleep() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void setExtra(List<Path> list) {
		System.out.println("\nAdding virtual streets to the city equivalent to the following path:\n" + list + "\n");

		streetsL = Stream
				.concat(Stream.of(streetsL),
						list.stream().map(path -> new Street(path.tail(), path.head(), path.toString())))
				.collect(Collectors.toList()).toArray(new Street[list.size() + streetsL.length]);

		streetsL2 = Stream
				.concat(Stream.of(streetsL2),
						list.stream().map(path -> new Street(path.head(), path.tail(), path.toString())))
				.collect(Collectors.toList()).toArray(new Street[list.size() + streetsL2.length]);

	}

	/**
	 * All Streets as directed edges
	 * 
	 * @return a collection of Street as a Stream
	 */
	public Stream<Street> oriented() {

		Stream<Street> res = Stream.concat(Stream.of(streetsL), Stream.of(streetsL2));
//		if(extra!=null)
//			return Stream.concat(res, Stream.of(extra));

		return res;
	}

	/**
	 * get the Squares
	 * 
	 * @return a collections of Squares as a Stream
	 */
	public Stream<Square> getSquares() {
		return Stream.of(squares);
	}

	public void toDot(boolean allArcs, boolean printStreetName, boolean printSquareName, boolean printMark,
			boolean printDoubleArrow, String string, Square current) {
		toDot(allArcs, printStreetName, printSquareName, printMark, printDoubleArrow, string, current,
				new LinkedList<Street>());

	}

}
