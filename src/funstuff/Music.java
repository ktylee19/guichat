package funstuff;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Music {

    private final int BUFFER_SIZE = 128000;
    private File soundFile;
    private AudioInputStream audioStream;
    private AudioFormat audioFormat;
    private SourceDataLine sourceLine;
    
    /**
     * Creates a music player that calls a source with a given key
     * 
     * @param keyword that prompts a song.
     */
    public Music(String code) {
        //given codeword, call playSound for given codeword.
        if (!code.equals(null)) {
            String myString = "src/funstuff/"+code+".wav";
            playSound(myString);
        }
    }
    /**
     * Checks if the message has a keyword in it.
     * If there are multiple keywords, then it chooses the first keyword only.
     * 
     * @param String message
     * @return keyword that is contained within the message, null otherwise 
     */
    public static String keywordIn(String message) {
        if (message.matches(".*((nyan cat)|(nyancat)).*")) {
            return "nyancat";
        }
        else if (message.matches(".*((rickroll)|(rick roll)).*")) {
            return "rickroll";
        }
        else if (message.matches(".*(number)\\s*")) {
            return "callme";
        }
        else if (message.matches(".*(debug).*")) {
            return "baby";
        }
        else if (message.matches(".*(se(x)+y).*")) {
            return "sexy";
        }
        else if (message.matches(".*(awkward).*")) {
            return "awkward";
        }
        else if (message.matches(".*(gro(o)+vy).*")) {
            return "groovy";
        }
        else if (message.matches("(.*(synchronize).*)|(.* noo(o)+ .*)|(noo(o)+ .*)|(.* noo(o)+)|(noo(o)+)|(.* 6.005 .*)|(6.005 .*)|(.* 6.005)|(6.005)")) {
            return "nooo";
        }
        else if (message.matches("(.* yess(s)+ .*)|(yess(s)+ .*)|(.* yess(s)+)|(yess(s)+)")) {
            return "ohhyes";
        }
        else if (message.matches(".*((master)|(mastah)|(pokemon)).*")) {
            return "pokemon";
        }
        
        else if (message.matches("(.*(help).*)|(.*(help)\\s*)")) {
            return "help";
        }
        return null;
    }

    /**
     * Plays song from source filename
     * 
     * @param filename the name of the file that is going to be played
     */
    private void playSound(String filename){
        String strFilename = filename;
        try {
            soundFile = new File(strFilename);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        try {
            audioStream = AudioSystem.getAudioInputStream(soundFile);
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        audioFormat = audioStream.getFormat();

        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        try {
            sourceLine = (SourceDataLine) AudioSystem.getLine(info);
            sourceLine.open(audioFormat);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        sourceLine.start();

        int nBytesRead = 0;
        byte[] abData = new byte[BUFFER_SIZE];
        while (nBytesRead != -1) {
            try {
                nBytesRead = audioStream.read(abData, 0, abData.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (nBytesRead >= 0) {
                sourceLine.write(abData, 0, nBytesRead);
            }
        }
        sourceLine.drain();
        sourceLine.close();
    }
}