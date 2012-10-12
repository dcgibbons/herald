/*
 * HeraldProperties.java
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

import java.util.Properties;

public class HeraldProperties extends Parameters {
    private int frameXPos = 0;
    private int frameYPos = 0;
    private int frameWidth = 320;
    private int frameHeight = 480;
    private String userHandle = "nobody";

    public String frameXPosName = "frame.xpos";
    public String frameYPosName = "frame.ypos";
    public String frameWidthName = "frame.width";
    public String frameHeightName = "frame.height";
    public String userHandleName = "user.handle";

    public HeraldProperties() {
        super("herald.props", "Herald Client Properties");
        getParameters();
    }

    protected void setDefaults(Properties defaults) {
        defaults.put(frameXPosName, Integer.toString(frameXPos));
        defaults.put(frameYPosName, Integer.toString(frameYPos));
        defaults.put(frameWidthName, Integer.toString(frameWidth));
        defaults.put(frameHeightName, Integer.toString(frameHeight));
        defaults.put(userHandleName, userHandle);
    }

    protected void updateSettingsFromProperties() {
        try {
            frameXPos = Integer.parseInt(properties.getProperty(frameXPosName));
            frameYPos = Integer.parseInt(properties.getProperty(frameYPosName));
            frameWidth = Integer.parseInt(properties.getProperty(frameWidthName));
            frameHeight = Integer.parseInt(properties.getProperty(frameHeightName));
            userHandle = properties.getProperty(userHandleName);
        } catch (NumberFormatException e) {
            // we don't care if the property was of the wrong format,
            // they've all got default values. So catch the exception
            // and keep going.
        }
    }

    protected void updatePropertiesFromSettings() {
        properties.put(frameXPosName, new Integer(frameXPos).toString());
        properties.put(frameYPosName, new Integer(frameYPos).toString());
        properties.put(frameWidthName, new Integer(frameWidth).toString());
        properties.put(frameHeightName, new Integer(frameHeight).toString());
        properties.put(userHandleName, userHandle);
    }

    public void setFrameXPos(int frameXPos) {
        this.frameXPos = frameXPos;
    }

    public int getFrameXPos() {
        return frameXPos;
    }

    public void setFrameYPos(int frameYPos) {
        this.frameYPos = frameYPos;
    }

    public int getFrameYPos() {
        return frameYPos;
    }

    public void setFrameWidth(int frameWidth) {
        this.frameWidth = frameWidth;
    }
    public int getFrameWidth() {
        return frameWidth;
    }

    public void setFrameHeight(int frameHeight) {
        this.frameHeight = frameHeight;
    }
    public int getFrameHeight() {
        return frameHeight;
    }

    public void setUserHandle(String userHandle) {
        this.userHandle = userHandle;
    }
    public String getUserHandle() {
        return userHandle;
    }
}
