package project;

public class Square {
	
	public String mark="";
	public Integer step;

	String name;
	
	public Square(String name) {
		this.name=name.trim();
	}

	public void mark(String m ){
		this.mark+=m;
	}

	@Override
	public boolean equals(Object o){
		if(o==null)
			return false;
		if(o instanceof String)
			return name.equals( ((String) o).trim() );
		if(o instanceof Square)
			return name.equals( ((Square) o).name.trim());
		
		return false;
	}
	
	public String toString(){
		return  "Square["+name+"]";
	}
	
	public String cleanName(){
		return name.replaceAll(" ", "_").replaceAll("-", "_").replaceAll("é", "e");
		
	}
	
	public String toDot(boolean printMark, boolean printSquareName, boolean red){
		String square = "\t" + cleanName() + " [label=\"";
		if (printMark && mark!=null && mark != "")
			square += mark + "\\n";
		if (printSquareName)
			square += name;
		square += "\"";
		if(red){
			square += ",color=\"red\"";
		}
		square += "];";
		return square;
	}
}

