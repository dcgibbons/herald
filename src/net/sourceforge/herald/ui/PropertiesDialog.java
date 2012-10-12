/*
 * $Id: PropertiesDialog.java,v 1.2 2000/07/14 01:02:10 dcgibbons Exp $
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

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.text.html.*;

import net.sourceforge.herald.*;

class PropertiesDialog extends JDialog {
    private boolean okay;

    /* general tab */
    private JCheckBox autoAwayOption;
    private JTextField autoAwayTimeout;
    private JCheckBox notifyMessageOption;
    private JCheckBox notifySoundOption; 
    private JButton soundsButton;
    private JTextField displayName;

    /* privacy tab */
    private DefaultListModel allowListModel;
    private JList allowList;
    private DefaultListModel blockedListModel;
    private JList blockedList;
    private DefaultListModel reverseListModel = new DefaultListModel();
    private JButton allowButton;
    private JButton blockButton;
    private JCheckBox notifyReverseOption;
    private JButton viewButton;
    private JButton okayButton;
    private JButton cancelButton;

    /* useful utility objects */
    Border emptyBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
    ResourceBundle msgs = ResourceBundle.getBundle("MessagesBundle");

    public PropertiesDialog(JFrame parent) {
        super(parent, true);

        setTitle(msgs.getString("PropertiesDialogTitle"));

        okayButton = new JButton("OK"); // TODO: get from ResourceBundle
        okayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                okay = true;
                setVisible(false);
            }
        });

        cancelButton = new JButton("Cancel"); // TODO: get from ResourceBundle
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                okay = false;
                setVisible(false);
            }
        });

        JPanel generalPane = createGeneralPane();
        JPanel privacyPane = createPrivacyPane();
        JPanel accountPane = createAccountPane();
        JPanel connectionPane = createConnectionPane();

        /* add all of the subpanels to the main content pane */
        JTabbedPane tabs = new JTabbedPane();
        tabs.add("General", generalPane); // TODO: get from ResourceBundle
        tabs.add("Privacy", privacyPane); // TODO: get from ResourceBundle
        tabs.add("Account", accountPane); // TODO: get from ResourceBundle
        tabs.add("Connection", connectionPane); // TODO: get from ResourceBundle
        tabs.setBorder(emptyBorder);

        JPanel p = new JPanel(new GridLayout(1, 0, 5, 5));
        p.add(okayButton);
        p.add(cancelButton);
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(p, BorderLayout.EAST);
        buttonPanel.setBorder(emptyBorder);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(tabs, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
       
        /* resize the dialog to the preferred size of all of the components */
        setSize(600, 400); // TODO: grr, this sucks
    }

    public boolean isOkay() {
        return okay;
    }

    public String getFriendlyName() {
        return displayName.getText();
    }

    public void setFriendlyName(String s) {
        displayName.setText(s);
    }

    public LinkedList getAllowList() {
        ListModel l = allowList.getModel();
        LinkedList ll = new LinkedList();
        for (int i = 0, n = l.getSize(); i < n; i++) {
            ll.add(l.getElementAt(i));
        }
        return ll;
    }

    public void setAllowList(LinkedList ll) {
        allowListModel.clear();
        ListIterator i = ll.listIterator(0);
        while (i.hasNext()) {
            Contact c = (Contact) i.next();
            allowListModel.addElement(c.getFriendlyName());
        }
    }

    public LinkedList getBlockedList() {
        return null;
    }

    public void setBlockedList(LinkedList ll) {
        blockedListModel.clear();
        ListIterator i = ll.listIterator(0);
        while (i.hasNext()) {
            Contact c = (Contact) i.next();
            blockedListModel.addElement(c.getFriendlyName());
        }
    }

    public boolean getReverseNotify() {
        return notifyReverseOption.isSelected();
    }

    public void setReverseNotify(boolean b) {
        notifyReverseOption.setSelected(b);
    }

    public void setReverseList(LinkedList ll) {
        reverseListModel.clear();
        ListIterator i = ll.listIterator(0);
        while (i.hasNext()) {
            Contact c = (Contact) i.next();
            reverseListModel.addElement(c.getFriendlyName());
        }
    }

    private JPanel createGeneralPane() {
        /* create general options list */
        JPanel generalPanel = new JPanel();

        displayName = new JTextField();
        displayName.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLoweredBevelBorder(), emptyBorder));
        JLabel displayNameLabel = new JLabel("Type your name as you want other users to see it.");
        displayNameLabel.setBorder(emptyBorder);
        
        JPanel displayNamePanel = new JPanel(new BorderLayout());
        displayNamePanel.add(displayNameLabel, BorderLayout.NORTH);
        displayNamePanel.add(displayName, BorderLayout.SOUTH);
        displayNamePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("My Display Name"), emptyBorder));
      
        generalPanel.add(displayNamePanel);
        return generalPanel;
    }

    private JPanel createPrivacyPane() {
        /* add list for Allowed contacts */
        allowListModel = new DefaultListModel();
        allowListModel.addListDataListener(new ListDataListener() {
            public void contentsChanged(ListDataEvent e) {
                allowButton.setEnabled(!blockedList.isSelectionEmpty());
                blockButton.setEnabled(!allowList.isSelectionEmpty());
            }
            public void intervalAdded(ListDataEvent e) {
            }
            public void intervalRemoved(ListDataEvent e) {
            }
        });

        allowList = new JList(allowListModel);
        allowList.setBorder(BorderFactory.createLoweredBevelBorder());
        allowList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                allowButton.setEnabled(!blockedList.isSelectionEmpty());
                blockButton.setEnabled(!allowList.isSelectionEmpty());
            }
        });

        JPanel allowedPanel = new JPanel(new BorderLayout());
        allowedPanel.add(new JLabel("My Allow List"), BorderLayout.NORTH); // TODO: put in resources
        allowedPanel.add(allowList, BorderLayout.CENTER);
        allowedPanel.setBorder(emptyBorder);


        /* add list for Blocked contacts */
        blockedListModel = new DefaultListModel();
        blockedListModel.addListDataListener(new ListDataListener() {
            public void contentsChanged(ListDataEvent e) {
                allowButton.setEnabled(!blockedList.isSelectionEmpty());
                blockButton.setEnabled(!allowList.isSelectionEmpty());
            }
            public void intervalAdded(ListDataEvent e) {
            }
            public void intervalRemoved(ListDataEvent e) {
            }
        });
        blockedList = new JList(blockedListModel);
        blockedList.setBorder(BorderFactory.createLoweredBevelBorder());
        blockedList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                allowButton.setEnabled(!blockedList.isSelectionEmpty());
                blockButton.setEnabled(!allowList.isSelectionEmpty());
            }
        });

        JPanel blockedPanel = new JPanel(new BorderLayout());
        blockedPanel.add(new JLabel("My Blocked List"), BorderLayout.NORTH); // TODO: put in resources
        blockedPanel.add(blockedList, BorderLayout.CENTER);
        blockedPanel.setBorder(emptyBorder);

        /* control buttons */
        allowButton = new JButton("<< Allow"); // TODO: put in resources
        allowButton.setEnabled(false);
        allowButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Object[] l = blockedList.getSelectedValues();
                for (int i = 0; i < l.length; i++) {
                    blockedListModel.removeElement(l[i]);
                    allowListModel.addElement(l[i]);
                }
            }
        });

        blockButton = new JButton("Block >>"); // TODO: put in resources
        blockButton.setEnabled(false);
        blockButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Object[] l = allowList.getSelectedValues();
                for (int i = 0; i < l.length; i++) {
                    allowListModel.removeElement(l[i]);
                    blockedListModel.addElement(l[i]);
                }
            }
        });

        /* create a panel with a complicated grid bag layout to make everything fit */
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        /* cell 1 */
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 2;
        c.ipadx = 0;
        c.ipady = 0;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0.5;
        c.weighty = 1.0;
        p.add(allowedPanel, c);

        /* cell 2 */
        c.gridx = 2;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 2;
        c.ipadx = 0;
        c.ipady = 0;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0.5;
        c.weighty = 1.0;
        p.add(blockedPanel, c);

        /* cell 3 */
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.ipadx = 0;
        c.ipady = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.SOUTH;
        c.weightx = 0.0;
        c.weighty = 0.5;
        p.add(allowButton, c);

        /* cell 4 */
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.ipadx = 0;
        c.ipady = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.NORTH;
        c.weightx = 0.0;
        c.weighty = 0.5;
        p.add(blockButton, c);

        JPanel anotherPanel = new JPanel(new BorderLayout());
        notifyReverseOption = new JCheckBox("Notify me when Passport users add me to their contact lists.");
        notifyReverseOption.setMnemonic('N');
        anotherPanel.add(notifyReverseOption, BorderLayout.NORTH);

        JLabel viewLabel = new JLabel("Which users have added me to their contact lists?");
        viewButton = new JButton("View");
        viewButton.setMnemonic('V');
        viewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JList l = new JList(reverseListModel);
                JOptionPane.showMessageDialog(PropertiesDialog.this, l, "Forward List", JOptionPane.PLAIN_MESSAGE);
            }
        });
        JPanel viewPanel = new JPanel(new BorderLayout());
        viewPanel.add(viewLabel, BorderLayout.CENTER);
        viewPanel.add(viewButton, BorderLayout.EAST);
        anotherPanel.add(viewPanel, BorderLayout.SOUTH);

        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 3;
        c.gridheight = 1;
        c.ipadx = 0;
        c.ipady = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0.0;
        c.weighty = 0.0;
        p.add(anotherPanel, c);

        p.setBorder(BorderFactory.createCompoundBorder(
                emptyBorder,
                BorderFactory.createTitledBorder(msgs.getString("PropertiesDialogFrameDesc"))));

        return p;
    }

    private JPanel createAccountPane() {
        JPanel p = new JPanel();
        return p;
    }

    private JPanel createConnectionPane() {
        JPanel p = new JPanel();
        return p;
    }
}
