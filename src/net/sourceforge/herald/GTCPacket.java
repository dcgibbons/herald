/*
 * GTCPacket.java
 *
 * Herald, An Instant Messenging Application
 *
 * Copyright � 2000 Chad Gibbons
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

public class GTCPacket extends MessengerPacket {
    protected int serialNo;
    protected boolean privacyPrompt;

    public GTCPacket(String rawPacket) throws ProtocolException {
        init(rawPacket);

        try {
            StringTokenizer st = new StringTokenizer(rawPacket);
            st.nextToken();
            st.nextToken();
            serialNo = Integer.parseInt(st.nextToken());
            privacyPrompt = st.nextToken().equals("A");
        } catch (NoSuchElementException ex) {
            throw new ProtocolException("Invalid GTC packet: " + rawPacket);
        }
    }

    public int getSerialNo() {
        return serialNo;
    }

    public boolean getPrivatePrompt() {
        return privacyPrompt;
    }
}

