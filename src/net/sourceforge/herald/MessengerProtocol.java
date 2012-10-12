/*
 * MessengerProtocol.java
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

/**
 * This class contains field definitions and utilities as defined by
 * the MSN Messenger V1.0 Protocol document.
 */
public final class MessengerProtocol {
    public final static int PORT = 1863;
    public final static String DIALECT_NAME = "MSNP2";
    public final static String END_OF_COMMAND = "\r\n";
    public final static String CMD_ACK = "ACK";
    public final static String CMD_ADD = "ADD";
    public final static String CMD_ANS = "ANS";
    public final static String CMD_BLP = "BLP";
    public final static String CMD_BYE = "BYE";
    public final static String CMD_CAL = "CAL";
    public final static String CMD_CHG = "CHG";
    public final static String CMD_FLN = "FLN";
    public final static String CMD_GTC = "GTC";
    public final static String CMD_INF = "INF";
    public final static String CMD_ILN = "ILN";
    public final static String CMD_IRO = "IRO";
    public final static String CMD_JOI = "JOI";
    public final static String CMD_LST = "LST";
    public final static String CMD_MSG = "MSG";
    public final static String CMD_NAK = "NAK";
    public final static String CMD_NLN = "NLN";
    public final static String CMD_OUT = "OUT";
    public final static String CMD_REM = "REM";
    public final static String CMD_RNG = "RNG";
    public final static String CMD_SYN = "SYN";
    public final static String CMD_USR = "USR";
    public final static String CMD_VER = "VER";
    public final static String CMD_XFR = "XFR";
    public final static String ERR_SYNTAX_ERROR = "200";
    public final static String ERR_INVALID_PARAMETER = "201";
    public final static String ERR_INVALID_USER = "205";
    public final static String ERR_FQDN_MISSING = "206";
    public final static String ERR_ALREADY_LOGIN = "207";
    public final static String ERR_INVALID_USERNAME = "208";
    public final static String ERR_INVALID_FRIENDLY_NAME = "209";
    public final static String ERR_LIST_FULL = "210";
    public final static String ERR_NOT_ON_LIST = "216";
    public final static String ERR_ALREADY_IN_THE_MODE = "218";
    public final static String ERR_ALREADY_IN_OPPOSITE_LIST = "219";
    public final static String ERR_SWITCHBOARD_FAILED = "280";
    public final static String ERR_NOTIFY_XFER_FAILED = "281";
    public final static String ERR_REQUIRED_FIELDS_MISSING = "300";
    public final static String ERR_NOT_LOGGED_IN = "302";
    public final static String ERR_INTERNAL_SERVER = "500";
    public final static String ERR_DB_SERVER = "501";
    public final static String ERR_FILE_OPERATION = "510";
    public final static String ERR_MEMORY_ALLOC = "520";
    public final static String ERR_SERVER_BUSY = "600";
    public final static String ERR_SERVER_UNAVAILABLE = "601";
    public final static String ERR_PERR_NS_DOWN = "601";
    public final static String ERR_DB_CONNECT = "603";
    public final static String ERR_SERVER_GOING_DOWN = "604";
    public final static String ERR_CREATE_CONNECTION = "707";
    public final static String ERR_BLOCKING_WRITE = "711";
    public final static String ERR_SESSION_OVERLOAD = "712";
    public final static String ERR_USER_TOO_ACTIVE = "713";
    public final static String ERR_TOO_MANY_SESSIONS = "714";
    public final static String ERR_NOT_EXPECTED = "715";
    public final static String ERR_BAD_FRIEND_FILE = "717";
    public final static String ERR_AUTHENTICATION_FAILED = "911";
    public final static String ERR_NOT_ALLOWED_WHEN_OFFLINE = "913";
    public final static String ERR_NOT_ACCEPTING_NEW_USERS = "920";
    public final static String STATE_ONLINE = "NLN";
    public final static String STATE_OFFLINE = "FLN";
    public final static String STATE_HIDDEN = "HDN";
    public final static String STATE_BUSY = "BSY";
    public final static String STATE_IDLE = "IDL";
    public final static String STATE_BRB = "BRB";
    public final static String STATE_AWAY = "AWY";
    public final static String STATE_PHONE = "PHN";
    public final static String STATE_LUNCH = "LUN";

    /**
     * Retrieves the next available transaction ID that is unique
     * within this virtual machine context.
     *
     * @returns int a signed 32-bit transaction ID
     */
    public static synchronized int getTransactionID() {
        return transactionID++;
    }

    public static synchronized void reset() {
        transactionID = 0;
    }

    private static int transactionID = 0;
}
