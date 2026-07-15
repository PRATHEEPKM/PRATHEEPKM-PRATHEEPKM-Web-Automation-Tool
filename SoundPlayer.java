package test;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

	public class SoundPlayer {
	    public static void main(String[] args) throws UnsupportedAudioFileException, Throwable  {
	        
	            File soundFile = new File("C:\\Users\\pid906c\\eclipse-workspace\\ProscanDev\\src\\test\\resources\\Sound\\freesound.wav");

	            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
	            Clip clip = AudioSystem.getClip();
	            clip.open(audioStream);
	            clip.start();
System.out.println("sound");
	            Thread.sleep(clip.getMicrosecondLength() / 1000);

	       
	        }
	    }
	
    


	


