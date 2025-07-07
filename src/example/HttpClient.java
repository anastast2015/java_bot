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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpClient {

	private static PoolingHttpClientConnectionManager connectionManager;
	private static CloseableHttpClient httpClient;
	private static HttpPost postToGetNftInfo;

	public HttpClient() {
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

	public NftDTO getBestValueNftInfo() {
	    String jsonResponse = makePostRequest();
	    ObjectMapper objectMapper = new ObjectMapper();
	    JsonNode rootNode;
	    
	    try {
	        rootNode = objectMapper.readTree(jsonResponse);
	    } catch (JsonMappingException e) {
	        ExceptionsHandler.handleHttpException(e);
	        return null;
	    } catch (JsonProcessingException e) {
	        return null; // Consider logging the error here for better debugging
	    }
	    
	    JsonNode arrayNode = rootNode.path("data").path("array");
	    
	    if (arrayNode.isEmpty()) {
	        // Log that the array is empty or handle it accordingly
	        return null;
	    }

	    int id = 0, power = 0, energyEfficiency = 0;
	    double priceUsdt = 0;

	    // Assuming we want the first item or the best value based on some criteria
	    for (JsonNode item : arrayNode) {
	        id = item.path("id").asInt();
	        power = item.path("power").asInt();
	        energyEfficiency = item.path("energyEfficiency").asInt();
	        priceUsdt = item.path("priceUsdt").asDouble();
	        // You might want to add logic to find the best value here instead of just taking the last item
	    }

	    return new NftDTO(id, power, energyEfficiency, priceUsdt);
	}

	private String makePostRequest() {
		try {
			CloseableHttpResponse response = httpClient.execute(postToGetNftInfo);
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
		postToGetNftInfo = new HttpPost("https://app.gmt.io/api/nft/marketplace-index");

		// HEADERS
		postToGetNftInfo.setHeader("Content-Type", "application/json");

		String bearerAndTokenStr = getBearerStr();
		postToGetNftInfo.setHeader("Authorization", bearerAndTokenStr);
		// Я хз что это такое и сломается ли оно в будущем
		postToGetNftInfo.setHeader("Baggage",
				"sentry-environment=production,sentry-release=2.89.0%2B4725d93a-client-app,sentry-public_key=db6515d1bbc954486a51cc059c1311b4,sentry-trace_id=b6b073548296434fb2787fc191ea5226");
		postToGetNftInfo.setHeader("Dnt", "1");
		postToGetNftInfo.setHeader("Referer",
				"https://app.gmt.io/marketplace?filters=%7B%22marketplace%22:%5B%22gmt-secondary%22%5D,%22powerRange%22:%7B%22min%22:1,%22max%22:1000%7D,%22energyEfficiencyRange%22:%7B%22min%22:20,%22max%22:22%7D%7D");
		postToGetNftInfo.setHeader("Sec-Ch-Ua",
				"\"Google Chrome\";v=\"129\", \"Not;A?Brand\";v=\"8\", \"Chromium\";v=\"129\"");
				
		postToGetNftInfo.setHeader("Sec-Ch-Ua-Mobile", "?0");
		postToGetNftInfo.setHeader("sec-ch-ua-platform", "Windows");
		postToGetNftInfo.setHeader("sentry-trace", "b6b073548296434fb2787fc191ea5226-a55f61ce8be4b2dc");
		postToGetNftInfo.setHeader("user-agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36");
		postToGetNftInfo.setHeader("x-device-type", "desktop");
		// REQUEST BODY
		String json = buildRequestBody();
		try {
			postToGetNftInfo.setEntity(new StringEntity(json));
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
		
		String token = "03AFcWeA5FcgtMLLp4vvbrof2llHdiZHN9Kn9M3qRVnjPbN4rzA0QHjOuEqof2kr2YGRGRB0tKV3wbP8M-HgWWUN9DZN5O7qLbs_kL2r-VaNJ_c6CULQyLcAdh3KEY8I7FFnLsHtylr239UwxBRfGVHpweucZUMlAI2yxmVJ4O4R1__FH-eUU4xQvU9Q7XTIRlsVBbf6PHWwtO54f7oNxFFKkeyTebxRxLpcHfov7YFEHeVsGRi4W_wd5Nq-83uDIZ-nFcqqjiZMXQQgvscWS9TftEytD4NHq8bkpmfVC-9ncq4nu78PupeYrYtM5MrZZFx52T6MP4TZsZZWLLIgNEFW1qcnuO26cJ7QYArEGUmq0x7Ejy7o7VibCiTqsgqECODJm3HQhxI25wQJ03Efs8olvdrExDU2nxyQJuhArcpia3lFRxJbjPI7t56O8ZJc1WII2sA_Ap9wb0ZE9CIY3YGiBbqyCS1FDTOFGrypi74CRGDlMFEFw5vXzEy21cQylKAsftzIXyT6Rhp16t8di2Py7CILLsC9rdAwQ5T2k_NGng8O0k7ELqMvx3fqv_tmMMUplPhndh_ixipGY8nWVenLMj1C57W38EUYkiUgwwynLQyF53ffC91IoqUfwlht-Bk8Fu9wVLrjzCVIn_PtxgnW8mdJzZPGNgnNyigR61GyPctA52lrwFhYs9dyK79M_nZ2Pdi79HrCUEcfeM6FNMfq44YQ3mJNX00tfvNhTWwWZAccr7LeTpvykn9AnZFpkzgTUV7atiHeBr7-yHh3G0PdYXOL5o25S54jVLfEvVl_NO5ifo6t50V5eKrDCzQJIWCU_62i9Nimgf0FPfR3GxDE7KYiBLbUr14A";
		return "Bearer " + token;
	}

}
