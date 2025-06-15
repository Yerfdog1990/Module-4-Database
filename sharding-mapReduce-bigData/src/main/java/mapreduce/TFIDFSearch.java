package mapreduce;

import java.util.*;

public class TFIDFSearch {

  public static void main(String[] args) {
    // Ensure the user provides search terms as command-line arguments
    if (args.length == 0) {
      System.out.println("Please provide search terms as arguments.");

      return;
    }

    // Capture the search terms
    List<String> searchTerms = Arrays.asList(args);

    // Sample documents (same as from the previous TF-IDF example)
    List<String> documents =
        Arrays.asList(
            "the cat in the hat",
            "the quick brown fox jumps over the lazy dog",
            "the cat likes the mouse",
            "the dog barks at the fox");

    // Compute TF-IDF scores using the previous TFIDFMapReduce class
    Map<String, Map<String, Double>> tfidf = TFIDFMapReduce.computeTFIDF(documents);

    // Map to store the document relevance scores based on the search terms
    Map<String, Double> documentScores = new HashMap<>();

    // For each document, calculate the total TF-IDF score for the given search terms
    for (String doc : documents) {
      Map<String, Double> termScores = tfidf.get(doc);
      double totalScore = 0.0;

      // Sum up the TF-IDF scores of the search terms for this document
      for (String term : searchTerms) {
        totalScore += termScores.getOrDefault(term.toLowerCase(), 0.0); // Case-insensitive search
      }

      // Store the total score for this document
      documentScores.put(doc, totalScore);
    }

    // Sort the documents by their relevance scores in descending order
    List<Map.Entry<String, Double>> sortedDocuments =
        documentScores.entrySet().stream()
            .sorted((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()))
            .toList();

    // Display the search results
    System.out.println("Search results for terms: " + String.join(", ", searchTerms));
    sortedDocuments.forEach(
        entry -> {
          if (entry.getValue() > 0) {
            System.out.println(
                "Document: " + entry.getKey() + " | Relevance Score: " + entry.getValue());
          }
        });
  }
}
