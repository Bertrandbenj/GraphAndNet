package project;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GogolLRec implements Car {

	City city;

	public GogolLRec() {

	}
	
	private Path recurse(AdjacencyLists adjM, final Path path,	Square current, String position) {
		System.out.println("recursing " + adjM.size() + "  " + path.size() + "\n"+path.toString());
		city.toDot(true, false, true, true, false, "GogolLRec_" + position +"_"+ path.size(), current, path);
		
		// We remove the street that just visited from the AdjacencyLists 
		AdjacencyLists adjacencyLists= path.isEmpty()? adjM: adjM.driveIn(path.getLast());
	
		if (adjacencyLists.isEmpty()) {
			city.toDot(true, false, true, true, false, "GogolLRec_FP_" + position +"_"+path.size(), current, path);
			//return path;
		}else{
			Path res = path;

			// we number the streets unused living the current square
			List<Street> exitingStreet = adjacencyLists.get(current);
			int degreR = 0;
			for (Street st : exitingStreet) {
				st.pos = degreR++;
				st.step = path.size();
				res = recurse( adjacencyLists, path.drive(st), st.sq2,position+"_"+st.pos);
			}

			return res;
		}
		return null;
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
		recurse( new AdjacencyLists(c), new Path(), current, "");
	}

}
