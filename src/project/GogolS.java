package project;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GogolS implements Car {

	City city;

	public GogolS() {

	}
	
	private Path recurse(AdjacencyLists adjM, Path path,	Square current, Integer position) {
		//System.out.println("recursing " + adjM.size() + "  " + position + "  " + path.size() );
		
		// We remove the street that just visited from the AdjacencyLists 
		//AdjacencyLists adjacencyLists= path.isEmpty()? adjM: adjM.driveIn(path.getLast());
	
		if (!adjM.isEmpty()) {			
			List<Street> exitingStreet = new ArrayList<Street>( adjM.get(current));
			for (Street st : exitingStreet) {
				st.step = position;
				path = recurse( adjM.driveIn(st), path.drive(st), st.sq2, position+1);
				if (path.isFinished())
					return path;
			}
			
		}
		return path;
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
		System.out.println("Driving Through the city : \n\tStarting : " + current);
		Path p = recurse( new AdjacencyLists(c), new Path(), current, 0);
		System.out.println(p);
		city.toDot(true, false, true, true, false, "GogolLRec_"+ p.size(), current, p);
	}

}
