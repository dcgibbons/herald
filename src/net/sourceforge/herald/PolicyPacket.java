/*
 * PolicyPacket.java
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

public class PolicyPacket extends MessengerPacket {
    protected String[] policies;

    public PolicyPacket() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(MessengerProtocol.CMD_INF)
              .append(" ")
              .append(MessengerProtocol.getTransactionID())
              .append(MessengerProtocol.END_OF_COMMAND);
        try {
            init(buffer.toString());
        } catch (ProtocolException ex) {
        }
    }

    public PolicyPacket(String rawPacket) throws ProtocolException {
        init(rawPacket);

        try {
            StringTokenizer st = new StringTokenizer(rawPacket);
            st.nextToken();
            st.nextToken();
            policies = new String[st.countTokens()];
            int i = 0;
            while (st.hasMoreTokens()) {
                policies[i++] = st.nextToken();
            }
        } catch (NoSuchElementException ex) {
            throw new ProtocolException("Invalid packet: " + rawPacket);
        }
    }

    protected void init(String[] policies) throws ProtocolException {
        this.policies = policies;

        StringBuffer buffer = new StringBuffer();
        buffer.append(MessengerProtocol.CMD_INF)
              .append(" ")
              .append(MessengerProtocol.getTransactionID())
              .append(" ");
        for (int i = 0; i < policies.length; i++) {
            buffer.append(policies[i]).append(" ");
        }
        buffer.append(MessengerProtocol.END_OF_COMMAND);

        String s = buffer.toString();
        init(s);
    }

    public String[] getPolicies() {
        return policies;
    }
}

