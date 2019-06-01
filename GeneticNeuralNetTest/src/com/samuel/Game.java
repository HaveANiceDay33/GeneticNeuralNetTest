package com.samuel;

import static com.osreboot.ridhvl.painter.painter2d.HvlPainter2D.hvlDrawQuad;

import java.util.ArrayList;
import java.util.Collections;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import com.osreboot.ridhvl.HvlMath;

public class Game {
	
	public static final float GRAVITY = 3000f;

	public static ArrayList<Obstacle> obstacles;
	public static float obTimer;
	
	public static float gameSpeed;
	public static int numAlive;
	
	public static float minFrequency, maxFrequency, score;
	
	/**
	 * Sets speeds and inits globals
	 */
	public static void init() {
		
		GeneticsHandler.init();
		
		obstacles = new ArrayList<>();
		gameSpeed = 300;
		obTimer = 0f;
		numAlive = GeneticsHandler.MAX_POP;
		
		minFrequency = 2f;
		maxFrequency = 4f;
		score = 0;
	}
	
	public static void update(float delta) {
		hvlDrawQuad(0, Display.getHeight()-150, Display.getWidth(), 150, Color.gray);
		
		/**
		 * Each time a block is spawned the times that is randomizes between are decreased and the timer is reset
		 */
		if(obTimer <= 0) {
			minFrequency -= 0.01;
			maxFrequency -= 0.01;
			obstacles.add(new Obstacle(HvlMath.randomIntBetween(60, 120)));
			obTimer = HvlMath.randomFloatBetween(minFrequency, maxFrequency);
		}
		
		/**
		 * draw all blocks
		 */
		
		for(int i = 0; i < obstacles.size(); i ++) {
			obstacles.get(i).draw(delta, gameSpeed);
			if(obstacles.get(i).xPos < -Obstacle.OB_W) {
				obstacles.remove(i);
				i--;
			}
		}
		
		/**
		 * Draw all players
		 */
		for(Player p : GeneticsHandler.population) {
			p.update(delta);
			if(p.updateCollisions() == true) {
				p.die();
				numAlive--;
			}
		}
		
		/**
		 * Sort the population by score and start the reproduction algorithm
		 */
		if(numAlive == 0) {
			Collections.sort(GeneticsHandler.population, GeneticsHandler.compareByScore.reversed());
			Player par1 = GeneticsHandler.population.get(0);
			Player par2 = GeneticsHandler.population.get(1);
			GeneticsHandler.population.clear();
			GeneticsHandler.duplicateParents(par1, par2);
			numAlive = GeneticsHandler.population.size();
			gameSpeed+=0;
			obstacles.clear();
		} else {
			gameSpeed+=delta*2;
		}
		
		//More drawing
		Collections.sort(GeneticsHandler.population, GeneticsHandler.compareByScore.reversed());
		GeneticsHandler.population.get(0).draw(delta);
		Main.font.drawWord("Generation :  "+GeneticsHandler.currentGeneration, Display.getWidth()-300, 20, Color.white, 0.3f);
		Main.font.drawWord("Players Alive :  "+numAlive, Display.getWidth()-300, 60, Color.white, 0.3f);
		
		Main.font.drawWord("Score :  "+HvlMath.cropDecimals(score, 2), Display.getWidth()-300, 100, Color.white, 0.4f);
		obTimer -= delta;
		score += delta;
	}
}
