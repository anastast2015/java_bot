package example;

import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

public class Config {

	public static String EMAIL;
	public static String PASSWORD;

	public static String LOW_ENERGY;
	public static String UPP_ENERGY;

	public static String LOW_PRICE;
	public static String UPP_PRICE;

	public static String LOW_POWER;
	public static String UPP_POWER;

	public static double DESIRED_RATIO;

	private static final File CONFIG_FILE = new File("config.properties");

	static {
		createConfigFile();
		Properties properties = new Properties();
		try (InputStream input = new FileInputStream(CONFIG_FILE)) {
			// Загружаем свойства из файла
			properties.load(input);

			EMAIL = properties.getProperty("email");
			PASSWORD = properties.getProperty("password");

			if (EMAIL.isEmpty() || PASSWORD.isEmpty()) {
				System.out.println("ENTER EMAIL AND PASSWORD INTO config.properties FILE!!!");
				System.exit(0);
			}

			LOW_ENERGY = properties.getProperty("fromEnergy");
			UPP_ENERGY = properties.getProperty("toEnergy");

			LOW_PRICE = properties.getProperty("fromPrice");
			UPP_PRICE = properties.getProperty("toPrice");

			LOW_POWER = properties.getProperty("fromPower");
			UPP_POWER = properties.getProperty("toPower");

			String desiredRatioString = properties.getProperty("dollarToThRatio");

			try {
				DESIRED_RATIO = Double.parseDouble(desiredRatioString);
			} catch (NumberFormatException e) {
				System.out.println("Wrong format given: " + desiredRatioString);
				System.out.println("Set 16.0 ratio as default");
				DESIRED_RATIO = 16.0; // Значение по умолчанию в случае ошибки
			}

		} catch (IOException ex) {
			System.out.println("Failed to load config: " + ex.getMessage());
			// Если не удалось загрузить, можно задать значение по умолчанию

			setDefaultValues();
		}

	}

	private static void setDefaultValues() {

		LOW_ENERGY = "20";
		UPP_ENERGY = "22";

		LOW_PRICE = "0";
		UPP_PRICE = "4000";

		LOW_POWER = "3";
		UPP_POWER = "1000";

		DESIRED_RATIO = 16;
	}

	private static void createConfigFile() {
		try {
			if (!CONFIG_FILE.exists()) {
				CONFIG_FILE.createNewFile();
				populateConfigWithDefaultValues();
			}
		} catch (IOException e) {
			System.out.println("Failed to create file ErrLogs.txt: " + e.getMessage());
		}
	}

	private static void populateConfigWithDefaultValues() {
		try (PrintStream ErrFileOut = new PrintStream(new FileOutputStream(CONFIG_FILE, true))) {
			ErrFileOut.println("email=");
			ErrFileOut.println("password=");

			ErrFileOut.println("fromEnergy=20");
			ErrFileOut.println("toEnergy=22");

			ErrFileOut.println("fromPrice=0");
			ErrFileOut.println("toPrice=4000");

			ErrFileOut.println("fromPower=3");
			ErrFileOut.println("toPower=1000");

			ErrFileOut.println("dollarToThRatio=16");

		} catch (FileNotFoundException ex) {
			System.out.println("Cant create/open log file" + ex.getMessage());
		}

	}
}
