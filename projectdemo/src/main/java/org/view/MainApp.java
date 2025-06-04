package org.view;

import java.io.IOException;
import java.util.List;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.model.OpenAIClient;
import org.model.QuizQuestion;

public class MainApp extends Application {

  private final List<QuizQuestion> questions =
      List.of(
          new QuizQuestion("What is 2 + 2?", "4"),
          new QuizQuestion("What is the capital of France?", "Paris"),
          new QuizQuestion("What color do you get when you mix red and white?", "Pink"));

  private int currentIndex = 0;

  private Label questionLabel;
  private TextField answerInput;
  private Button submitButton;
  private TextArea explanationArea;

  private OpenAIClient openAIClient;

  @Override
  public void start(Stage primaryStage) {
    // TODO: Replace with your actual OpenAI API key securely
    String apiKey = System.getenv("OPENAI_API_KEY");
    if (apiKey == null || apiKey.isBlank()) {
      showErrorAndExit("OpenAI API key not found in environment variable OPENAI_API_KEY");
      return;
    }
    openAIClient = new OpenAIClient(apiKey);

    questionLabel = new Label();
    answerInput = new TextField();
    answerInput.setPromptText("Enter your answer here...");
    submitButton = new Button("Submit");
    explanationArea = new TextArea();
    explanationArea.setEditable(false);
    explanationArea.setWrapText(true);

    submitButton.setOnAction(e -> handleSubmit());

    VBox root =
        new VBox(
            10,
            questionLabel,
            answerInput,
            submitButton,
            new Label("Explanation:"),
            explanationArea);
    root.setPadding(new Insets(15));

    primaryStage.setScene(new Scene(root, 500, 400));
    primaryStage.setTitle("AI-Enabled Quiz App");
    primaryStage.show();

    loadQuestion();
  }

  private void loadQuestion() {
    if (currentIndex < questions.size()) {
      QuizQuestion q = questions.get(currentIndex);
      questionLabel.setText("Question " + (currentIndex + 1) + ": " + q.getQuestion());
      answerInput.clear();
      explanationArea.clear();
      submitButton.setDisable(false);
    } else {
      questionLabel.setText("Quiz complete! Thank you.");
      answerInput.setDisable(true);
      submitButton.setDisable(true);
    }
  }

  private void handleSubmit() {
    QuizQuestion current = questions.get(currentIndex);
    String studentAnswer = answerInput.getText();

    if (studentAnswer.isBlank()) {
      showAlert(Alert.AlertType.WARNING, "Please enter an answer before submitting.");
      return;
    }

    if (current.isCorrect(studentAnswer)) {
      explanationArea.setText("Correct! Well done.");
    } else {
      explanationArea.setText("Incorrect. Fetching explanation from AI...");
      submitButton.setDisable(true);

      // Run OpenAI request asynchronously to avoid freezing UI
      new Thread(
              () -> {
                try {
                  String explanation =
                      openAIClient.getExplanation(current.getQuestion(), studentAnswer);
                  javafx.application.Platform.runLater(
                      () -> {
                        explanationArea.setText(explanation);
                        submitButton.setDisable(false);
                      });
                } catch (IOException | InterruptedException ex) {
                  javafx.application.Platform.runLater(
                      () -> {
                        explanationArea.setText("Failed to get explanation: " + ex.getMessage());
                        submitButton.setDisable(false);
                      });
                }
              })
          .start();
    }

    currentIndex++;
    // Load next question after a short delay so user can read explanation
    new Thread(
            () -> {
              try {
                Thread.sleep(5000);
              } catch (InterruptedException ignored) {
              }
              javafx.application.Platform.runLater(this::loadQuestion);
            })
        .start();
  }

  private void showAlert(Alert.AlertType type, String message) {
    Alert alert = new Alert(type, message, ButtonType.OK);
    alert.showAndWait();
  }

  private void showErrorAndExit(String message) {
    showAlert(Alert.AlertType.ERROR, message);
    System.exit(1);
  }

  public static void main(String[] args) {
    launch(args);
  }
}
