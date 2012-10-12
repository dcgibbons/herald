/*
 * Parameters.java
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

import net.sourceforge.util.*;

public abstract class Parameters {

    private String propertiesFilename;
    private String propertiesDescription;

    protected Properties properties = null;

    protected Parameters(String fname, String desc) {
        this.propertiesFilename = fname;
        this.propertiesDescription = desc;
    }

    abstract protected void setDefaults(Properties defaults);
    abstract protected void updatePropertiesFromSettings();
    abstract protected void updateSettingsFromProperties();

    public void getParameters() {
        Properties defaults = new Properties();
        FileInputStream in = null;

        setDefaults(defaults);
        properties = new Properties(defaults);

        try {
            String folder = System.getProperty("user.home");
            String filesep = System.getProperty("file.separator");
            String subdir = ".herald";
            File subdirFile = new File(folder + filesep + subdir);
            in = new FileInputStream(new File(subdirFile, propertiesFilename));
            properties.load(in);
        } catch (java.io.FileNotFoundException e) {
            in = null;
        } catch (java.io.IOException e) {
        } finally {
            if (in != null) {
                try { in.close(); } catch (java.io.IOException e) { }
                in = null;
            }
        }

        updateSettingsFromProperties();
    }

    public void saveParameters() {
        updatePropertiesFromSettings();

        FileOutputStream out = null;

        try {
            String folder = System.getProperty("user.home");
            String filesep = System.getProperty("file.separator");
            String subdir = ".herald";
            File subdirFile = new File(folder + filesep + subdir);
            if (!subdirFile.exists()) {
                subdirFile.mkdirs();
            }
            out = new FileOutputStream(new File(subdirFile, propertiesFilename));
            properties.store(out, propertiesDescription);
        } catch (IOException ex) {
            MessageLogSingleton.instance().append(
                "Unable to save properties: " + ex.getMessage());
        } finally {
            if (out != null) {
                try { 
                     out.close(); 
                } catch (java.io.IOException e) {
                }
                out = null;
            }
        }
    }
}
