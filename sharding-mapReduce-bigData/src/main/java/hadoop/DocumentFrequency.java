package hadoop;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class DocumentFrequency {

  public static class DFMapper extends Mapper<Object, Text, Text, IntWritable> {
    private static final IntWritable one = new IntWritable(1);
    private final Text word = new Text();

    @Override
    protected void map(Object key, Text value, Context context)
        throws IOException, InterruptedException {
      // Key = document id and term frequency
      String line = value.toString();
      String[] parts = line.split(" ", 2); // Assuming docID and term are space-separated
      if (parts.length < 2) {
        return;
      }
      String term = parts[1]; // Get the term only
      word.set(term);
      context.write(word, one);
    }
  }

  public static class DFReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context)
        throws IOException, InterruptedException {
      int documentCount = 0;
      for (IntWritable val : values) {
        documentCount += val.get();
      }
      context.write(key, new IntWritable(documentCount));
    }
  }

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();

    // Set the security configuration to avoid fetching the subject
    conf.set("hadoop.security.authentication", "simple");
    System.setProperty("HADOOP_USER_NAME", "your_username"); // Add your username as required
    System.setProperty("hadoop.home.dir", "/"); // Prevent possible Hadoop home dir issues

    // Create a new job instance
    Job job = Job.getInstance(conf, "document frequency");
    job.setJarByClass(DocumentFrequency.class);
    job.setMapperClass(DFMapper.class);
    job.setReducerClass(DFReducer.class);

    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);

    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));

    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
