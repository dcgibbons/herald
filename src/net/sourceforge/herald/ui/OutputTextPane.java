/*
 * $Id: OutputTextPane.java,v 1.1 2000/06/12 04:58:18 dcgibbons Exp $
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
import javax.swing.*;
import javax.swing.text.*;

import net.sourceforge.herald.*;
import net.sourceforge.util.*;

public class OutputTextPane extends JTextPane {
    public OutputTextPane() {
        super();
        initStyles();
        addMouseListener(new MouseWatcher());
    }

    protected void initStyles() {
        Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

        Style regular = addStyle("regular", def);
        StyleConstants.setFontFamily(def, "SansSerif");

        Style s = addStyle("italic", regular);
        StyleConstants.setItalic(s, true);

        s = addStyle("bold", regular);
        StyleConstants.setBold(s, true);

        s = addStyle("underline", regular);
        StyleConstants.setUnderline(s, true);

        s = addStyle("small", regular);
        StyleConstants.setFontSize(s, 10);

        s = addStyle("large", regular);
        StyleConstants.setFontSize(s, 16);
    }

    private Cursor savedCursor = null;

    private String mouseOnURL(Point pt) {
        int pos = viewToModel(pt);
        String url = null;
        try {
            Document doc = getDocument();
            int start = doc.getStartPosition().getOffset();
            int end = doc.getEndPosition().getOffset();

            /* TODO: this algorithm needs to be internationalized */
            int wordStart = pos;
            while (wordStart > start) {
                if (Character.isWhitespace(doc.getText(wordStart, 1).charAt(0))) {
                    break;
                }
                wordStart--;
            }

            int wordEnd = pos;
            while (wordEnd < end) {
                if (Character.isWhitespace(doc.getText(wordEnd, 1).charAt(0))) {
                    break;
                }
                wordEnd++;
            }

            String word = doc.getText(wordStart, wordEnd-wordStart).trim();
            if (word.toLowerCase().startsWith("http://")
                || word.toLowerCase().startsWith("file://")
                || word.toLowerCase().startsWith("ftp://")) {
                url = word;
            }
        } catch (BadLocationException ex) {
        }
        return url;
    }

    class MouseWatcher extends MouseAdapter {
        private MouseMotionWatcher motionWatcher = new MouseMotionWatcher();

        public void mouseClicked(MouseEvent e) {
            String url = mouseOnURL(new Point(e.getX(), e.getY()));
            if (url != null) {
                Cursor c = getCursor();
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                BrowserControl.displayURL(url);
                setCursor(c);
            }
        }

        public void mouseEntered(MouseEvent e) {
            addMouseMotionListener(motionWatcher);
        }

        public void mouseExited(MouseEvent e) {
            removeMouseMotionListener(motionWatcher);
        }
    }

    class MouseMotionWatcher extends MouseMotionAdapter {
        private Cursor savedCursor = null;

        public void mouseMoved(MouseEvent e) {
            String url = mouseOnURL(new Point(e.getX(), e.getY()));
            if (url != null) {
                if (savedCursor == null) {
                    savedCursor = getCursor();
                }
                Cursor c = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
                setCursor(c);
            } else if (savedCursor != null) {
                setCursor(savedCursor);
                savedCursor = null;
            }
        }
    }
}
