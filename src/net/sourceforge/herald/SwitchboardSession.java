/*
 * SwitchboardSession.java
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
import java.security.*;
import java.util.*;
import javax.swing.event.*;
import sun.misc.*;

public class SwitchboardSession extends MessengerSession {
    protected int sessionID;
    protected LinkedList contacts = new LinkedList();
    protected EventListenerList listenerList = new EventListenerList();
    protected MessengerClient client;

    public SwitchboardSession(MessengerClient client, String userHandle, Contact c, 
            RedirectPacket p) throws IOException {
        this.client = client;

        redirect(p.getAddress());

        int transID = MessengerProtocol.getTransactionID();

        StringBuffer buffer = new StringBuffer();
        buffer.append(MessengerProtocol.CMD_USR)
              .append(" ")
              .append(transID)
              .append(" ")
              .append(userHandle)
              .append(" ")
              .append(p.getAuthChallengeInfo())
              .append(MessengerProtocol.END_OF_COMMAND);

        addPacketListener(transID, new ValidateSwitchboardAuth(c));
        sendMessage(buffer.toString());
    }

    public SwitchboardSession(MessengerClient client, String userHandle, Contact c, RingPacket p) 
            throws IOException {
        this.client = client;

        redirect(p.getSwitchboardServerAddress());

        SwitchboardAcceptPacket sap = new SwitchboardAcceptPacket(c.getContactName(), 
                p.getAuthChallengeInfo(), p.getSessionID());
        addPacketListener(sap.getTransactionID(), new ValidateAnswerAuth());
        sendMessage(sap);
    }

    public MessengerClient getClient() {
        return client;
    }

    public int getSessionID() {
        return sessionID;
    }

    public synchronized boolean contains(Contact c) {
        return contacts.contains(c);
    }

    public LinkedList getContacts() {
        return contacts;
    }

    public void sendTextMessage(String msg) {
        try {
            String encoding = "UTF-8";
            StringBuffer headers = new StringBuffer();
            headers.append("MIME-Version: 1.0\r\n")
                   .append("Content-Type: text/plain; charset=")
                   .append(encoding)
                   .append("\r\n")
                   .append("\r\n");

            byte[] headersBuffer = headers.toString().getBytes("US-ASCII");

            StringTokenizer st = new StringTokenizer(msg, "\n", true);
            StringBuffer expMsgBuf = new StringBuffer();
            while (st.hasMoreTokens()) {
                String t = st.nextToken();
                if (t.equals("\n")) {
                    if (st.hasMoreTokens()) {
                        expMsgBuf.append("\r\n");
                    }
                } else {
                    expMsgBuf.append(t);
                }
            }
            byte[] msgText = expMsgBuf.toString().getBytes(encoding);

            StringBuffer m = new StringBuffer();
            m.append(MessengerProtocol.CMD_MSG)
             .append(" ")
             .append(Integer.toString(MessengerProtocol.getTransactionID()))
             .append(" N ")
             .append(Integer.toString(headersBuffer.length + msgText.length))
             .append(MessengerProtocol.END_OF_COMMAND);

            byte[] msgBuffer = m.toString().getBytes("US-ASCII");

            byte[] buffer = new byte[msgBuffer.length + headersBuffer.length + msgText.length];
            int pos = 0;
            System.arraycopy(msgBuffer, 0, buffer, pos, msgBuffer.length);
            pos += msgBuffer.length;
            System.arraycopy(headersBuffer, 0, buffer, pos, headersBuffer.length);
            pos += headersBuffer.length;
            System.arraycopy(msgText, 0, buffer, pos, msgText.length);
            pos += msgText.length;

            log.append("Sending this message:\r\n" +
                    new String(buffer, "US-ASCII"));
            sessionOut.write(buffer);
        } catch (UnsupportedEncodingException ex) {
        } catch (IOException ex) {
        }
    }

    protected synchronized void postprocessPacket(MessengerPacket p) {
        if (p instanceof JoinPacket) {
            JoinPacket jp = (JoinPacket) p;
            Contact c = new Contact(jp.getUserHandle(), jp.getFriendlyName());
            log.append(c + " has joined the conversation.");
            contacts.add(c);
            fireContactJoined(c);

        } else if (p instanceof ByePacket) {
            ByePacket bp = (ByePacket) p;
            String userHandle = bp.getUserHandle();

            Contact c = null;
            ListIterator i = contacts.listIterator();
            while (i.hasNext()) {
                c = (Contact) i.next();
                if (c.getContactName().equals(userHandle)) {
                    break;
                } else {
                    c = null;
                }
            }

            contacts.remove(c);
            fireContactLeft(c);
        }
    }

    public void addSwitchboardListener(SwitchboardListener l) {
        listenerList.add(SwitchboardListener.class, l);
    }

    public void removeSwitchboardListener(SwitchboardListener l) {
        listenerList.remove(SwitchboardListener.class, l);
    }

    protected void fireContactJoined(Contact c) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length-2; i >= 0; i -= 2) {
            if (listeners[i] == SwitchboardListener.class) {
                SwitchboardListener l = (SwitchboardListener) listeners[i+1];
                l.contactJoined(c);
            }
        }
    }

    protected void fireContactLeft(Contact c) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length-2; i >= 0; i -= 2) {
            if (listeners[i] == SwitchboardListener.class) {
                SwitchboardListener l = (SwitchboardListener) listeners[i+1];
                l.contactLeft(c);
            }
        }
    }

    protected void fireMessageArrived(MessagePacket p) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length-2; i >= 0; i -= 2) {
            if (listeners[i] == SwitchboardListener.class) {
                SwitchboardListener l = (SwitchboardListener) listeners[i+1];
                l.messageArrived(p);
            }
        }
    }

    class ValidateSwitchboardAuth implements PacketListener {
        protected Contact contact;

        ValidateSwitchboardAuth(Contact c) {
            contact = c;
        }

        public void packetReceived(MessengerPacket p) {
            if (p instanceof AuthPacket) {
                CallPacket cp = new CallPacket(contact);

                addPacketListener(cp.getTransactionID(), new ValidateSwitchboardSession(contact));
                sendMessage(cp);
            } else {
                log.append("Switchboard authentication failed: " + p);
            }
        }
    }

    class ValidateSwitchboardSession implements PacketListener {
        protected Contact contact;

        ValidateSwitchboardSession(Contact c) {
            contact = c;
        }

        public void packetReceived(MessengerPacket p) {
            if (p instanceof CallPacket) {
                CallPacket cp = (CallPacket) p;
                log.append("Switchboard session established: "  + " Status: " +
                        cp.getInfo() + " SessionID: " + cp.getSessionID());
                sessionID = cp.getSessionID();

            } else {
                log.append("Switchboard session establishment failed: " + p);
            }
        }
    }

    class ValidateAnswerAuth implements PacketListener {
        public void packetReceived(MessengerPacket p) {
            if (p instanceof SwitchboardAcceptPacket) {
                SwitchboardAcceptPacket sap = (SwitchboardAcceptPacket) p;
                if (sap.getLocalUserHandle().equals("OK")) {
                    removePacketListener(sap.getTransactionID());
                }

            } else if (p instanceof InitialRosterPacket) {
                InitialRosterPacket irp = (InitialRosterPacket) p;
                Contact c = new Contact(irp.getUserHandle(), irp.getFriendlyName());
                synchronized (SwitchboardSession.this) {
                    contacts.add(c);
                }
                fireContactJoined(c);
            }
        }
    }
}
