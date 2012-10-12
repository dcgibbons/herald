/*
 * VersionPacket.java
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

public class VersionPacket extends MessengerPacket {
    protected String[] dialects;

    public VersionPacket() throws ProtocolException {
        init(new String[] { MessengerProtocol.DIALECT_NAME });
    }

    public VersionPacket(String rawPacket) throws ProtocolException {
        init(rawPacket);

        try {
            StringTokenizer st = new StringTokenizer(rawPacket);
            st.nextToken();
            st.nextToken();
            int n = st.countTokens();
            dialects = new String[n];
            for (int i = 0; i < n; i++) {
                dialects[i] = st.nextToken();
            }
        } catch (NoSuchElementException ex) {
            throw new ProtocolException("Invalid packet: " + rawPacket);
        }
    }

    protected void init(String[] dialects) throws ProtocolException {
        this.dialects = dialects;

        StringBuffer buffer = new StringBuffer();
        buffer.append("VER ")
              .append(MessengerProtocol.getTransactionID())
              .append(" ");
        for (int i = 0; i < dialects.length; i++) {
            buffer.append(dialects[i]).append(" ");
        }
        buffer.append(MessengerProtocol.END_OF_COMMAND);

        init(buffer.toString());
    }

    protected String[] getDialects() {
        return dialects;
    }
}

