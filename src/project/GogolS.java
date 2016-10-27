package project;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GogolS implements Gogol{
	public GogolS(){
	}

	@Override
	public void driveThrough(City city, Square startingPoint, String file) {
		System.out.println("Driving Through the city : \n");
		
		city.addReverseEdges();
		Map<Square, List<Street>> adj = city.adjacentStreet();
		//System.out.println("adj.size:"+adj.size() );
		
		LinkedList<Square> path = new LinkedList<Square>();
		path.add(startingPoint);
		Square current = startingPoint;
		int step=0;
		while(!adj.isEmpty()){
			System.out.println("\t at : " + current.name );
			Street st = adj.get(current).remove(0);
			System.out.println("\t in : " + st.name );
			st.mark+=" "+step++;
			path.add(st.sq2);
			if(adj.get(current).isEmpty()){
				adj.remove(current);
			}
			current=st.sq2;
		}
		//System.out.println(path.stream().map(s->s.mark+" "+s.name).collect(Collectors.joining("\n")) );
		
		city.toDot(false, true, true, false, "GogolS_"+file);
	}
}
