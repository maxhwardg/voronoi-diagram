package geometry;

/**
 * A class representing a bounding box in 2D space, defined by the co-ordinates
 * of its upper-left and lower-right corner vertices.
 * 
 * @author Max Ward-Graham, Bon Sawyer
 */
public class BoundingBox
{
	/** Epsilon value for floating-point comparisons. */
	private static final double EPSILON = Geometry.EPSILON;
	
	/** The upper-left x-coordinate. */
	public double xMin;
	
	/** The upper-left y-coordinate. */
	public double yMin;
	
	/** The lower-right x-coordinate. */
	public double xMax;
	
	/** The lower-right y-coordinate. */
	public double yMax;
	
	/**
	 * Constructs a new bounding box from specified upper-left and lower-right
	 * corner co-ordinates.
	 * 
	 * @param xMin the upper left x-coordinate.
	 * @param xMin the upper left y-coordinate.
	 * @param xMin the lower right x-coordinate.
	 * @param xMin the lower right y-coordinate.
	 */
	public BoundingBox(double xMin, double yMin, double xMax, double yMax)
	{
		this.xMin = xMin;
		this.yMin = yMin;
		this.xMax = xMax;
		this.yMax = yMax;
	}

	/**
	 * Constructs a new bounding box from specified upper-left and lower-right
	 * corner vertices.
	 * 
	 * @param topLeft the top-left vertex of the bounding box.
	 * @param bottomRight the bottom-right vertex of the bounding box.
	 */
	public BoundingBox(Vertex topLeft, Vertex bottomRight)
	{
		this(topLeft.x, topLeft.y, bottomRight.x, bottomRight.y);
	}

	/**
	 * Checks whether a specified vertex falls within this bounding box.
	 * 
	 * @param v the vertex to compare to the bounding box.
	 * @return true if the vertex is within the bounds, false otherwise.
	 */	
	public boolean contains(Vertex v)
	{
		return ((v.x > xMin - EPSILON) && (v.x < xMax + EPSILON)
			&& (v.y > yMin - EPSILON) && (v.y < yMax + EPSILON));
	}
	
	/**
	 * Returns a string representation of this bounding box, in the format
	 * "(x1, y1)->(x2, y2)" (where x1, y1 is top-left vertex, and x2, y2 is
	 * bottom-right vertex).
	 *
	 * @return the String representation of the bounding box.
	 */
	@Override public String toString()
	{
		return String.format("[(%f, %f)->(%f, %f)]", xMin, yMin, xMax, yMax);
	}
}