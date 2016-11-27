package project;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Path extends LinkedList<Street> {

	private static final long serialVersionUID = -547652609247814339L;
	

	public Path(Path in) {
		super(in);
	}

	public Path() {
		super();
	}
	
	public Square head(){
		return super.getLast().sq2;
	}
	
	public Square tail(){
		return super.getFirst().sq1;
	}
	
	boolean isSlowMoving(){
		double sizePath= this.size();
		double sizeDiscovered=stream().map(st -> st.name ).distinct().count();
		//System.out.println("slow moving "+(100*sizeDiscovered/sizePath)+ " "+sizePath+"  "+sizeDiscovered+ this );
		if((100*sizeDiscovered/sizePath) < 75){
			System.out.println("slow moving "+(100*sizeDiscovered/sizePath)+ " "+ this );
			return  true;
		}	
		return false;
	}
	
	

	/**
	 * Drive in the street 
	 * @param street 
	 * @return a new path containing "this" path and the given street
	 */
	public Path drive(Street street) {
		Path path = new Path(this);
		
		if (!path.add(street)) 
			System.err.println("Path not increased " + street);
		
		return path;
	}

	/**
	 * Pretty printed
	 */
	public String toString() {
		return "Path [" + this.stream().map(s -> s.name).collect(Collectors.joining(",")) + "]";
	}

	/**
	 * @return the dot graph as a String
	 */
	public String toDot() {
		String res =  "digraph G {\n" ;
		int i=1;
		 for(Street s : this){
			 res += s.sq1.cleanName() + " -> " + s.sq2.cleanName() +" [label=\""+s.name+ i++ +"\"]\n";
		 }			
		return res + "\n}";
	}
	
	public boolean containsAllNodes(Stream<Square> sq){
		return containsAll(sq.collect(Collectors.toList()));
	}
	
	public boolean isClose(){
		return getLast().sq2.equals(getFirst().sq1);
	}
	
	public boolean containsStreets(){
		
		List<String> local = City
			.singleton
			.oriented()
			.map(st -> st.name)
			.distinct()
			.collect(Collectors.toList());
			
		return local.containsAll(names()) && names().containsAll(local);
	}
	
	private List<String> names(){
		return stream().map(st -> st.name)
		.distinct()
		.collect(Collectors.toList());
	}
	
	public boolean isFinished (){
		return isClose() && containsStreets();
	}
	
	
	/**
	 * 2 paths are equals if their tails and heads are equals as well as there size
	 */
	@Override
	public boolean equals(Object o){
		Path p = (Path)o;
		return p.head().equals(head()) && p.tail().equals(tail()) && p.size()==size();
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