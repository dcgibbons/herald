/*
 * ErrorPacket.java
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
public class ErrorPacket extends MessengerPacket {
    protected String errorInfo;
    protected String[] params;

    /**
     * Constructs a new ErrorPacket object from raw packet text.
     *
     * @exception   ProtocolException   if the packet is malformed.
     */
    public ErrorPacket(String rawPacket) throws ProtocolException {
        init(rawPacket);

        try {
            StringTokenizer st = new StringTokenizer(rawPacket);
            st.nextToken();
            st.nextToken();
            if (st.hasMoreTokens()) {
                errorInfo = st.nextToken();
                params = new String[st.countTokens()];
                int i = 0;
                while (st.hasMoreTokens()) {
                    params[i++] = st.nextToken();
                }
            }
        } catch (NoSuchElementException ex) {
            throw new ProtocolException("Malformed packet: " + rawPacket);
        }
    }

    /**
     * Retrieves the error info field for this packet.
     *
     * @returns String  the error info field for this packet.
     */
    public String getErrorInfo() {
        return errorInfo;
    }

    /**
     * Retrieves any optional parameters for this error packet.
     *
     * @returns String[]    any optional parameters for this packet.
     */
    public String[] getParams() {
        if (params == null) {
            return new String[] {};
        } else {
            return params;
        }
    }
}
