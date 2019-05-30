package com.samuel;

import com.osreboot.ridhvl.display.collection.HvlDisplayModeDefault;
import com.osreboot.ridhvl.painter.painter2d.HvlFontPainter2D;
import com.osreboot.ridhvl.template.HvlTemplateInteg2D;

public class Main extends HvlTemplateInteg2D{
	
	public static final int 
	CIRCLE_INDEX = 0,
	FONT_INDEX = 1;

	static HvlFontPainter2D font;
	
	public static void main(String [] args){
		new Main();
	} 

	public Main(){
		super(60, 1440, 720, "Genetic Neural Network Testing", new HvlDisplayModeDefault());
	}

	@Override
	public void initialize() {
		//load fonts and textures
		getTextureLoader().loadResource("circle");
		getTextureLoader().loadResource("osFont");
		font =  new HvlFontPainter2D(getTexture(FONT_INDEX), HvlFontPainter2D.Preset.FP_INOFFICIAL,.5f,8f,0); //font definition
		Game.init();
	}

	@Override
	public void update(float delta) {
		Game.update(delta);
	}
	
}
