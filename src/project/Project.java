package project;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class Project {

	public enum Algo {
		GogolS, GogolL, GogolXL
	}

	static Algo algoToUse = Algo.GogolS;
	static String fileToUse = "nonsens.txt";
	public static boolean printDot = false;

	public static void main(String[] args) throws InterruptedException {
		// Parsing parameters
		Arrays.asList(args).stream().forEach(s -> {
			if (s.startsWith("file=")) {
				fileToUse = s.substring(5, s.length()-4);
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
					printDot = true;
					break;
				}
			}
		});
		System.out.println("Using file : " + fileToUse);
		System.out.println("Printing a Dot graph : " + printDot);
		System.out.println("Executing algo : " + algoToUse);

		
		City c = new Parser(fileToUse+".txt").buildCity();
		if (printDot)
			c.toDot(false,true, true, false, true,fileToUse,null);
		
		Car algorithm = null;
		switch (algoToUse) {
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

		algorithm.driveThrough(c, c.startingNode(), fileToUse);

	}

	/**
	 * Fail safe command execution 
	 * @param cmd
	 * @return
	 */
	public static Object run(String cmd) {
		try {

			Process process = new ProcessBuilder(new String[] { "bash", "-c", cmd }).start();
	
			ArrayList<String> output = new ArrayList<String>();
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			while ((line = br.readLine()) != null) {
				output.add(line);
				System.out.println(line);
			}
			// There should really be a timeout here.
			if (0 != process.waitFor())
				return null;

			return output;

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return null;
	}

}
