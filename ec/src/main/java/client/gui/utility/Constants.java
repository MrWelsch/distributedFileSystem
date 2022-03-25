/**
 * Class to centralize all constants used.
 * @author Nico Welsch
 * @version 1.0
 * @date 22.03.2022
 */

package client.gui.utility;

public class Constants {
    public static final String TITLE = "  VIRTUELLES VERTEILTES FILESYSTEM v1.0 ";
    public static final String MENU = " F1: INIT DC|F2: CREATE FILE|F3: FORWARD|F4: BACKWARD|F5: RENAME|F6: Search|ENTER: OPEN|BACKSPACE: DELETE|ESC: Quit ";
    public static final String FILE_VIEWER_MENU = "  ESC: MAIN MENU         BACKSPACE: DELETE";
    public static final String INIT_INTERFACE = ">_ USER INTERFACE READY";
    public static final String INIT_SERVER = ">_ CONNECTING TO SERVER";
    public static final String CHECK_SERVER = ">_ WAITING FOR SERVER REPLY";
    public static final String INIT_FS = ">_ INITIALIZING FILE SYSTEM";
    public static final String PROGRESS_BAR = ">_ ....................................................";

    public static final String FILE_NAME_PROMPT = "ENTER FILENAME";
    public static final String FILE_NUMBER_PROMPT = "ENTER FILE NUMBER";
    public static final String FILE_CONTENT_PROMPT = "ENTER FILE CONTENT";
    public static final String CUSTOM_SERVER_PROMPT = "DO YOU WANT TO USE A CUSTOM SERVER? (Y/N)";
    public static final String IP_ADRESS_PROMPT = "ENTER IP ADRESS";
    public static final String PORT_PROMPT = "ENTER PORT";
    public static final String PATH_PROMPT = "ENTER PATH";
    
    
    public static final int FIRST_COLUMN = 1;
    public static final int SECOND_COLUMN = 12;

    public static final int FIRST_COLUMN_CONTENT = 5;
    public static final int SECOND_COLUMN_CONTENT = 14;

    public static final int FIRST_COLUMN_SEPERATOR = 0;
    public static final int SECOND_COLUMN_SEPERATOR= 11;

    public static final int FIRST_ROW = 2;
    public static final int SECOND_ROW = 4;

    public static final int FIRST_ROW_SEPERATOR = 1;
    public static final int SECOND_ROW_SEPERATOR = 3;

    // GLOBAL SETTINGS
    public static final String FALLBACK_IP = "localhost:8080";

}
