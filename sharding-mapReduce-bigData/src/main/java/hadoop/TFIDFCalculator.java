package hadoop;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/*
How it works:
Job 1 (TF - Term Frequency): The first MapReduce job calculates the frequency of each term for each document.
Job 2 (DF - Document Frequency and IDF): The second job calculates how many documents contain each term and computes the inverse document frequency (IDF).
Job 3 (TF-IDF Calculation): The final job multiplies the TF of each term by its IDF to produce the TF-IDF score for that term in each document.

Running the Jobs:
First, run the TermFrequency job on your dataset.
Then, run the DocumentFrequency job on the output of the TF job.
Finally, run the TFIDFCalculator job on the output of the previous two jobs.
 */
public class TFIDFCalculator {

  public static class TFIDFMapper extends Mapper<Object, Text, Text, Text> {

    @Override
    protected void map(Object key, Text value, Context context)
        throws IOException, InterruptedException {
      // Input is from the TF output: "docID term TF"
      String[] parts = value.toString().split("\\s+");
      if (parts.length < 3) {
        return;
      }

      String docID = parts[0];
      String term = parts[1];
      String tf = parts[2]; // Term frequency value

      context.write(new Text(term), new Text(docID + ":" + tf));
    }
  }

  public static class TFIDFReducer extends Reducer<Text, Text, Text, DoubleWritable> {

    private int totalDocuments = 0;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
      // In the setup, we could calculate or set the total number of documents in the corpus
      // Here, you can fetch the number of documents from a shared resource like HDFS
      totalDocuments = context.getConfiguration().getInt("totalDocuments", 1);
    }

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context)
        throws IOException, InterruptedException {
      int documentCount = 0;
      // First, calculate the number of documents containing this term (document frequency)
      for (Text val : values) {
        documentCount++;
      }

      // Calculate IDF
      double idf = Math.log((double) totalDocuments / (documentCount + 1));

      // For each document that contains this term, calculate the TF-IDF score
      for (Text val : values) {
        String[] docAndTF = val.toString().split(":");
        String docID = docAndTF[0];
        double tf = Double.parseDouble(docAndTF[1]);

        // Calculate TF-IDF score
        double tfidf = tf * idf;
        context.write(new Text(docID + " " + key), new DoubleWritable(tfidf));
      }
    }
  }

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    conf.setInt("totalDocuments", 4); // Set the total number of documents in the corpus

    Job job = Job.getInstance(conf, "TF-IDF Calculator");

    job.setJarByClass(TFIDFCalculator.class);
    job.setMapperClass(TFIDFMapper.class);
    job.setReducerClass(TFIDFReducer.class);

    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(DoubleWritable.class);

    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));

    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
