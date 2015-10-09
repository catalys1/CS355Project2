package cs355.model.drawing;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;


public class Model extends CS355Drawing {
	
	private ArrayList<Shape> shapes;
	
	
	public Model() {
		shapes = new ArrayList<>();
	}
	

	@Override
	public Shape getShape(int index) {

		if (index < shapes.size()) {
			return shapes.get(index);
		}
		return null;
	}
	
	public Shape getLast() {
		return shapes.get(shapes.size()-1);
	}
	
	public boolean updateShape(int index, Shape s) {
		
		if (index < shapes.size()) {
			shapes.set(index, s);
			setChanged();
			notifyObservers();
			return true;
		}
		return false;
	}
	
	public boolean updateShape(Shape s) {
		
		if (shapes.size() > 0) {
			shapes.set(shapes.size()-1, s);
			setChanged();
			notifyObservers();
			return true;
		}
		return false;
	}

	@Override
	public int addShape(Shape s) {
		shapes.add(s);
		setChanged();
		notifyObservers();
//		System.out.println(String.format("Shape added: %s", shapes.get(shapes.size()-1)));
		System.out.println(String.format("There are %d shapes in the model", shapes.size()));
		return shapes.size() - 1;
	}

	@Override
	public void deleteShape(int index) {

		if (index < shapes.size()) {
			shapes.remove(index);
			update();
		}
	}
	
	public int hitTest(Point2D.Double worldPoint, double tolerance) {
		for (int i=shapes.size()-1; i > -1; i--) {
			Shape s = shapes.get(i);
			if (s.pointInShape(worldPoint, tolerance)) {
				return i;
			}
		}
		return -1;
	}
	
	@Override
	public void moveToFront(int index) {
		if (index > -1 && index < shapes.size()) {
			shapes.add(shapes.remove(index));
			update();
		}

	}

	@Override
	public void movetoBack(int index) {
		if (index > -1 && index < shapes.size()) {
			shapes.add(0, shapes.remove(index));
			update();
		}
	}

	@Override
	public void moveForward(int index) {
		if (index > -1 && index < shapes.size()-1) {
			Shape s = shapes.get(index);
			shapes.set(index, shapes.get(index+1));
			shapes.set(index+1, s);
			update();
		}
	}

	@Override
	public void moveBackward(int index) {
		if (index > 0 && index < shapes.size()) {
			Shape s = shapes.get(index);
			shapes.set(index, shapes.get(index-1));
			shapes.set(index-1, s);
			update();
		}
	}
	
	@Override
	public List<Shape> getShapes() {
		return shapes;
	}

	@Override
	public List<Shape> getShapesReversed() {
		
		ArrayList<Shape> revShapes = new ArrayList<>();
		for (int i = shapes.size(); --i >= 0;) {
			revShapes.add(shapes.get(i));
		}
		return revShapes;
	}

	@Override
	public void setShapes(List<Shape> shapes) {

		this.shapes = (ArrayList<Shape>)shapes;
		notifyObservers();

	}
	
	/**
	 * Return a list of indexes corresponding to shapes that
	 * have changed in some way since the last query.
	 * @return A list of Integers
	 */
	public List<Integer> getUpdateIndecies() {
		// Currently just returns a list of all indexes. Will probably change
		List<Integer> inds = new ArrayList<>();
		for (int i = 0; i < shapes.size(); i++) {
			inds.add(i);
		}
		return inds;
	}
	
	public void update() {
		setChanged();
		notifyObservers();
	}

}
