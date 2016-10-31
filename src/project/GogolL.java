package project;

import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GogolL implements Gogol {

	public GogolL() {

	}

	/**
	 * <pre>
	 * (1) Choisir un sommet quelconque r dans le graphe et construire une
	 * anti-arborescence dans G d’anti-racine r. Une anti-arborescence
	 * d’anti-racine r est un sous-graphe qui devient une arborescence de racine
	 * r une fois que tous ses arcs ont ́ete inverses. 
	 * 
	 * (2) Pour chaque
	 * sommet x de G faire Num ́eroter de 1 ` a d + e sortant de x) les arcs de
	 * G sortant de x, en mettant le plus grand numero G (x) (degr ́ sur l’arc
	 * sortant appartenant ` a l’anti-arborescence (pour l’anti-racine, la num
	 * ́erotation est quelconque). 
	 * 
	 * (3) Partir de r, et tant qu’il existe un arc
	 * non encore utilise permettant de quitter le sommet ou l’on se 
	 * trouve, choisir celui de plus petit numero et le parcourir.
	 * <pre>
	 */
	@Override
	public void driveThrough(City city, Square current, String file) {
		System.out.println("Driving Through the city : \n\tStarting : "+current);
		int step = 0;
		Map<Square,Integer> degres = degreOfX(city);
		Map<Square, List<Street>> adjM = city.adjacentStreet();
		
		final Square sq=current;
		/*
		 *  numerote chaque arc
		 */
		adjM.forEach( (s,l) -> {
			int i = degres.get(s)/2;
//			List<Street> list = l.stream().filter(st->st.sq2==sq).collect(Collectors.toList());
			l.sort((s1,s2)->{
				if(s1.sq2==sq)
					return -1;
				if(s2.sq2==sq)
					return 1;
				
				return 0;
			});
//			if(!list.isEmpty()){
//				Street t = list.get(0);
//				t.mark(""+i--);
//				l.remove(t);
//			}
			for(Street st : l){
				st.pos= i--;
			}
		});
		
		city.toDot(true,false, true, true, false, "GogolL_" + file+"_step_"+step,current);
		
		List<Street> usedStreet = new ArrayList<Street>();
		while(usedStreet.size() < city.getStreets().count()){
			step++;
			
			List<Street> curAdj = adjM.get(current);
			curAdj.removeIf(s->{
				System.out.println("contains used ? "+usedStreet.contains(s));
				return usedStreet.contains(s);
			});
			curAdj.sort((s1,s2)-> {
				System.out.println("comparing "+s1.pos +" & "+ s2.pos +""+s1.pos.compareTo(s2.pos));
				return s1.pos.compareTo(s2.pos);
			});
			
			System.out.println("curent next Street : "+curAdj.stream().map(s->s.name).collect(Collectors.joining(",")));
			Street next = curAdj.get(0);
			usedStreet.addAll(city
					.getStreets()
					.filter(s->s.name==next.name)
					.collect(Collectors.toList()));
			next.mark("step:"+step+"\n");
			current = next.sq2;
			
			
//			int i = degres.get(current)/2;
//			current.mark("out: "+i);
//			for(Street st : curAdj){
//				st.mark(","+i--);
//				System.out.println(current+"   "+st.name+" "+st.mark);
//				if(i==1){
//					usedStreet.addAll(city
//							.getStreets()
//							.filter(s->s.name==st.name)
//							.collect(Collectors.toList()));
//					current = curAdj.get(i).sq2;
//				}
//			}
			
			System.out.println("used street: "+ usedStreet.stream().map(s->s.name).collect(Collectors.joining(", ")));
			city.toDot(true,false, true, true, false, "GogolL_" + file+"_step_"+step,current);
		}
		
		LinkedList<GraphObj> path = new LinkedList<GraphObj>();


		//city.toDot(false,false, true, true, false, "GogolN_" + file,null);
	}
	
//	private void recurse(List<Street> usedStreet, Stream<Street> streets, Square current){
//		List<Street> st = streets
//				.filter(s->!usedStreet.contains(s))
//				.collect(Collectors.toList());
//	}
	
	private Stream<Street> subGraph(Stream<Street> in, Street t){
		return in.filter(x->x!=t);
	}
	
	
	
	private Map<Square,Integer> degreOfX(City c){
		return c.getStreets()
				.flatMap(street -> Stream.of( street.sq1, street.sq2 ))
				.collect(toMap(s -> s, s -> 1, Integer::sum));
	}
}
