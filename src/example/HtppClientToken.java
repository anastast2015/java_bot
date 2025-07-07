package example;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HtppClientToken {

	private static PoolingHttpClientConnectionManager connectionManager;
	private static CloseableHttpClient httpClient;
	private static HttpPost postToGetTokenInfo;

	public HtppClientToken() {
		initConnectionManager();
		initClient();
		initPostQuery();
	}

	private void initClient() {

		httpClient = HttpClients.custom().setConnectionManager(connectionManager).build();

	}

	private void initConnectionManager() {
		connectionManager = new PoolingHttpClientConnectionManager();
		connectionManager.setMaxTotal(100); // Максимальное количество соединений
		connectionManager.setDefaultMaxPerRoute(10); // Максимальное количество соединений на маршрут
	}

	public TokenDTO getTokennfo() {
		String jsonResponse = makePostRequest();

		if (jsonResponse == null || jsonResponse.isEmpty()) {
			// Логируем или обрабатываем случай, когда ответ пустой
			return null;
		}

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode;

		try {
			rootNode = objectMapper.readTree(jsonResponse);
		} catch (JsonMappingException e) {
			ExceptionsHandler.handleHttpException(e);
			return null;
		} catch (JsonProcessingException e) {
			// Логируем ошибку обработки JSON
			ExceptionsHandler.handleHttpException(e);
			return null;
		}

		JsonNode arrayNode = rootNode.path("data").path("array");

		if (arrayNode.isMissingNode() || !arrayNode.isArray() || arrayNode.size() == 0) {
			// Логируем или обрабатываем случай, когда массив пуст или отсутствует
			return null;
		}

		String token = "";

		for (JsonNode item : arrayNode) {
			token = item.path("jwtToken").asText(); // Используем asText для получения строки
			break; // Выходим из цикла после первого токена
		}

		if (token.isEmpty()) {
			// Логируем случай, когда токен не найден
			return null;
		}

		return new TokenDTO(token);
	}

	private String makePostRequest() {
		try {
			CloseableHttpResponse response = httpClient.execute(postToGetTokenInfo);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				return EntityUtils.toString(entity);
			}
		} catch (Exception e) {
			ExceptionsHandler.handleHttpException(e);
			return "";
		}
		return "";
	}

	private void initPostQuery() {
		// LINK
		postToGetTokenInfo = new HttpPost("https://app.gmt.io/api/auth/login");

		// HEADERS
		postToGetTokenInfo.setHeader("Content-Type", "application/json");

		//String bearerAndTokenStr = getBearerStr();
		postToGetTokenInfo.setHeader("Accept", "application/json");
		// Я хз что это такое и сломается ли оно в будущем
		postToGetTokenInfo.setHeader("Access-Control-Allow-Origin",
				"https://app.gmt.io");
		postToGetTokenInfo.setHeader("Access-Control-Allow-Credentials", "true");
		postToGetTokenInfo.setHeader("report-only",
				"https://csp-reporting.cloudflare.com/cdn-cgi/script_monitor/report?m=I06EKRrbgnJvbLpY98vsIwr.bepS1eUeZDV5jcSkT3g-1726249463-1.0.1.1-mWaK22O93LMzRTL7qG21j.8DfkARHmsLMaWzmWNCOj_LrHMpJseWDRWpuIVofeEqwvJC8MArn5btaUBx0H1s86_SKQjgOA0Ln31vFsvqvG9Hn3u3D2YCSdZaW1E7T4rq03pnmgu0S.R2CY2mVr5e4Q; report-to cf-csp-endpoint");
		
		// REQUEST BODY
		String json = buildRequestBody();
		try {
			postToGetTokenInfo.setEntity(new StringEntity(json));
		} catch (UnsupportedEncodingException e) {
			ExceptionsHandler.handleHttpException(e);
		}
	}

	private String buildRequestBody() {
		StringBuilder str = new StringBuilder();
		str.append("{\r\n" + "    \"pagination\": {\r\n" + "        \"skip\": 0,\r\n" + "        \"limit\": 1\r\n" // Если
																													// надо
																													// больше
																													// карточек
																													// менять
																													// limit
				+ "    },\r\n" + "    \"filters\": {\r\n" + "        \"marketplace\": [\r\n"
				+ "            \"gmt-secondary\"\r\n" + "        ],\r\n" + "        \"status\": [\r\n"
				+ "            \"available\"\r\n" + "        ],\r\n" + "        \"nftCollectionIds\": null,\r\n"
				+ "        \"body\": null,\r\n" + "        \"topFan\": null,\r\n" + "        \"screen\": null,\r\n"
				+ "        \"ui\": null,\r\n" + "        \"downFan\": null,\r\n" + "        \"buttons\": null,\r\n"
				+ "        \"upgrades\": null,\r\n" + "        \"walls\": null,\r\n" + "        \"basement\": null,\r\n"
				+ "        \"items\": null,\r\n" + "        \"stickers\": null,\r\n" + "        \"power\": {\r\n"
				+ "            \"min\": ");
		str.append(Config.LOW_POWER).append(",\r\n" + "            \"max\": ");

		str.append(Config.UPP_POWER)
				.append("\r\n" + "        },\r\n" + "        \"energyEfficiency\": {\r\n" + "            \"min\": ");
		str.append(Config.LOW_ENERGY).append(",\r\n" + "            \"max\": ");
		str.append(Config.UPP_ENERGY)
				.append("\r\n" + "        },\r\n" + "        \"price\": {\r\n" + "            \"min\": ");
		str.append(Config.LOW_PRICE).append(",\r\n" + "            \"max\": ");
		str.append(Config.UPP_PRICE)
				.append("\r\n" + "        },\r\n" + "        \"currency\": \"GMT\"\r\n" + "    },\r\n"
						+ "    \"sort\": {\r\n" + "        \"thCost\": 1\r\n" // Если хотим фильтр по цене то заменить
																				// thCost на price, вместе они почему-то
																				// не работают
						+ "    }\r\n" + "}");
		return str.toString();
	};

	private String getBearerStr() {
		// TODO Тут реализовать получение токена

		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MTc3Mzc2MywiZW1haWwiOiJhbmFzdGFzdDIwMTVAZ21haWwuY29tIiwiaWF0IjoxNzI2MDUwNDEyLCJleHAiOjE3MjYwNTQwMTIsImF1ZCI6WyJnZW5kYWxmIl19.PoI34vd2HMslZu4LvyRFRFWpGcy9rLON9zBvxL8hK90";
		return "Bearer " + token;
	}

	public static void main(String[] args) {
		HtppClientToken authService = new HtppClientToken();
        TokenDTO tokenDTO = authService.getTokennfo();

        if (tokenDTO != null) {
            System.out.println("Полученный токен: " + tokenDTO);
        } else {
            System.out.println("Не удалось получить токен.");
        }
    }
	
}
