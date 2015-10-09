package cs355.view;

import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

import cs355.GUIFunctions;
import cs355.model.drawing.*;

public class View implements ViewRefresher {
	
	private Shape border;
	private List<DrawableShape> displayShapes;
	private Model model;
	private DrawableFactory factory;
	
	public View(Model m) {
		border = null;
		model = m;
		model.addObserver(this);
		displayShapes = new LinkedList<>();
		factory = DrawableFactory.getInstance();
	}
	

	@Override
	public void update(Observable arg0, Object arg1) {
		
//		System.out.println("Updating view");	
		if (arg1 instanceof Shape) {
			border = (Shape)arg1;
		}
		else if (arg1 != null){
			border = null;
		}
		
		GUIFunctions.refresh();
	}

	@Override
	public void refreshView(Graphics2D g2d) {
		
		List<Shape> shapes = model.getShapes();
		
//		System.out.println(String.format("Shapes: %d Indecies: %d", shapes.size(), inds.size()));
		displayShapes = new LinkedList<>();
		for (Shape s : shapes) {
			DrawableShape ds = factory.createDrawableShape(s);
			displayShapes.add(ds);
		}
		
		
		for (DrawableShape s : displayShapes) {
			s.draw(g2d, false);
		}
		
		if (border != null) {
			factory.createDrawableShape(border).draw(g2d, true);
		}
	}
	
}
