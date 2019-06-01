package com.samuel;

import java.util.ArrayList;
import java.util.Comparator;

import org.newdawn.slick.Color;

import com.osreboot.ridhvl.HvlMath;

public class GeneticsHandler {
	public static final int MAX_POP = 50000;
	
	public static int currentGeneration = 1;
	public static ArrayList<Player> population;
	
	public static void init() {
		population = new ArrayList<>();
		for(int i = 0; i < MAX_POP; i++) {
			populate(new Player(new Color(HvlMath.randomFloatBetween(0f, 1f), HvlMath.randomFloatBetween(0f, 1f), HvlMath.randomFloatBetween(0f, 1f))));
		}
	} 
	
	public static void populate(Player p) {
		population.add(p);
	}
	
	public static void duplicateParents(Player par1, Player par2) {

		Player parent1 = par1;
		Player parent2 = par2;
		//Keep the originals
		try {
			Player p = (Player) parent1.clone();
			p.dead = false;
			p.score = 0;
			populate(p);
			Player p2 = (Player) parent2.clone();
			p2.dead = false;
			p2.score = 0;
			populate(p2);
		} catch (CloneNotSupportedException e) {}
		
		try {
			for(int i = 0; i < (MAX_POP-2); i++) {
				Player child1 = (Player) parent1.clone();
				Player child2 = (Player) parent2.clone();
				mutatePlayer(crossOverGenes(child1, child2));
			}
		} catch (CloneNotSupportedException e) {} 
		
		currentGeneration++;
		Game.score = 0;
		Game.gameSpeed = 300;
		Game.maxFrequency = 4f;
		Game.minFrequency = 2f;
	}
	
	public static Player crossOverGenes(Player c1, Player c2) {
		Color childColor;
		double colorRand = Math.random();
		if(colorRand < 0.5) {
			childColor = c1.thisColor;
		} else {
			childColor = c2.thisColor;
		}
		Player child = new Player(childColor);
		
		float geneticBias = (c1.score - c2.score)/c1.score;
	
		for(int l = 0; l < child.decisionNet.layers.size(); l++) {
			for(int n = 0; n < child.decisionNet.layers.get(l).numNodes; n++) {
				for(int i = 0; i < child.decisionNet.layers.get(l).nodes.get(n).connectionWeights.size(); i++) {
					double rand = Math.random();
					if(rand < 0.5 + geneticBias) {
						child.decisionNet.layers.get(l).nodes.get(n).connectionWeights.put(i, c1.decisionNet.layers.get(l).nodes.get(n).connectionWeights.get(i));
					} else {
						child.decisionNet.layers.get(l).nodes.get(n).connectionWeights.put(i, c2.decisionNet.layers.get(l).nodes.get(n).connectionWeights.get(i));
					}
				}
				double biasRand = Math.random();
				if(biasRand < 0.5 + geneticBias) {
					child.decisionNet.layers.get(l).nodes.get(n).bias = c1.decisionNet.layers.get(l).nodes.get(n).bias;
				} else {
					child.decisionNet.layers.get(l).nodes.get(n).bias = c2.decisionNet.layers.get(l).nodes.get(n).bias;
				}
			}
		}
		return child;
	}

	public static void mutatePlayer(Player p) {
		for(int l = 0; l < p.decisionNet.layers.size(); l++) {
			for(int n = 0; n < p.decisionNet.layers.get(l).numNodes; n++) {
				for(int i = 0; i < p.decisionNet.layers.get(l).nodes.get(n).connectionWeights.size(); i++) {
					double rand = Math.random();
					if(rand < 0.01) {
						p.decisionNet.layers.get(l).nodes.get(n).connectionWeights.put(i, (float) Math.random());
					}
				}
				double biasRand = Math.random();
				if(biasRand < 0.05) {
					p.decisionNet.layers.get(l).nodes.get(n).bias = (float) Math.random();
				}
			}
		}
		populate(p);
	}
	
	public static Comparator<Player> compareByScore = new Comparator<Player>() {
	    @Override
	    public int compare(Player p1, Player p2) {
	        return Float.compare(p1.getScore(), p2.getScore());
	    }
	};
}
