/*
 * SoundLoader.java - a class presented in the JavaTutorial.
 */

package net.sourceforge.util;

import java.applet.*;
import java.net.*;
import java.net.*;
import javax.swing.*;

public class SoundLoader extends Thread {
    SoundList soundList;
    URL completeURL;
    String relativeURL;

    public SoundLoader(SoundList soundList, URL baseURL, String relativeURL) {
        this.soundList = soundList;
        try {
            completeURL = new URL(baseURL, relativeURL);
        } catch (MalformedURLException e){
        }
        this.relativeURL = relativeURL;
        setPriority(MIN_PRIORITY);
        start();
    }

    public void run() {
        AudioClip audioClip = Applet.newAudioClip(completeURL);
        soundList.putClip(audioClip, relativeURL);
    }
}
