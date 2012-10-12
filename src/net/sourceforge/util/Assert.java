/*
 * $Id: Assert.java,v 1.2 2000/06/12 04:58:18 dcgibbons Exp $
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

package net.sourceforge.util;

import java.util.Properties;

/**
 * This class provides a simple assertion mechanism. If an assertion fails,
 * the application is terminated and a stack trace is printed.
 *
 * By default, assertions are enabled. The default state can be altered
 * by the <code>assert.enabled</code> boolean system property.
 *
 * Assertions can be enabled or disabled by using the <code>setEnabled</code> 
 * method.
 *
 * @author Chad Gibbons
 */
public class Assert {
    public static void test(boolean condition) {
        if (isEnabled && !condition) {
            System.out.println("***** ASSERTION FAILED *****");
            Thread.dumpStack();
            System.exit(1);
        }
    }

    public static boolean isEnabled() {
        return isEnabled;
    }

    public static synchronized void setEnabled(boolean b) {
        isEnabled = b;
    }

    private static boolean isEnabled = Boolean.getBoolean("assert.enabled");
}
