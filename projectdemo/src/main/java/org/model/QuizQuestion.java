package org.model;

public class QuizQuestion {
  private final String question;
  private final String correctAnswer;

  public QuizQuestion(String question, String correctAnswer) {
    this.question = question;
    this.correctAnswer = correctAnswer;
  }

  public String getQuestion() {
    return question;
  }

  public String getCorrectAnswer() {
    return correctAnswer;
  }

  public boolean isCorrect(String answer) {
    return correctAnswer.equalsIgnoreCase(answer.trim());
  }
}
