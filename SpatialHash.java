import geometry.*;

/**
 * A class encapsulating a spatial hash table, allowing for quick guesses of
 * the nearest neighbours of Voronoi cells.
 * 
 * @author Max Ward-Graham, Bon Sawyer
 */
public class SpatialHash
{
	/** True if the structure can be resized, false otherwise. */
	private boolean allowResize;
	
	/** Total number of cells. */
	private int cells = 0;
	
	/** True if the structure is empty, false otherwise. */
	private boolean empty;
	
	/** The number of failed guesses. */
	public int misses = 0;

	/** The size of each bucket. */
	private int size;

	/** The lookup table. */
	private Cell[][] space = null;
	
	/** The size of the space mapped over. */
	private int totalSize;
	
	/**
	 * An array of table location offsets, which can be iterated to find a
	 * location's immediate neighbours, including the initial location itself.
	 */
	private static final int[][] offsets = {
		{0, 0}, {1, 0}, {0, 1}, {-1, 0}, {0, -1},
		{1, 1}, {-1, 1}, {1, -1}, {-1, -1}
	};
	
	/**
	 * Constructs a spatial hash that will resize itself to fit any size diagram.
	 * This works much like an ArrayList, and should have 
	 * O(1) amortized performance for all operations.
	 * Useful for an interactive Voronoi Diagram, when we
	 * don't know how many points will be added.
	 * 
	 * @param s The size of the plane over which we are hashing.
	 */
	public SpatialHash(int s) {
		totalSize = s;
		allowResize = true;
		empty = true;
		resize(10);
	}

	/**
	 * Constructs a spatial hash with a specific number of buckets.
	 * This kind of spatial hash will never resize itself.
	 * Useful if we are constructing a Voronoi Diagram for
	 * a set number of source points.
	 * 
	 * @param init The initial size of the hash.
	 * @param s The size of the plane over which we are hashing.
	 */
	public SpatialHash(int init, int s) {
		totalSize = s;
		allowResize = false;
		empty = true;
		resize(init);
	}
	
	/**
	 * Estimates the closest cell to the specified vertex.
	 * 
	 * @param v the vertex for which to guess the closest cell.
	 * @return the Voronoi cell estimated to be closest to the vertex.
	 */
	public Cell guessClosest(Vertex v)
	{
		if (empty == true)
			return null;
		
		// Convert vertex co-ordinates to table location.
		int i = ((int) v.x) / size;
		int j = ((int) v.y) / size;
		
		// Check this cell and all adjacent cells.
		for (int[] os : offsets) {
			int a = i + os[0];
			int b = j + os[1];
			if (isValid(a, b)) {
				return space[a][b];
			}
		}
		misses++;
		return null;
	}
	
	/**
	 * Places the specified cell into the spatial hash table.
	 * 
	 * @param c the Voronoi cell to place spatially into the hash table.
	 */
	public void put(Cell c)
	{
		cells++;
		if (allowResize && cells > (space.length * space.length) * 3) {
			resize(cells * 10);
		}
		int i = ((int) c.generator.x) / size;
		int j = ((int) c.generator.y) / size;
		
		if (space[i][j] == null) {
			space[i][j] = c;
			empty = false;
		}
	}

	/**
	 * Determines whether a specified table location is valid. A valid table
	 * location is within the table's bounds, and contains a non-null value.
	 *
	 * @param i the horizontal (x-associated) table location to check.
	 * @param j the vertical (y-associated) table location to check.
	 * @return true if table location within table bounds and non-null,
	 *     false otherwise.
	 */
	private boolean isValid(int i, int j)
	{
		return (i >= 0 && i < space.length && j >= 0 && j < space.length
			&& space[i][j] != null);
	}
	
	/**
	 * Resizes the bucket array and hash function.
	 * 
	 * @param n The number of buckets to resize to.
	 */
	private void resize(int n) {
		int buckets = (int) Math.sqrt(n);
		size = (int) Math.ceil((double) totalSize / buckets);
		Cell[][] tmp = space;
		space = new Cell[buckets + 1][buckets + 1];
		if (tmp != null) {
			for (int i = 0; i < tmp.length; i++)
				for (int j = 0; j < tmp.length; j++)
					if (tmp[i][j] != null)
						put(tmp[i][j]);

		}
	}
}