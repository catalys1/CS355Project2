package cs355.controller;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.Iterator;

import cs355.GUIFunctions;
import cs355.PointOps;
import cs355.model.drawing.*;
import cs355.view.View;
import cs355.view.ViewRefresher;


/**
 * Controller class. Handles interfacing with the user. Stores relevant GUI values
 * 
 */
public class Controller implements CS355Controller {
	
	public enum shapeType {
		LINE, SQUARE, RECTANGLE, CIRCLE, ELLIPSE, TRIANGLE
	}
	
	public enum actionType {
		DRAW, SELECT
	}
	
	private final double TWO_PI = Math.PI * 2;
	
	// Data members
	private actionType action;			// What action to take when a click happens
	private int activeHandle;			// Indicates if a handle is active, and which one it is (for lines)
	private Shape activeShape;			// Shape being drawn, or selected by user
	private int actShapeIndex;			// Index in the model of the current active shape
	private boolean buildingTri;		// Whether a triangle is being constructed
	private Model model;				// Reference to the model
	private Point2D.Double pivot;		// The mouse pressed point, for drawing a shape
	private Color selectedColor;		// The current drawing color
	private shapeType selectedShape;	// The type of shape being drawn, set by the selected button
	private int triPoints;				// Number of points of the triangle that have been placed
	private ViewRefresher view;			// A reference to the view
	
	
	public Controller(Model m, View v) {
		action = null;
		activeShape = null;
		activeHandle = 0;
		buildingTri = false;
		model = m;
		pivot = null;
		selectedColor = Color.white;
		selectedShape = null;
		triPoints = 0;
		view = v;
	}
	

	@Override
	public void mousePressed(MouseEvent arg0) {

		switch (action) {
		
		case DRAW:
			if (selectedShape != null) {
				createShape(PointOps.toDouble(arg0.getPoint()));			
			}
			break;
		case SELECT:
			pivot = PointOps.toDouble(arg0.getPoint());
			// If there is an active shape, first check to see if a handle was clicked on
			if (activeShape != null) {
				Point2D.Double worldPoint = PointOps.toDouble(arg0.getPoint());
				if (activeShape instanceof Line) {
					activeHandle= HandleHitTester.lineHandleHitTest((Line)activeShape, worldPoint, 2);
				}
				else {
					boolean hand = HandleHitTester.handleHitTest(activeShape, worldPoint, 2);
					if (hand) activeHandle = 1;
				}
			}
			if (activeHandle == 0) {
				int shapeIndex = model.hitTest(PointOps.toDouble(arg0.getPoint()), 4);
				if (shapeIndex >= 0) {
					actShapeIndex = shapeIndex;
					activeShape = model.getShape(shapeIndex);
					GUIFunctions.changeSelectedColor(activeShape.getColor());
				}
				else {
					activeShape = null;
					actShapeIndex = -1;
					GUIFunctions.changeSelectedColor(selectedColor);
					view.update(null, new Object());
				}
				view.update(null, activeShape);
			}
			break;
		}
		
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		
		switch (action) {
		case DRAW:
			if (selectedShape != null) {
				Point2D.Double cpoint = new Point2D.Double(arg0.getPoint().x, arg0.getPoint().y);
				modifyShape(cpoint);
				if (!buildingTri) {
					model.updateShape(activeShape);
				}
			}
			break;
		case SELECT:
			if (activeShape != null) {
				Point2D.Double cpoint = PointOps.toDouble(arg0.getPoint());
				if (activeHandle > 0) {
					moveHandle(cpoint);
				}
				else {
					Point2D.Double delta = PointOps.subtract(cpoint, pivot);
					pivot = cpoint;
					activeShape.setCenter(PointOps.add(activeShape.getCenter(), delta));
					if (activeShape instanceof Line) {
						Line l = (Line)activeShape;
						l.setEnd(PointOps.add(l.getEnd(), delta));
					}
				}
				model.update();
				view.update(null, activeShape);
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		
		switch (action) {
		case DRAW:
			if (selectedShape != null) {
				
				Point2D.Double rpoint = new Point2D.Double(arg0.getPoint().x, arg0.getPoint().y); 
				modifyShape(rpoint);
				pivot = null;
				if (!buildingTri) {
					model.updateShape(activeShape);
					buildingTri = false;
					triPoints = 0;
					activeShape = null;
				}
				else if (triPoints == 3) {
					finishTriangle();
					model.addShape(activeShape);
					buildingTri = false;
					triPoints = 0;
					activeShape = null;
				}
			}
			break;
		case SELECT:
			activeHandle = 0;
			break;
		}
	}
	
	private void moveHandle(Point2D.Double p) {
		if (activeShape instanceof Line) {
			Line l = (Line)activeShape;
			Point2D.Double delta = PointOps.subtract(p, pivot);
			pivot = p;
			if (activeHandle == 1) {
				l.setCenter(PointOps.add(l.getCenter(), delta));
			}
			else if (activeHandle == 2) {
				l.setEnd(PointOps.add(l.getEnd(), delta));
			}
		}
		else {
			Point2D.Double obj = new Point2D.Double();
			AffineTransform t = new AffineTransform();
			t.rotate(-activeShape.getRotation());
			t.translate(-activeShape.getCenter().x, -activeShape.getCenter().y);
			t.transform(p, obj);
			double theta = Math.atan2(obj.x, -obj.y);
			double rotation = 0;
			if (activeShape instanceof Triangle) {
				Triangle triangle = (Triangle)activeShape;
				Point2D.Double n = PointOps.add(triangle.getA(), PointOps.scale(PointOps.normalize(triangle.getA()), 20));
				double y = n.y;
				double x = n.x;
				double phi = Math.atan2(x, -y);
				rotation = activeShape.getRotation() + theta - phi;
			}
			else {
				rotation = activeShape.getRotation() + theta;
			}
			if (rotation > TWO_PI) rotation -= TWO_PI;
			else if (rotation < TWO_PI) rotation += TWO_PI;
			activeShape.setRotation(rotation);
		}
	}

	private void createShape(Point2D.Double p) {
		if (selectedShape != shapeType.TRIANGLE) {
			buildingTri = false;
			triPoints = 0;
		}
		
		Point2D.Double start = new Point2D.Double(p.x, p.y);
		pivot = start;
		
		switch (selectedShape) {
		case LINE:
			activeShape = new Line(selectedColor, start, start); break;
		case SQUARE:
			activeShape = new Square(selectedColor, start, 0); break;
		case RECTANGLE:
			activeShape = new Rectangle(selectedColor, start, 0, 0); break;
		case CIRCLE:
			activeShape = new Circle(selectedColor, start, 0);	break;
		case ELLIPSE:
			activeShape = new Ellipse(selectedColor, start, 0, 0); break;
		case TRIANGLE:
			if (triPoints == 0) {
				buildingTri = true;
				activeShape = new Triangle(selectedColor, start, start, start, start);
				triPoints = 1;
			}
			else if (triPoints == 1) {
				((Triangle)activeShape).setB(start);
				triPoints = 2;
			}
			else if (triPoints == 2) {
				((Triangle)activeShape).setC(start);
				triPoints = 3;
			}
			break;
		}
		
		if (!buildingTri)
			model.addShape(activeShape);
	}
	
	private void modifyShape(Point2D.Double p) {
		switch (selectedShape) {
		case LINE: 
			((Line)activeShape).setEnd(p);
			break;
		case SQUARE:
			modifySquare(p);
			break;
		case RECTANGLE:
			modifyRectangle(p);
			break;
		case CIRCLE:
			modifyCircle(p);
			break;
		case ELLIPSE:
			modifyEllipse(p);
			break;
		case TRIANGLE:
			break;
		}
	}
	
	private void modifySquare(Point2D.Double p) {
		Square sq = (Square)activeShape;
		double xdif = Math.abs(p.x - pivot.x);
		double ydif = Math.abs(p.y - pivot.y);
		double size = xdif < ydif ? xdif : ydif;
		sq.setSize(size); 
		if (p.x < pivot.x) {	// TO the left
			if (p.y < pivot.y)	// Top left quadrant
				sq.setCenter(new Point2D.Double(pivot.x-size/2, pivot.y-size/2));
			else 				// Bottom left quadrant
				sq.setCenter(new Point2D.Double(pivot.x-size/2, pivot.y+size/2));
		}
		else if (p.y < pivot.y) {	// Top right quadrant
			sq.setCenter(new Point2D.Double(pivot.x+size/2, pivot.y-size/2));
		}
		else {						// Bottom right quadrant
			sq.setCenter(new Point2D.Double(pivot.x+size/2, pivot.y+size/2));
		}
		activeShape = sq;
	}
	
	private void modifyRectangle(Point2D.Double p) {
		Rectangle rec = (Rectangle)activeShape;
		double width = Math.abs(p.x - pivot.x);
		double height = Math.abs(p.y - pivot.y);
		rec.setWidth(width);
		rec.setHeight(height);
		if (p.x < pivot.x) {
			if (p.y < pivot.y)
				rec.setCenter(new Point2D.Double(pivot.x-width/2, pivot.y-height/2));
			else 
				rec.setCenter(new Point2D.Double(pivot.x-width/2, pivot.y+height/2));
		}
		else if (p.y < pivot.y) {
				rec.setCenter(new Point2D.Double(pivot.x+width/2, pivot.y-height/2));
		}
		else {
			rec.setCenter(new Point2D.Double(pivot.x+width/2, pivot.y+height/2));
		}
		activeShape = rec;
	}
	
	private void modifyCircle(Point2D.Double p) {
		Circle cir = (Circle)activeShape;
		double xdif = Math.abs(p.x - pivot.x);
		double ydif = Math.abs(p.y - pivot.y);
		double rad = xdif < ydif ? xdif / 2 : ydif / 2;
		cir.setRadius(rad);
		if (p.x < pivot.x) {
			if (p.y < pivot.y)
				cir.setCenter(new Point2D.Double(pivot.x-rad, pivot.y-rad));
			else 
				cir.setCenter(new Point2D.Double(pivot.x-rad, pivot.y+rad));
		}
		else if (p.y < pivot.y) {
			cir.setCenter(new Point2D.Double(pivot.x+rad, pivot.y-rad));
		}
		else {
			cir.setCenter(new Point2D.Double(pivot.x+rad, pivot.y+rad));
		}
		activeShape = cir;
	}
	
	private void modifyEllipse(Point2D.Double p) {
		Ellipse el = (Ellipse)activeShape;
		double width = Math.abs(p.x - pivot.x);
		double height = Math.abs(p.y - pivot.y);
		el.setWidth(width);
		el.setHeight(height);
		width = width/2;
		height = height/2;
		if (p.x < pivot.x) {
			if (p.y < pivot.y)
				el.setCenter(new Point2D.Double(pivot.x-width, pivot.y-height));
			else 
				el.setCenter(new Point2D.Double(pivot.x-width, pivot.y+height));
		}
		else if (p.y < pivot.y) {
			el.setCenter(new Point2D.Double(pivot.x+width, pivot.y-height));
		}
		else {
			el.setCenter(new Point2D.Double(pivot.x+width, pivot.y+height));
		}
	}
	
	private void finishTriangle() {
		Triangle tri = (Triangle)activeShape;
		Point2D.Double a = tri.getA();
		Point2D.Double b = tri.getB();
		Point2D.Double c = tri.getC();
		double cx = (a.x + b.x + c.x) / 3;
		double cy = (a.y + b.y + c.y) / 3; 
		Point2D.Double center = new Point2D.Double(cx, cy);
		a = new Point2D.Double(a.x-cx, a.y-cy);
		b = new Point2D.Double(b.x-cx, b.y-cy);
		c = new Point2D.Double(c.x-cx, c.y-cy);
		tri.setCenter(center);
		tri.setA(a);
		tri.setB(b);
		tri.setC(c);
	}
	
	@Override
	public void colorButtonHit(Color c) {
		selectedColor = c;
		GUIFunctions.changeSelectedColor(c);
		if (activeShape != null) {
			activeShape.setColor(selectedColor);
			model.update();
		}
	}
	
	private void buttonChange(actionType newAction, shapeType newShape, boolean drawButton) {
		action = newAction;
		selectedShape = newShape;
		if (drawButton) {
			activeShape = null;
			actShapeIndex = -1;
			GUIFunctions.changeSelectedColor(selectedColor);
			view.update(null, new Object());
		}
	}

	@Override
	public void lineButtonHit() {
		buttonChange(actionType.DRAW, shapeType.LINE, true);
	}

	@Override
	public void squareButtonHit() {
		buttonChange(actionType.DRAW, shapeType.SQUARE, true);
	}

	@Override
	public void rectangleButtonHit() {
		buttonChange(actionType.DRAW, shapeType.RECTANGLE, true);
	}

	@Override
	public void circleButtonHit() {
		buttonChange(actionType.DRAW, shapeType.CIRCLE, true);
	}

	@Override
	public void ellipseButtonHit() {
		buttonChange(actionType.DRAW, shapeType.ELLIPSE, true);
	}

	@Override
	public void triangleButtonHit() {
		buttonChange(actionType.DRAW, shapeType.TRIANGLE, true);
	}

	@Override
	public void selectButtonHit() {
		buttonChange(actionType.SELECT, null, false);
	}

	@Override
	public void zoomInButtonHit() {

	}

	@Override
	public void zoomOutButtonHit() {

	}

	@Override
	public void hScrollbarChanged(int value) {

	}

	@Override
	public void vScrollbarChanged(int value) {

	}

	@Override
	public void openScene(File file) {

	}

	@Override
	public void toggle3DModelDisplay() {

	}

	@Override
	public void keyPressed(Iterator<Integer> iterator) {

	}

	@Override
	public void openImage(File file) {

	}

	@Override
	public void saveImage(File file) {

	}

	@Override
	public void toggleBackgroundDisplay() {

	}

	@Override
	public void saveDrawing(File file) {
		boolean saved = model.save(file);
		if (!saved) {
			System.out.println("Failed to save drawing");
		}
	}

	@Override
	public void openDrawing(File file) {
		boolean opened = model.open(file);
		if (!opened) {
			System.out.println("Failed to open drawing");
		}
	}

	@Override
	public void doDeleteShape() {
		if (actShapeIndex > -1)
			model.deleteShape(actShapeIndex);
		actShapeIndex = -1;
		activeShape = null;
		view.update(null, new Object());
	}

	@Override
	public void doEdgeDetection() {

	}

	@Override
	public void doSharpen() {

	}

	@Override
	public void doMedianBlur() {

	}

	@Override
	public void doUniformBlur() {

	}

	@Override
	public void doGrayscale() {

	}

	@Override
	public void doChangeContrast(int contrastAmountNum) {

	}

	@Override
	public void doChangeBrightness(int brightnessAmountNum) {

	}

	@Override
	public void doMoveForward() {
		if (actShapeIndex > -1 && activeShape != null); {
			model.moveForward(actShapeIndex);
			actShapeIndex += actShapeIndex < model.getNumShapes()-1 ? 1 :0;
		}
	}

	@Override
	public void doMoveBackward() {
		if (actShapeIndex > -1 && activeShape != null); {
			model.moveBackward(actShapeIndex);
			actShapeIndex -= actShapeIndex > 0 ? 1 :0;
		}
	}

	@Override
	public void doSendToFront() {
		if (actShapeIndex > -1 && activeShape != null); {
			model.moveToFront(actShapeIndex);
			actShapeIndex = model.getNumShapes()-1;actShapeIndex += actShapeIndex < model.getNumShapes()-1 ? 1 :0;
		}
	}

	@Override
	public void doSendtoBack() {
		if (actShapeIndex > -1 && activeShape != null); {
			model.movetoBack(actShapeIndex);
			actShapeIndex = 0;
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {}
	@Override
	public void mouseEntered(MouseEvent arg0) {}
	@Override
	public void mouseExited(MouseEvent arg0) {}
	@Override
	public void mouseMoved(MouseEvent arg0) {}


}
