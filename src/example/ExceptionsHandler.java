package example;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExceptionsHandler {
	private static final int allowedPerMinute = 15;
	
	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private static final Date date = new Date();
	private static int currentExceptionsNum;
	private static ArrayList<Exception> currentExceptions = new ArrayList<Exception>();
	private static final File ErrLogFile = new File("ErrLogs.txt");

	public ExceptionsHandler() {
		scheduler.schedule(() -> {
			// Каждую минуту обнуляем текущий счетчик ошибок
			currentExceptionsNum = 0;
		}, 1, TimeUnit.MINUTES);

		createLogFile();
	}

	public static void handleException(Exception e) {
		currentExceptionsNum++;
		currentExceptions.add(e);

		System.out.println("currently exceptions per min = " + currentExceptionsNum);
		logException(e);
		if (currentExceptionsNum > allowedPerMinute) {
			// Насильно перезапускаем
			System.out.println("Forcing restart due to errors");

			logAllCurrentExceptionsBeforeRestart();
			currentExceptionsNum = 0;
			currentExceptions.clear();

			OpenVLCPlayer.forceRestartDriver();
		}
	}

	private static void logException(Exception e) {
		String logMessage = "[PID = " + OpenVLCPlayer.pid + "][" + date.toString() + "] Exception: " + e;
		System.out.println(logMessage);
		e.printStackTrace();

		try (PrintStream ErrFileOut = new PrintStream(new FileOutputStream(ErrLogFile, true))) {

			ErrFileOut.println(logMessage);
			e.printStackTrace(ErrFileOut);

		} catch (FileNotFoundException ex) {
			System.out.println("Cant create/open log file" + ex.getMessage());
		}

	}

	private static void logAllCurrentExceptionsBeforeRestart() {
		logStringToFile("____________________START OF ERRORS LIST____________________");
		for (int i = 0; i < currentExceptionsNum; i++) {
			logException(currentExceptions.get(i));
		}
		logStringToFile("_____________________END OF ERRORS LIST_____________________");
	}

	private void createLogFile() {
		try {
			if (!ErrLogFile.exists()) {
				ErrLogFile.createNewFile();
			}
		} catch (IOException e) {
			System.out.println("Failed to create file ErrLogs.txt: " + e.getMessage());
		}
	}

	private static void logStringToFile(String logMessage) {
		try (PrintStream ErrFileOut = new PrintStream(new FileOutputStream(ErrLogFile, true))) {
			ErrFileOut.println(logMessage);

		} catch (FileNotFoundException ex) {
			System.out.println("Cant create/open log file" + ex.getMessage());
		}
	}
	
	public static void handleHttpException(Exception e) {
		currentExceptionsNum++;
		currentExceptions.add(e);

		System.out.println("currently exceptions per min = " + currentExceptionsNum);
		logException(e);
	}
}
