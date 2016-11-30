package project;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GogolL implements Car {

	City city;

	public GogolL() {
	}

	/**
	 * <pre>
	 * (1) Choisir un sommet quelconque r dans le graphe et construire une
	 * anti-arborescence dans G d’anti-racine r. Une anti-arborescence
	 * d’anti-racine r est un sous-graphe qui devient une arborescence de racine
	 * r une fois que tous ses arcs ont ́ete inverses.
	 * 
	 * (2) Pour chaque sommet x de G faire Num ́eroter de 1 ` a d + e sortant de
	 * x) les arcs de G sortant de x, en mettant le plus grand numero G (x)
	 * (degr ́ sur l’arc sortant appartenant ` a l’anti-arborescence (pour
	 * l’anti-racine, la num ́erotation est quelconque).
	 * 
	 * (3) Partir de r, et tant qu’il existe un arc non encore utilise
	 * permettant de quitter le sommet ou l’on se trouve, choisir celui de plus
	 * petit numero et le parcourir.
	 * 
	 * <pre>
	 */
	@Override
	public void driveThrough(City c, Square current, String file) {
		this.city = c;
		c.toDot(true,true, true, false, false,file+"L",null);
		Map<Square, List<Street>> adjM = city.adjacentStreet();
		
		System.out.println("Driving Through the city : \n\tStarting : " + current);
		

		Path arbo = arborescence(current, new Path());
		System.out.println(arbo.toDot()+"===================\n" + arbo);
		
		
		numerotation(arbo);
		city.toDot(true, false, true, true, true, "GogolL_" + file + "_step_0" , current,arbo);

		
		Path usedStreet = new Path();
		for (int step=1; step<=city.oriented().count()/2;step++) {
			//System.out.println(adjM);
			//System.out.println(adjM.size()+" "+adjM.get(current).stream().map(s-> s.name).collect(Collectors.joining()));
			Street next = adjM.get(current)
					.stream()
					.filter(s -> !usedStreet.contains(s.name))
					.sorted((s1, s2) -> s1.pos.compareTo(s2.pos))
					.findFirst()
					.get();
			
			usedStreet.add(next);
			next.step = step;
			current = next.sq2;

			System.out.println(usedStreet);
			city.toDot(true, false, true, true, false, "GogolL_" + file + "_step_" + step, current, usedStreet);
			System.out.println();
		}

	}

	public void numerotation(Path arbo) {
	
		List<Street> antiArbo = city.oposingArcs(arbo);
		
		city.adjacentStreet().forEach((sq, list) -> {
			
			int degre = city.verticesDegree().get(sq);
			
			list.sort( (s1,s2) -> {
				int res = 0;
				if(antiArbo.contains(s1)) res-=1;
				if(antiArbo.contains(s2)) res+=1;
				if(antiArbo.contains(city.reverseEdge(s1))) res+=2;
				if(antiArbo.contains(city.reverseEdge(s2))) res-=2;
				//System.out.println(s1.name +" : "+s2.name+" : "+res);
				return res;
			});
	
			for(Street t : list){
				t.pos=degre--;
			}
			
			//System.out.println(sq.name +"'s sorted list : "+list.stream().map(x->x.name+":"+x.pos).collect(Collectors.joining(" - "))+"\n");

		});
	}

	public Path arborescence(Square current, Path pathTaken) {
		//System.out.println("arbo "+current+pathTaken);
		if (pathTaken.size() == city.getSquares().count())
			return pathTaken;

		List<Street> streetsOut = city.adjacentStreet().get(current);

		for (Street street : streetsOut) {
			if (!pathTaken.contains(street.sq2)) {
				pathTaken = arborescence(street.sq2, pathTaken.drive(street));
			}

		}

		return pathTaken;
	}

}
