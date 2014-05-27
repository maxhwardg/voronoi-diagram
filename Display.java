import geometry.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.util.ArrayList;

/**
 * A simple visualiser displaying a Voronoi diagram, and allowing the end user
 * to add new vertices via mouse clicks.
 * 
 * @author Max Ward-Graham, Bon Sawyer
 */
class Display extends JPanel implements MouseListener
{	
	/** Colour of Voronoi diagram background. */
	private static final Color COLOR_BACKGROUND = Color.BLACK;
	
	/** Colour of Voronoi cell borders. */
	private static final Color COLOR_BORDER = Color.RED;
	
	/** Colour of Voronoi generators. */
	private static final Color COLOR_VERTEX = Color.WHITE;
	
	/** Diameter of circles used to show vertices. (Odd value recommended.) */
	private static final int VERTEX_WIDTH = 3;

	/** Title of window containing visualiser. */
	private static final String WINDOW_TITLE = "Voronoi";
	
	/** Voronoi diagram to display. */
	private Diagram diag;
	
	/** Maximum vertex co-ordinate. */
	private double maxDimension;
	
	/**
	 * Scale factor for converting between Voronoi co-ordinates and
	 * visualiser pixel co-ordinates.
	 */
	private double scale;
	
	/** Current pixel size (width and height) of visualiser. */
	private int size;
	
	/**
	 * Constructs and displays a new visualiser from a Voronoi diagram.
	 * 
	 * @param diag the Voronoi diagram to display.
	 * @param size the initial pixel size (width and height) of visualiser.
	 * @param maxDimension the size (width and height) of the Voronoi plane.
	 */
	Display(Diagram diag, int size, double maxDimension)
	{
		super();
		this.diag = diag;
		
		// Set up initial size and scale attributes.
		this.maxDimension = maxDimension;
		this.scale = (double) size / maxDimension;
		this.size = size;
		
		// Set up listener for mouse events.
		this.addMouseListener(this);
		
		// Create and display application window.
		JFrame window = new JFrame(WINDOW_TITLE);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.getContentPane().setPreferredSize(new Dimension(size, size));
		window.getContentPane().add(this);
		window.pack();
		window.setVisible(true);
		window.repaint();
	}
	
	/**
	 * Paints current diagram in visualiser panel. Required for extending
	 * JPanel class.
	 * 
	 * @param g Graphics context required for drawing.
	 */
	public void paint(Graphics g)
	{
		// Check visualiser's current size, in case user has resized window.
		// Update size and scale variables as required.
		Dimension d = this.getSize();
		int width = d.width;
		int height = d.height;
		size = Math.min(width, height);
		scale = (double) size / maxDimension;
		
		// Ensure no drawing outside Voronoi plane.
		g.setClip(0, 0, size, size);
		
		// Draw plane background.
		g.setColor(COLOR_BACKGROUND);
		g.fillRect(0, 0, size, size);
		
		// Get Voronoi cells.		
		ArrayList<Cell> cells = diag.getCells();
		int numCells = cells.size();
		
		// Draw all but the first three cells (which are the incremental
		// algorithm's 'special' cells, not intended to be displayed).
		for (int i = 3; i < numCells; i++) {
			Cell c = cells.get(i);
			// Draw generator vertex
			g.setColor(COLOR_VERTEX);
			drawVertex(c.generator, g);
			// Draw cell borders
			g.setColor(COLOR_BORDER);
			for (Line l : c.borders) {
				drawLine(l.start, l.end, g);
			}
		}
	}
	
	/**
	 * Draws a Voronoi vertex as a filled circle.
	 *
	 * @param v vertex to be drawn.
	 * @param g Graphics context required for drawing.
	 */
	private void drawVertex(Vertex v, Graphics g)
	{
		// Offset used to correctly position vertex, so that vertex location is
		// at centre of circle (not at top-left).
		final int offset = (VERTEX_WIDTH - 1) / 2;
		// Translate from Voronoi co-ordinates to visualiser co-ordinates.
		int i = (int) Math.rint(scale * v.x);
		int j = (int) Math.rint(scale * v.y);
		g.drawOval(i - offset, j - offset, VERTEX_WIDTH - 1, VERTEX_WIDTH - 1);
		g.fillOval(i - offset, j - offset, VERTEX_WIDTH - 1, VERTEX_WIDTH - 1);
	}
	
	/**
	 * Draws a line segment between two vertex locations.
	 *
	 * @param v1 First endpoint of line.
	 * @param v1 Second endpoint of line.
	 * @param g Graphics context required for drawing.
	 */
	private void drawLine(Vertex v1, Vertex v2, Graphics g)
	{
		int i1 = (int) Math.rint(scale * v1.x);
		int j1 = (int) Math.rint(scale * v1.y);
		int i2 = (int) Math.rint(scale * v2.x);
		int j2 = (int) Math.rint(scale * v2.y);
		g.drawLine(i1, j1, i2, j2);
	}

	/**
	 * Processes mouse click. If click was within visualiser display area,
	 * a new point is passed to the Voronoi diagram, adding a new cell.
	 */
	@Override public void mousePressed(MouseEvent e)
	{
		int i = e.getX();
		int j = e.getY();
		
		if (i >= size || j >= size)
			return;
		
		double x = (double) i / scale;
		double y = (double) j / scale;
		diag.addVertex(new Vertex(x, y));
		
		this.repaint();
	}
	
	@Override public void mouseReleased(MouseEvent e)
	{
		// Do nothing
	}
	
	@Override public void mouseExited(MouseEvent e)
	{
		// Do nothing
	}
	
	@Override public void mouseEntered(MouseEvent e)
	{
		// Do nothing
	}
	
	@Override public void mouseClicked(MouseEvent e)
	{
		// Do nothing
	}
}