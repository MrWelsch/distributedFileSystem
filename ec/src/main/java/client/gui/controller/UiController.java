/**
 * Class to Create and Modify the User Interface of the End User Client
 * @author Nico Welsch
 * @version 1.0
 * @date 22.03.2022
 */

package client.gui.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.TextColor.ANSI;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialog;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialogBuilder;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import org.json.simple.parser.ParseException;

import client.gui.utility.Constants;
import client.service.Service;

public class UiController {

    private TerminalSize terminalSize;

    private TextColor fg, bg;

    private Screen screen;

    Service service = new Service();

    public static String fileName, fileContent, ipAdress, port, newFileName, folder, path;
    
    private boolean isOn;

    private int width, height;

    private int[] posX = { Constants.FIRST_COLUMN_CONTENT, Constants.SECOND_COLUMN_CONTENT };

    private String[] labels = { "#", "FILES" };
    private String[] locationDescriptions;

    public String[] files = service.getRootNode();

    Map<Integer, String> fileSetup = new HashMap<>();

    /**
     * Constructor
     * @throws IOException
     */
    public UiController() throws IOException {
        try {
            this.screen = new DefaultTerminalFactory().createScreen();
            this.terminalSize = screen.getTerminalSize();
            this.init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Standard configurations
     * @throws IOException
     */
    private void init() throws IOException {
        fg = TextColor.ANSI.RED;
        bg = TextColor.ANSI.BLACK;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // CONTROLLER                                                                                                                                //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Starts the User Client
     * @throws IOException
     * @throws ParseException
     */
    public void turnOn() throws IOException, ParseException {
        screen.startScreen();
        dialogueServer();
        initUi();
        drawUI();
        mainScreen();
    }

    /**
     * Shuts down the User Client
     * @throws IOException
     */
    public void turnOff() throws IOException {
        isOn = false;
        initShutDown();
        screen.stopScreen();
    }

    /**
     * Loads and displays the Main Screen of the UI
     * @throws IOException
     * @throws ParseException
     */
    public void mainScreen() throws IOException, ParseException {
        drawUI();
        displayData();
        isOn = true;
        initMenu();
        screen.stopScreen();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // UI OPTICS                                                                                                                                 //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Combines all layout related functions and creates the UI layout
     * @throws IOException
     */
    public void drawUI() throws IOException {

        int row = 2;

        // Disabling cursor visibility
        screen.setCursorPosition(null);                      

        width = screen.getTerminalSize().getColumns() - 1;                                      
        height = screen.getTerminalSize().getRows() - 1;

        screen.clear();

        // Using a key-value data structure for mapping data descriptions to screen.
        Map<Integer, String> colSetup = new HashMap<>();

        for (int i = 0; i < posX.length; i++) {
            colSetup.put(posX[i], labels[i]);
        }

        // Iterating over HashMap and display it in the UI;
        colSetup.forEach((k, v) -> screen.newTextGraphics().putString(k, row, v));

        drawTitle(Constants.TITLE);
        drawMenu(Constants.MENU);
        drawBorders();

        // Refreshing the screen to make the changes visible.
        screen.refresh();
    }

    /**
     * Draws the Title Bar of the UI
     * @param 'header' which accepts a String value that is displayed as content.
     * @throws IOException
     */
    public void drawTitle(String header) throws IOException {
        // Inverted Bar
        screen.newTextGraphics().drawLine(0, 0, width, 0, new TextCharacter(' ')
                .withBackgroundColor(fg).withForegroundColor(bg));
        // Text
        screen.newTextGraphics().setBackgroundColor(fg).setForegroundColor(bg)
                .putCSIStyledString(0, 0, header);
    }

    /**
     * Draws the footer of the UI
     * @param 'footer' which accepts a String value that is displayed as content.
     * @throws IOException
     */
    public void drawMenu(String footer) throws IOException {
        // Inverted Bar
        screen.newTextGraphics().drawLine(0, height, width, height, new TextCharacter(' ')
                .withBackgroundColor(fg).withForegroundColor(bg));
        // Text
        screen.newTextGraphics().setBackgroundColor(fg).setForegroundColor(bg)
                .putCSIStyledString(0, height, footer);
    }

    /**
     * Draws the column and row borders of the UI
     * @throws IOException
     */
    public void drawBorders() throws IOException {
        // SET COLOR
        final TextColor myColor = ANSI.BLUE;
        final TextGraphics drawLine = screen.newTextGraphics().setForegroundColor(myColor);

        // GRID COORDINATES
        final Integer[] columns = { Constants.FIRST_COLUMN, Constants.SECOND_COLUMN };
        final Integer[] columnSeperators = { Constants.FIRST_COLUMN_SEPERATOR, Constants.SECOND_COLUMN_SEPERATOR,width };
        final Integer[] rows = { Constants.FIRST_ROW, Constants.SECOND_ROW, height - 1 };
        final Integer[] rowSeperators = { Constants.FIRST_ROW_SEPERATOR, Constants.SECOND_ROW_SEPERATOR, height - 1 };
        final char[] straightConnectorCharacters = { Symbols.DOUBLE_LINE_T_DOWN, Symbols.DOUBLE_LINE_CROSS,Symbols.DOUBLE_LINE_T_UP };
        final char[] leftCornerConnectorCharacters = { Symbols.DOUBLE_LINE_TOP_LEFT_CORNER, Symbols.DOUBLE_LINE_T_RIGHT,Symbols.DOUBLE_LINE_BOTTOM_LEFT_CORNER };
        final char[] rightCornerConnectorCharacters = { Symbols.DOUBLE_LINE_TOP_RIGHT_CORNER,Symbols.DOUBLE_LINE_T_LEFT, Symbols.DOUBLE_LINE_BOTTOM_RIGHT_CORNER };

        // HORIZONTAL LINES
        for (int i = 0; i < rowSeperators.length; i++) {
            for (int j = 0; j < columns.length - 1; j++) {
                screen.newTextGraphics()
                        .drawLine(columns[j],                                       // From X
                                rowSeperators[i],                                   // From Y
                                columnSeperators[j + 2] - 1,                        // To X
                                rowSeperators[i],                                   // To Y
                                new TextCharacter(Symbols.DOUBLE_LINE_HORIZONTAL)   // Drawn Element
                                        .withForegroundColor(myColor));
            }
        }

        // VERTICAL LINES
        for (int i = 0; i < rows.length; i++) {
            for (int j = 0; j < columnSeperators.length; j++) {
                // First Row
                drawLine.setCharacter(columnSeperators[j], rows[i], Symbols.DOUBLE_LINE_VERTICAL);

                // Second Row
                screen.newTextGraphics()
                        .drawLine(columnSeperators[j],                              // From X
                                rows[1],                                            // From Y
                                columnSeperators[j],                                // To X
                                rows[2],                                            // To Y
                                new TextCharacter(Symbols.DOUBLE_LINE_VERTICAL)     // Drawn Element
                                        .withForegroundColor(myColor));
            }
        }

        // CONNECTORS
        for (int i = 0; i < rowSeperators.length; i++) {
            for (int j = 0; j < columnSeperators.length - 1; j++) {
                // Straight
                drawLine.setCharacter(columnSeperators[j + 1], rowSeperators[i], straightConnectorCharacters[i]);

                // Corners
                if (j == 0)
                    drawLine.setCharacter(columnSeperators[j], rowSeperators[i], leftCornerConnectorCharacters[i]);
                else if (j == columnSeperators.length - 2)
                    drawLine.setCharacter(columnSeperators[j + 1], rowSeperators[i], rightCornerConnectorCharacters[i]);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // UI LOGIC //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Binds developer defined functions to certain key strokes
     * @throws IOException
     * @throws ParseException
     */
    private void initMenu() throws IOException, ParseException {
        // Keystroke setup
        com.googlecode.lanterna.input.KeyStroke keyStroke;

        while (isOn) {

            // Monitoring keyboard input
            keyStroke = screen.pollInput();
            if (keyStroke != null && keyStroke.getKeyType() == KeyType.Escape) {            // QUIT EC
                screen.clear();
                turnOff();
                break;
            } else if (keyStroke != null && keyStroke.getKeyType() == KeyType.F1) {         // INIT DC
                screen.clear();
                dialogueDataClient();

                mainScreen();
            } else if (keyStroke != null && keyStroke.getKeyType() == KeyType.F2) {         // CREATE FILE
                screen.clear();
                dialogueFileCreation();

                mainScreen();
            } else if (keyStroke != null && keyStroke.getKeyType() == KeyType.F3) {         // FORWARD
                screen.clear();
                dialogueForward();

                mainScreen();
            } else if (keyStroke != null && keyStroke.getKeyType() == KeyType.F4) {         // BACKWARD
                screen.clear();
                dialogueBackward();

                mainScreen();
            } else if (keyStroke != null && keyStroke.getKeyType() == KeyType.F5) {         // RENAME FILE
                screen.clear();
                dialogueFileRename();

                mainScreen();
            } else if (keyStroke != null && keyStroke.getKeyType() == KeyType.F6) {         // SEARCH FILE
                screen.clear();
                dialogueFileSearch();

                mainScreen();
            } else if (keyStroke != null && keyStroke.getKeyType() == KeyType.Enter) {      // SHOW FILE CONTENT
                screen.clear();
                dialogueFileOpening();

                mainScreen();
            } else if (keyStroke != null && keyStroke.getKeyType() == KeyType.Backspace) {  // DELETE FILE
                screen.clear();
                dialogueFileDeletion();

                mainScreen();
            } 
				
			// Make changes visible.
			screen.refresh();
		}
	}

    /**
     * Puts an array of data into a Map and displays it as key-value pairs in the UI
     * TOSSED: Scrolling
     * @throws IOException
     */
    private void displayData() throws IOException {
        // Row Starting Position and offset for each row
        int row = Constants.SECOND_ROW;
        int rowOffset = 2;
        int posY = 4;

        height = screen.getTerminalSize().getRows() - 1;

        files = service.getCurrentNodes();

        fileSetup.clear();
        
        for (int i = 0; i < files.length; i++) {
            fileSetup.put(posY, files[i]);
            posY++;
        }

        // values
        fileSetup.forEach((k, v) -> screen.newTextGraphics().putString(Constants.SECOND_COLUMN_CONTENT, k, v));
        
        // keys
        fileSetup.forEach(
                (k, v) -> screen.newTextGraphics().putString(Constants.FIRST_COLUMN_CONTENT, k, Integer.toString(k)));

        screen.refresh();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // DIALOGUES //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Prompt to ask the User if a custom server adress should be used.
     * If not used the fallback IP will be used.
     * @throws IOException
     */
    private void dialogueServer() throws IOException {

        String ipAdressServer, prompt;
        final WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);

        prompt = TextInputDialog.showDialog(textGUI, "", Constants.CUSTOM_SERVER_PROMPT, "");

        if (prompt.equals("y") || prompt.equals("Y")) {
            ipAdressServer = TextInputDialog.showDialog(textGUI, "", Constants.IP_ADRESS_PROMPT, "");
            if (ipAdressServer.equals("")) {
                ipAdressServer = Constants.FALLBACK_IP;
            }
            screen.clear();
        } else if (prompt.equals("n") || prompt.equals("N")) {
            ipAdressServer = Constants.FALLBACK_IP;
            screen.clear();
        } else {
            dialogueServer();
        }
    }

    /**
     * Initializes a new data Client
     * @throws IOException
     */
    private void dialogueDataClient() throws IOException {

        final WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);
        
        //USER-INPUT
        ipAdress = TextInputDialog.showDialog(textGUI, "", Constants.IP_ADRESS_PROMPT, "");
        if (ipAdress.equals("")) {
            dialogueDataClient();
        }
        port = TextInputDialog.showDialog(textGUI, "", Constants.PORT_PROMPT, "");
        if (port.equals("")) {
            dialogueDataClient();
        }
        path = TextInputDialog.showDialog(textGUI, "", Constants.PATH_PROMPT, "");
        if (path.equals("")) {
            dialogueDataClient();
        }

        screen.clear();

        // SEND TO SERVER
        service.initDataclient(ipAdress, port, path);

        // User client <-> Server Handshake
        initDataClient();
    }

    /**
     * Creates a new File in the current directory
     * @throws IOException
     */
    private void dialogueFileCreation() throws IOException {

        final WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);

        width = screen.getTerminalSize().getColumns()-5;
        height = screen.getTerminalSize().getRows()-5;

        // PUTS FILE NAME INTO THE VARIABLE fileName
        fileName = TextInputDialog.showDialog(textGUI, "", Constants.FILE_NAME_PROMPT, "");
        if (fileName.equals("")) {
            dialogueFileCreation();
        }

        // PUTS FILE CONTENT INTO THE VARIABLE fileContent (Currently not implemented)
        fileContent = new TextInputDialogBuilder()
                                .setTitle(Constants.FILE_CONTENT_PROMPT)
                                .setTextBoxSize(new TerminalSize(width, height))
                                .build()
                                .showDialog(textGUI);
        
        screen.clear();

        //SEND TO SERVER                        
        service.createNode(fileName);
        
        // User client <-> Server Handshake
        initFileCreation();
    }

    /**
     * Moves into a prompted folder
     * @throws IOException
     * @throws ParseException
     */
    private void dialogueForward() throws IOException, ParseException {

        int keyValue;

        final WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);

        width = screen.getTerminalSize().getColumns()-5;
        height = screen.getTerminalSize().getRows()-5;

        // PUTS FOLDER NUMBER INTO THE VARIABLE folder
        String folderNr = TextInputDialog.showDialog(textGUI, "", Constants.FILE_NUMBER_PROMPT, "");
        if (folderNr.equals("")) {
            dialogueForward();
        }

        keyValue = Integer.parseInt(folderNr);
        
        folder = fileSetup.get(keyValue);           // GET KEY VALUE
        
        service.moveDown(folder);                   // SEND TO SERVER

        screen.clear();  
    }

    /**
     * Moves out of the current folder
     * @throws IOException
     * @throws ParseException
     */
    private void dialogueBackward() throws IOException, ParseException {

        final WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);

        width = screen.getTerminalSize().getColumns()-5;
        height = screen.getTerminalSize().getRows()-5;
        
        service.moveUp();                           // SEND TO SERVER

        screen.clear(); 
    }

    /**
     * Renames a file / folder
     * @throws IOException
     * @throws ParseException
     */
    private void dialogueFileRename() throws IOException, ParseException {

        int keyValue;

        final WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);

        width = screen.getTerminalSize().getColumns()-5;
        height = screen.getTerminalSize().getRows()-5;

        // PUTS FILE NUMBER INTO THE VARIABLE fileNr
        String fileNr = TextInputDialog.showDialog(textGUI, "", Constants.FILE_NUMBER_PROMPT, "");
        if (fileNr.equals("")) {
            dialogueFileRename();
        }

        keyValue = Integer.parseInt(fileNr);

        fileName = fileSetup.get(keyValue);

        // PUTS NEW FILE NAME INTO VARIABLE newFileName
        newFileName = TextInputDialog.showDialog(textGUI, "", Constants.FILE_NAME_PROMPT, "");
        if (newFileName.equals("")) {
            dialogueFileRename();
        }

        screen.clear();

        service.renameNode(fileName, newFileName);  //SEND TO SERVER

        // User client <-> Server Handshake
        initFileRenaming();
    }

    /**
     * Searches for a file / folder
     * @throws IOException
     * @throws ParseException
     */
    private void dialogueFileSearch() throws IOException, ParseException {

        final WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);

        width = screen.getTerminalSize().getColumns()-5;
        height = screen.getTerminalSize().getRows()-5;

        // PUTS FILE NAME INTO THE VARIABLE file
        String file = TextInputDialog.showDialog(textGUI, "", Constants.FILE_NAME_PROMPT, "");
        if (file.equals("")) {
            dialogueFileSearch();
        }
        
        screen.clear();

        service.searchForNode(file); //SEND TO SERVER

        // TOSSED: What to do if file is found? Open?
    }

    /**
     * Prints out the content of a file
     * @throws IOException
     * @throws ParseException
     */
    private void dialogueFileOpening() throws IOException, ParseException {

        int value;
        int keyValue;
        String file;

        // Keystroke setup
        com.googlecode.lanterna.input.KeyStroke keyStroke;

        final WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);

        width = screen.getTerminalSize().getColumns() - 5;
        height = screen.getTerminalSize().getRows() - 5;

        // PUTS FILE NUMBER INTO THE VARIABLE fileNr
        String fileNr = TextInputDialog.showDialog(textGUI, "", Constants.FILE_NUMBER_PROMPT, "");

        // KEY TO VALUE (FILENAME)
        value = Integer.parseInt(fileNr);
        file = fileSetup.get(value);
        
        screen.clear();

        // TOSSED: FOOTER IS NOT DISPLAYED AT SCREEN HEIGHT EVEN THOUGH IT IS SET AS SUCH?
        drawMenu(Constants.FILE_VIEWER_MENU);

        cursorWait(0, 2, 666);
        typeln(fileContent, 0, 0);

        while (isOn) {

            // Monitoring keyboard input
            keyStroke = screen.pollInput();
            if (keyStroke != null && keyStroke.getKeyType() == KeyType.Escape) {            // BACK TO MAIN MENU
                screen.clear();
                mainScreen();
                break;

            } else if (keyStroke != null && keyStroke.getKeyType() == KeyType.Backspace) {  // DELETE FILE
                keyValue = Integer.parseInt(file);

                fileName = fileSetup.get(keyValue);

                fileSetup.remove(keyValue);
            }
        }
    }

    /**
     * Deletes a file / folder
     * @throws IOException
     * @throws ParseException
     */
    private void dialogueFileDeletion() throws IOException, ParseException {

        int keyValue;

        final WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);

        width = screen.getTerminalSize().getColumns() - 5;
        height = screen.getTerminalSize().getRows() - 5;

        // PUTS FILE NAME INTO THE VARIABLE fileName
        String file = TextInputDialog.showDialog(textGUI, "", Constants.FILE_NUMBER_PROMPT, "");

        keyValue = Integer.parseInt(file);

        fileName = fileSetup.get(keyValue);

        screen.clear();

        service.deleteNode(fileName);           // SEND TO SERVER
        
        // User client <-> Server Handshake
        initFileDeletion();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // REST COMMUNICATION //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * User client <-> Server Handshake to check if a connection was established
     * @throws IOException
     */
    private void initUi() throws IOException {
        cursorWait(0, 0, 1111);
        typeln(Constants.INIT_INTERFACE, 0, 0);
        cursorWait(0, 0, 999);
        typeln(Constants.INIT_SERVER, 0, 4);
        cursorWait(0, 0, 888);
        typeln(Constants.PROGRESS_BAR, 0, 6);
        cursorWait(0, 0, 777);
        typeln(Constants.INIT_INTERFACE, 0, 6);
        cursorWait(0, 0, 666);
    }

    /**
     * User client <-> Server Handshake to check if a data client was added
     * @throws IOException
     */
    private void initDataClient() throws IOException {
        // CONFIRMATION DIALOG
        cursorWait(0, 2, 666);
        typeln(Constants.CHECK_SERVER, 0, 0);
        cursorWait(0, 2, 1111);
        typeln(">_ DATA CLIENT AT" + "      " + ipAdress + " ON PORT " + port + "      " + " WITH ROOT-FOLDER " + path + "      " + "INILIALIZED", 0, 1);
        cursorWait(0, 2, 1111);
    }

    /**
     * User client <-> Server Handshake to check if a file was created on the specified data client
     * @throws IOException
     */
    private void initFileCreation() throws IOException {
        cursorWait(0, 2, 666);
        typeln(Constants.CHECK_SERVER, 0, 0);
        cursorWait(0, 2, 1111);
        typeln(">_ FILE" + "      " + fileName + "      " + "WITH CONTENT:" + "      " + fileContent + "      "
                + "CREATED", 0, 1);
        cursorWait(0, 2, 1111);
    }

    /**
     * User client <-> Server Handshake to check if a file was deleted on the specified data client
     * @throws IOException
     */
    private void initFileDeletion() throws IOException {
        cursorWait(0, 2, 666);
        typeln(Constants.CHECK_SERVER, 0, 0);
        cursorWait(0, 2, 1111);
        typeln(">_ FILE" + "      " + fileName +  "      " + "DELETED", 0, 1);
        cursorWait(0, 2, 1111);
    }

    /**
     * User client <-> Server Handshake to check if a file was renamed on the specified data client
     * @throws IOException
     */
    private void initFileRenaming() throws IOException {
        cursorWait(0, 2, 666);
        typeln(Constants.CHECK_SERVER, 0, 0);
        cursorWait(0, 2, 1111);
        typeln(">_ FILE"  + "      " + fileName + "      " + "WAS RENAMENT TO" + "      " + newFileName , 0, 1);
        cursorWait(0, 2, 1111);
    }

    /**
     * Confirmation of the UI shutdown
     * @throws IOException
     */
    private void initShutDown() throws IOException {
        cursorWait(0, 2, 666);
        typeln(">_ SYSTEM EXIT HOTKEY ON", 0, 0);
        cursorWait(0, 2, 1111);
        typeln(">_ SESSION TERMINATED", 0, 1);
        cursorWait(0, 2, 1111);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // UTILITIES //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Function to print out a string at a specified column and row on the current screen.
     * @param 'msg' to print
     * @param 'col' = column
     * @param 'row' = row
     */
    public void typeln(String msg, int col, int row) {
        TextColor defC = fg;
        fg = TextColor.ANSI.GREEN;
        int interval = 11;

        for (int i = 0; i < msg.length(); i++) {
            screen.setCursorPosition(new TerminalPosition(col + i, row));
            screen.setCharacter((col + i), row, new TextCharacter(msg.charAt(i), fg, bg));

            try {
                screen.refresh();
                Thread.sleep(ThreadLocalRandom.current().nextInt(interval * 3));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        fg = defC;
    }

    /**
     * Timer to hold the cursor at a specifiend location to wait for a server reply
     * @param 'col' = column
     * @param 'row' = row
     * @param millis = milli seconds to wait
     */
    public void cursorWait(int col, int row, int millis) {

        screen.setCursorPosition(null);
        // screen.setCursorPosition(new TerminalPosition(col, row));

        try {
            screen.refresh();

            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter for location descripions
     * @return
     */
    public String[] getLocationDescriptions() {
        return locationDescriptions;
    }

    /**
     * Setter for location descripions
     * @param locationDescriptions
     */
    public void setLocationDescriptions(String[] locationDescriptions) {
        this.locationDescriptions = locationDescriptions;
    }

}

