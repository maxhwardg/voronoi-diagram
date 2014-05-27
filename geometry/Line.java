package geometry;

/**
 * A class representing a line segment in 2D space.
 * Optionally contains a reference to a neighbouring cell in a Voronoi diagram.
 * 
 * @author Max Ward-Graham, Bon Sawyer
 */
public class Line
{
	/** Epsilon value for floating-point comparisons. */
	private static final double EPSILON = Geometry.EPSILON;
	
	/** First endpoint of the line segment. */
	public Vertex start;
	
	/** Second endpoint of the line segment. */
	public Vertex end;
	
	/** The neighbouring Voronoi cell containing an equivalent line segment. */
	public Cell neigh;
	
	/**
	 * Constructs a line segment from two specified vertices, setting its
	 * neighbouring Voronoi cell reference to 'null'.
	 * 
	 * @param start the first endpoint of the new line segment.
	 * @param end the second endpoint of the new line segment.
	 */
	public Line(Vertex start, Vertex end)
	{
		this.start = start;
		this.end = end;
		neigh = null;
	}

	/**
	 * Constructs a line segment from two specified vertices, setting its
	 * neighbouring Voronoi cell reference to a specified cell.
	 * 
	 * @param start the first endpoint of the new line segment.
	 * @param end the second endpoint of the new line segment.
	 * @param neigh the neighbouring Voronoi cell.
	 */
	public Line(Vertex start, Vertex end, Cell neigh)
	{
		this.start = start;
		this.end = end;
		this.neigh = neigh;
	}
	
	/**
	 * Returns a String representation of this line segment, in the format
	 * "[(x, y) -> (x, y)]".
	 * 
	 * @return the string representation of the line segment.
	 */
	@Override public String toString()
	{
		return String.format("[%s -> %s]", start.toString(), end.toString());
	}

	/**
	 * Finds the perfect bisector of this line, returning it as a new line.
	 * 
	 * @param xMin x-value of minimum bound for new line.
	 * @param yMin y-value of minimum bound for new line.
	 * @param xMax x-value of maximum bound for new line.
	 * @param yMax y-value of maximum bound for new line.
	 * 
	 * @return a new line segment representing the bisector of this line.
	 */
	public Line bisector(double xMin, double yMin, double xMax, double yMax)
	{
		// Calculate the equation of the perpendicular line. A line is defined
		// by 'y = mx + c' where m is the gradient, and c is some constant.
		
		// Compute mid point, which perpendicular line with pass through.
		double xMid = (start.x + end.x) / 2;
		double yMid = (start.y + end.y) / 2;
		
		// Check for co-linear points.
		if (Math.abs(start.x - end.x) < EPSILON)
			return new Line(new Vertex(xMin, yMid), new Vertex(xMax, yMid));
		else if (Math.abs(start.y - end.y) < EPSILON)
			return new Line(new Vertex(xMid, yMin), new Vertex(xMid, yMax));
		
		// Compute gradient and c for bisector line.
		double m = -(end.x - start.x) / (end.y - start.y);
		double c = yMid - (m * xMid);
		
		// Check to ensure line segment is within min/max bounds.
		// We assume the line travels from yMin to yMax, then check if the line
		// travels left to right or vice-versa, and clip it appropriately.
		
		// Determine first vertex:
		double x1 = (yMin - c) / m;
		double y1 = yMin;
		if (x1 < xMin) {
			x1 = xMin;
			y1 = m * xMin + c;
		} else if (x1 > xMax) {
			x1 = xMax;
			y1 = m * xMax + c;
		}
		
		// Determine second vertex:
		double x2 = (yMax - c) / m;
		double y2 = yMax;
		if (x2 < xMin) {
			x2 = xMin;
			y2 = m * xMin + c;
		} else if (x2 > xMax) {
			x2 = xMax;
			y2 = m * xMax + c;
		}
		
		return new Line(new Vertex(x1, y1), new Vertex(x2, y2));
	}
	
	public Line bisector(BoundingBox bb)
	{
		return bisector(bb.xMin, bb.yMin, bb.xMax, bb.yMax);
	}
	
	/**
	 * Finds the intersection of this line with another line segment, returning
	 * it as a vertex.
	 *
	 * @param l the other line segment with which to find an intersection.
	 * @return the intersection as a new Vertex object.
	 */
	public Vertex intersection(Line l)
	{
		// Calculate determinant.
		double det = (start.x - end.x) * (l.start.y - l.end.y) - (start.y - end.y) * (l.start.x - l.end.x);
		
		// Check for parallel lines.
		if (Math.abs(det) <= EPSILON)
			return null;

		// Calculate the x, y coordinates of intersection.
		double a = (start.x * end.y - start.y * end.x);
		double b = (l.start.x * l.end.y - l.start.y * l.end.x);
		double x = (a * (l.start.x - l.end.x) - (start.x - end.x) * b) / det;
		double y = (a * (l.start.y - l.end.y) - (start.y - end.y) * b) / det;
		
		// Check that the x and y coordinates are within bounds of both lines,
		// returning 'null' if not.
		if ( (x + EPSILON < Math.min(start.x, end.x))
				|| (x - EPSILON > Math.max(start.x, end.x))
				|| (x + EPSILON < Math.min(l.start.x, l.end.x))
				|| (x - EPSILON > Math.max(l.start.x, l.end.x)) ) {
			return null;
		}
		else if ( (y + EPSILON < Math.min(start.y, end.y))
				|| (y - EPSILON > Math.max(start.y, end.y))
				|| (y + EPSILON < Math.min(l.start.y, l.end.y))
				|| (y - EPSILON > Math.max(l.start.y, l.end.y)) ) {
			return null;
		}
		else {
			return new Vertex(x, y);
		}
	}

}
