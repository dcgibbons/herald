/*
 * $Id: AboutDialog.java,v 1.1 2000/06/12 04:58:18 dcgibbons Exp $
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
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.text.html.*;

import net.sourceforge.herald.*;

class AboutDialog extends JDialog implements HyperlinkListener {
    public AboutDialog(JFrame parent) {
        super(parent);

        ResourceBundle msgs = ResourceBundle.getBundle("MessagesBundle");
    
        setTitle(msgs.getString("AboutDialogTitle"));

        ResourceBundle verInfo = ResourceBundle.getBundle("Version");
        Double release = new Double(Double.parseDouble(verInfo.getString("Release")));
        String releaseType = verInfo.getString("ReleaseType");
        DateFormat df = DateFormat.getDateInstance(DateFormat.YEAR_FIELD);
        Date copyright;
        try {
            copyright = df.parse(verInfo.getString("ReleaseCopyrightYear"));
        } catch (ParseException ex) {
            copyright = new Date(); // liar, liar!
        }
        Object[] messageArguments = {
            release,
            releaseType,
            copyright,
        };

        MessageFormat formatter = new MessageFormat(
                msgs.getString("AboutDialogCopyright"));
        String output = formatter.format(messageArguments);

        JEditorPane ep = new JEditorPane("text/html", output);
        ep.setEditable(false);
        ep.addHyperlinkListener(this);

        JScrollPane editorScrollPane = new JScrollPane(ep);
        editorScrollPane.setPreferredSize(new Dimension(250, 145));
        editorScrollPane.setMinimumSize(new Dimension(10, 10));
        editorScrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel p2 = new JPanel();
        JButton ok = new JButton(msgs.getString("OkButton"));
        p2.add(ok);

        Container c = getContentPane();
        c.setLayout(new BorderLayout());
        c.add(editorScrollPane, BorderLayout.CENTER);
        c.add(p2, BorderLayout.SOUTH);

        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setVisible(false);
            }
        });

        setSize(600, 400); // TODO: grr
    }

    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            JEditorPane pane = (JEditorPane) e.getSource();
            if (e instanceof HTMLFrameHyperlinkEvent) {
                HTMLFrameHyperlinkEvent  evt = (HTMLFrameHyperlinkEvent)e;
                HTMLDocument doc = (HTMLDocument)pane.getDocument();
                doc.processHTMLFrameHyperlinkEvent(evt);
            } else {
                try {
                    pane.setPage(e.getURL());
                } catch(Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }
}
