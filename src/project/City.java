package project;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class City {
	private ArrayList<Street> streets = new ArrayList<Street>();
	private ArrayList<Square> squares = new ArrayList<Square>();
	
	
	public City(ArrayList<Street> sts, ArrayList<Square> sqs){
		streets = sts;
		squares=sqs;
	}
	
	void toDot() {
		String res = "digraph {\nedge [dir=\"both\"];\n";
		// -------- Edges description
		res += squares.stream().map(sq -> "  " + sq.cleanName() + "[label=\"" + sq.name + "\"];")
				.collect(Collectors.joining("\n"));

		// -------- Vectors
		res += "\n\n";
		res += streets.stream()
				.map(st -> "  " + st.sq1.cleanName() + " -> " + st.sq2.cleanName() + " [label=\"" + st.name + "\"];")
				.collect(Collectors.joining("\n"));

		// --------- Reverse Edges (using edge[dir=both])
		// res+="\n";
		// res+=streets.stream()
		// .map(st -> st.sq2.cleanName()+" -> "+st.sq1.cleanName()+";")
		// .collect(Collectors.joining("\n"));

		System.out.println(res + "\n}");
	}
	
}
