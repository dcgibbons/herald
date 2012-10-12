/*
 * $Id: MessengerClient.java,v 1.8 2000/07/20 03:36:44 dcgibbons Exp $
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
import javax.swing.event.*;

import net.sourceforge.util.*;

public class MessengerClient extends MessengerSession {
    protected String[] securityPolicies;
    protected EventListenerList listenerList;
    protected MessengerEvent messengerEvent;
    protected boolean connected;
    protected String userHandle;
    protected char[] password;
    protected String friendlyName;
    protected ClientProperties props;
    protected List switchboardSessions;
    private MessageLogSingleton log = MessageLogSingleton.instance();

    public MessengerClient() {
        super();
        listenerList = new EventListenerList();
        switchboardSessions = new LinkedList();
    }

    public Contact getCurrentUser() {
        return new Contact(userHandle, friendlyName);
    }

    public ClientProperties getClientProperties() {
        return props;
    }

    /**
     * Begins a connection and dialog to the Messenger server with the
     * the specified user handle and password. Any registered
     * MessengerListener objects will receive notification of the
     * success or failure of the connection request.
     *
     * @param String userHandle the user's handle, in this
     *                          implementation it will always be
     *                          a HotMail e-mail address.
     * @param String password The password for the specified
     *                        user's handle.
     * @exception IllegalStateException if the connection is
     *                                  already established.
     */
    public synchronized void connect(String userHandle, char[] password)
            throws IllegalStateException {
        if (connected) {
            throw new IllegalStateException("already connected");
        } else {
            connected = true;
            this.userHandle = userHandle;
            this.password = password;
            MessengerProtocol.reset();
            new Thread(new ConnectionThread()).start();
        }
    }

    /**
     * Disconnects from the current Messenger session. Any registered
     * MessengerListener objects will receive notification of the
     * success of the disconnect request.
     *
     * @exception   IllegalStateException   if a connection is not
     *                                      active.
     */
    public synchronized void disconnect() throws IllegalStateException {
        if (!connected) {
            throw new IllegalStateException("not connected");
        } else {
            new Thread(new DisconnectThread()).start();
        }
    }

    public void changeOnlineState(String state) {
        ChangePacket p = null;
        try {
            p = new ChangePacket();
            p.initToState(state);
            sendMessage(p);
            addPacketListener(p.getTransactionID(), new StateChanger());
        } catch (ProtocolException ex) {
        }
    }

    /**
     * Opens a switchboard session in order to message a specific
     * <i>contact</i>.
     */
    public void openSwitchboardSession(Contact c) {
        boolean found = false;
        ListIterator i = switchboardSessions.listIterator(0);
        while (i.hasNext()) {
            SwitchboardSession sbs = (SwitchboardSession) i.next();
            if (sbs.contains(c)) {
                found = true;
                break;
            }
        }
        if (found) {
            // TODO: hmm, now what?
            log.append("Found active switchboard session for contact " + c);
        } else {
            RedirectPacket p = new RedirectPacket();
            addPacketListener(p.getTransactionID(), 
                    new SwitchboardRedirector(c));
            sendMessage(p);
        }
    }

    /**
     * Reports the current connection state of the client.
     *
     * @return  the current connection state.
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Registers <i>l</i> to receive MessengerEvents when state
     * changes to the client occur.
     */
    public void addMessengerListener(MessengerListener l) {
        listenerList.add(MessengerListener.class, l);
    }

    /**
     * Unregisters <i>l</i> so that it will no longer receive
     * MessengerEvents when the client state changes.
     *
     * @see #addMessengerListener
     */
    public void removeMessengerListener(MessengerListener l) {
        listenerList.remove(MessengerListener.class, l);
    }

    protected void fireMessengerConnected() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length-2; i >= 0; i -= 2) {
            if (listeners[i] == MessengerListener.class) {
                if (messengerEvent == null) {
                    messengerEvent = new MessengerEvent(this);
                }
                MessengerListener l = (MessengerListener) listeners[i+1];
                l.messengerConnected(messengerEvent, friendlyName);
            }
        }
    }

    protected void fireMessengerDisconnected(String reason) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length-2; i >= 0; i -= 2) {
            if (listeners[i] == MessengerListener.class) {
                if (messengerEvent == null) {
                    messengerEvent = new MessengerEvent(this);
                }
                MessengerListener l = (MessengerListener) listeners[i+1];
                l.messengerDisconnected(messengerEvent, reason);
            }
        }
    }

    protected void fireMessengerSynchronized() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length-2; i >= 0; i -= 2) {
            if (listeners[i] == MessengerListener.class) {
                if (messengerEvent == null) {
                    messengerEvent = new MessengerEvent(this);
                }
                MessengerListener l = (MessengerListener) listeners[i+1];
                l.messengerSynchronized(messengerEvent);
            }
        }
    }

    protected void fireMessengerUserStateChange(String state) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length-2; i >= 0; i -= 2) {
            if (listeners[i] == MessengerListener.class) {
                MessengerListener l = (MessengerListener) listeners[i+1];
                l.messengerUserStateChange(state);
            }
        }
    }

    protected void fireMessengerContactState(Contact c) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length-2; i >= 0; i -= 2) {
            if (listeners[i] == MessengerListener.class) {
                MessengerListener l = (MessengerListener) listeners[i+1];
                l.messengerContactState(c);
            }
        }
    }

    protected void fireMessengerSwitchboardEstablished(SwitchboardSession sbs) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length-2; i >= 0; i -= 2) {
            if (listeners[i] == MessengerListener.class) {
                MessengerListener l = (MessengerListener) listeners[i+1];
                l.messengerSwitchboardEstablished(sbs);
            }
        }
    }

    class ConnectionThread implements Runnable {
        public void run() {
            try {
                String address = Herald.DISPATCH_SERVER + ":" + 
                        Integer.toString(MessengerProtocol.PORT);
                connectToServer(address);

                VersionPacket p = new VersionPacket();
                addPacketListener(p.getTransactionID(), new VerifyProtocol());
                sendMessage(p);

            } catch (Exception ex) {
                log.append("Connect failed: " + ex.getMessage());
                connected = false;
                fireMessengerDisconnected(null);
            }
        }
    }

    class DisconnectThread implements Runnable {
        public void run() {
            log.append("Disconnecting...");

            listenerThread.interrupt();

            LogoutPacket p = new LogoutPacket();
            sendMessage(p);

            disconnectSession();

            try {
                listenerThread.join();
            } catch (InterruptedException ex) {
                log.append("Interrupted while waiting for listenerThread");
            } finally {
                listenerThread = null;
            }
           
            connected = false;
            fireMessengerDisconnected(null);
        }
    }

    protected void preprocessPacket(MessengerPacket p) {
        if (p instanceof LogoutPacket) {
            log.append("LogoutPacket received -- forcing user disconnect!");

            Thread.currentThread().interrupt();

            disconnectSession();

            listenerThread = null;
           
            connected = false;

            // TODO: disconnect reasons need to be moved to the user interface
            fireMessengerDisconnected("You have been logged off because you have logged on from another location.");
        }
    }

    protected void postprocessPacket(MessengerPacket p) {
        if (p instanceof RedirectPacket) {
            RedirectPacket rp = (RedirectPacket) p;
            if (rp.getType().equals("NS")) {
                removePacketListener(rp.getTransactionID());

                try {
                    redirect(rp.getAddress());
                    VersionPacket vp = new VersionPacket();
                    addPacketListener(vp.getTransactionID(), 
                            new VerifyProtocol());
                    sendMessage(vp);
                } catch (IOException ex) {
                    log.append("Unable to redirect connection: " + ex.getMessage());
                }
            }
        }

        if (p instanceof RingPacket) {
            RingPacket rp = (RingPacket) p;
            Contact c = new Contact(userHandle, friendlyName);
            try {
                SwitchboardSession sbs = new SwitchboardSession(this, 
                        userHandle, c, rp);
                switchboardSessions.add(sbs);
                fireMessengerSwitchboardEstablished(sbs);
            } catch (IOException ex) {
                log.append("SwitchboardSession failed: " + ex.getMessage());
            }
        }

        /* contact state change notifications */
        if (p instanceof InitialStatePacket) {
            InitialStatePacket isp =  (InitialStatePacket) p;
            Contact c = new Contact(isp.getUserHandle(), isp.getFriendlyName(),
                    isp.getSubstate());
            fireMessengerContactState(c);

        } else if (p instanceof OfflinePacket) {
            OfflinePacket op = (OfflinePacket) p;
            Contact c = new Contact(op.getUserHandle(), null,                                       MessengerProtocol.STATE_OFFLINE);
            fireMessengerContactState(c);

        } else if (p instanceof OnlinePacket) {
            OnlinePacket op = (OnlinePacket) p;
            Contact c = new Contact(op.getUserHandle(), op.getFriendlyName(), 
                    op.getSubstate());
            fireMessengerContactState(c);
        }
    }

    /*
     * This class userHandles packets in response to a protocol verification
     * request.
     */
    class VerifyProtocol implements PacketListener {
        public void packetReceived(MessengerPacket p) {
            removePacketListener(p.getTransactionID());

            if (!(p instanceof VersionPacket)) {
                log.append("Unexpected response to protocol version check: " + p);
            } else {
                String[] dialects = ((VersionPacket) p).getDialects();
                boolean found = false;
                for (int i = 0; i < dialects.length; i++) {
                    if (dialects[i].toUpperCase().equals(MessengerProtocol.DIALECT_NAME)) {
                        found = true;
                    }
                }

                if (found) {
                    Authenticator auth = new Authenticator(MessengerClient.this);
                    auth.addAuthenticationListener(new AuthListener());
                    auth.authenticate(userHandle, password);
                }
            }
        }
    }

    /*
     * This listener handles responses to the authentication request.
     */
    class AuthListener implements AuthenticationListener {
        public void authenticationSuccessful(AuthenticationEvent ev) {
            for (int i = 0, n = password.length; i < n; i++) {
                password[i] = ' ';
            }

            friendlyName = ev.getFriendlyName();
            fireMessengerConnected();

            /* load properties */
            int serialNo;
            try {
                String folder = System.getProperty("user.home");
                String filesep = System.getProperty("file.separator");
                String subdir = ".herald";
                String fname = folder + filesep + subdir + filesep + ev.getUserHandle() + ".props"; // TODO: are user handles save for file names?
                FileInputStream fin = new FileInputStream(fname);
                ObjectInputStream in = new ObjectInputStream(fin);
                props = (ClientProperties) in.readObject();
                serialNo = props.serialNo;
                fin.close();
            } catch (Exception ex) {
                log.append("Unable to read client properties: " + ex.getMessage());
                log.append("Loading properties from server...");
                serialNo = 0;
            }

            try {
                SyncPacket cp = new SyncPacket(serialNo);
                addPacketListener(cp.getTransactionID(), new Synchronizer());
                sendMessage(cp);
            } catch (Exception ex) {
                log.append("Synchronization failed: " + ex.getMessage());
            }
        }

        public void authenticationFailed(AuthenticationEvent ev) {
            /* authfailed should only be called by the listenerThread */
            Assert.test(listenerThread == Thread.currentThread());

            log.append("AuthentictionEvent received: authenticationFailed");
            disconnectSession();
            fireMessengerDisconnected(MessengerProtocol.ERR_AUTHENTICATION_FAILED);
        }
    }

    /*
     * This class userHandles client/server property synchronization.
     */
    class Synchronizer implements PacketListener {
        protected boolean done = false;

        public void packetReceived(MessengerPacket p) {
            if (p instanceof SyncPacket) {
                SyncPacket sp = (SyncPacket) p;

                if (props != null && sp.getSerialNo() == props.serialNo) {
                    log.append("Properties are already synchronized."); 
                    done = true;
                } else {
                    log.append("Server has newer properties than client... " + 
                            "waiting for synchronization");
                    MessengerClient.this.props = new ClientProperties();
                    props.serialNo = sp.getSerialNo();
                }

            } else if (p instanceof GTCPacket) {

                GTCPacket gtcp = (GTCPacket) p;
                props.prompt = gtcp.getPrivatePrompt();

            } else if (p instanceof BLPPacket) {

                BLPPacket blpp = (BLPPacket) p;
                props.privacyMode = blpp.getBlockMessages();

            } else if (p instanceof ListPacket) {

                ListPacket lp = (ListPacket) p;
                Contact user = new Contact(lp.getUserHandle(), lp.getFriendlyName());
                String list = lp.getList();
                LinkedList l = null;
                if (list.equals("FL")) {
                    l = props.forwardList;
                } else if (list.equals("AL")) {
                    l = props.allowList;
                } else if (list.equals("BL")) {
                    l = props.blockedList;
                } else if (list.equals("RL")) {
                    l = props.reverseList;
                    if (lp.getItemNo() == lp.getTotalItems()) {
                        done = true;
                    }
                }
                l.add(user);
            }

            if (done) {
                /* dump properties */
                try {
                    String folder = System.getProperty("user.home");
                    String filesep = System.getProperty("file.separator");
                    String subdir = ".herald";
                    File subdirFile = new File(folder + filesep + subdir);
                    if (!subdirFile.exists()) {
                        subdirFile.mkdirs();
                    }
                    FileOutputStream fout = new FileOutputStream(new File(subdirFile, userHandle + ".props"));
                    ObjectOutputStream out = new ObjectOutputStream(fout);
                    out.writeObject(props);
                    out.flush();
                    fout.close();
                } catch (IOException ex) {
                    log.append("Unable to save properties: " + ex.getMessage());
                }

                log.append("Synchronization complete.");
                fireMessengerSynchronized();
                removePacketListener(p.getTransactionID());
            }
        }
    }

    /*
     * This class handles a client state change acknowledgement from the 
     * the server.
     */
    class StateChanger implements PacketListener {
        public void packetReceived(MessengerPacket p) {
            if (p instanceof ChangePacket) {
                ChangePacket cp = (ChangePacket) p;
                fireMessengerUserStateChange(cp.getState());
            }
        }
    }

    /*
     * This class handles a switchboard redirect request.
     */
    class SwitchboardRedirector implements PacketListener {
        protected Contact contact;

        SwitchboardRedirector(Contact c) {
            contact = c;
        }

        public void packetReceived(MessengerPacket p) {
            if (p instanceof RedirectPacket) {
                RedirectPacket rp = (RedirectPacket) p;

                try {
                    SwitchboardSession sbs = 
                            new SwitchboardSession(MessengerClient.this,
                                    userHandle, contact, rp);
                    switchboardSessions.add(sbs);
                    fireMessengerSwitchboardEstablished(sbs);
                } catch (IOException ex) {
                    log.append("Unable to create switchboard session: " + ex.getMessage());
                }
            }
        }
    }
}
