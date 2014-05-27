package geometry;

import java.util.ArrayList;

/**
* A class representing a cell in a Voronoi diagram.
* 
* @author Max Ward-Graham, Bon Sawyer
*/
public class Cell {
	/** Unique identifier of this cell. Good for perfect hashing. */
	public int id;
	
	/** The generator (associated vertex) of this cell. */
	public Vertex generator;
	
	/** The line segments bounding this cell. */
	public ArrayList<Line> borders;

	/**
	 * Constructs a new cell with the specified vertex as its generator, and
	 * the specified integer as its unique identifier.
	 * 
	 * @param generator the generator vertex of this cell.
	 * @param id the cell's unique identifier for perfect hasing purposes.
	 */
	public Cell(Vertex generator, int id)
	{
		this.generator = generator;
		this.id = id;
		borders = new ArrayList<Line>();
	}

	/**
	 * Returns the cell's unique identifier (as set at construction time).
	 * 
	 * @return the unique identifier of the cell.
	 */
	@Override public int hashCode()
	{
		return id;
	}

	/**
	 * Returns a string representation of the cell, equivalent to the string
	 * representation of the cell's generator.
	 * 
	 * @return the string representation of the cell's generator.
	 */
	@Override public String toString()
	{
		return generator.toString();
	}
}