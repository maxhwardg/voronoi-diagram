import geometry.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

/**
 * Main class for executing Voronoi diagram generator and visualiser.
 * 
 * @author Max Ward-Graham, Bon Sawyer
 */
public class Voronoi
{
	/** Program name, printed as part of 'usage' statements. */
	private static final String APP_NAME = "Voronoi";
	
	/**
	 * Default maximum vertex co-ordinate (used as width and height of Voronoi
	 * plane in 'blank' and 'random' modes).
	 */
	private static final double DEFAULT_MAX_VERTEX = 600.0;
	
    /** Initial width and height of visualiser display area, in pixels. */
	private static final int PIXEL_WIDTH = 600;
	
	/**
	 * Main function, launched when program executes. Processes command-line
	 * arguments, selects an appropriate program mode, then calculates a
	 * Voronoi diagram and displays it in a visualiser.
	 */
	public static void main(String[] args)
	{

		if (args.length < 1) {
			printf("Error: No arguments specified.\n");
			exitWithUsage();
		}
		
		Diagram diag = null;
		
		// Select a program mode from command line arguments:
		//   'blank' mode  : start with a blank diagram
		//   'file' mode   : read vertices from a text file
		//   'random' mode : start with a diagram of random vertices
		switch(args[0].toUpperCase().charAt(0)) {
		case 'B':
			// Generate blank diagram
			diag = new Diagram(DEFAULT_MAX_VERTEX);
			break;
		case 'F':
			// Generate diagram from vertices in file
			if (args.length < 2) {
				printf("Error: No filename specified.\n");
				exitWithUsage();
			} else {
				String filename = args[1];
				diag = readFile(filename);
			}
			break;
		case 'R':
			// Generate diagram of random vertices
			if (args.length < 2) {
				printf("Error: No vertex count specified.\n");
				exitWithUsage();
			} else {
				int numVerts = 0;
				boolean numValid = true;
				// Check for valid vertex count argument (non-negative integer).
				try {
					numVerts = Integer.parseInt(args[1]);
					numValid = (numVerts >= 0);
				} catch (NumberFormatException e) {
					numValid = false;
				}
				// Terminate if specified vertex count invalid.
				if (!numValid) {
					printf("Error: invalid vertex count: '%s' (non-negative integer required)\n", args[1]);
					exitWithUsage();
				}
				ArrayList<Vertex> verts = randomVerts(numVerts, DEFAULT_MAX_VERTEX);
				diag = new Diagram(verts, DEFAULT_MAX_VERTEX);
			}
			break;
		default:
			printf("Error: unrecognised mode '%s'\n", args[0]);
			exitWithUsage();
		}
		
		// Create and display visualisation of diagram.
		double maxVertex = diag.getSize();
		Display disp = new Display(diag, PIXEL_WIDTH, maxVertex);
	}
	
	/**
	 * Displays usage information to user, then terminates program.
	 */
	private static void exitWithUsage()
	{
		printf("Usage:\n");
		// Explain 'blank' mode
		printf("    java %s B\n", APP_NAME);
		printf("      Generates blank diagram. All vertices to be added via mouse clicks.\n");
		// Explain 'file' mode
		printf("OR: java %s F [filename]\n", APP_NAME);
		printf("      Generates starting diagram from vertices in text file.\n");
		printf("      Expected file format:\n");
		printf("        First line : single number (plane size)\n");
		printf("        Other lines: x y (vertex co-ordinates, separated by whitespace)\n");
		printf("          [Ignored: negative values, values greater than plane size]\n");
		// Explain 'random' mode
		printf("OR: java %s R [numVertices]\n", APP_NAME);
		printf("      Generates starting diagram of random vertices.\n");
		printf("        numVertices : Number of random vertices (int)\n");
		// Terminate
		System.exit(1);
	}

	/**
	 * Prints formatted string and tokens to System.out.
	 *
	 * @param s Formatted String, in standard 'printf' format.
	 * @param o Array of tokens referred to in s.
	 */	
	private static void printf(String s, Object... o)
	{
		System.out.printf(s, o);
	}
	
	/**
	 * Generates a collection of random vertices.
	 *
	 * @param numVertices the number of vertices to generate.
	 * @param maxDimension the maximum co-ordinate value (minimum always 0).
	 */
	private static ArrayList<Vertex> randomVerts(int numVertices, double maxDimension)
	{
		Random r = new Random();
		ArrayList<Vertex> verts = new ArrayList<Vertex>();
		
		for (int i = 0; i < numVertices; i++) {
			double x = r.nextDouble() * maxDimension;
			double y = r.nextDouble() * maxDimension;
			verts.add(new Vertex(x, y));
		}
		
		return verts;
	}
	
	/**
	 * Reads a text file containing vertex co-ordinates, then calculates the
	 * Voronoi diagram based on these co-ordinates. Expects first line of file
	 * to contain a double or integer representing plane size (and hence
	 * maximum vertex co-ordinate), and each subsequent line to contain two
	 * doubles or integers (x and y co-ordinates of a vertex, respectively).
	 *
	 * @param filename path to text file containing vertex co-ordinates.
	 */
	private static Diagram readFile(String filename)
	{
		Scanner scan = null;
		
		// Check that a valid file was specified.
		try {
			File f = new File(filename);
			scan = new Scanner(f);
		} catch (Exception e) {
			printf("Error: Invalid file: '%s'\n", filename);
			exitWithUsage();
		}
		
		printf("Reading vertices from file '%s'\n", filename);
		
		// Initialise vertex collection and loop variables.
		ArrayList<Vertex> verts = new ArrayList<Vertex>();
		int lineNumber = 1;
		String currLine = null;
		
		// Determine plane size (and hence maximum vertex co-ordinate).
		double maxVert = 0.0;
		boolean maxValid = false;
		try {
			currLine = scan.nextLine();
			maxVert = Double.parseDouble(currLine);
			maxValid = (maxVert > 0.0);
		} catch (Exception e) {
			maxValid = false;
		}
		
		// Terminate if plane size invalid (negative or not a number).
		if (!maxValid) {
			printf("Error: first line of file '%s'\n", filename);
			printf("Line 1: '%s'\n", currLine);
			printf("Expected: single non-negative number representing plane width and height\n");
			System.exit(1);
		}
		
		// Read in vertices from remaining lines. Do not add (but do report)
		// any with co-ordinates less than zero, or greater than plane size.
		while (scan.hasNextLine()) {
			currLine = scan.nextLine();
			Scanner line = new Scanner(currLine);
			lineNumber++;
			try {
				double x = line.nextDouble();
				double y = line.nextDouble();
				// Check that co-ordinates are within plane boundaries.
				if (x >= 0.0 && x <= maxVert && y >= 0.0 && y <= maxVert) {
					verts.add(new Vertex(x, y));
				} else {
					printf("Ignored: line %d: (%.2f, %.2f) (outside plane boundaries)\n", lineNumber, x, y);
				}
			} catch (Exception e) {
				printf("Error: invalid line in file '%s'\n", filename);
				printf("Line %d: '%s'\n", lineNumber, currLine);
				printf("Required format: 'x y' (co-ordinates separate by whitespace)\n");
				System.exit(1);
			}
		}
		
		return new Diagram(verts, maxVert);
	}
}
