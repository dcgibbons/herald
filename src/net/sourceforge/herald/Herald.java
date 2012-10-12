/*
 * $Id: Herald.java,v 1.3 2000/06/12 04:58:18 dcgibbons Exp $
 *
 * Herald, An Instant Messenging Application
 *
 * Copyright © 2000 Chad Gibbons
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package net.sourceforge.herald;

import java.net.*;
import java.util.*;

import net.sourceforge.herald.ui.*;
import net.sourceforge.util.*;

/**
 * This class contains the main entry point for the Herald application along
 * with useful application constants.
 *
 * @author  Chad Gibbons
 */
public class Herald {
    /* TODO: move these to their proper locations */
    public final static String DISPATCH_SERVER = "msgr-ns10.hotmail.com";
    public static SoundList soundList;

    public static void main(String[] args) {
        /* TODO: the sound code needs to be un-hacked */
        URL codeBase = null;
        try {
            codeBase = new URL("file:" + System.getProperty("user.dir") + "/");
        } catch (MalformedURLException e) {
            System.err.println(e.getMessage());
        }
        soundList = new SoundList(codeBase);
        soundList.startLoading("sounds/newemail.wav");
        soundList.startLoading("sounds/newmsg.wav");
        soundList.startLoading("sounds/online.wav");

        MessengerClient client = new MessengerClient();
        HeraldFrame frame = new HeraldFrame(client);
        frame.setVisible(true);
    }
}
