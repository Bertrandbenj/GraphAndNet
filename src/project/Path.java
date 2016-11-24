package project;

import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.transform.stream.StreamSource;

public class Path extends LinkedList<Street> {

	private static final long serialVersionUID = -547652609247814339L;

	public Path(Path in) {
		super(in);
	}

	public Path() {
		super();
	}

	public Path drive(Street t) {
		Path res = new Path(this);
		if (!res.add(t)) {
			System.err.println("Path not increased " + t);
		}
		return res;
	}

	public String toString() {
		return "Path [" + this.stream().map(s -> s.name).collect(Collectors.joining(",")) + "]";
	}

	public String toDot() {
		return "digraph G {\n" + this.stream().map(s -> s.sq1.cleanName() + "_ -> " + s.sq2.cleanName() +"_ [label=\""+s.name+"\"]")
				.collect(Collectors.joining("\n")) + "\n}";
	}

	@Override
	public boolean contains(Object o) {

		if (o == null)
			return false;

		if (o instanceof String)
			return this.stream().anyMatch(st -> st.name.equals(o));
		
		if (o instanceof Square)
			return this.stream().flatMap(st -> Stream.of(st.sq1, st.sq2)).anyMatch(sq -> sq.equals((Square) o));

		if (o instanceof Street)
			return this.stream().anyMatch(sq -> sq.equals((Street) o));
		
		return false;
	}

}