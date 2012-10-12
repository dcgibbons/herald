/*
 * $Id: MessengerSession.java,v 1.5 2000/06/12 04:58:18 dcgibbons Exp $
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

import java.applet.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.event.*;

import net.sourceforge.util.*;

/**
 * This class provides the basic functionality of any connection to a
 * Messenger server. A thread is created internally to this object
 * that listens for all incoming traffic from the server connection.
 * All packets from the server are parsed and then provided to the object
 * for any processing required.
 *
 * @author  Chad Gibbons
 */
public abstract class MessengerSession {
    protected Hashtable packetListeners;
    protected Socket sessionSocket;
    protected PrintStream sessionOut;
    protected BufferedReader sessionIn;
    protected Thread listenerThread;
    protected EventListenerList messageListenerList;
    protected MessageLogSingleton log = MessageLogSingleton.instance();

    /**
     * Constructs a MessengerSession object.
     */
    public MessengerSession() {
        packetListeners = new Hashtable();
        messageListenerList = new EventListenerList();
    }

    /**
     * Sends <i>raw</i> to the session server.
     *
     * @see MessengerPacket
     */
    public synchronized void sendMessage(String raw) {
        log.append("MSG SND: " + raw);
        sessionOut.print(raw);
    }

    /**
     * Sends <i>p</i> to the session server.
     *
     * @see MessengerPacket
     */
    public void sendMessage(MessengerPacket p) {
        sendMessage(p.toString());
    }

    /**
     * Registers <i>l</i> to receive MessengerPackets received for
     * the specific transaction <i>id</i>. Only one listener can be
     * specified for a specific transaction <i>id</i>.
     *
     * @see MessengerPacket
     */
    public void addPacketListener(int id, PacketListener l) {
        packetListeners.put(new Integer(id), l);
    }

    /**
     * Unregisters the current listener for the specified transaction
     * <i>id</i>.
     *
     * @see #addPacketListener
     */
    public void removePacketListener(int id) {
        packetListeners.remove(new Integer(id));
    }

    protected void firePacketReceived(MessengerPacket p) {
        int txID = p.getTransactionID();
        Object o = packetListeners.get(new Integer(txID));
        if (o != null && o instanceof PacketListener) {
            ((PacketListener) o).packetReceived(p);
        }
    }

    public void addMessageListener(MessageListener l) {
        messageListenerList.add(MessageListener.class, l);
    }

    public void removeMessageListener(MessageListener l) {
        messageListenerList.remove(MessageListener.class, l);
    }

    protected void fireMessageReceived(MessageEvent e) {
        Object[] listeners = messageListenerList.getListenerList();
        for (int i = listeners.length-2; i >= 0; i -= 2) {
            if (listeners[i] == MessageListener.class) {
                MessageListener l = (MessageListener) listeners[i+1];
                l.messageReceived(e);
            }
        }
    }

    protected synchronized void connectToServer(String address) 
            throws IOException, SocketException {

        Assert.test(sessionSocket == null);
        Assert.test(sessionOut == null);
        Assert.test(sessionIn == null);

        log.append("Connecting to " + address);

        int port;
        String host = null;
        try {
            StringTokenizer t = new StringTokenizer(address, ":");
            host = t.nextToken();
            try {
                port = (short) Integer.parseInt(t.nextToken());
            } catch (NumberFormatException ex) {
                port = MessengerProtocol.PORT;
            }
        } catch (NoSuchElementException ex) {
            throw new IOException("invalid server address");
        }

        sessionSocket = new Socket(host, port);
        OutputStream socketOut = sessionSocket.getOutputStream();
        sessionOut = new PrintStream(socketOut);

        /*
         * Messenger protocol consists of packets with the US-ASCII
         * encoding only. Additional message data might be in other formats,
         * but that data is read outside of the bounds of the BufferedReader. 
         */
        InputStreamReader reader = new InputStreamReader(
            sessionSocket.getInputStream(), "US-ASCII");
        sessionIn = new BufferedReader(reader);

        if (listenerThread == null) {
            listenerThread = new Thread(new SessionListener());
            listenerThread.start();
        }
    }

    protected synchronized void redirect(String address) 
            throws IOException, ProtocolException {

        /* redirect should only be called by the listenerThread */
        Assert.test(listenerThread == Thread.currentThread());

        log.append("Redirecting to " + address);

        if (sessionSocket != null) {
            log.append("Shutting down session socket prior to redirection...");
            disconnectSession();
        }

        connectToServer(address);
    }

    protected synchronized void disconnectSession() {
        log.append("Disconnecting from session socket...");

        try {
            sessionSocket.close();
        } catch (Exception ex) {
            log.append("Error closing session socket: " + ex.getMessage()); 
        }

        sessionSocket = null;
        sessionOut = null;
        sessionIn = null;
    }

    /**
     * This methods provides basic processing on any <i>line</i> of data
     * received from the session's socket. 
     */
    private void processLine(String line) throws Exception {
        log.append("MSG RCV: " + line);

        /* Attempt to create an MessengerPacket instance from
          the raw packet data */
        MessengerPacket p = null;
        try {
            p = MessengerPacket.getInstance(line);
        } catch (ProtocolException ex) {
            log.append("Bad packet: " + ex.getMessage());
            return;
        }

        /* allow any subclasses a chance to perform pre-processing */
        preprocessPacket(p);

        /* send the packet to any receivers based on transaction ID */
        firePacketReceived(p);

        /* Read and process the remaining data if this is a message packet */
        if (p instanceof MessagePacket) {
            readMessageData((MessagePacket) p);
            processMessage((MessagePacket) p);
        }

        /* allow any subclasses a chance to perform post-processing */
        postprocessPacket(p);
    }

    /**
     * Subclasses should override this method if they wish to perform
     * pre-processing of packets before the MessengerSession calls
     * the intended receiver of the packet.
     */
    protected void preprocessPacket(MessengerPacket p) {
    }

    /**
     * Subclasses should override this method if they wish to perform
     * post-processing of packets after the MessengerSession calls
     * the intended receiver of the packet.
     */
    protected void postprocessPacket(MessengerPacket p) {
    }

    /*
     * Reads the remaining block of data following a MessagePacket.
     */
    private void readMessageData(MessagePacket p) {
        int length = p.getLength();
        char[] buffer = new char[length];
        try {
            int n = 0;
            while (n < length) {
                n += sessionIn.read(buffer, n, length-n);
            }
        } catch (IOException ex) {
            log.append("Unable to read message data: " + ex.getMessage());
            return;
       }

       p.setRawData(buffer);
    }

    /**
     * Subclasses should override this method to perform processing
     * on MessagePacket messages.
     */
    protected void processMessage(MessagePacket p) {
        String hdr = p.getHeaderData("Content-Type");
        StringTokenizer st = new StringTokenizer(hdr, ";");
        String contentType = st.nextToken().trim();

        if (contentType.equals("text/plain")) {
            String charset = "US-ASCII";
            while (st.hasMoreTokens()) {
                String t = st.nextToken();
                StringTokenizer st2 = new StringTokenizer(t, "=");
                String field = st2.nextToken().trim();
                String value = st2.nextToken().trim();

                if (t.toLowerCase().equals("charset")) {
                    charset = value.toUpperCase();
                }
            }

            String data = null;
            try {
                data = new String(p.getMessageData(), charset);
            } catch (UnsupportedEncodingException ex) {
                log.append("Unable to use specified encoding: " + charset + 
                        ", " + ex.getMessage());
                data = new String(p.getMessageData());
            }

            Contact c = new Contact(p.getUserHandle(), p.getFriendlyName());
            MessageEvent e = new MessageEvent(this, c, data);
            fireMessageReceived(e);

        } else if (contentType.equals("text/x-msmsgscontrol")) {
            log.append("text/x-msmsgscontrol received:\r\n" +
                new String(p.getMessageData()));

        } else if (contentType.equals("text/x-msmsgsprofile")) {
            log.append("Login time is: " + p.getHeaderData("LoginTime"));

        } else if (contentType.equals("application/x-msmsgsemailnotification")) {
            log.append("You have e-mail: \r\n" + 
                    new String(p.getMessageData()));
            AudioClip clip = Herald.soundList.getClip("sounds/newemail.wav");
            clip.play();
            log.append("AudioClip played: " + clip.toString());
        } else {
            log.append("Whoops - an unknown Content-Type (" + contentType + 
                    ") message was received. Ignoring...");
        }
    }

    /*
     * An inner class that listens for incoming data received from the
     * session's socket connection.
     */
    class SessionListener implements Runnable {
        public void run() {
            log.append("SessionListener started.");

            try {
                String line = null;
                while ((line = sessionIn.readLine()) != null) {
                    if (Thread.interrupted()) {
                        log.append("SessionListener thread was interrupted.");
                        break;
                    } else { 
                        processLine(line);
                    }
                }
            } catch (Exception ex) {
                log.append("SessionListener caught exception: " + 
                        ex.getMessage());
            }

            log.append("SessionListener stopped.");
        }
    }
}
