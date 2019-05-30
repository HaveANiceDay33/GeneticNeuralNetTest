package com.samuel;

import static com.osreboot.ridhvl.painter.painter2d.HvlPainter2D.hvlDrawQuadc;

import java.util.Comparator;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import com.osreboot.ridhvl.HvlMath;
import com.osreboot.ridhvl.action.HvlAction1;
import com.osreboot.ridhvl.input.HvlInput;
import com.osreboot.ridhvl.menu.HvlMenu;

public class Player implements Cloneable{
	
	public static final float PLAYER_SIZE = 40;
	public static final float JUMP_TIME = 1f;
	
	public Network decisionNet;
	Color thisColor;
	
	public float yPos, drawPos, xPos;
	public float yVel;
	private HvlInput jump;
	
	public float score;
	public boolean dead;
	
	public Player(Color c) {
		thisColor = c;
		yPos = 0;
		yVel = 0;
		xPos = HvlMath.randomIntBetween(40, 60);
		decisionNet = new Network(3,4,1);
		dead = false;
		jump = new HvlInput(new HvlInput.InputFilter() {
			@Override
			public float getCurrentOutput() {
				if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
					return 1;
				}else {
					return 0;
				}	 
			}
		});
		
		jump.setPressedAction(new HvlAction1<HvlInput>() {
			@Override
			public void run(HvlInput a) {
				jump();
			}
		});
		
	}
	
	protected Object clone() throws CloneNotSupportedException {
	    Player cloned = (Player)super.clone();  
	    return cloned;
	}
	
	public Obstacle closestObstacle() {
		Obstacle closest = null;
		for(Obstacle o : Game.obstacles) {
			if(o.xPos > -Obstacle.OB_W) {
				if(closest == null) {
					closest = o;
				}
				float distance = HvlMath.distance(xPos+PLAYER_SIZE, drawPos, closest.xPos, closest.yPos);
				float distanceTest = HvlMath.distance(xPos+PLAYER_SIZE, drawPos, o.xPos, o.yPos);
				if(distanceTest < distance) {
					closest = o;
				}
			}
		}
		return closest;
	}
		
	private void jump() {
		if(yPos == 0) {
			yVel = -1300;
		}
	}
	
	public void die() {
		dead = true;
	}
	
	public boolean updateCollisions() {
		if(Game.obstacles.size() > 0 && !dead) {
			Obstacle o = closestObstacle();
			if(((xPos + PLAYER_SIZE/2 > o.xPos && o.xPos + Obstacle.OB_W > xPos + PLAYER_SIZE/2) || 
					(xPos + PLAYER_SIZE/2 < o.xPos + Obstacle.OB_W && xPos - PLAYER_SIZE/2 > o.xPos) ||  
					(xPos - PLAYER_SIZE/2 < o.xPos + Obstacle.OB_W && xPos + PLAYER_SIZE/2 > o.xPos + Obstacle.OB_W)) &&
					drawPos + PLAYER_SIZE/2 > Display.getHeight() - 150 - o.height) {
				return true;
			}
		}
		
		return false;
	}
	
	private void updateNetwork() {
		if(Game.obstacles.size() > 0) {
			
			Obstacle o = closestObstacle();
			
			float distance = HvlMath.distance(xPos+PLAYER_SIZE,  Display.getHeight()-150 - PLAYER_SIZE/2, 
					o.xPos, Display.getHeight() - 150 - o.height);
			
			float distanceBetween;
			if(Game.obstacles.size() > 1 && Game.obstacles.get(0).xPos > -Obstacle.OB_W) {
				distanceBetween = Game.obstacles.get(1).xPos - o.xPos;
			} else {
				distanceBetween = 0;
			}
			
			decisionNet.layers.get(0).nodes.get(0).value = HvlMath.map(distance, 0, Display.getWidth() - xPos, 0, 1);
			decisionNet.layers.get(0).nodes.get(1).value = HvlMath.map(closestObstacle().height, 60, 120, 0, 1);
			decisionNet.layers.get(0).nodes.get(2).value = HvlMath.map(distanceBetween, Game.gameSpeed * Game.minFrequency, Game.gameSpeed * Game.maxFrequency, 0, 1);
			
			NetworkMain.propogateAsNetwork(decisionNet);
			if(decisionNet.lastLayer().nodes.get(0).value > 0.75) {
				jump();
			}
		}
	}
	
	public void drawNetwork(float delta) {
		decisionNet.draw(delta, Main.font, Main.getTexture(Main.CIRCLE_INDEX));
	}
	
	public void draw(float delta) {
		if(!dead) {
			updateNetwork();
			
			yVel += Game.GRAVITY * delta;
			yPos += yVel * delta;
			drawPos = Display.getHeight()-150 - PLAYER_SIZE/2 + yPos;
			
			if(drawPos > Display.getHeight()-150 - PLAYER_SIZE/2) {
				yPos = 0;
				yVel = 0;
			}
			hvlDrawQuadc(xPos, drawPos, PLAYER_SIZE, PLAYER_SIZE, thisColor);
			score+=delta;
		}
	}
	
	public int compareTo(Player p) {
		return Float.compare(getScore(), p.getScore());
	}
	
	public void setNetwork(Network n) {
		decisionNet = n;
	}
	
	public Network getNetwork() {
		return decisionNet;
	}
	
	public float getScore() {
		return score;
	}
}
