/*
 * Created on Jul 15, 2004
 */
package org.getopt.stempel;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.StringTokenizer;

/**
 * This class is a simple benchmark for the stemmer. You need to
 * provide it with a test file, containing words to stem. The file
 * will be processed three times, and at the end the application will
 * print a short report. NOTE: the file encoding is assumed to be UTF-8.
 * 
 * @author Andrzej Bialecki &lt;ab@getopt.org&gt;
 */
public class Benchmark {

  /** Number of times to run the benchmark. */
  public static final int COUNT = 3;
  
  /**
   * Provide the name of the test file as the first argument. The test
   * file should contain just the words to stem, any other tokens will
   * skew the results. Words must be separated by space(s) and/or newlines.
   * <p>The second, optional argument is a resource path to the
   * stemmer table to be used instead of the default one.
   * <p>Timing includes only the time it takes for the
   * {@link Stemmer#stem(String, boolean)} method to return a value.
   * {@link Stemmer#MIN_LENGTH} is set to 0 in order to process all input
   * tokens.
   */
  public static void main(String[] args) throws Exception {
    BufferedReader br;
    Stemmer stemmer;
    
    if (args.length > 1) stemmer = new Stemmer(args[1]);
    else stemmer = new Stemmer();
    stemmer.MIN_LENGTH = 0;
    
    long[] total = new long[COUNT];
    long[] missed = new long[COUNT];
    long[] time = new long[COUNT];
    long delta;
    for (int i = 0; i < COUNT; i++) {
      total[i] = 0L;
      missed[i] = 0L;
      time[i] = 0L;
      br = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "UTF-8"));
      String line, res, token;
      while ((line = br.readLine()) != null) {
        StringTokenizer st = new StringTokenizer(line);
        while (st.hasMoreTokens()) {
          token = st.nextToken().toLowerCase();
          delta = System.currentTimeMillis();
          res = stemmer.stem(token, false);
          delta = System.currentTimeMillis() - delta;
          time[i] += delta;
          total[i]++;
          if (res == null) missed[i]++;
        }
      }
      br.close();
    }
    System.out.println("--------- Stemmer benchmark report: -----------");
    System.out.println("Stemmer table:  " + stemmer.getTableResPath());
    System.out.println("Input file:     " + args[0]);
    System.out.println("Number of runs: " + COUNT);
    System.out.print("\n RUN NUMBER:\t");
    double[] hitrate = new double[COUNT];
    double[] missrate = new double[COUNT];
    double[] wps = new double[COUNT];
    double[] uspw = new double[COUNT];
    for (int i = 0; i < COUNT; i++) {
      System.out.print("\t" + (i + 1));
      hitrate[i] = (double)(total[i] - missed[i]) / (double)total[i];
      missrate[i] = (double)missed[i] / (double)total[i];
      wps[i] = (double)total[i] / (double)time[i] * 1000.0;
      uspw[i] = (double)time[i] * 1000.0 / (double)total[i];
    }
    System.out.println();
    printLongArray("Total input words", total);
    printLongArray("Missed output words", missed);
    printLongArray("Time elapsed [ms]", time);
    printDoubleArray("Hit rate percent", hitrate, "#00.00%");
    printDoubleArray("Miss rate percent", missrate, "#00.00%");
    printDoubleArray("Words per second", wps, "00000");
    printDoubleArray("Time per word [us]", uspw, "#0.00");
  }
  
  public static void printLongArray(String head, long[] res) {
    System.out.print(" " + head);
    for (int i = 0; i < res.length; i++) {
      System.out.print("\t" + res[i]);
    }
    System.out.println();
  }

  public static void printDoubleArray(String head, double[] res, String fmt) {
    DecimalFormat df = new DecimalFormat(fmt);
    System.out.print(" " + head);
    for (int i = 0; i < res.length; i++) {
      System.out.print("\t" + df.format(res[i]));
    }
    System.out.println();
  }
}
