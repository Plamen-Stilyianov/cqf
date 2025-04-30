package project;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by IntelliJ IDEA.
 * User: plamen
 * Date: 25-Jun-2009
 * Time: 22:20:12
 * To change this template use File | Settings | File Templates.
 */
public final class ReadWithScanner {

  public static void main(String... aArgs) throws FileNotFoundException {
    ReadWithScanner parser = new ReadWithScanner("E:\\temp\\pca.csv");
    parser.processLineByLine();
    log("Done.");
  }

  /**
  * @param aFileName full name of an existing, readable file.
  */
  public ReadWithScanner(String aFileName){
    fFile = new File(aFileName);
  }

  /** Template method that calls {@link #processLine(String)}.  */
  public final void processLineByLine() throws FileNotFoundException {
    Scanner scanner = new Scanner(fFile);
    try {
      int i = 0;
      //first use a Scanner to get each line
      while ( scanner.hasNextLine() ){
        processLine( scanner.nextLine() );
        i++;
      }
      log(i);
    }
    finally {
      //ensure the underlying stream is always closed
      scanner.close();
    }
  }

  /**
  * Overridable method for processing lines in different ways.
  *
  * <P>This simple default implementation expects simple name-value pairs, separated by an
  * '=' sign. Examples of valid input :
  * <tt>height = 167cm</tt>
  * <tt>mass =  65kg</tt>
  * <tt>disposition =  "grumpy"</tt>
  * <tt>this is the name = this is the value</tt>
  */
  protected void processLine(String aLine){
    //use a second Scanner to parse the content of each line
    Scanner scanner = new Scanner(aLine);
    scanner.useDelimiter("}");
    if ( scanner.hasNext() ){
      String line = scanner.next();
     // String value = scanner.next();
     // log("Name is : " + quote(name.trim()) + ", and Value is : " + quote(value.trim()) );
      log(line + ",}");
    }
    else {
      log("Empty or invalid line. Unable to process.");
    }
    //(no need for finally here, since String is source)
    scanner.close();
  }

  // PRIVATE //
  private final File fFile;

  private static void log(Object aObject){
    System.out.println(String.valueOf(aObject));
  }

  private String quote(String aText){
    String QUOTE = "'";
    return QUOTE + aText + QUOTE;
  }
}

