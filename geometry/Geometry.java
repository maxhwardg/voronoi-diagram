package geometry;

/**
 * A static class holding some general geometric functions and constants.
 * 
 * @author Max Ward-Graham, Bon Sawyer
 */
public class Geometry
{
	/** Epsilon value for floating-point comparisons. */
	public static final double EPSILON = 0.0000001;

	/**
	 * Given an initial vertex, determines which of two other specified
	 * vertices is closest to it, in terms of Euclidean distance.
	 * 
	 * @param to vertex from which to measure distances of other two vertices.
	 * @param v1 the first of two vertices being compared.
	 * @param v2 the second of two vertices being compared.
	 * @return the vertex (either v1 or v2) closest to the initial vertex.
	 */
	public static Vertex closerTo(Vertex to, Vertex v1, Vertex v2)
	{
		double d1 = distSquared(to, v1);
		double d2 = distSquared(to, v2);
		return (d1 < d2) ? v1 : v2;
	}
	
	/**
	 * Calculates the cross product of two vertices, with respect to another
	 * initial vertex.
	 *
	 * @param v1 vertex with respect to which cross product will be calculated.
	 * @param v2 first vertex of cross product calculation.
	 * @param v3 second vertex of cross product calculation.
	 * @param the calculated cross product.
	 */
	public static double crossProduct(Vertex v1, Vertex v2, Vertex v3)
	{
		return (v2.x - v1.x) * (v3.y - v1.y) - (v2.y - v1.y) * (v3.x - v1.x);
	}
	
	/**
	 * Calculates the Euclidean distance between two vertices.
	 * 
	 * @param v1 the first vertex.
	 * @param v2 the second vertex.
	 * @return the Euclidean distance between the vertices.
	 */
	public static double distance(Vertex v1, Vertex v2)
	{
		return Math.sqrt(distSquared(v1, v2));
	}

	/**
	 * Calculates the squared Euclidean distance between two vertices.
	 * Useful for distance comparisons due to the omission of costly square
	 * root calculations.
	 * 
	 * @param v1 the first vertex.
	 * @param v2 the second vertex.
	 * @return the squared Euclidean distance between the vertices.
	 */	
	public static double distSquared(Vertex v1, Vertex v2)
	{
		return (v2.x - v1.x) * (v2.x - v1.x) + (v2.y - v1.y) * (v2.y - v1.y);
	}
	
	/**
	 * Given an initial vertex, determines which of two other specified
	 * vertices is further from it, in terms of Euclidean distance.
	 * 
	 * @param to vertex from which to measure distances of other two vertices.
	 * @param v1 the first of two vertices being compared.
	 * @param v2 the second of two vertices being compared.
	 * @return the vertex (either v1 or v2) furthest from the initial vertex.
	 */
	public static Vertex furthestFrom(Vertex to, Vertex v1, Vertex v2)
	{
		double d1 = distSquared(to, v1);
		double d2 = distSquared(to, v2);
		return (d1 < d2) ? v2 : v1;
	}
}
