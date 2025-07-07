package example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class Logger {

	private static void createLogFile(File logFile) {
		try {
			if (!logFile.exists()) {
				logFile.createNewFile();
			}
		} catch (IOException e) {
			System.out.println("Failed to create file ErrLogs.txt: " + e.getMessage());
		}
	}
	
	//Указывать fileName без формата(напрмер .txt)
	public static File createLogFileWithName(String fileName) {
		File logFile = new File(fileName + ".txt");
		createLogFile(logFile);
		return logFile;
	}
	
	public static void logStringToFile(String logMessage, String fileName) {
		File logFile = createLogFileWithName(fileName);
		try (PrintStream FileOut = new PrintStream(new FileOutputStream(logFile, true))) {
			FileOut.println(logMessage);

		} catch (FileNotFoundException ex) {
			System.out.println("Cant create/open log file" + ex.getMessage());
		}
	}
}
