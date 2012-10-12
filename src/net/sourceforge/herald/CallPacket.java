/*
 * CallPacket.java
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
 * Provides a wrapper for all error messages as defined in the MSN 
 * Messenger V1.0 protocol specification.
 */
public class CallPacket extends MessengerPacket {
    protected String info;
    protected int sessionID;

    public CallPacket(String rawPacket) throws ProtocolException {
        init(rawPacket);

        try {
            StringTokenizer st = new StringTokenizer(rawPacket);
            st.nextToken();
            st.nextToken();
            info = st.nextToken();
            if (st.hasMoreTokens()) {
                sessionID = Integer.parseInt(st.nextToken());
            }
        } catch (NoSuchElementException ex) {
            throw new ProtocolException("Malformed packet: " + rawPacket);
        }
    }

    public CallPacket(Contact c) {
        info = c.getContactName();
        try {
            init();
        } catch (ProtocolException ex) {
        }
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
        try {
            init();
        } catch (ProtocolException ex) {
        }
    }

    public int getSessionID() {
        return sessionID;
    }

    protected void init() throws ProtocolException {
        StringBuffer buffer = new StringBuffer();
        buffer.append(MessengerProtocol.CMD_CAL)
              .append(" ")
              .append(MessengerProtocol.getTransactionID())
              .append(" ")
              .append(info)
              .append(MessengerProtocol.END_OF_COMMAND);
        init(buffer.toString());
    }

}
