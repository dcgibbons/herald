/*
 * StatesBundle_en.java
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

import java.util.*;

public class StatesBundle_en extends ListResourceBundle {
    public Object[][] getContents() {
        return contents;
    }

    private Object[][] contents = {
        { MessengerProtocol.STATE_ONLINE, "Online" },
        { MessengerProtocol.STATE_AWAY, "Away" },
        { MessengerProtocol.STATE_BUSY, "Busy" },
        { MessengerProtocol.STATE_IDLE, "Idle" },
        { MessengerProtocol.STATE_PHONE, "On The Phone" },
        { MessengerProtocol.STATE_LUNCH, "Out To Lunch" },
        { MessengerProtocol.STATE_HIDDEN, "Hidden" },
        { MessengerProtocol.STATE_BRB, "Be Right Back" },
    };
}
