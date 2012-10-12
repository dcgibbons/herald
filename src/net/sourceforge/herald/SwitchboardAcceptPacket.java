/*
 * SwitchboardAcceptPacket.java
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

import java.net.ProtocolException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class SwitchboardAcceptPacket extends MessengerPacket {
    protected String localUserHandle;
    protected String authResponseInfo;
    protected String sessionID;

    public SwitchboardAcceptPacket(String rawPacket) throws ProtocolException {
        init(rawPacket);

        try {
            StringTokenizer st = new StringTokenizer(rawPacket);
            st.nextToken();
            st.nextToken();
            localUserHandle = st.nextToken();
            if (!localUserHandle.equals("OK")) {
                authResponseInfo = st.nextToken();
                sessionID = st.nextToken();
            }
        } catch (NoSuchElementException ex) {
        }
    }

    public SwitchboardAcceptPacket(String handle, String auth, String sessionID) 
            throws ProtocolException {

        int transID = MessengerProtocol.getTransactionID();

        StringBuffer buffer = new StringBuffer();
        buffer.append(MessengerProtocol.CMD_ANS)
              .append(" ")
              .append(transID)
              .append(" ")
              .append(handle)
              .append(" ")
              .append(auth)
              .append(" ")
              .append(sessionID)
              .append(MessengerProtocol.END_OF_COMMAND);
        init(buffer.toString());
    }

    public String getLocalUserHandle() {
        return localUserHandle;
    }

    public String getAuthResponseInfo() {
        return authResponseInfo;
    }

    public String getSessionID() {
        return sessionID;
    }
}
