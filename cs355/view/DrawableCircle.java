package cs355.view;

import java.awt.Graphics2D;
import cs355.model.drawing.Circle;

public class DrawableCircle extends DrawableShape {
	
	Circle circle;
	
	public DrawableCircle(Circle c) {
		super(c);
		circle = c;
	}

	@Override
	public void draw(Graphics2D g2d, boolean outline) {
		super.draw(g2d, outline);
		int r = (int)circle.getRadius();
		if (outline) {
			g2d.setColor(DrawableShape.borderColor);
			g2d.setStroke(DrawableShape.borderStroke);
			g2d.drawRect(-r, -r, 2*r, 2*r);
			int hs = 16;
			g2d.drawOval(-hs/2, -(r+10+hs), hs, hs);
		}
		else {
			g2d.setColor(circle.getColor());			
			g2d.fillOval(-r, -r, 2*r, 2*r);
		}
	}

}
