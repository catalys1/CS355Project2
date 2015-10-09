package cs355.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
//import cs355.model.drawing.Shape;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import cs355.model.drawing.Shape;

public class DrawableShape {
	
	public static Color borderColor = Color.red;
	public static Stroke borderStroke = new BasicStroke(2);
	
	private Shape shape;
	
	public DrawableShape(Shape s) {
		shape = s;
	}

	public void draw(Graphics2D g2d, boolean outline) {
		AffineTransform objToWorld = new AffineTransform();
		objToWorld.translate(shape.getCenter().x, shape.getCenter().y);
		objToWorld.rotate(shape.getRotation());
		g2d.setTransform(objToWorld);
	}
	
	
}
