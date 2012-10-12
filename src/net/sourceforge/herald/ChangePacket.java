/*
 * ChangePacket.java
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
 * This class represents Change Packets as specified by the 
 * MSN Messenger V1.0 protocol.
 */
public class ChangePacket extends MessengerPacket {
    protected String state;

    public ChangePacket() throws ProtocolException {
        state = "xxx";
        init();
    }

    public ChangePacket(String rawPacket) throws ProtocolException {
        init(rawPacket);

        try {
            StringTokenizer st = new StringTokenizer(rawPacket);
            st.nextToken();
            st.nextToken();
            state = st.nextToken();
        } catch (NoSuchElementException ex) {
            throw new ProtocolException("Invalid packet: " + rawPacket);
        }
    }

    public void initToState(String state) throws ProtocolException {
        this.state = state;
        init();
    }

    protected void init() throws ProtocolException {
        StringBuffer buffer = new StringBuffer();
        int requestID = MessengerProtocol.getTransactionID();
        buffer.append("CHG ")
              .append(MessengerProtocol.getTransactionID())
              .append(" ")
              .append(state)
              .append(MessengerProtocol.END_OF_COMMAND);
        init(buffer.toString());
    }

    public String getState() {
        return state;
    }
}
