package project;

public class Square {
	
	public String mark="";

	String name;
	public Square(String name) {
		this.name=name.trim();
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
}

