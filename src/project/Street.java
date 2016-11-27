package project;

import java.util.List;

public class Street {

	public Square getSq1() {
		return sq1;
	}

	public Square getSq2() {

		return sq2;
	}

	public void mark(String m) {
		this.mark += m;
	}

	final Square sq1, sq2;
	final String name;

	private String mark;
	Integer weight;
	public Integer pos;
	public Integer step;
	
	public Street(Square square1, Square square2, Integer weight) {
		this.sq1 = square1;
		this.sq2 = square2;
		this.name = square1.name +"-"+square2.name;
		this.weight=weight;
	}

	public Street(Square square1, Square square2, String string2) {
		this.sq1 = square1;
		this.sq2 = square2;
		this.name = string2.trim();
	}

	public String toString() {
		return "Street[" + name + "," + sq1 + "," + sq2 + "," + pos + "," + step + "," + mark + "]";
	}

	public String toDot(boolean printStreetName, boolean printMark, List<Street> path) {
		// System.out.println(this);

		String label = "";
		if (printStreetName)
			label += name ;
		if (printMark && mark != null && mark != "")
			label += "mark:" + mark + "\\n";
		if (pos != null)
			label += "pos:" + pos + "\\n";
		if (step != null)
			label += "step:" + step + "\\n";

		label = "label=\"" + label + "\"";
		label += path.contains(this) ? ",color=red":"";
		
		String edge = "\t" + sq1.cleanName() + " -> " + this.sq2.cleanName() + " [" + label	+ "];";

		return edge;
	}

	public boolean equals(Object o) {
		if (o == null)
			return false;
		
		if (o instanceof String) {
			Street s = (Street) o;
			return s.name == name ;
		}
		
		if (o instanceof Street) {
			Street s = (Street) o;
			return s.sq1 == sq1 && s.sq2 == sq2 && s.name == name;
		} 
		return false;
	}

}
