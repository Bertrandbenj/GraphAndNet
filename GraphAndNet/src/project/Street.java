package project;

public class Street {
	
	Square sq1, sq2;
	String name;

	public Street(Square square1, Square square2, String string2) {
		this.sq1=square1;
		this.sq2=square2;
		this.name=string2.trim();
	}
	

	public String toString(){
		return "Street["+name+","+sq1+","+sq2+"]";
	}

}
