package cs355.view;

import java.awt.Graphics2D;

import cs355.controller.HandleHitTester;
import cs355.model.drawing.Square;


public class DrawableSquare extends DrawableShape {
	
	private Square square;
	
	
	public DrawableSquare(Square s) {
		super(s);
		square = s;
	}

	@Override
	public void draw(Graphics2D g2d, boolean outline) {
		super.draw(g2d, outline);
		int size = (int)square.getSize();
		int off = size / 2;
		if (outline) {
			g2d.setColor(DrawableShape.borderColor);
			g2d.setStroke(DrawableShape.borderStroke);
			g2d.drawRect(-off, -off, size, size);
			int hs = HandleHitTester.HANDLE_SIZE;
			int dis = HandleHitTester.HANDLE_DISPLACEMENT;
			g2d.drawOval(-hs/2, -(off+dis+hs), hs, hs);
		}
		else {
			g2d.setColor(square.getColor());
			g2d.fillRect(-off, -off, size, size);
		}
	}

}
