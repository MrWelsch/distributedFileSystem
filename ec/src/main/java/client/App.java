/**
 * Class to start the app.
 * @author Nico Welsch
 * @version 1.0
 * @date 22.03.2022
 */

package client;

import java.io.IOException;

import org.json.simple.parser.ParseException;

import client.gui.controller.UiController;

//  mvn exec:java -Dexec.mainClass="client.App"
public class App 
{
    public static void main( String[] args ) throws IOException, InterruptedException, ParseException
    {
        UiController lanterna = new UiController();
        lanterna.turnOn();
    }
}
