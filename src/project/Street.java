package project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Street {
	
	public Square getSq1() {
		return sq1;
	}


	public Square getSq2() {
		
		return sq2;
	}


	Square sq1, sq2;
	String name;
	public String mark="";

	public Street(Square square1, Square square2, String string2) {
		this.sq1=square1;
		this.sq2=square2;
		this.name=string2.trim();
	}
	

	public String toString(){
		return "Street["+name+","+sq1+","+sq2+"]";
	}
	
	public boolean equals(Object o){
		if(o==null) return false ;
		if(o instanceof Street){
			Street s = (Street)o;
			return s.sq1==sq1 && s.sq2==sq2 && s.name==name;
		}else{
			return false;
		}
	}

}
