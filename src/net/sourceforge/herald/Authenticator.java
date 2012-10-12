/*
 * Authenticator.java
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
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.swing.event.EventListenerList;

import net.sourceforge.util.*;

public class Authenticator {
    private EventListenerList listenerList = new EventListenerList();
    private MessengerClient client;
    private String userHandle;
    private char[] password;
    private String securityPolicy;
    private MessageLogSingleton log = MessageLogSingleton.instance();

    public Authenticator(MessengerClient client) {
        this.client = client;
    }

    public void authenticate(String userHandle, char[] password) {
        this.userHandle = userHandle;
        this.password = password;

        /* determine the supported authentication protocols from the server */
        PolicyPacket clientPacket = new PolicyPacket();
        client.addPacketListener(clientPacket.getTransactionID(), new GetAuthProtocols());
        client.sendMessage(clientPacket);
    }

    public void addAuthenticationListener(AuthenticationListener l) {
        listenerList.add(AuthenticationListener.class, l);
    }

    public void removeAuthenticationListener(AuthenticationListener l) {
        listenerList.remove(AuthenticationListener.class, l);
    }

    protected void fireAuthenticationSuccessful(String userHandle, String friendlyName) {
        Object[] listeners = listenerList.getListenerList();
        AuthenticationEvent ev = new AuthenticationEvent(this, userHandle, friendlyName);
        for (int i = listeners.length-2; i >= 0; i -= 2) {
            if (listeners[i] == AuthenticationListener.class) {
                AuthenticationListener l = (AuthenticationListener) listeners[i+1];
                l.authenticationSuccessful(ev);
            }
        }
    }

    protected void fireAuthenticationFailed() {
        Object[] listeners = listenerList.getListenerList();
        AuthenticationEvent ev = new AuthenticationEvent(this, null, null);
    for (int i = listeners.length-2; i >= 0; i -= 2) {
            if (listeners[i] == AuthenticationListener.class) {
                AuthenticationListener l = (AuthenticationListener) listeners[i+1];
                l.authenticationFailed(ev);
            }
        }
    }

    /* Select an authentication protocol based on the first available
       one in this environment. */
    protected void selectPolicy(String[] policies) {
        for (int i = 0; i < policies.length; i++) {
            MessageDigest md;
            try {
                md = MessageDigest.getInstance(policies[i]);
            } catch (NoSuchAlgorithmException ex) {
                md = null;
            }

            if (md != null) {
                securityPolicy = policies[i];
                break;
            }
        }
    }

    /* Calculate the authentication response by producing a message
       digest hash of the challenge text and the user's password. */
    protected String calcAuthResponse(String challenge) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(securityPolicy);
        md.update(challenge.getBytes());
        for (int i = 0, n = password.length; i < n; i++) {
            md.update((byte) password[i]);
        }
        byte[] digest = md.digest();

        StringBuffer digestText = new StringBuffer();
        for (int i = 0; i < digest.length; i++) {
            int v = (digest[i] < 0) ? digest[i] + 256 : digest[i];
            String hex = Integer.toHexString(v);
            if (hex.length() == 1) {
                digestText.append("0");
            }
            digestText.append(hex);
        }

        return digestText.toString();
    }


    /*
     * This class handles packets in response for accepted authentication protocols.
     */
    class GetAuthProtocols implements PacketListener {
        public void packetReceived(MessengerPacket p) {
            client.removePacketListener(p.getTransactionID());

            if (p instanceof PolicyPacket) {
                PolicyPacket pp = (PolicyPacket) p;
                selectPolicy(pp.getPolicies());

                AuthPacket clientPacket = null;
                try {
                    clientPacket = new AuthPacket(securityPolicy, AuthPacket.INITIATE_INFO, userHandle);
                    client.addPacketListener(clientPacket.getTransactionID(), new GetAuthChallenge());
                    client.sendMessage(clientPacket);
                } catch (ProtocolException ex) {
                    fireAuthenticationFailed();
                }
            }
        }
    }

    /*
     * This class handles packets in response to authentication requests.
     */
    class GetAuthChallenge implements PacketListener {
        public void packetReceived(MessengerPacket p) {
            client.removePacketListener(p.getTransactionID());

            if (p instanceof AuthPacket) {
                try {
                    AuthPacket ap = (AuthPacket) p;
                    String challenge = ap.getAuthInfo();
                    String response = calcAuthResponse(challenge);
                    AuthPacket clientPacket = null;
                    clientPacket = new AuthPacket(securityPolicy, AuthPacket.RESPONSE_INFO, response);
                    client.addPacketListener(clientPacket.getTransactionID(), new GetAuthResponse());
                    client.sendMessage(clientPacket);
                } catch (Exception ex) {
                    log.append("Unable to respond to authentication request: " + ex.getMessage());
                }
            }
        }
    }

    class GetAuthResponse implements PacketListener {
        public void packetReceived(MessengerPacket p) {
            client.removePacketListener(p.getTransactionID());
            boolean success = false;
            String friendlyName = null;

            try {
                if (p instanceof AuthPacket) {
                    AuthPacket ap = (AuthPacket) p;
                    try {
                        friendlyName = URLDecoder.decode(ap.authInfo);
                    } catch (Exception ex) {
                        log.append("Unable to decode friendly name: " + ex.getMessage());
                        friendlyName = ap.authInfo;
                    }
                    success = true;
                }
            } finally {
                if (success) {
                    fireAuthenticationSuccessful(userHandle, friendlyName);
                } else {
                    fireAuthenticationFailed();
                }
            }
        }
    }
}
