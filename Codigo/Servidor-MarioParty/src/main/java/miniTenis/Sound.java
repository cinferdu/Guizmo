package miniTenis;

import java.applet.Applet;
import java.applet.AudioClip;

public class Sound {
	public static final AudioClip BALL = Applet.newAudioClip(Sound.class.getResource("bounce.wav"));
	public static final AudioClip GAMEOVER = Applet.newAudioClip(Sound.class.getResource("gameover.wav"));
	//public static final AudioClip BACK = Applet.newAudioClip(Sound.class.getResource("back.wav"));
}