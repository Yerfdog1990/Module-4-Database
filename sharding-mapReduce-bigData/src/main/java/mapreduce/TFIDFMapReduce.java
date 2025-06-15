package mapreduce;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** TF => Term Frequency IDF => Inverse Document Frequency TF/IDF ratio (KPI) */
public class TFIDFMapReduce {
  private static final List<String> DOCUMENTS =
      List.of(
          "the cat in the hat",
          "the quick brown fox jumps over the lazy dog",
          "the cat likes the mouse",
          "the dog barks at the cat");

  private static String[] splitDocumentIntoTerms(String document) {
    return document.split("\\s+");
  }

  // 1 - Compute TF for each document
  // Ratio of a term in a document -> number of occurrences/ number of terms
  public static Map<String, Map<String, Double>> computeTF(List<String> documents) {
    return documents.stream()
        .collect(
            Collectors.toMap( // aggregate by document / List.of terms
                Function.identity(),
                doc -> {
                  List<String> terms = Arrays.asList(splitDocumentIntoTerms(doc));
                  Map<String, Long> termFreq = // Calculate occurrences of each term
                      terms.stream()
                          .collect(
                              Collectors.groupingBy(Function.identity(), Collectors.counting()));
                  int numberOfTerms = termFreq.size();
                  return termFreq.entrySet().stream() // Calculate the ratio of each term
                      .collect(
                          Collectors.toMap(
                              Map.Entry::getKey,
                              entry -> (double) entry.getValue() / numberOfTerms));
                }));
  }

  // 2 - Compute the IDF
  public static Map<String, Double> computeIDF(List<String> documents) {
    int numberOfDocuments = documents.size();
    // List all the terms in all documents
    List<String> allTermsInAllDocuments =
        documents.stream()
            .flatMap(doc -> Arrays.asList(splitDocumentIntoTerms(doc)).stream())
            .distinct()
            .toList();
    // 3 - Calculate the frequency of each term in all the documents
    Map<String, Long> numDocsContainingEachTerm =
        allTermsInAllDocuments.stream()
            .collect(
                Collectors.toMap(
                    Function.identity(),
                    term ->
                        documents.stream()
                            .filter(
                                doc -> Arrays.asList(splitDocumentIntoTerms(doc)).contains(term))
                            .count())); // Number of documents containing each term

    // 4 - Calculate frequency (IDF) -> logarithmic scale
    return numDocsContainingEachTerm.entrySet().stream()
        .collect(
            Collectors.toMap(
                Map.Entry::getKey,
                entry -> Math.log10((double) numberOfDocuments / (1 + entry.getValue()))));
  }

  // 5 - computeTFIDF
  public static Map<String, Map<String, Double>> computeTFIDF(List<String> documents) {
    Map<String, Map<String, Double>> tf = computeTF(documents);
    Map<String, Double> idf = computeIDF(documents);

    // Multiply TF by IDF for each term in each document
    return tf.entrySet().stream()
        .collect(
            Collectors.toMap(
                Map.Entry::getKey, // Preserve the terms as the key in the map
                docEntry -> {
                  Map<String, Double> tfIdf =
                      docEntry.getValue().entrySet().stream()
                          .collect(
                              Collectors.toMap( // All terms, term is the key
                                  Map.Entry::getKey,
                                  termEntry ->
                                      termEntry.getValue()
                                          * idf.getOrDefault(termEntry.getKey(), 0.0)));
                  return tfIdf; // Calculate TF/IDF
                }));
  }

  // Main method
  public static void main(String[] args) {
    System.out.println("Documents: ");
    DOCUMENTS.forEach(System.out::println);

    // Compute TF/IDF
    Map<String, Map<String, Double>> tfIdf = computeTFIDF(DOCUMENTS);
    System.out.println("\nTF-IDF scores: ");
    System.out.println("<------------------------------------------>");
    tfIdf.forEach(
        (doc, terms) -> {
          System.out.println("Document: " + doc);
          terms.forEach((term, tfIdfScore) -> System.out.println("\t" + term + ": " + tfIdfScore));
        });
  }
}
