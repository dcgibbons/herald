/*
 * AuthPacket.java
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
 * This class represents Authorization Packets as specified by the 
 * MSN Messenger V1.0 protocol.
 */
public class AuthPacket extends MessengerPacket {
    public final static int INITIATE_INFO = 0;
    public final static int CHALLENGE_INFO = 1;
    public final static int RESPONSE_INFO = 2;

    protected String securityPolicy;
    protected int infoType;
    protected String authInfo;

    public AuthPacket(String rawPacket) throws ProtocolException {
        init(rawPacket);

        try {
            StringTokenizer st = new StringTokenizer(rawPacket);
            st.nextToken();
            st.nextToken();
            securityPolicy = st.nextToken();
            String s = st.nextToken();
            if (s == "I") {
                infoType = INITIATE_INFO;
            } else {
                infoType = RESPONSE_INFO;
            }
            authInfo = st.nextToken();
        } catch (NoSuchElementException ex) {
            throw new ProtocolException("Invalid packet: " + rawPacket);
        }
    }

    public AuthPacket(String securityPolicy, int infoType, String authInfo)
            throws ProtocolException {
        this.securityPolicy = securityPolicy;
        this.infoType = infoType;
        this.authInfo = authInfo;
        init();
    }

    protected void init() throws ProtocolException {
        StringBuffer buffer = new StringBuffer();
        int requestID = MessengerProtocol.getTransactionID();
        buffer.append(MessengerProtocol.CMD_USR)
              .append(" ")
              .append(MessengerProtocol.getTransactionID())
              .append(" ")
              .append(securityPolicy)
              .append(" ");
        if (infoType == INITIATE_INFO) {
            buffer.append("I ");
        } else {
            buffer.append("S ");
        }
        buffer.append(authInfo)
              .append(MessengerProtocol.END_OF_COMMAND);

        init(buffer.toString());
    }

    public String getSecurityPolicy() {
        return securityPolicy;
    }

    public int getInfoType() {
        return infoType;
    }

    public String getAuthInfo() {
        return authInfo;
    }
}
