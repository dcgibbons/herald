/* 
 * PasswordDialog.java 
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
import java.io.*;
import java.util.*;
import javax.swing.*;

class PasswordDialog extends JDialog implements ActionListener {
    public PasswordDialog(JFrame parent) {
        super(parent, "Connect", true);
        setLocationRelativeTo(parent);
        JPanel labelPanel = new JPanel(new GridLayout(2, 0));
        labelPanel.add(new JLabel("Username:"));
        labelPanel.add(new JLabel("Password:"));
        labelPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JPanel fieldPanel = new JPanel(new GridLayout(2, 0));
        username = new JTextField("");
        username.addActionListener(this);
        password = new JPasswordField("");
        password.addActionListener(this);
        fieldPanel.add(username);
        fieldPanel.add(password);
        fieldPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JPanel buttonPanel1 = new JPanel(new GridLayout(1, 0, 5, 5));
        okButton = new JButton("OK");
        okButton.addActionListener(this);
        buttonPanel1.add(okButton);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        buttonPanel1.add(cancelButton);
        JPanel buttonPanel2 = new JPanel(new BorderLayout());
        buttonPanel2.add(buttonPanel1, BorderLayout.EAST);
        buttonPanel2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JPanel p1 = new JPanel(new BorderLayout());
        p1.add(labelPanel, BorderLayout.WEST);
        p1.add(fieldPanel, BorderLayout.CENTER);
        p1.add(buttonPanel2, BorderLayout.SOUTH);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(p1, BorderLayout.NORTH);
        /* determine a reasonable default size */
        username.setText("username@somewhere-big.org");
        pack();
        setSize(getPreferredSize());
        username.setText("");
        username.requestFocus();
    }   

    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();
        setVisible(false);
        if (source != cancelButton) {
            ok = true;
        }
    }

    public boolean showDialog(ConnectInfo xfer) {
        ok = false;
        setVisible(true);
        if (ok) {
            xfer.username = username.getText();
            xfer.password = password.getPassword();
        }
        dispose();
        return ok;
    }

    private JTextField username;
    private JPasswordField password;
    private boolean ok;
    private JButton okButton;
    private JButton cancelButton;
}
