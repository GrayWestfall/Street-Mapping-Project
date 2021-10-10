/* Code & Commentary by Grayson Westfall */

import javax.swing.*;
import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Color;

// JFrame subclass to display the graphed map visually
public class MapFrame extends JFrame {
		
	protected MapComponent mapComponent; // Has a personal JPanel
	
	// Class that defines the personal JPanel
	protected class MapComponent extends JPanel {
		
		protected double minX = Double.POSITIVE_INFINITY; // The minimum current longitude value
		protected double minY = Double.POSITIVE_INFINITY; // The minimum current latitude value
		protected double maxX = Double.NEGATIVE_INFINITY; // The maximum current longitude value
		protected double maxY = Double.NEGATIVE_INFINITY; // The maximum current latitude value
		
		// ArrayList of arrays of coordinates each formatted as follows {longitude1, latitude1, longitude2, latitude2}
		protected ArrayList<double[]> coordinates = new ArrayList<double[]>(); 
		
		// Constructor calls addStreets with the edge bag 
		public MapComponent(Bag<StreetEdge> edges) {
			addStreets(edges);
		}
		
		@Override
		// paintComponent just calls drawStreets
		public void paintComponent(Graphics g) {
			drawStreets(g);
		}
		
		/* Draws all streets using the coordinates we've
		 * added and uses the current width and height 
		 * of the frame to help scale the image */
		protected void drawStreets(Graphics g) {
			
			int padding = 10;										// 10-pixel padding
			double mapWidthRatio = (getWidth() - padding) / maxX;	// create ratio of image according to width of frame
			double mapHeightRatio = (getHeight() - padding) / maxY; // create ratio of image according to height of frame
			
			/* Take minimum of the two ratios to scale them equally 
			 * and still fit the entire image in the frame */
			double globalRatio = Math.min(mapWidthRatio, mapHeightRatio);
			
			// Padding for width and height that make sure image is centered
			double heightPadding = (getHeight() - (globalRatio * maxY)) / 2;
			double widthPadding = (getWidth() - (globalRatio * maxX)) / 2;
			
			for (double[] coordinateSet : coordinates) {
				
				g.setColor(Color.BLACK); // Reset color to black if previously set to red
				
				// Create new X and Y values using the ratios and padding
				int adjustedX1 = (int) (widthPadding + (coordinateSet[0] * globalRatio));
				int adjustedY1 = (int) (getHeight() - heightPadding - (coordinateSet[1] * globalRatio)); // Subtract from getHeight() since it starts at top left
				int adjustedX2 = (int) (widthPadding + (coordinateSet[2] * globalRatio));
				int adjustedY2 = (int) (getHeight() - heightPadding - (coordinateSet[3] * globalRatio));
				
				// If the associated edge was red
				if (coordinateSet[4] < 0) {
					g.setColor(Color.RED);
				}
				
				// Draw the line with the new values
				g.drawLine(adjustedX1, adjustedY1, adjustedX2, adjustedY2);
			}
		}
		
		// Add coordinate sets to the 'coordinates' field for each street
		protected void addStreets(Bag<StreetEdge> edges) {

			// For each edge of the graph
			for (StreetEdge street : edges) { 
				
				// Omits double edge mistake in input for ur.txt
				if (street.edgeName.equals("r15")) {
					continue;
				}
				
				// Call conversion function on each value to convert it to radians from degrees
				double longitude1 = longitudeConvert(street.v.longitude);
				double latitude1 = latitudeConvert(street.v.latitude);
				double longitude2 = longitudeConvert(street.w.longitude);
				double latitude2 = latitudeConvert(street.w.latitude);
				
				/* Number to be put in coordinate set
				 * that will tell the color of the edge */
				double colorNum;
				if (street.color == Color.BLACK) {
					colorNum = Double.POSITIVE_INFINITY;
				}
				else {
					colorNum = Double.NEGATIVE_INFINITY;
				}
			
				// Reassign minX and minY values using the new edge
				minX = Math.min(minX, Math.min(longitude1, longitude2));
				minY = Math.min(minY, Math.min(latitude1, latitude2));
			
				// Add the new set of coordinates to 'coordinates'
				double[] coordinateSet = {longitude1, latitude1, longitude2, latitude2, colorNum};
				coordinates.add(coordinateSet);
			}

			/* After creating all sets of coordinates, rescale
			 * them using the minimum values (this will set the
			 * minimum values to 0 in the process) */
			for (double[] coordinateSet : coordinates) {
				coordinateSet[0] = coordinateSet[0] - minX;
				coordinateSet[1] = coordinateSet[1] - minY;
				coordinateSet[2] = coordinateSet[2] - minX;
				coordinateSet[3] = coordinateSet[3] - minY;
				
				// Assign values for maxX and maxY to be used in scaling the image later
				maxX = Math.max(maxX, Math.max(coordinateSet[0], coordinateSet[2]));
				maxY = Math.max(maxY, Math.max(coordinateSet[1], coordinateSet[3]));
			}
		}
		
		// Convert longitude value argument from degrees to radians
		private double longitudeConvert(double longitude) {
			longitude = longitude * (Math.PI / 180);
			return longitude;
		}
		
		// Convert longitude value argument from degrees to radians
		private double latitudeConvert(double latitude) {
			latitude = latitude * (Math.PI / 180);
			latitude = Math.log(Math.tan((Math.PI/4) + 0.5 * latitude));
			return latitude;
		}
	}
	
	/* Constructor initializes values
	 * for the frame as well as creating
	 * the personal mapComponent */
	protected MapFrame(UndirectedGraph graph) {
		setSize(500, 500);
		setTitle("Street Mapping");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mapComponent = new MapComponent(graph.edges);
		add(mapComponent);
	}

}
