package project;

import com.sun.org.apache.xml.internal.resolver.helpers.Debug;

import java.io.FileNotFoundException;
import java.io.File;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: plamen
 * Date: 09-Jul-2009
 * Time: 00:43:33
 * To change this template use File | Settings | File Templates.
 */
public class DataFile {
      private static double [][] matrix;
      private static int sizeCol;
      private static int sizeRow;
      private static Debug debug = new Debug();
      private DataFile(){}

      public static double[][] getMatrix(String file) throws FileNotFoundException {

          matrix = readFile(file);
          return matrix;
      }

      private static double[][] readFile(String fileIn) throws FileNotFoundException {
          File file = new File(fileIn);
          sizeRow = getLineRows(file);
          sizeCol = getLineCols(file);

          double [][] doublesMtx = new double[sizeRow][sizeCol];

          StringBuilder builder = new StringBuilder();


          Scanner scanner = new Scanner(file);
          scanner.useDelimiter("},");
          Scanner scannerLine;
          String strLine = null;
          String trimLine = null;

          int i = 0;
          int j = 0;
          try{
              //first use a Scanner to get each line
              while ( scanner.hasNextLine() ){
                    j=0;
                    strLine = scanner.nextLine();
                    trimLine = strLine.substring(strLine.indexOf("{") + 1, strLine.indexOf("},"));
                    scannerLine = new Scanner(trimLine);
                    scannerLine.useDelimiter(",");
                    while(scannerLine.hasNext()){
                       doublesMtx[i][j] = new Double(scannerLine.next());
                        j++;
                    }

                i++;
              }
          } catch(Exception e){
              System.out.println("sizeCol: " + sizeCol);
              System.out.println("sizeRow: " + sizeRow);
              System.out.println(strLine);
              System.out.println(trimLine);
              System.out.println("j: " + j + " and i: " + i);
              System.out.println(doublesMtx.length);
              e.printStackTrace();

          }
          return doublesMtx;
      }

    private static int getLineCols(File file) throws FileNotFoundException {
        int size = 0;
        Scanner scanner = new Scanner(file);
        scanner.useDelimiter("},");
        String line = null;
        if ( scanner.hasNextLine() ){
            line = scanner.nextLine();
        }
        String s = null;
        String lineIn = line.substring(line.indexOf("{")+1,line.indexOf("},"));
        StringTokenizer tokenizer = new StringTokenizer(lineIn,",");
        while(tokenizer.hasMoreElements()){
             s = tokenizer.nextToken();
             size++;
        }
        return size;
    }

    private static int getLineRows(File file) throws FileNotFoundException {
        int size = 0;

        Scanner scanner = new Scanner(file);
        scanner.useDelimiter("},");
        String s = null;
        while ( scanner.hasNextLine() ){
            s = scanner.nextLine();
            size++;
        }
        return size;
    }
}

