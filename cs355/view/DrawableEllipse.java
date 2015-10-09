package cs355.view;

import java.awt.Graphics2D;

import cs355.model.drawing.Ellipse;

public class DrawableEllipse extends DrawableShape {
	
	Ellipse ellipse;
	
	public DrawableEllipse(Ellipse e) {
		super(e);
		ellipse = e;
	}

	@Override
	public void draw(Graphics2D g2d, boolean outline) {
		super.draw(g2d, outline);
		int width = (int)ellipse.getWidth();
		int height = (int)ellipse.getHeight();
		if (outline) {
			g2d.setColor(DrawableShape.borderColor);
			g2d.setStroke(DrawableShape.borderStroke);
			g2d.drawRect(-width/2, -height/2, width, height);
			int hs = 16;
			g2d.drawOval(-hs/2, -(height/2+10+hs), hs, hs);
		}
		else {
			g2d.setColor(ellipse.getColor());
			g2d.fillOval(-width/2, -height/2, width, height);
		}
	}

}
