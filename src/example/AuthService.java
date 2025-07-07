package example;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class AuthService {
    private final CloseableHttpClient httpClient = HttpClients.createDefault();
    private final String url = "https://app.gmt.io/api/auth/login"; // Укажите ваш URL
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TokenDTO getTokenInfo() {
        String jsonResponse = makePostRequest();

        if (jsonResponse == null || jsonResponse.isEmpty()) {
            // Логируем или обрабатываем случай, когда ответ пустой
            return null;
        }

        JsonNode rootNode;

        try {
            rootNode = objectMapper.readTree(jsonResponse);
        } catch (JsonMappingException e) {
            ExceptionsHandler.handleHttpException(e);
            return null;
        } catch (Exception e) { // Обработка всех исключений
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
        HttpPost postRequest = new HttpPost(url);
        postRequest.setHeader("Content-Type", "application/json");

        String jsonInputString = "{\"username\":\"anastast2015@gmail.com\", \"password\":\"Makarov_88\"}";

        try {
            postRequest.setEntity(new StringEntity(jsonInputString));
            CloseableHttpResponse response = httpClient.execute(postRequest);

            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("HTTP Status Code: " + statusCode);

            HttpEntity entity = response.getEntity();
            String responseBody = EntityUtils.toString(entity);
            System.out.println("Response from server: " + responseBody); // Логируем ответ

            return responseBody;
        } catch (Exception e) {
            ExceptionsHandler.handleHttpException(e);
            return "";
        }
    }

    public static void main(String[] args) {
        AuthService authService = new AuthService();
        TokenDTO tokenDTO = authService.getTokenInfo();

        if (tokenDTO != null) {
            System.out.println("Полученный токен: " + tokenDTO);
        } else {
            System.out.println("Не удалось получить токен.");
        }
    }
}
