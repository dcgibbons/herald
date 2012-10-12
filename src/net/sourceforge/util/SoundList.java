/*
 * SoundList.java - a class presented in the Java Tutorial.
 */

package net.sourceforge.util;

import java.applet.*;
import java.net.*;
import javax.swing.*;

/**
 * Loads and holds a bunch of audio files whose locations are specified
 * relative to a fixed base URL.
 */
public class SoundList extends java.util.Hashtable {
    JApplet applet;
    URL baseURL;

    public SoundList(URL baseURL) {
        super(5); //Initialize Hashtable with capacity of 5 entries.
        this.baseURL = baseURL;
    }

    public void startLoading(String relativeURL) {
        new SoundLoader(this, baseURL, relativeURL);
    }

    public AudioClip getClip(String relativeURL) {
        return (AudioClip)get(relativeURL);
    }

    public void putClip(AudioClip clip, String relativeURL) {
        put(relativeURL, clip);
    }
}
