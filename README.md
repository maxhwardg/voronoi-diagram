------
Voronoi Tessalations and Visualisations
-----

Implementation of an incremental voronoi diagram algorithm. O(n^2) worst case, O(n) expected case.

-----------
Compilation
-----------
The program consists of Java source code, utilising only standard libraries.

To install, please unzip the included files, retaining the 'geometry' and
'samples' subdirectories (which respectively contain a custom package of
geometry classes, and some example test data).

The main class from which to launch the program is "Voronoi.java".
To compile using the standard java compile, simply enter the following on a
command line, from the directory location of Voronoi.java:

	javac Voronoi.java

---------
Execution
---------
The program can be run in any of three modes, described below. All modes launch
a visualiser, and permit the user to add new vertices by clicking on the
desired locations in the visualiser window.

(1) "File" mode: reads plane size and vertex co-ordinates from a specified text
file, formatted as follows:
	- First line: single number indicating plane size (width and height)
	- Subsequent lines: two numbers per line, separated by whitespace, each
	  pair of numbers representing a vertex's x and y co-ordinates.

	Command line syntax:
		java Voronoi F [filepath]
  
(2) "Blank" mode: displays an empty diagram (enabling the user to build a new
diagram from scratch using the mouse).
	
	Command line syntax:
		java Voronoi B

(3) "Random" mode: generates a specified number of random vertices:
	
	Command line syntax:
		java Voronoi R [number of vertices]

[Note: a condensed version of the above can be displayed by typing
	java Voronoi
to display 'usage' information.]

-----------
Sample data
-----------
Test data similar to that described in the "Empirical Testing" document
(submitted for this project alongside the source code) is included in the
following files:

	samples/random.txt
	samples/skewleft.txt
	samples/spiral.txt
	samples/topleft.txt

They can be displayed using the standard 'file mode' procedure described above.
For example:
	
	java Voronoi F samples/spiral.txt
===============
