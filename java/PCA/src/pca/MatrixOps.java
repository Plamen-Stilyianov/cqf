/*
@Author J E Hasbun 2007
Performs various real matrix operations
@Copyright (c) 2007
This software is to support the Open Source Physics library
http://www.opensourcephysics.org under the terms of the GNU General Public
License (GPL) as published by the Free Software Foundation.
*/

package pca;
import java.text.NumberFormat;

    class MatrixOps {
     NumberFormat nf = NumberFormat.getInstance();
     public void MaxMatrix1D(double A[], int n, int Row[], double Max[]) {
       //finds the maximum elements of a column of A
       //returns the maximum of a column as Max and its array position as Row
       Max[0] =A[0];
       Row[0] =0;
       for (int i = 0; i < n; i++) {
         if (A[i] > Max[0]) {
           Max[0]= A[i];
           Row[0]=i;
         }
       }
      }

      public void MaxMatrix2D(double A[][], int n, int Row[], double Max[]) {
        //finds the maximum elements of each column;  returns the maximums in
        //Max and their array positions in Row
        for (int i = 0; i < n; i++) {
        int k = 0;
        Max[i] = A[k][i];
        Row[i] =k;
        for (int j = 0; j < n; j++) {
          if (A[j][i] > Max[i]){
            Max[i] = A[j][i];
            Row[i] = j;
          }
        }
        k = k + 1;
        }
      }

      public void copy1d(double A[], double B[], int n) {
        //copies 1D vector A onto 1D vector B
         for(int i=0; i<n; i++){
         B[i] = A[i];
         }
      }

      public void copy2d(double A[][], double B[][], int n) {
        //copies 2D vector A onto a 2D vector B
        for (int i = 0; i < n; i++) {
          for (int j = 0; j < n; j++) {
            B[i][j] = A[i][j];
          }
        }
      }

      public void add1d(double A[], double B[], double C[], int n) {
        //adds 1D matrices C=A+B
        for (int i = 0; i < n; i++) {
            C[i] = A[i]+B[i];
        }
      }


      public void add2d(double A[][], double B[][], double C[][], int n) {
        //adds 2D matrices C=A+B
        for (int i = 0; i < n; i++) {
          for (int j = 0; j < n; j++) {
            C[i][j] = A[i][j]+B[i][j];
          }
        }
      }

      public void minus1d(double A[], double B[], double C[], int n) {
        //subtracs 1D matrices C=A-B
        for (int i = 0; i < n; i++) {
            C[i] = A[i]-B[i];
        }
      }


      public void minus2d(double A[][], double B[][], double C[][], int n) {
        //subtracts 2D matrices C=A-B
        for (int i = 0; i < n; i++) {
          for (int j = 0; j < n; j++) {
            C[i][j] = A[i][j]-B[i][j];
          }
        }
      }

      public void abs1d(double A[], double B[], int n) {
        //does the absolute value of a 1D matrix
        for (int i = 0; i < n; i++) {
            B[i] = Math.abs(A[i]);
        }
      }


      public void abs2d(double A[][], double B[][], int n) {
        //does the absolute value of a 2D matrix
        for (int i = 0; i < n; i++) {
          for (int j = 0; j < n; j++) {
            B[i][j] = Math.abs(A[i][j]);
          }
        }
      }

      public void diag(double A[][], double B[][], int n) {
        //finds the diagonal elements of A and puts them into B
        for (int i = 0; i < n; i++) {
          for (int j = 0; j < n; j++) {
            B[i][j] = 0;
          }
          B[i][i]=A[i][i];
        }
      }

      public void unit(double A[][], int n) {
       //finds the diagonal elements of A and puts them into B
       for (int i = 0; i < n; i++) {
         for (int j = 0; j < n; j++) {
           A[i][j] = 0;
         }
         A[i][i]=1.0;
       }
     }

      public void transpose(double A[][], double B[][], int n) {
        //finds the transpose of A and puts it into B
        for (int i = 0; i < n; i++) {
          for (int j = 0; j < n; j++) {
            B[i][j] = A[j][i];
          }
        }
      }

      public double Trace(double A[][], int n) {
        //finds the trace of the 2D matrix A
        double sum=0;
        for (int i = 0; i < n; i++) {
            sum= A[i][i]+sum;
        }
        return sum;
      }

      public double SumDiagElSq(double A[][], int n) {
        //finds the sums of the squared of the diagonal elements of A
        double sum=0;
        for (int i = 0; i < n; i++) {
            sum= A[i][i]*A[i][i]+sum;
        }
        return sum;
      }

      public void printMat1D(double A[], int n, String name) {
      nf.setMaximumFractionDigits(3);
      nf.setMinimumFractionDigits(1);
      System.out.println(name);
        for(int i=0; i<n;i++){
            System.out.print(nf.format(A[i])+"   ");
        }
      System.out.println(" ");
      }

      public void printMat2D(double A[][], int n, String name) {
      nf.setMaximumFractionDigits(3);
      nf.setMinimumFractionDigits(1);
      System.out.println(name);
        for(int i=0; i<n;i++){
          for(int j = 0; j < n; j++ ) {
            System.out.print(nf.format(A[i][j])+"   ");
          }
        System.out.println(" ");
        }
      }

    }
