package com.samuel;

import static com.osreboot.ridhvl.painter.painter2d.HvlPainter2D.hvlDrawQuad;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

public class Obstacle {
	public static final float OB_W = 60;
	public float xPos, yPos, height;
	public Obstacle(float height) {
		this.height = height;
		xPos = Display.getWidth() + OB_W;
	}
	public void draw(float delta, float speed) {
		xPos -= speed * delta;
		yPos = Display.getHeight() - 150 - this.height;
		hvlDrawQuad(this.xPos, yPos, OB_W, this.height, Color.white);
		
	}
}
 