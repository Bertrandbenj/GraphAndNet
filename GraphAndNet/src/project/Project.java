package project;

import java.util.Arrays;

public class Project {

	public enum Algo {
		GogolS, GogolL, GogolXL
	}

	static Algo algoToUse = Algo.GogolS;
	static String fileToUse = "nonsens.txt";
	static boolean printDot = false;

	public static void main(String[] args) {
		// Parsing parameters 
		Arrays.asList(args).stream().forEach(s -> {
			if (s.startsWith("file=")) {
				fileToUse = s.substring(5);
			} else {
				switch (s) {
				case "-S":
					algoToUse = Algo.GogolS;
					break;
				case "-L":
					algoToUse = Algo.GogolL;
					break;
				case "-XL":
					algoToUse = Algo.GogolXL;
					break;
				case "-Dot":
					printDot=true;
					break;
				}
			}
		});
		System.out.println("Using file : "+fileToUse);
		System.out.println("Printing a Dot graph : "+printDot);
		System.out.println("Executing algo : "+algoToUse);

		City c = new Parser(fileToUse).buildCity();
		if (printDot)
			c.toDot();
		
		Gogol algorithm =null;
		switch(algoToUse){
		case GogolS:
			algorithm = new GogolS();
			break;
		case GogolL:
			algorithm = new GogolL();
			break;
		case GogolXL:
			algorithm = new GogolXL();
			break;
		default: 
			System.err.println("error please set -S -L -XL to choose an algorithm");
		}
		
		algorithm.driveThrough(c);
	}

}
