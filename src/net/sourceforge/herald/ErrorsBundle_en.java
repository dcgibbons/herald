/*
 * ErrorsBundle_en.java
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

import java.util.*;

public class ErrorsBundle_en extends ListResourceBundle {
    public Object[][] getContents() {
        return contents;
    }

    private Object[][] contents = {
        { MessengerProtocol.ERR_SYNTAX_ERROR, "syntax error in packet" },
        { MessengerProtocol.ERR_INVALID_PARAMETER, "invalid parameter" },
        { MessengerProtocol.ERR_INVALID_USER, "invalid user" },
        { MessengerProtocol.ERR_FQDN_MISSING, "fully qualified domain name missing" },
        { MessengerProtocol.ERR_ALREADY_LOGIN, "you are already logged in" },
        { MessengerProtocol.ERR_INVALID_USERNAME, "invalid user name" },
        { MessengerProtocol.ERR_INVALID_FRIENDLY_NAME, "invalid friendly name" },
        { MessengerProtocol.ERR_LIST_FULL, "list is full" },
        { MessengerProtocol.ERR_NOT_ON_LIST, "contact is not on the list" },
        { MessengerProtocol.ERR_ALREADY_IN_THE_MODE, "already in the mode (?)" },
        { MessengerProtocol.ERR_ALREADY_IN_OPPOSITE_LIST, "already in opposite list (?)" },
        { MessengerProtocol.ERR_SWITCHBOARD_FAILED, "switchboard server failed" },
        { MessengerProtocol.ERR_NOTIFY_XFER_FAILED, "notification transfer failed" },
        { MessengerProtocol.ERR_REQUIRED_FIELDS_MISSING, "required fields missing" },
        { MessengerProtocol.ERR_NOT_LOGGED_IN, "you are not logged on" },
        { MessengerProtocol.ERR_INTERNAL_SERVER, "internal server error" },
        { MessengerProtocol.ERR_DB_SERVER, "database server error" },
        { MessengerProtocol.ERR_FILE_OPERATION, "File operation failure" },
        { MessengerProtocol.ERR_MEMORY_ALLOC, "memory allocation failure" },
        { MessengerProtocol.ERR_SERVER_BUSY, "server is too busy to handle your request" },
        { MessengerProtocol.ERR_SERVER_UNAVAILABLE, "server is unavailable" },
        { MessengerProtocol.ERR_PERR_NS_DOWN, "peer name server is down" },
        { MessengerProtocol.ERR_DB_CONNECT, "database connection failure" },
        { MessengerProtocol.ERR_SERVER_GOING_DOWN, "server is shutting down" },
        { MessengerProtocol.ERR_CREATE_CONNECTION, "unable to create connection" },
        { MessengerProtocol.ERR_BLOCKING_WRITE, "blocking write (?)" },
        { MessengerProtocol.ERR_SESSION_OVERLOAD, "Session server is overloaded" },
        { MessengerProtocol.ERR_USER_TOO_ACTIVE, "user is too active" },
        { MessengerProtocol.ERR_TOO_MANY_SESSIONS, "too many active sessions" },
        { MessengerProtocol.ERR_NOT_EXPECTED, "request unexpected" },
        { MessengerProtocol.ERR_BAD_FRIEND_FILE, "bad friend file" },
        { MessengerProtocol.ERR_AUTHENTICATION_FAILED, "authentication failed" },
        { MessengerProtocol.ERR_NOT_ALLOWED_WHEN_OFFLINE, "not allowed when offline" },
        { MessengerProtocol.ERR_NOT_ACCEPTING_NEW_USERS, "server is not accepting new users" },
    };
}
