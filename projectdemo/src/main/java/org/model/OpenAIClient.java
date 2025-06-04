package org.model;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OpenAIClient {

  private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
  private final String apiKey;

  public OpenAIClient(String apiKey) {
    this.apiKey = apiKey;
  }

  /** Requests an explanation from OpenAI for the given question and answer. */
  public String getExplanation(String question, String studentAnswer)
      throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();

    // Construct the prompt for AI to explain the answer
    String prompt =
        "Explain why the correct answer to this question is correct, and the student's answer is incorrect.\n"
            + "Question: "
            + question
            + "\n"
            + "Student's Answer: "
            + studentAnswer
            + "\n";

    // Build JSON payload
    JSONObject message = new JSONObject();
    message.put("role", "user");
    message.put("content", prompt);

    JSONArray messages = new JSONArray();
    messages.put(message);

    JSONObject body = new JSONObject();
    body.put("model", "gpt-3.5-turbo"); // Using the correct model name
    body.put("messages", messages);
    body.put("max_tokens", 200);
    body.put("temperature", 0.7);

    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create(OPENAI_API_URL))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + apiKey)
            .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
            .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    // Check if the response status code indicates success
    if (response.statusCode() != 200) {
      return "Error: API returned status code " + response.statusCode() + "\n" + response.body();
    }

    try {
      JSONObject responseJson = new JSONObject(response.body());
      JSONArray choices = responseJson.getJSONArray("choices");
      if (choices.length() > 0) {
        JSONObject firstChoice = choices.getJSONObject(0);
        JSONObject messageObj = firstChoice.getJSONObject("message");
        return messageObj.getString("content").trim();
      }
    } catch (JSONException e) {
      return "Error parsing API response: "
          + e.getMessage()
          + "\nResponse body: "
          + response.body();
    }

    return "No explanation available.";
  }
}
