/*
 * $Id: ConversationFrame.java,v 1.2 2000/07/14 01:02:10 dcgibbons Exp $
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

package net.sourceforge.herald.ui;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;

import net.sourceforge.herald.*;
import net.sourceforge.util.*;

public class ConversationFrame extends JFrame
        implements ActionListener, KeyListener, MessageListener, SwitchboardListener {

    private static final int INPUT_TEXT_ROWS = 3;

    protected JPanel buttonPanel;
    protected JButton inviteButton;
    protected JButton blockButton;

    protected JPanel contactsPanel;
    protected JLabel contactsLabel;

    protected JPanel infoPanel;

    protected JPanel messagePanel;
    protected OutputTextPane outputArea;
    protected JScrollPane outputScrollPane;
    protected JTextArea inputArea;
    protected JButton sendButton;

    protected SwitchboardSession session;

    private Cursor savedCursor = null;

    private MessageLogSingleton log = MessageLogSingleton.instance();

    public ConversationFrame(SwitchboardSession session) {
        this.session = session;
        this.session.addSwitchboardListener(this);
        this.session.addMessageListener(this);

        setSize(600, 400); // TODO: fix me; size and position should be fetched from the properties, no?
        setTitle(calcTitle());

        /* create the button bar */
        buttonPanel = new JPanel();
        inviteButton = new JButton("Invite");
        blockButton = new JButton("Block");
        buttonPanel.add(inviteButton);
        buttonPanel.add(blockButton);

        /* create the contacts panel */
        contactsPanel = new JPanel(new FlowLayout());
        contactsLabel = new JLabel(calcToList());
        contactsPanel.add(contactsLabel);

        /* create the output area */
        outputArea = new OutputTextPane();
        outputArea.setEditable(false);
        outputScrollPane = new JScrollPane(outputArea);
        outputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        outputScrollPane.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Output"),
                                BorderFactory.createEmptyBorder(5, 5, 5, 5)),
                outputScrollPane.getBorder()));

        /* create the input area */
        inputArea = new JTextArea();
        inputArea.setRows(ConversationFrame.INPUT_TEXT_ROWS);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.addKeyListener(this);
        JScrollPane inputScrollPane = new JScrollPane(inputArea);
        inputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel sendPanel = new JPanel(new BorderLayout());
        sendPanel.add(inputScrollPane);
        sendPanel.add(sendButton = new JButton("Send"), BorderLayout.EAST);
        sendButton.addActionListener(this);
        sendPanel.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Input"),
                                BorderFactory.createEmptyBorder(5, 5, 5, 5)),
                sendPanel.getBorder()));

        JPanel textPanels = new JPanel(new BorderLayout());
        textPanels.add(outputScrollPane, BorderLayout.CENTER);
        textPanels.add(sendPanel, BorderLayout.SOUTH);

        /* add the primary components to the root panel */
        JPanel p = new JPanel(new BorderLayout());
        p.add(buttonPanel, BorderLayout.NORTH);
        p.add(contactsPanel, BorderLayout.CENTER);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(p, BorderLayout.NORTH);
        getContentPane().add(textPanels, BorderLayout.CENTER);
    }

    public boolean contains(Contact c) {
        return session.contains(c);
    }

    protected String calcTitle() {
        StringBuffer buffer = new StringBuffer();
        int n = 0;
        synchronized (session) {
            LinkedList contacts = session.getContacts();
            ListIterator i = contacts.listIterator(0);
            while (i.hasNext()) {
                Contact c = (Contact) i.next();
                if (i.hasNext()) {
                    buffer.append(", ");
                }
                buffer.append(c.getFriendlyName());
            }
        }
        buffer.append(" - Instant Message");
        return buffer.toString();
    }

    protected String calcToList() {
        StringBuffer buffer = new StringBuffer();
        int n = 0;
        synchronized (session) {
            LinkedList contacts = session.getContacts();
            ListIterator i = contacts.listIterator(0);
            while (i.hasNext()) {
                Contact c = (Contact) i.next();
                if (i.hasNext()) {
                    buffer.append(", ");
                }
                buffer.append(c.getFriendlyName())
                      .append("<")
                      .append(c.getContactName())
                      .append(">");
            }
        }

        return buffer.toString();
    }

    public void contactJoined(Contact c) {
        setTitle(calcTitle());
        contactsLabel.setText(calcToList());
        String msg = c.getFriendlyName() + " has joined the conversation.\r\n";
        try {
            Document doc = outputArea.getDocument();
            doc.insertString(doc.getLength(), msg, outputArea.getStyle("bold"));
        } catch (BadLocationException ex) {
        }
    }

    public void contactLeft(Contact c) {
        setTitle(calcTitle());
        contactsLabel.setText(calcToList());
        String msg = c.getFriendlyName() + " has left the conversation.\r\n";
        try {
            Document doc = outputArea.getDocument();
            doc.insertString(doc.getLength(), msg, outputArea.getStyle("bold"));
        } catch (BadLocationException ex) {
        }
    }

    public void messageArrived(MessagePacket p) {
    }

    public void messageReceived(MessageEvent e) {
        Document doc = outputArea.getDocument();

        synchronized (doc) {
            String hdr = e.getContact().getFriendlyName() + " says:\r\n";
            String msg = e.getData();
            if (!msg.endsWith("\n")) {
                msg = msg + "\n";
            }
            try {
                doc.insertString(doc.getLength(), hdr, outputArea.getStyle("bold"));

                /* cycle through the message, checking each word for a http URL */
                StringTokenizer st = new StringTokenizer(msg, " \t\n\r\f", true);
                while (st.hasMoreTokens()) {
                    String s = st.nextToken();
                    Style style;
                    if (s.toLowerCase().startsWith("http://")) {
                        style = outputArea.getStyle("underline");
                    } else {
                        style = outputArea.getStyle("regular");
                    }
                    doc.insertString(doc.getLength(), s, style);
                }

                Caret caret = outputArea.getCaret();
                caret.setDot(doc.getLength());
            } catch (BadLocationException ex) {
            }
        }

        try {
            AudioClip clip = Herald.soundList.getClip("sounds/newmsg.wav");
            clip.play();
            log.append("AudioClip played: " + clip.toString());
        } catch (Exception ex) {
            log.append("Unable to play AudioClip: " + ex.getMessage());
        }
    }

    protected void submitInput() {
        try {
            Document doc = inputArea.getDocument();
            synchronized (doc) {
                int n = doc.getLength();
                String s = doc.getText(0, n);
                doc.remove(0, n);
                session.sendTextMessage(s);
                messageReceived(new MessageEvent(this, session.getClient().getCurrentUser(), s));
            }
        } catch (BadLocationException ex) {
        }
    }

    /*
     * interface ActionListener
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == sendButton) {
            submitInput();
        }
    }

    /*
     * interface KeyListener
     */
    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {        
        if (e.getKeyCode() == KeyEvent.VK_ENTER)  {
            e.consume();
            if (e.isShiftDown())  {
                try {                    
                    Document doc = inputArea.getDocument();                    
                    Style s = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);                    
                    StringBuffer buf = new StringBuffer();                    
                    buf.append(e.getKeyChar());                    
                    doc.insertString(doc.getLength(), buf.toString(), s);                
                } catch (BadLocationException ex) {                
                }            
            } else {                
                submitInput();            
            }        
        }    
    }    

    public void keyTyped(KeyEvent e) {
    }
}
