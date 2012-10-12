/*
 * Contact.java
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

public class Contact {
    protected String contactName;
    protected String friendlyName;
    protected String state;

    public Contact(String contactName, String friendlyName) {
        this(contactName, friendlyName, MessengerProtocol.STATE_ONLINE);
    }

    public Contact(String contactName, String friendlyName, String state) {
        this.contactName = contactName;

        if (friendlyName != null) {
            try {
                this.friendlyName = URLDecoder.decode(friendlyName);
            } catch (Exception ex) {
                this.friendlyName = friendlyName;
            }
        }
        this.state = state.toUpperCase();
    }

    public String getContactName() {
        return contactName;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public String getState() {
        return state;
    }

    public boolean isOnline() {
        boolean online = true;
        if (state.equals(MessengerProtocol.STATE_OFFLINE) ||
                state.equals(MessengerProtocol.STATE_HIDDEN)) {
            online = false;
        }
        return online;
    }

    public int hashCode() {
        return contactName.hashCode();
    }

    public boolean equals(Object o) {
        if (o instanceof Contact) {
            return this.contactName.equals(((Contact) o).contactName);
        } else {
            return false;
        }
    }

    public String toString() {
        return friendlyName + " " + states.get(state);
    }

    /*
     * A small hashtable used to retrieve descriptions of contact states.
     */
    private static Hashtable states = new Hashtable();
    static {
        states.put(MessengerProtocol.STATE_ONLINE, "(Online)");
        states.put(MessengerProtocol.STATE_OFFLINE, "(Offline)");
        states.put(MessengerProtocol.STATE_BUSY, "(Busy)");
        states.put(MessengerProtocol.STATE_IDLE, "(Idle)");
        states.put(MessengerProtocol.STATE_AWAY, "(Away)");
        states.put(MessengerProtocol.STATE_PHONE, "(Phone)");
        states.put(MessengerProtocol.STATE_LUNCH, "(Out to Lunch)");
        states.put(MessengerProtocol.STATE_HIDDEN, "(Hidden)");
    }
}
