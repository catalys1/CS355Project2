package cs355;

import java.awt.Point;
import java.awt.geom.Point2D;


public class PointOps {
	
	// Convert a point to a Point2D.Double
	public static Point2D.Double toDouble(Point x) {
		return new Point2D.Double((double)x.x, (double)x.y);
	}
	
	// Return the negative of a point
	public static Point2D.Double negate(Point2D.Double x) {
		return new Point2D.Double(-x.x, -x.y);
	}
	
	// Return a scaled version of, scaled by c
	public static Point2D.Double scale(Point2D.Double x, double c) {
		return new Point2D.Double(x.x * c, x.y * c);
	}
	
	// Add two points together
	public static Point2D.Double add(Point2D.Double a, Point2D.Double b) {
		return new Point2D.Double(a.x+b.x, a.y+b.y);
	}
	
	// Subtract point b from point a
	public static Point2D.Double subtract(Point2D.Double a, Point2D.Double b) {
		return new Point2D.Double(a.x-b.x, a.y-b.y);
	}
	
	// Return a unit vector in the direction of the vector x
	public static Point2D.Double normalize(Point2D.Double x) {
		double magnitude = Math.sqrt(x.x*x.x + x.y*x.y);
		return new Point2D.Double(x.x/magnitude, x.y/magnitude);
	}
	
	// Return a vector perpendicular to vector x
	public static Point2D.Double perp(Point2D.Double x) {
		return new Point2D.Double(-x.y, x.x);
	}
	
	// Return a vector normal to vector x (perpendicular unit vector)
	public static Point2D.Double normal(Point2D.Double x) {
		Point2D.Double y = perp(x);
		return normalize(y);
	}
	
	// Return the dot product, a dot b
	public static double dot(Point2D.Double a, Point2D.Double b) {
		return (a.x*b.x) + (a.y*b.y);
	}

}
