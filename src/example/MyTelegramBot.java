package example;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

//7293215020
public class MyTelegramBot extends TelegramLongPollingBot {

	private List<Long> chatIds = new ArrayList<>(); // Хранение идентификаторов чатов пользователей

	@Override
	public void onUpdateReceived(Update update) {
		// Обработка входящего сообщения
		if (update.hasMessage() && update.getMessage().hasText()) {
			long chatId = update.getMessage().getChatId();
			if (!chatIds.contains(chatId)) {
				chatIds.add(chatId); // Добавление нового идентификатора чата
			}
			String incomingMessageText = update.getMessage().getText();
			// Ваша логика обработки сообщения здесь
			String responseMessage = "Вы написали: " + incomingMessageText;
			sendTextMessage(chatId, responseMessage);
		}
	}

	private void sendTextMessage(long chatId, String text) {
		try {
			execute(new SendMessage(String.valueOf(chatId), text));
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	// Новый метод для отправки сообщения всем пользователям
	public void broadcastMessage(String message) {
		for (Long chatId : chatIds) {
			sendTextMessage(chatId, message);
		}
	}

	// Новый метод для отправки сообщения через ваш Telegram бот
	public void sendMessageToTelegramBot(String message) {
		long chatId = 761320567; // Замените YOUR_CHAT_ID на реальный идентификатор чата
		// 867921548
		sendTextMessage(chatId, message);
	}

	public void sendMessageToTelegaBot(String message) {
		long chatId = 867921548; // Замените YOUR_CHAT_ID на реальный идентификатор чата
		// 867921548
		sendTextMessage(chatId, message);
	}

	public void sendMessageToMishaTG(String message) {
		long chatId = 669746369; // Замените YOUR_CHAT_ID на реальный идентификатор чата
		sendTextMessage(chatId, message);
	}

	@Override
	public String getBotUsername() {
		return "YourBotUsername";
	}

	@Override
	public String getBotToken() {
		return "7293215020:AAFPS0CHRWQlz29GBDnQvF3OzTEoqfIKkrY"; // Замените YOUR_BOT_TOKEN на ваш токен бота
	}

	public static void main(String[] args) {
		try {
			TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
			botsApi.registerBot(new MyTelegramBot());
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}
}