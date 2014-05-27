import geometry.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A class representing a Voronoi diagram, calculated using the 'incremental'
 * algorithm, and employing spatial hashing to improve performance.
 * 
 * @author Max Ward-Graham, Bon Sawyer
 */
public class Diagram
{
	/**
	 * Constant by which to multiply plane bounds to achieve 'super' bounds,
	 * ensuring the cells enclosing the three 'special' vertices required by
	 * the algorithm do not encroach upon the actual Voronoi plane.
	 */
	private static final double SUPER_FACTOR = 4;
	
	/**
	 * Constant by which to multiply the super bounds when calculating the
	 * bisectors for adding the three special vertices.
	 */
	private static final double INIT_BISECT_FACTOR = 4;
	
	/**
	 * Constant by which to multiply the super bounds when calculating the
	 * bisectors for general vertices as they are added.
	 */
	private static final double GENERAL_BISECT_FACTOR = 3;
	
	/** Bounding box defining the range of vertex co-ordinates permitted. */
	private BoundingBox boundary;
	
	/** Bounding box for calculating bisectors when adding vertices. */
	private BoundingBox bisectorBound;
	
	/** Collection of cells comprising the Voronoi diagram. */
	private ArrayList<Cell> cells;
	
	/**
	 * Spatial hash-table for quickly determining the closest existing cell to
	 * a given new vertex.
	 */
	public SpatialHash hash;
	
	/**
	 * How many search steps have been done by the find cell method.
	 */
	public int searches = 0;

	/** Variable for assigning unique identifiers to Voronoi cells. */
	private int idCell = 0;
	
	/**
	 * Constructs a blank diagram to which cells can be added later.
	 * 
	 * @param maxDimension Maximum permitted vertex co-ordinate.
	 */
	public Diagram(double maxDimension)
	{
		// Call helper method common to all constructors.
		setup(maxDimension);
		// Set up spatial hash table.
		double size = Math.max(boundary.xMax - boundary.xMin, boundary.yMax - boundary.yMin);
		// This will create a SpatialHash that resizes itself to fit any diagram.
		// We do this as we don't know how many verticies will be added.
		hash = new SpatialHash((int) Math.ceil(size) + 1);
	}

	/**
	 * Constructs a diagram from specified array of vertices.
	 * 
	 * @param verts The collection of vertices to add to the diagram.
	 * @param maxDimension Maximum permitted vertex co-ordinate.
	 */
	public Diagram(Collection<Vertex> verts, double maxDimension)
	{
		// Call helper method common to all constructors.
		setup(maxDimension);
		
		// Set up spatial hash table.
		double size = Math.max(boundary.xMax - boundary.xMin, boundary.yMax - boundary.yMin);
		// Use a spatial hash of a specific size, since we know how many verticies
		// Will be in this diagram.
		// number of cells / 10 seems to give good performance
		hash = new SpatialHash(verts.size() / 10, (int) Math.ceil(size) + 1);
		
		// Add vertices.
		for (Vertex v : verts) {
			addVertex(v);
		}
	}
	
	/**
	 * Returns the collection of Voronoi cells comprising the diagram.
	 * 
	 * @return the ArrayList of Voronoi cells generated so far.
	 */
	public ArrayList<Cell> getCells()
	{
		return cells;
	}
	
	/**
	 * Returns the maximum permitted vertex co-ordinate (i.e. the diagram's
	 * width, equivalent to its height in this implementation, where the
	 * diagram is always square).
	 *
	 * @return the maximum vertex co-ordinate.
	 */
	public double getSize()
	{
		return boundary.xMax;
	}
	
	/**
	 * Adds a new vertex to the diagram, also determining and adding its
     * corresponding Voronoi cell.
	 * 
	 * @param v the vertex to add to the diagram.
	 */
	public boolean addVertex(Vertex v)
	{
		// Create a new Voronoi cell for this vertex.
		Cell newCell = new Cell(v, idCell++);
		
		// Find the existing cell surrounding the vertex.
		Cell first = findCell(v);
		
		// Skip adding v if it is a duplicate
		if(v.equals(first.generator)) {
			System.out.println("Trying to add duplicate vertex: " + v);
			return false;
		}
		
		// Keeps track of the old borders for every cell we modify.
		// Can be used to revert any changes to the Voronoi diagram.
		HashMap<Cell, ArrayList<Line>> visited = new HashMap<Cell, ArrayList<Line>>();
		
		// Boundary construction loop.
		Cell currCell = first;
		do {
			// halfplane divider
			Line hp = (new Line(v, currCell.generator)).bisector(bisectorBound);
			// the first intersection
			Vertex i1 = null;
			// the first intersected line
			Line l1 = null;
			// the new set of borders for currCell
			ArrayList<Line> newBorder = new ArrayList<Line>();
			
			// Go through all lines of cell looking for intersection.
			int numIntersections = 0;
			Cell next = null;
			
			for (Line currLine : currCell.borders) {
				Vertex intersection = hp.intersection(currLine);
				// deal with intersections
				if (intersection != null) {
					++numIntersections;
					// first intersection
					if (i1 == null) {
						i1 = intersection;
						l1 = currLine;
						// on second intersection, do changes
					} else {
						// make sure point is left of line
						if (Geometry.crossProduct(i1, intersection, v) > 0) {
							newCell.borders.add(new Line(i1, intersection, currCell));
							newBorder.add(new Line(i1, intersection, newCell));
							next = currLine.neigh;
						} else {
							newCell.borders.add(new Line(intersection, i1, currCell));
							newBorder.add(new Line(intersection, i1, newCell));
							next = l1.neigh;
						}
						// add the bisected part of the intersected lines to the
						// updated border of currCell
						Vertex tmp = (Geometry.closerTo(currLine.start, v, currCell.generator) == currCell.generator) ? currLine.start : currLine.end;
						newBorder.add(new Line(intersection, tmp, currLine.neigh));
						tmp = (Geometry.closerTo(l1.start, v, currCell.generator) == currCell.generator) ? l1.start : l1.end;
						newBorder.add(new Line(i1, tmp, l1.neigh));
					}
				}

				// update borders. Add to borders if line is completely closer
				// to currCell
				if (Geometry.closerTo(currLine.start, currCell.generator, v) == currCell.generator
						&& Geometry.closerTo(currLine.end, currCell.generator, v) == currCell.generator)
					newBorder.add(currLine);
			}
			// detect error cases
			if (numIntersections != 2) {
				System.out.printf("Skipped degenerate cell (%.2f, %.2f) [wrong # of intersections: %d]\n", v.x, v.y, numIntersections);
				// revert changes
				for (Map.Entry<Cell, ArrayList<Line>> k : visited.entrySet())
					k.getKey().borders = k.getValue();
				// the simple modification for avoiding errors as mentioned in the report:
				// return this.addVertex(new Vertex(v.x +0.001, v.y+0.001));
				return false;
			} else if (visited.containsKey(currCell)) {
				System.out.printf("Skipped degenerate cell (%.2f, %.2f) [missed cell border]\n", v.x, v.y);
				// revert changes
				for (Map.Entry<Cell, ArrayList<Line>> k : visited.entrySet())
					k.getKey().borders = k.getValue();
				return false;
			}
			// update everything
			visited.put(currCell, currCell.borders);
			currCell.borders = newBorder;
			currCell = next;
		} while (currCell != first);
		addCell(newCell);
		return true;
	}
	
	/**
	 * Adds a new cell to the Voronoi diagram. Assumes that the cell is already
	 * complete (generator and borders properly defined).
	 * 
	 * @param c the Voronoi cell to add.
	 */
	private void addCell(Cell c)
	{
		if (cells.size() >= 3)
			hash.put(c);
		cells.add(c);
	}

	/**
	 * Finds the cell in the current diagram that contains a specified vertex
	 * within its borders.
	 * 
	 * @param v the vertex for which the containing cell is sought.
	 * @return the Voronoi cell containing the vertex.
	 */
	private Cell findCell(Vertex v)
	{
		Cell guess = hash.guessClosest(v);
		Cell curr = (cells.size() <= 3 || guess == null) ? cells.get(cells.size() - 1) : guess;
		double best = Geometry.distSquared(curr.generator, v);
		double old;
		do {
			searches++;
			old = best;
			for (Line vl : curr.borders) {
				double dist = Geometry.distSquared(vl.neigh.generator, v);
				if (dist < best) {
					curr = vl.neigh;
					best = dist;
				}
			}
		} while (old > best);
		return curr;
	}
	
	/**
	 * Helper method for constructors (containing operations common to multiple
	 * constructors). Sets instance variables and adds the three 'special'
	 * initial vertices required by the algorithm.
	 *
	 * @param maxDimension Maximum permitted vertex co-ordinate.
	 */
	private void setup(double maxDimension)
	{
		// Define the Voronoi plane, with the following restrictions:
        // (1) The upper left corner is always (0.0, 0.0).
		// (2) The lower right x and y co-ordinates must be the same
		//     (i.e. the plane must be square).
		boundary = new BoundingBox(0.0, 0.0, maxDimension, maxDimension);
		cells = new ArrayList<Cell>();
		
		// Extract boundary co-ordinates from bounding box.
		double xMin = boundary.xMin;
		double yMin = boundary.yMin;
		double xMax = boundary.xMax;
		double yMax = boundary.yMax;
		
		// Calculate other useful temporary values.
		double xRange = xMax - xMin;
		double yRange = yMax - yMin;
		double xSuper = xRange * SUPER_FACTOR;
		double ySuper = yRange * SUPER_FACTOR;
		
		// Define a 'super' bounding box for calculating bisector lines when
		// adding general vertices. Store as instance variable.
		double xMinBisect = xMin - xSuper * GENERAL_BISECT_FACTOR;
		double yMinBisect = yMin - ySuper * GENERAL_BISECT_FACTOR;
		double xMaxBisect = xMax + xSuper * GENERAL_BISECT_FACTOR;
		double yMaxBisect = yMax + ySuper * GENERAL_BISECT_FACTOR;
		this.bisectorBound
			= new BoundingBox(xMinBisect, yMinBisect, xMaxBisect, yMaxBisect);
		
		// Define a 'super' bounding box for calculating bisector lines when
		// adding the three special vertices.
		double xMinInit = xMin - xSuper * INIT_BISECT_FACTOR;
		double yMinInit = yMin - ySuper * INIT_BISECT_FACTOR;
		double xMaxInit = xMax + xSuper * INIT_BISECT_FACTOR;
		double yMaxInit = yMax + ySuper * INIT_BISECT_FACTOR;
		BoundingBox initBound
			= new BoundingBox(xMinInit, yMinInit, xMaxInit, yMaxInit);
		
		// Add three 'special' vertices, forming a large triangle, and their
		// corresponding Voronoi cells. This will encapsulate any diagram
		// permitted by the vertex boundaries, simplifying computation by
		// avoiding many boundary case problems. We also make sure to keep the
		// intersection point of these three cells in the exact center of the
		// visible diagram, so it doesn't appear skewed.
		
		// Create the three vertices.
		Vertex v1 = new Vertex(xMin + xRange / 2, yMin - ySuper + yRange / 2);
		Vertex v2 = new Vertex(xMax + xSuper - xRange / 2, yMax + ySuper - ySuper / 2);
		Vertex v3 = new Vertex(xMin - xSuper + xRange / 2, yMax + ySuper - ySuper / 2);

		// Create the Voronoi cells to contain these vertices.
		Cell c1 = new Cell(v1, idCell++);
		Cell c2 = new Cell(v2, idCell++);
		Cell c3 = new Cell(v3, idCell++);
		
		// Find the bisectors that will determine the cell borders.
		Line l1 = (new Line(v1, v2)).bisector(initBound);
		Line l2 = (new Line(v2, v3)).bisector(initBound);
		Line l3 = (new Line(v1, v3)).bisector(initBound);
		
		// Find the intersections of the bisectors.
		Vertex i1 = l1.intersection(l2);
		Vertex i2 = l2.intersection(l3);
		Vertex i3 = l1.intersection(l3);
		
		// Derive the actual borders from the bisectors and intersections.
		c1.borders.add(new Line(l1.start, i1, c2));
		c2.borders.add(new Line(l1.start, i1, c1));
		c2.borders.add(new Line(i2, l2.end, c3));
		c3.borders.add(new Line(i2, l2.end, c2));
		c1.borders.add(new Line(l3.start, i3, c3));
		c3.borders.add(new Line(l3.start, i3, c1));
		
		// Add the finished cells to the diagram.
		addCell(c1);
		addCell(c2);
		addCell(c3);
	}
}
