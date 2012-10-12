/*
 * ListPacket.java
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

public class ListPacket extends MessengerPacket {
    protected String list;
    protected int serialNo;
    protected int itemNo;
    protected int totalItems;
    protected String userHandle;
    protected String friendlyName;

    public ListPacket(String rawPacket) throws ProtocolException {
        init(rawPacket);

        try {
            StringTokenizer st = new StringTokenizer(rawPacket);
            st.nextToken();
            st.nextToken();
            list = st.nextToken();
            serialNo = Integer.parseInt(st.nextToken());
            itemNo = Integer.parseInt(st.nextToken());
            totalItems = Integer.parseInt(st.nextToken());
            if (st.hasMoreTokens()) {
                userHandle = st.nextToken();
                friendlyName = st.nextToken();
            }
        } catch (NoSuchElementException ex) {
            throw new ProtocolException("invalid ListPacket");
        }
    }

    public String getList() {
        return list;
    }
    public int getSerialNo() {
        return serialNo;
    }
    public int getItemNo() {
        return itemNo;
    }
    public int getTotalItems() {
        return totalItems;
    }
    public String getUserHandle() {
        return userHandle;
    }
    public String getFriendlyName() {
        return friendlyName;
    }
}
