/*
 * RedirectPacket.java
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

public class RedirectPacket extends MessengerPacket {
    protected String type;
    protected String address;
    protected String securityPackage;
    protected String authChallengeInfo;

    public RedirectPacket() {
        command = MessengerProtocol.CMD_XFR;
        transactionID = MessengerProtocol.getTransactionID();
        type = "SB";

        StringBuffer buffer = new StringBuffer();
        buffer.append(command)
              .append(" ")
              .append(transactionID)
              .append(" ")
              .append(type)
              .append(MessengerProtocol.END_OF_COMMAND);
        rawPacket = buffer.toString();
    }
        
    public RedirectPacket(String rawPacket) throws ProtocolException {
        init(rawPacket);

        try {
            StringTokenizer st = new StringTokenizer(rawPacket);
            st.nextToken();
            st.nextToken();
            type = st.nextToken();
            address = st.nextToken();
            if (type.equals("SB")) {
                securityPackage = st.nextToken();
                authChallengeInfo = st.nextToken();
            }
        } catch (NoSuchElementException ex) {
            throw new ProtocolException("Invalid packet: " + rawPacket);
        }
    }

    public String getType() {
        return type;
    }

    public String getAddress() {
        return address;
    }

    public String getSecurityPackage() {
        return securityPackage;
    }

    public String getAuthChallengeInfo() {
        return authChallengeInfo;
    }
}
