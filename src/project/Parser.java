package project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Parser {

	private Path filePath;

	public Parser(String file) {
		filePath = Paths.get(file);
	}

	City buildCity() {
		ArrayList<Street> streets = new ArrayList<Street>();
		ArrayList<Square> square = new ArrayList<Square>();
		
		try (Stream<String> stream = Files.lines(filePath)) {

			Iterator<String> l = stream.collect(Collectors.toList()).iterator();

			Integer nbSquare = Integer.parseInt(removeDot(l.next()));
			Integer nbStreets = Integer.parseInt(removeDot(l.next()));

			System.out.println("Parsing " + nbSquare + " Squares and " + nbStreets + " Streets");

			while (nbSquare-- > 0) {
				Square s = new Square(removeDot(l.next()));
				// System.out.println("parsed "+s);
				square.add(s);
			}

			while (nbStreets-- > 0) {
				String[] str = removeDot(l.next()).split(";");

				// we get the Matching Squares. simpler using streams
				Square _1 = square.stream().filter(sq -> sq.equals(str[1])).findAny().get();
				Square _2 = square.stream().filter(sq -> sq.equals(str[2])).findAny().get();
				Street s = new Street(_1, _2, str[0]);
				// System.out.println("parsed "+s);
				streets.add(s);
			}
			
			return new City(streets,square,true);
		} catch (IOException e) {
			System.err.println("Parsing file : " + filePath);
			e.printStackTrace();
		}
		return null;
	}



	/**
	 * Function that trim the dot at the end of a line
	 * 
	 * @param in
	 *            the input String like "inputString."
	 * @return "inputString" in this example
	 */
	String removeDot(String in) {
		return in.substring(0, in.length() - 1);
	}
}
