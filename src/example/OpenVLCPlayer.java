
package example;

import java.time.Duration;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

//	C:\\Users\\Administrator\\Desktop\\d\\chromedriver.exe

public class OpenVLCPlayer {

	public static final String email_entrance = Config.EMAIL;
	public static final String password = Config.PASSWORD;

	public static final String lowEnergy = Config.LOW_ENERGY;
	public static final String uppEnergy = Config.UPP_ENERGY;

	public static final String lowPrice = Config.LOW_PRICE;
	public static final String uppPrice = Config.UPP_PRICE;

	public static final String lowPower = Config.LOW_POWER;
	public static final String uppPower = Config.UPP_POWER;

	public static final double compareWith = Config.DESIRED_RATIO;
	public static final String pathDriver = ".\\\\bin\\\\chromedriver.exe";

	public static final int iterationsBeforeRestart = 2000;

	// url = https://app.gmt.io/nft-miners

	// Вынес драйвер сюда чтоб видимость не карала
	private static WebDriver driver;
	private static MyTelegramBot bot;
	private static ExceptionsHandler eHandler = new ExceptionsHandler();
	private static HttpClient httpClient = new HttpClient();
	
	public static long pid = ProcessHandle.current().pid();

	private static int iterations;

	public static void main(String[] args) {
		//Тест запроса
		
		while (true) {
			try {
				Thread.sleep(800);
			} catch (InterruptedException e) {
				System.out.println("Пока!");
			}
			NftDTO bestMinerData = httpClient.getBestValueNftInfo();

			if (bestMinerData != null) {
			    String message = "Через запросы увидели < " + Config.DESIRED_RATIO + " " + bestMinerData.toString();
			    System.out.println(bestMinerData.toString() + " iteration = " + iterations);
			    iterations++;
			    if (bestMinerData.Ratio < Config.DESIRED_RATIO) {
			        Logger.logStringToFile(message, "testLogs");
			    }
			} else {
			    // Handle the case where bestMinerData is null, such as logging or notifying the user
			    System.out.println("No valid NFT data received.");
			}
				
			}
//			try {
//				System.in.read();
//				System.exit(0);
//			} catch (IOException e) {
//				System.exit(0);
//			}
		}
		
		
//		try {
//			prepareDriver();
//			iterations = 0;
//			while (true) {
//				try {
//					// ВРЕМЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯ
//					Thread.sleep(3200);
//
//					priceChangeFilter(driver, lowPrice, uppPrice);
//					filterUppChange(driver);
//					// Thread.sleep(1000);
//					FirstTHCard(driver);
//					// SecondTHCard(driver);
//					iterations++;
//
//					// Тест рестартов
//					// ExceptionsThrower.ThrowException();
//
//					// Thread.sleep(1000);
//					System.out.println("[PID = " + pid + "]Iteration k = " + iterations);
//
//					if (iterations == iterationsBeforeRestart) {
//						iterations = 0; // Сбросить счетчик k
//						driverRestart();
//					}
//
//				} catch (Exception e) {
//					ExceptionsHandler.handleException(e);
//					handleModalWindow(driver);
//				}
//			}
//
//		} catch (Exception e) {
//			ExceptionsHandler.handleException(e);
//		}
//	}

	private static void driverRestart() {
		try {
			iterations = 0;
			Thread.sleep(2000); // Подождать 2 секунды для завершения всех операций
			driver.quit(); // Закрыть текущий экземпляр драйвера
			driver = null;
			String message = "Time to restart";

			bot.sendMessageToMishaTG(message);
			
			
			bot.sendMessageToTelegramBot(message);
			//bot.sendMessageToTelegaBot(message);

			driver = newOpenRestart(); // Создать новый экземпляр драйвера
		} catch (Exception e) {
			ExceptionsHandler.handleException(e);
		}

	}

	private static void prepareDriver() {
		try {
			// запуск сайта
			driver = websiteLaunch(pathDriver);
			// Очистка кэша браузера
			driver.manage().deleteAllCookies(); // Удаление всех cookies
			driver.navigate().refresh(); // Обновление страницы для очистки кэша
			Thread.sleep(15000);
			cookieClose(driver);
			Thread.sleep(6000);
			// заполнение входных данных
			fillLoginInformation(email_entrance, password, driver);
			String messageToSend = "Выполнен вход | +Конфиг +Логи";

			bot.sendMessageToTelegramBot(messageToSend);
			bot.sendMessageToMishaTG(messageToSend);

			Thread.sleep(15000);
			goSecondaryMarketplaceTab(driver);
			Thread.sleep(2000);

			energyChangeFilter(lowEnergy, uppEnergy, driver);
			Thread.sleep(2000);
			priceChangeFilter(driver, lowPrice, uppPrice);
			Thread.sleep(2000);
			powerChangeFilter(driver, lowPower, uppPower);
			Thread.sleep(2000);

			saleFilterSecondsryChange(driver);
			// driver.findElement(By.cssSelector("body")).click();
			Thread.sleep(5000);
		} catch (Exception e) {
			ExceptionsHandler.handleException(e);
		}
	}

	public static void forceRestartDriver() {
		try {
			driverRestart();
		} catch (Exception e) {
			ExceptionsHandler.handleException(e);
		}

	}

	/**
	 * @param email_entrance
	 * @param password
	 * @param lowEnergy
	 * @param uppEnergy
	 * @param lowPrice
	 * @param uppPrice
	 * @param lowPower
	 * @param pathDriver
	 * @return
	 * @throws InterruptedException
	 */
	private static WebDriver newOpenRestart() throws InterruptedException {
		WebDriver driver;
		driver = websiteLaunch(pathDriver);
		driver.manage().deleteAllCookies(); // Удаление всех cookies
		driver.navigate().refresh();

		Thread.sleep(15000);
		cookieClose(driver);
		Thread.sleep(5000);
		// заполнение входных данных
		fillLoginInformation(email_entrance, password, driver);

		Thread.sleep(20000);

		goSecondaryMarketplaceTab(driver);
		Thread.sleep(15000);
		energyChangeFilter(lowEnergy, uppEnergy, driver);
		Thread.sleep(7000);
		priceChangeFilter(driver, lowPrice, uppPrice);
		Thread.sleep(7000);
		powerChangeFilter(driver, lowPower, uppPower);
		Thread.sleep(7000);
		saleFilterSecondsryChange(driver);
		// driver.findElement(By.cssSelector("body")).click();
		Thread.sleep(2000);
		return driver;
	}

	/**
	 * @param driver
	 */
	public static void FirstTHCard(WebDriver driver) {
		try {
			WebElement priceGrid = driver
					.findElement(By.cssSelector(".row.row-grid.catalog-index__cards-row.ng-star-inserted"));

			WebElement priceElement = priceGrid
					.findElement(By.xpath("./div[2]/nft-card/a/div[2]/nft-card-price/div[1]/div[2]/span/span[2]"));
			// Если элемент найден, продолжаем обработку
			String priceText = priceElement.getText().trim();
			System.out.println("Value 1 TH: " + priceText);
			// Date date = new Date();
			// ("output.txt", "Значение 1 TH: " + priceText + date.toString()); // Запись в
			// файл

			// Извлекаем числовую часть из строки цены
			String numericPrice = priceText.replaceAll("[^0-9.]", "");

			try {
				double priceValue = Double.parseDouble(numericPrice);

				if (priceValue < compareWith) {
					System.out.println("$ to TH is lower than " + compareWith + " , take");
					String messageToSend = "Вижу  " + priceValue;

					bot.sendMessageToMishaTG(messageToSend);
					bot.sendMessageToTelegramBot(messageToSend);
					bot.sendMessageToTelegaBot(messageToSend);

					buttonFirstCardBue(driver);
					BueBortTH(driver);

				} else {
					System.out.println("$ to TH is higher than " + compareWith + " , skip");

				}
			} catch (NumberFormatException e) {
				ExceptionsHandler.handleException(e);
				System.err.println("Error while converting the number: " + e.getMessage());
				// driver.findElement(By.cssSelector("body")).click();
				handleModalWindow(driver);
			}

		} catch (Exception e) {
			System.err.println("Error while searching elem: " + e.getMessage());
			ExceptionsHandler.handleException(e);
			// Вот это убрать, можно сюда добавить как раз ретрай попытки найти цену
			// handleModalWindow(driver);
			// driver.findElement(By.cssSelector("body")).click();
			// Здесь можно добавить действия, если элемент не был найден вовремя
		}
	}

	/**
	 * полна покупка
	 * 
	 * @param driver
	 * @throws InterruptedException
	 */
	private static void BueBortTH(WebDriver driver) throws InterruptedException {
		// Нажатие на первую кнопку
		WebElement button2 = driver
				.findElement(By.xpath("/html/body/app-root/nft-bag/div/div[2]/div/div/div[1]/button[3]"));
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", button2);

		// Нажатие на вторую кнопку
		WebElement button3 = driver.findElement(By.xpath(
				"/html/body/app-root/nft-bag/div/div[2]/div/div/div[3]/payment-methods-group/div/catalog-config-item[2]/button"));
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", button3);

		// Нажатие на третью кнопку
		WebElement button4 = driver
				.findElement(By.xpath("/html/body/app-root/nft-bag/div/div[2]/div/div/div[4]/button[1]"));
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", button4);

		Thread.sleep(3000);

		String messageToSend = "Бронь";

		bot.sendMessageToTelegramBot(messageToSend); // Вызываем метод sendMessageToTelegramBot
		bot.sendMessageToMishaTG(messageToSend);
		// bot.sendMessageToTelegaBot(messageToSend); // Вызываем метод
		// sendMessageToTelegramBot

//		try {
//			driver = newOpenRestart(email_entrance, password, lowEnergy, uppEnergy, lowPrice, uppPrice,
//					lowPower, pathDriver);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			 System.err.println("Ошибка при записи данных в файл: " + e.getMessage());

		// handleModalWindow(driver);
	}

	/**
	 * @param driver
	 */
	private static void buttonFirstCardBue(WebDriver driver) {

		WebElement button = driver.findElement(By.xpath(
				"/html/body/app-root/main/marketplace/nft-index/div/div[1]/div[3]/div/div[2]/nft-card/a/div[2]/div[2]/div/button"));
		JavascriptExecutor executor1 = (JavascriptExecutor) driver;
		executor1.executeScript("arguments[0].click();", button);

		// //*[@id="app"]/marketplace/nft-index/div/div[1]/div[3]/div/div[2]/nft-card/a/div[2]/div[3]/div/button
		// /html/body/app-root/main/marketplace/nft-index/div/div[1]/div[3]/div/div[2]/nft-card/a/div[2]/div[3]/div/button
	}

	/**
	 * @param driver
	 */
	private static void saleFilterSecondsryChange(WebDriver driver) {
		try {
			WebElement saleButton = driver
					.findElement(By.cssSelector("button[data-qa='nft-index__dropdown-filter-seller']"));
			saleButton.click();
			Thread.sleep(2000);

			WebElement secondaryLabel = driver
					.findElement(By.cssSelector("input[name='seller'][value='gmt-secondary'][type='radio']"));
			WebElement secondaryOption = secondaryLabel.findElement(By.xpath("./following-sibling::span[1]"));
			secondaryOption.click();
			Thread.sleep(1000);
		} catch (Exception e) {
			// driver.findElement(By.cssSelector("body")).click();
			ExceptionsHandler.handleException(e);
		}
	}

	private static void handleModalWindow(WebDriver driver) {
		try {
			driver.findElement(By.cssSelector("body")).click();
			System.out.println("Clicked to skip modal window");

		} catch (Exception e) {
			System.out.println("Pop-up exception");
			ExceptionsHandler.handleException(e);
		}
	}

	/**
	 * @param driver
	 */
	private static void cookieClose(WebDriver driver) {
		WebElement acceptAllButton = driver
				.findElement(By.cssSelector("button.btn.btn-lg.w-100.text-nowrap.btn-primary.ng-star-inserted"));
		acceptAllButton.click();
		System.out.println("Button 'Coockie' clicked");
	}

	/**
	 * @param driver
	 * @throws InterruptedException
	 */
	private static void filterUppChange(WebDriver driver) throws InterruptedException {
		boolean success = false;
		while (!success) {
			try {
				Thread.sleep(200);
				WebElement element = driver.findElement(By.cssSelector("button[data-qa='nft-index__btn-sort']"));
				JavascriptExecutor executor = (JavascriptExecutor) driver;
				executor.executeScript("arguments[0].click();", element);
				Thread.sleep(200);
				WebElement button = driver.findElement(By.xpath("//span[contains(text(), '$ / 1 TH: low to high')]"));
				button.click();
				System.out.println("List is updated");
				success = true;

			} catch (Exception e) {
				ExceptionsHandler.handleException(e);
				handleModalWindow(driver);
				// Возможно, здесь стоит добавить задержку перед следующей попыткой клика

			}
		}
	}

	/**
	 * @param driver
	 * @throws InterruptedException
	 */
	private static void powerChangeFilter(WebDriver driver, String lowPower, String uppPower)
			throws InterruptedException {
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

			JavascriptExecutor js = (JavascriptExecutor) driver;

			WebElement minInput = wait.until(ExpectedConditions
					.presenceOfElementLocated(By.xpath("//nft-filter-computing-power//input[@formcontrolname='min']")));

			setValueAndTriggerEvents(js, minInput, lowPower);

			System.out.println("Min power: " + js.executeScript("return arguments[0].value;", minInput));

			WebElement maxInput = wait.until(ExpectedConditions
					.presenceOfElementLocated(By.xpath("//nft-filter-computing-power//input[@formcontrolname='max']")));

			setValueAndTriggerEvents(js, maxInput, uppPower);

			System.out.println("Max power: " + js.executeScript("return arguments[0].value;", maxInput));
		} catch (Exception e) {
			ExceptionsHandler.handleException(e);
			handleModalWindow(driver);
			// driver.findElement(By.cssSelector("body")).click();
		}
	}

	/**
	 * @param lowEnergy
	 * @param uppEnergy
	 * @param driver
	 * @throws InterruptedException
	 */
	private static void energyChangeFilter(String lowEnergy, String uppEnergy, WebDriver driver)
			throws InterruptedException {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		JavascriptExecutor js = (JavascriptExecutor) driver;

		WebElement minInput = wait.until(ExpectedConditions.presenceOfElementLocated(
				By.xpath("(//nft-filter-energy-efficiency//input[@formcontrolname='min'])[1]")));
		WebElement maxInput = wait.until(ExpectedConditions.presenceOfElementLocated(
				By.xpath("(//nft-filter-energy-efficiency//input[@formcontrolname='max'])[1]")));

		setValueAndTriggerEvents(js, minInput, lowEnergy);

		Thread.sleep(500);

		setValueAndTriggerEvents(js, maxInput, uppEnergy);

		System.out.println("Min value: " + js.executeScript("return arguments[0].value;", minInput));
		System.out.println("Max value: " + js.executeScript("return arguments[0].value;", maxInput));

	}

	/**
	 * @param driver
	 * @throws InterruptedException
	 */
	private static void priceChangeFilter(WebDriver driver, String lowPrice, String uppPrice)
			throws InterruptedException {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		JavascriptExecutor js = (JavascriptExecutor) driver;

		WebElement minInput = wait.until(ExpectedConditions
				.presenceOfElementLocated(By.xpath("//nft-filter-price//input[@formcontrolname='min']")));
		WebElement maxInput = wait.until(ExpectedConditions
				.presenceOfElementLocated(By.xpath("//nft-filter-price//input[@formcontrolname='max']")));

		setValueAndTriggerEvents(js, minInput, lowPrice);

		Thread.sleep(500);

		setValueAndTriggerEvents(js, maxInput, uppPrice);

		System.out.println("Min price: " + js.executeScript("return arguments[0].value;", minInput));
		System.out.println("Max price: " + js.executeScript("return arguments[0].value;", maxInput));

	}

	/**
	 * @return
	 * @throws InterruptedException
	 */
	private static WebDriver websiteLaunch(String pathDriver) throws InterruptedException {
		System.setProperty("webdriver.chrome.driver", pathDriver);

		Map<String, Object> prefs = new HashMap<>();
		prefs.put("profile.managed_default_content_settings.images", 2);

		ChromeOptions options = new ChromeOptions();
		options.setExperimentalOption("prefs", prefs);

		WebDriver driver = new ChromeDriver(options);
		driver.manage().window().maximize();
		options.addArguments("--headless=new");
		// Очистка кэша и cookies
		driver.manage().deleteAllCookies();

		Thread.sleep(15000);
		driver.get("https://app.gmt.io/nft-miners");

		return driver;
	}

	/**
	 * @param driver
	 */
	private static void goSecondaryMarketplaceTab(WebDriver driver) {
		WebElement buttonSecond = driver.findElement(By.cssSelector("a#nft-marketplace-dropdown"));
		buttonSecond.click();
		System.out.println("Entered secondary market");
	}

	/**
	 * @param email_entrance
	 * @param password
	 * @param driver
	 */
	private static void fillLoginInformation(String email_entrance, String password, WebDriver driver) {
		// Вход в систему
		WebElement buttonEntrance = driver.findElement(By.cssSelector("span.btn__text.hidden-empty.text-truncate"));
		buttonEntrance.click();
		System.out.println("Started sign-in");
		WebElement lineEmail = driver.findElement(By.cssSelector("input[data-qa='login__email-input']"));
		lineEmail.sendKeys(email_entrance);
		System.out.println("Email entered");

		WebElement linePassword = driver.findElement(By.cssSelector("input[data-qa='login__password-input']"));
		linePassword.sendKeys(password);
		System.out.println("Password entered");

		WebElement buttonEntranceIn = driver.findElement(By.cssSelector("button[data-qa='login__submit']"));
		buttonEntranceIn.click();
		System.out.println("Sign-in completed");
	}

	private static void setValueAndTriggerEvents(JavascriptExecutor js, WebElement element, String value) {
		js.executeScript("arguments[0].value = arguments[1];", element, value);
		js.executeScript("arguments[0].dispatchEvent(new Event('input'));", element);
		js.executeScript("arguments[0].dispatchEvent(new Event('change'));", element);
		js.executeScript("arguments[0].blur();", element);
	}
}
