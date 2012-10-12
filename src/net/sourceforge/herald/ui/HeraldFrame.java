/*
 * $Id: HeraldFrame.java,v 1.2 2000/07/14 01:02:10 dcgibbons Exp $
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
import java.lang.reflect.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import net.sourceforge.herald.*;
import net.sourceforge.util.*;

public class HeraldFrame extends JFrame implements MessengerListener {
    private MessengerClient client;
    private HeraldProperties props;

    private String propFileName;

    private JMenuBar menuBar;
    private JToolBar toolbar;

    private JMenu fileMenu;
    private JMenu statusMenu;
    private JMenu viewMenu;
    private JMenu helpMenu;

    private Action logOnAction;
    private Action logOffAction;
    private Action addContactAction;
    private Action removeContactAction;
    private Action propertiesAction;
    private Action exitAction;
    private Action toolbarAction;
    private Action debugAction;
    private Action aboutAction;

    private JList contactList;
    private DefaultListModel contactListModel;

    private JLabel statusLabel;

    private String handle;
    private String friendlyName;
    private String state;

    private ProgressMonitor progressMonitor;
    private javax.swing.Timer connectTimer;

    private MessageLogSingleton log = MessageLogSingleton.instance();
    private JFrame debugFrame = null;

    private boolean tryingToConnect = false;

    private LinkedList conversations = new LinkedList();

    private ResourceBundle errors = 
            ResourceBundle.getBundle("net.sourceforge.herald.ErrorsBundle");
    private ResourceBundle states = 
            ResourceBundle.getBundle("net.sourceforge.herald.StatesBundle");
    private ResourceBundle msgs =
            ResourceBundle.getBundle("MessagesBundle");

    public HeraldFrame(MessengerClient client) {
        this.client = client;
        client.addMessengerListener(this);

        props = new HeraldProperties();
        handle = props.getUserHandle();

        setTitle(msgs.getString("HeraldFrameTitle"));
        setLocation(new Point(props.getFrameXPos(), props.getFrameYPos()));
        setSize(props.getFrameWidth(), props.getFrameHeight());

        addWindowListener(new WindowEventHandler());

        /* create the main menu bar */
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        /* create the actions */
        logOnAction = new LogOnAction();
        logOffAction = new LogOffAction();
        addContactAction = new AddContactAction();
        removeContactAction = new RemoveContactAction();
        propertiesAction = new PropertiesAction();
        exitAction = new ExitAction();
        toolbarAction = new ToolBarAction();
        debugAction = new DebugAction();
        aboutAction = new AboutAction();

        /* populate the menus */
        fileMenu = new JMenu(msgs.getString("FileMenu"));
        statusMenu = new JMenu(msgs.getString("StatusMenu"));
        viewMenu = new JMenu(msgs.getString("ViewMenu"));
        helpMenu = new JMenu(msgs.getString("HelpMenu"));

        // TODO: what's the right way to internationalize mnemonics?
        fileMenu.add(logOnAction).setMnemonic('L'); 
        fileMenu.add(logOffAction).setMnemonic('O');
        fileMenu.addSeparator();
        fileMenu.add(statusMenu).setMnemonic('S');
        fileMenu.addSeparator();
        fileMenu.add(addContactAction).setMnemonic('A');
        fileMenu.add(removeContactAction).setMnemonic('R');
        fileMenu.add(propertiesAction).setMnemonic('P');
        fileMenu.add(exitAction).setMnemonic('X');
        menuBar.add(fileMenu);

        statusMenu.getAccessibleContext().setAccessibleDescription(
                msgs.getString("StatusMenuDescription"));
        addStateMenuItem(MessengerProtocol.STATE_ONLINE, 'O');
        addStateMenuItem(MessengerProtocol.STATE_AWAY, 'A');
        addStateMenuItem(MessengerProtocol.STATE_BUSY, 'B');
        addStateMenuItem(MessengerProtocol.STATE_BRB, 'R');
        addStateMenuItem(MessengerProtocol.STATE_PHONE, 'P');
        addStateMenuItem(MessengerProtocol.STATE_LUNCH, 'L');
        statusMenu.addSeparator();
        statusMenu.add(new StateChangeAction(MessengerProtocol.STATE_HIDDEN,
                msgs.getString("StatusMenuHidden"),
                msgs.getString("StatusMenuHiddenDescription"))).setMnemonic('F');

        viewMenu.add(toolbarAction);
        viewMenu.add(debugAction);
        menuBar.add(viewMenu);

        helpMenu.setMnemonic(KeyEvent.VK_H);
        helpMenu.getAccessibleContext().setAccessibleDescription(
                msgs.getString("HelpMenuDescription"));
        helpMenu.add(aboutAction).setMnemonic('A');
        menuBar.add(helpMenu);

        /* populate the toolbar */
        toolbar = new JToolBar();
        toolbar.add(logOnAction);
        toolbar.add(logOffAction);

        /* create the contact list panel */
        contactListModel = new DefaultListModel();
        contactList = new JList(contactListModel);
        contactList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent ev) {
                if (ev.getClickCount() == 2) {
                    int i = contactList.locationToIndex(ev.getPoint());
                    if (i >= 0) {
                        Contact c = (Contact) contactListModel.get(i);
                        ListIterator li = conversations.listIterator(0);
                        while (li.hasNext()) {
                            ConversationFrame cf = (ConversationFrame) li.next();
                            if (cf.contains(c)) {
                                cf.show();
                                break;
                            }
                        }

                        HeraldFrame.this.client.openSwitchboardSession(c);
                    }
                }
            }
        });

        /* create the status label */
        statusLabel = new JLabel();
        statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());

        /* add the primary components to the root panel */
        JPanel p = new JPanel(new BorderLayout());
        p.add(toolbar, BorderLayout.NORTH);
        p.add(contactList, BorderLayout.CENTER);
        p.add(statusLabel, BorderLayout.SOUTH);
        getContentPane().add(p);

        setDisconnectedState();
    }

    private void addStateMenuItem(String state, char mnemonic) {
        Object[] msgArgs =  {
            states.getObject(state)
        };
        MessageFormat formatter = new MessageFormat(
                msgs.getString("StatusMenuItemDescription"));
        String desc = formatter.format(msgArgs);
        Action a = new StateChangeAction(state, (String) msgArgs[0], desc);
        JMenuItem item = statusMenu.add(a);
        item.setMnemonic(mnemonic);
    }

    private void exitApp() {
        props.setFrameXPos(getLocation().x);
        props.setFrameYPos(getLocation().y);
        props.setFrameWidth(getWidth());
        props.setFrameHeight(getHeight());
        props.setUserHandle(handle);
        props.saveParameters();
        setVisible(false);
        dispose();
        System.exit(0);
    }

    private void setConnectedState() {
        logOnAction.setEnabled(false);
        logOffAction.setEnabled(true);
        addContactAction.setEnabled(true);
        removeContactAction.setEnabled(true);
        propertiesAction.setEnabled(true);
        statusMenu.setEnabled(true);
    }

    private void setDisconnectedState() {
        logOnAction.setEnabled(true);
        logOffAction.setEnabled(false);
        addContactAction.setEnabled(false);
        removeContactAction.setEnabled(false);
        propertiesAction.setEnabled(false);
        statusMenu.setEnabled(false);
        statusLabel.setText("Not logged on"); // TODO: get from ResourceBundle
        contactListModel.removeAllElements();
    }

    /*
     * interface MessengerListener
     */
    public void messengerConnected(MessengerEvent evt, final String friendlyName) {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    progressMonitor.setProgress(1);
                    progressMonitor.setNote(msgs.getString("ConnectProgressNote1"));

                    /* We're technically on-line at this point, but the
                       user interface probably should not indicate this
                       until the client's properties have been synchronized. */

                    HeraldFrame.this.friendlyName = friendlyName;
                    client.changeOnlineState(MessengerProtocol.STATE_ONLINE);
                }
            });
        } catch (Exception ex) {
        }
    }

    public void messengerDisconnected(MessengerEvent evt, final String reason) {
        log.append("messengerDisconnected event received, reason=" + reason);

        Runnable r = null;

        if (tryingToConnect) {
            r = new Runnable() {
                public void run() {
                    connectTimer.stop();
                    connectTimer = null;
                    setDisconnectedState();
                    contactListModel.removeAllElements();

                    String errMsg = (String) errors.getObject(reason);
                    String msg;
                    if (errMsg != null) {
                        msg = errMsg + " Retry connection?";
                    } else {
                        msg = "Unknown error. Retry connection?";
                    }
                    String title = "Connection attempt failed. ";

                    int n = JOptionPane.showConfirmDialog(HeraldFrame.this, msg, title,
                            JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                    if (n == JOptionPane.YES_OPTION) {
                        // TODO: this feels like a cheap hack...
                        logOnAction.actionPerformed(null);
                    }
                }
            };
        } else {
            r = new Runnable() {
                public void run() {
                    setDisconnectedState();
                    if (reason != null) {
                        JOptionPane.showMessageDialog(HeraldFrame.this, reason, "Client Disconnected", 
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
        }

        try {
            SwingUtilities.invokeAndWait(r);
        } catch (Exception ex) {
        }
    }

    public void messengerSynchronized(MessengerEvent evt) {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    progressMonitor.setProgress(2);
                    progressMonitor.setNote(msgs.getString("ConnectProgressNote2"));
                    progressMonitor.close();
                    progressMonitor = null;
                    connectTimer.stop();
                    connectTimer = null;
                    tryingToConnect = false;
                    setConnectedState();
                }
            });
        } catch (Exception ex) {
            System.err.println("Exception caught: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void messengerUserStateChange(final String state) {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    HeraldFrame.this.state = state;
                    // TODO: internationalize this
                    statusLabel.setText(friendlyName + " (" + 
                        states.getObject(state) + ")");
                }
            });
        } catch (Exception ex) {
        }
    }

    public void messengerContactState(final Contact c) {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    log.append("Contact state change: " + c);
                    if (c.isOnline()) {
                        contactListModel.removeElement(c);
                        contactListModel.addElement(c);

                        AudioClip clip = Herald.soundList.getClip("sounds/online.wav");
                        clip.play();
                        log.append("AudioClip played: " + clip.toString());
                    } else {
                        contactListModel.removeElement(c);
                        // TODO: internationalize this
                        log.append(c.getContactName() + " is now offline.");
                    }
                }
            });
        } catch (Exception ex) {
        }
    }

    public void messengerSwitchboardEstablished(final SwitchboardSession sbs) {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    ConversationFrame cf = new ConversationFrame(sbs);
                    conversations.add(cf);
                    cf.show();
                }
            });
        } catch (Exception ex) {
        }
    }

    /*
     * A WindowListener object to handle requests to shutdown the application.
     */
    class WindowEventHandler extends WindowAdapter {
        public void windowClosing(WindowEvent ev) {
            exitApp();
        }
    }


    /*
     * An Action object used to change the user's on-line status.
     */
    class StateChangeAction extends AbstractAction {
        String state;
        public StateChangeAction(String state, String name, String desc) {
            this.state = state;
            putValue(Action.NAME, name);
            putValue(Action.SHORT_DESCRIPTION, desc);
        }

        public void actionPerformed(ActionEvent evt) {
            client.changeOnlineState(state);
        }
    }

    /*
     * An Action object used to log on to the messaging server.
     */
    class LogOnAction extends AbstractAction {
        public LogOnAction() {
            String name = msgs.getString("LogOnActionName");
            String desc = msgs.getString("LogOnActionDescription");
            putValue(Action.NAME, name);
            putValue(Action.SHORT_DESCRIPTION, desc);
            // TODO - putValue(Action.SMALL_ICON, new ImageIcon("images/logon.gif"));
        }

        public void actionPerformed(ActionEvent evt) {
            ConnectInfo xfer = new ConnectInfo("username", new char[1]);
            PasswordDialog dlg = new PasswordDialog(HeraldFrame.this);
            if (dlg.showDialog(xfer)) {
                logOnAction.setEnabled(false);
                String stsMsg = msgs.getString("LoggingOnStatus");
                statusLabel.setText(stsMsg);

                String progMsg = msgs.getString("ConnectProgressMessage");
                String note = msgs.getString("ConnectProgressNote0");
                progressMonitor = new ProgressMonitor(HeraldFrame.this,
                        progMsg, note, 0, 2);

                tryingToConnect = true;
                client.connect(xfer.username, xfer.password);

                connectTimer = new javax.swing.Timer(30 * 1000, new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        // TODO
                        JOptionPane.showMessageDialog(HeraldFrame.this,
                                "Connect failed.", "Timeout Expired", JOptionPane.ERROR_MESSAGE);
                        connectTimer.stop();
                    }
                });
                connectTimer.start();
            }
        }
    }

    /*
     * An Action object used to log off from the messaging server.
     */
    class LogOffAction extends AbstractAction {
        public LogOffAction() {
            putValue(Action.NAME, "Log Off");
            putValue(Action.SHORT_DESCRIPTION, "Disconnects from the server and logs you off.");
            // TODO - putValue(Action.SMALL_ICON, new ImageIcon("images/logoff.gif"));
        }

        public void actionPerformed(ActionEvent evt) {
            logOffAction.setEnabled(false);
            statusLabel.setText("Disconnecting...");
            client.disconnect();
        }
    }

    /*
     * An Action object used to add a new contact.
     */
    class AddContactAction extends AbstractAction  {
        public AddContactAction()  {
            putValue(Action.NAME, "Add Contact...");
            putValue(Action.SHORT_DESCRIPTION, "Adds a new contact");
        }

        public void actionPerformed(ActionEvent evt)  {
        }
    }

    /*
     * An Action object used to remove a contact.
     */
    class RemoveContactAction extends AbstractAction  {
        public RemoveContactAction()  {
            putValue(Action.NAME, "Remove Contact...");
            putValue(Action.SHORT_DESCRIPTION, 
                    "Removes a contact from your contact list...");
        }

        public void actionPerformed(ActionEvent evt)  {
        }
    }

    /*
     * An action object used to change properties...
     */
    class PropertiesAction extends AbstractAction  {
        public PropertiesAction()  {
            putValue(Action.NAME, "Properties...");
            putValue(Action.SHORT_DESCRIPTION, "Modifies your account properties");
        }

        public void actionPerformed(ActionEvent evt)  {
            /* create the dialog and fill in the current properties */
            PropertiesDialog dlg = new PropertiesDialog(HeraldFrame.this);
            dlg.setFriendlyName(friendlyName);

            ClientProperties cp = client.getClientProperties();
            dlg.setAllowList(cp.allowList);
            dlg.setBlockedList(cp.blockedList);
            dlg.setReverseList(cp.reverseList);
            dlg.setReverseNotify(cp.prompt);

            /* display the dialog relative to the parent frame */
            dlg.setLocationRelativeTo(HeraldFrame.this);
            dlg.setVisible(true);

            /* extract the variables, if the user pressed OK */
            if (dlg.isOkay()) {
                String newFriendlyName = dlg.getFriendlyName();
                if (!newFriendlyName.equals(friendlyName)) {
                    System.out.println("User updated displayName to: " + newFriendlyName);
                }

                /* determine if any users were added to the Allow List */
                // TODO

                /* determine if any users were added to the Blocked List */
                // TODO

                /* determine if the prompt field has changed */
                boolean newPrompt = dlg.getReverseNotify();
                if (newPrompt != cp.prompt) {
                    System.out.println("User updated prompt to: " + newPrompt);
                }
            }
        }
    }

    /*
     * An Action object used to exit the application.
     */
    class ExitAction extends AbstractAction {
        public ExitAction() {
            putValue(Action.NAME, "Exit");
            putValue(Action.SHORT_DESCRIPTION, "Exits the application.");
        }

        public void actionPerformed(ActionEvent evt) {
            exitApp();
        }
    }

    /*
     * An Action object used to toggle the visibility of the application toolbar.
     */
    class ToolBarAction extends AbstractAction {
        public ToolBarAction() {
            putValue(Action.NAME, "Toolbar");
            putValue(Action.SHORT_DESCRIPTION, "Toggles visibility of the toolbar.");
        }

        public void actionPerformed(ActionEvent evt) {
            toolbar.setVisible(!toolbar.isVisible());
        }
    }

    /*
     * An action object used to toggle the visibility of the debug console.
     */
    class DebugAction extends AbstractAction {
        public DebugAction() {
            putValue(Action.NAME, "Debug Messages");
            putValue(Action.SHORT_DESCRIPTION, "Toggles visiblity of the debug console.");
        }

        public void actionPerformed(ActionEvent evt) {
            if (debugFrame == null) {
                debugFrame = new JFrame();
                debugFrame.getContentPane().add(log.toComponent());
                debugFrame.setSize(320, 240);
                debugFrame.setVisible(true);
            }
            debugFrame.setVisible(debugFrame.isVisible());
        }
    }


    /*
     * An Action object used to display the application About dialog.
     */
    class AboutAction extends AbstractAction {
        public AboutAction() {
            putValue(Action.NAME, "About Herald");
            putValue(Action.SHORT_DESCRIPTION, "Displays information about this application.");
        }

        public void actionPerformed(ActionEvent evt) {
            AboutDialog dlg = new AboutDialog(HeraldFrame.this);
            dlg.setLocationRelativeTo(HeraldFrame.this);
            dlg.setVisible(true);
        }
    }
}
