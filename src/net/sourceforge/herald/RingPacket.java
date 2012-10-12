/*
 * RingPacket.java
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

/**
 * This class represents Ring Packets as specified by the 
 * MSN Messenger V1.0 protocol.
 */
public class RingPacket extends MessengerPacket {
    protected String sessionID;
    protected String switchboardServerAddress;
    protected String securityPolicy;
    protected String authChallengeInfo;
    protected String callingUserHandle;
    protected String callingUserFriendlyName;

    public RingPacket(String rawPacket) throws ProtocolException {
        try {
            StringTokenizer st = new StringTokenizer(rawPacket);
            command = st.nextToken();
            sessionID = st.nextToken();
            switchboardServerAddress = st.nextToken();
            securityPolicy = st.nextToken();
            authChallengeInfo = st.nextToken();
            callingUserHandle = st.nextToken();
            callingUserFriendlyName = st.nextToken();
/*
        S: RNG SessionID SwitchboardServerAddress SP AuthChallengeInfo
           CallingUserHandle CallingUserFriendlyName
*/
        } catch (NoSuchElementException ex) {
            throw new ProtocolException("Invalid packet: " + rawPacket);
        }
    }

    public String getSessionID() {
        return sessionID;
    }

    public String getSwitchboardServerAddress() {
        return switchboardServerAddress;
    }

    public String getSecurityPolicy() {
        return securityPolicy;
    }

    public String getAuthChallengeInfo() {
        return authChallengeInfo;
    }

    public String getCallingUserHandle() {
        return callingUserHandle;
    }

    public String getCallingUserFriendlyName() {
        return callingUserFriendlyName;
    }
}
