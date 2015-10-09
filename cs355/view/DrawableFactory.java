package cs355.view;


import cs355.model.drawing.*;


public class DrawableFactory {
	
	private static DrawableFactory fact;
	
	
	private DrawableFactory() {}
	
	static {
		fact = new DrawableFactory();
	}
	
	public static DrawableFactory getInstance() {
		return fact;
	}
	
	public DrawableShape createDrawableShape(Shape shape) {
		DrawableShape ds = null;
		
		if(shape instanceof Line) {
			ds =  new DrawableLine((Line)shape);
		}
		else if (shape instanceof Square) {
			ds = new DrawableSquare((Square)shape);
		}
		else if (shape instanceof Rectangle) {
			ds = new DrawableRectangle((Rectangle)shape);
		}
		else if (shape instanceof Circle) {
			ds = new DrawableCircle((Circle)shape);
		}
		else if (shape instanceof Ellipse) {
			ds = new DrawableEllipse((Ellipse)shape);
		}
		else if (shape instanceof Triangle) {
			ds = new DrawableTriangle((Triangle)shape);
		}
			
		return ds;
	}

}
