package cs355.controller;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import cs355.PointOps;
import cs355.model.drawing.*;

public class HandleHitTester {
	
	public static final int HANDLE_SIZE = 16;
	public static final int HANDLE_DISPLACEMENT = 10;

	public static boolean handleHitTest(Shape s, Point2D.Double worldPoint, double tolerance) {
		
		double offx = 0;
		double offy = HANDLE_DISPLACEMENT + HANDLE_SIZE/2;
		if (s instanceof Square) {
			offy += ((Square)s).getSize() / 2;
		}
		else if (s instanceof Rectangle) {
			offy += ((Rectangle)s).getHeight() / 2;	
		}
		else if (s instanceof Circle) {
			offy += ((Circle)s).getRadius();
		}
		else if (s instanceof Ellipse) {
			offy += ((Ellipse)s).getHeight() / 2;
		}
		else if (s instanceof Triangle) {
			Triangle triangle = (Triangle)s;
			Point2D.Double n = PointOps.add(triangle.getA(), PointOps.scale(PointOps.normalize(triangle.getA()), 20));
			offy = -n.y;
			offx = n.x;
		}
		
		Point2D.Double objPt = new Point2D.Double();
		transformPoint(worldPoint, objPt, s);
		
		Point2D.Double hc = new Point2D.Double(offx, -offy);
		Point2D.Double dif = PointOps.subtract(objPt, hc);
		double tol = HANDLE_SIZE/2 + tolerance;
		
		if (Math.abs(dif.x) <= tol && Math.abs(dif.y) <= tol)
			return true;
		
		return false;
	}
	
	public static int lineHandleHitTest(Line l, Point2D.Double worldPoint, double tolerance) {
		Point2D.Double objPt = new Point2D.Double();
		transformPoint(worldPoint, objPt, l, 0, 0);
		
		double tol = HANDLE_SIZE/2 + tolerance;
		if (Math.abs(objPt.x) <= tol && Math.abs(objPt.y) <= tol)
			return 1;

		transformPoint(worldPoint, objPt, l, l.getCenter().x - l.getEnd().x, l.getCenter().y - l.getEnd().y);
		if (Math.abs(objPt.x) <= tol && Math.abs(objPt.y) <= tol)
			return 2;
		
		return 0;
	}
	
	private static void transformPoint(Point2D.Double worldPoint, Point2D.Double objectPoint, Shape s) {
		AffineTransform worldToObj = new AffineTransform();
		worldToObj.rotate(-s.getRotation());
		worldToObj.translate(-s.getCenter().x, -s.getCenter().y);
		worldToObj.transform(worldPoint, objectPoint);
	}
	
	private static void transformPoint(Point2D.Double worldPoint, Point2D.Double objectPoint, Shape s, double offx, double offy) {
		AffineTransform worldToObj = new AffineTransform();
		worldToObj.rotate(-s.getRotation());
		worldToObj.translate(-(s.getCenter().x-offx), -(s.getCenter().y-offy));
		worldToObj.transform(worldPoint, objectPoint);
	}
}
