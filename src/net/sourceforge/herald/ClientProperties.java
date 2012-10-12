/*
 * ClientProperties.java
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
import java.util.*;

public class ClientProperties implements Serializable {
    public int serialNo;

    /**
     * Reverse List Prompting
     *
     * The client can change its persistent setting for when to prompt
     * the user in reaction to a Reverse List change. This is accomplished
     * via the GTC command:
     *
     *     C: GTC TrID [ A | N ]
     *     S: GTC TrID Ser# [ A | N ]
     *
     * The value of the A/N parameter determines how the client should
     * behave when it discovers that a user is in its RL, but is not in its
     * AL or BL.
     *
     * A - Prompt the user as to whether the new user in the RL should be
     *     added to the AL or the BL
     * N - Automatically add the new user in the RL to the AL
     */
    public boolean prompt;

    public boolean privacyMode;
    public LinkedList forwardList = new LinkedList();
    public LinkedList allowList = new LinkedList();
    public LinkedList blockedList = new LinkedList();
    public LinkedList reverseList = new LinkedList();
}
