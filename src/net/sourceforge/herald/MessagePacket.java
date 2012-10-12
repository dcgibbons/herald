/*
 * MessagePacket.java
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

import java.io.*;
import java.net.*;
import java.util.*;

import net.sourceforge.util.*;

/**
 * This class represents Message Packets as specified by the 
 * MSN Messenger V1.0 protocol.
 *
 * @author  Chad Gibbons
 */
public class MessagePacket extends MessengerPacket {
    protected String userHandle;
    protected String friendlyName;
    protected int length;
    protected String rawData;
    protected Hashtable headers;
    protected byte[] dataBuffer;
    private MessageLogSingleton log = MessageLogSingleton.instance();

    public MessagePacket(String rawPacket) throws ProtocolException {
        init(rawPacket);
    }

    protected void init(String rawPacket) throws ProtocolException {
        this.rawPacket = rawPacket;
        try {
            StringTokenizer t = new StringTokenizer(rawPacket);
            command = t.nextToken();
            userHandle = t.nextToken();
            friendlyName = t.nextToken();
            try {
                length = Integer.parseInt(t.nextToken());
            } catch (NumberFormatException ex) {
                throw new ProtocolException("invalid message length");
            }
        } catch (NoSuchElementException ex) {
            throw new ProtocolException("malformed packet");
        }
    }

    public String getUserHandle() {
        return userHandle;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public int getLength() {
        return length;
    }

    public void setRawData(char[] rawData) {
        this.rawData = new String(rawData);
        parse(this.rawData);
    }

    protected void parse(String buf) {
        int n = buf.indexOf(CRLF2);
        parseHeaders(buf.substring(0, n));
        parseData(buf.substring(n + CRLF2.length()));
    }

    protected void parseHeaders(String headers) {
        try {
            StringTokenizer st = new StringTokenizer(headers, "\r\n");
            String key = "";
            String value = "";
            while (st.hasMoreTokens()) {
                String line = st.nextToken();
                StringTokenizer subTokens = new StringTokenizer(line, ":");
                key = subTokens.nextToken();
                value = subTokens.nextToken().trim();
                headerFields.put(key, value);
            }
        } catch (Exception ex) {
            log.append("Unable to process message headers: " + ex.getMessage());
        }
    }

    protected void parseData(String data) {
        try {
            dataBuffer = data.getBytes("US-ASCII");
        } catch (UnsupportedEncodingException ex) {
            log.append("Fatal Exception: " + ex.getMessage());
            System.exit(1);
            // TODO: it would be nice to present the user with something...
        }
    }

    public byte[] getMessageData() {
        return dataBuffer;
    }

    protected final static String CRLF2 = "\r\n\r\n";
    protected final static String LF2 = "\n\n";

    protected Hashtable headerFields = new Hashtable();
    protected static final String CONTENT_LENGTH = "Content-Length";

    public int getContentLength() {
        if (headerFields.containsKey(CONTENT_LENGTH)) {
            return Integer.parseInt((String) headerFields.get(CONTENT_LENGTH));
        } else {
            return -1;
        }
    }

    public String getHeaderData(String headerKey) {
        return (String) headerFields.get(headerKey);
    }
}
