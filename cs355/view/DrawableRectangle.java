package cs355.view;

import java.awt.Graphics2D;
import cs355.model.drawing.Rectangle;

public class DrawableRectangle extends DrawableShape {

	Rectangle rectangle;
	
	public DrawableRectangle(Rectangle r) {
		super(r);
		rectangle = r;
	}
	
	@Override
	public void draw(Graphics2D g2d, boolean outline) {
		super.draw(g2d, outline);
		double w = rectangle.getWidth();
		double h = rectangle.getHeight();
		if (outline) {
			g2d.setColor(DrawableShape.borderColor);
			g2d.setStroke(DrawableShape.borderStroke);
			g2d.drawRect((int)(-w/2), (int)(-h/2), (int)w, (int)h);
			int s = 16;
			g2d.drawOval(-s/2, -(int)(h/2+10+s), s, s);
		}
		else {
			g2d.setColor(rectangle.getColor());
			g2d.fillRect((int)(-w/2), (int)(-h/2), (int)w, (int)h);
		}
	}

}
