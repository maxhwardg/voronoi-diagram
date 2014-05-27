package geometry;

/**
 * A class representing a single vertex in 2D space.
 * 
 * @author Max Ward-Graham, Bon Sawyer
 */
public class Vertex
{
	/** Epsilon value for floating-point comparisons. */
	private static final double EPSILON = Geometry.EPSILON;
	
	/** The vertex's x-coordinate. */
	public double x;
	
	/** The vertex's y-coordinate. */
	public double y;
	
	/**
	 * Constructs a vertex at the specified (x, y) location.
	 * 
	 * @param x the vertex's x-coordinate.
	 * @param y the vertex's y-coordinate. 
	 */
	public Vertex(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	/**
	 * Returns a String representation of this vertex, in the format "(x, y)".
	 * 
	 * @return the string representation of the vertex.
	 */
	@Override public String toString()
	{
		return String.format("(%f, %f)", x, y);
	}

	/**
	 * Determines whether or not two vertices are equal. Two instances of
	 * Vertex are considered equal if their x and y values differ by no more
	 * than the epsilon value specified in the 'Geometry' class.
	 * 
	 * @param obj the object to be compared with this vertex
	 * @return true if the compared object is an instance of Vertex and has
	 *     equivalent x, y values; false otherwise.
	 */	
	@Override public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		
		// Check that other object is of the correct type.
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		// Compare x and y values.
		Vertex other = (Vertex) obj;
		return (Math.abs(x - other.x) < EPSILON)
			&& (Math.abs(y - other.y) < EPSILON);
	}
}
