/*
@Author J E Hasbun 2007
Uses the Jacobi method in finding the eigenvalues and eigenvectors of a
symmetric matrix
Ref: Numerical Methods using MATLAB 3rd ed. J H Mathews and K D Fink
(Prentice Hall, 1999).
@Copyright (c) 2007
This software is to support the Open Source Physics library
http://www.opensourcephysics.org under the terms of the GNU General Public
License (GPL) as published by the Free Software Foundation.
*/

package pca;
import java.text.NumberFormat;

  class EigenJacobi{
    MatrixOps matrixops=new MatrixOps();

    public void EigenJacobi(double A[][],double[][] DiagD,double[][] V,
                     int NumColRow, double tol, int icmax) {
    /*
    Finds the eigenvalues and eigenvectors associated with the symmetric matrix A
    Input: A of size NumColRow x NumColRow and symmetric
           tol is the tolerance level of convergence
           icmax the maximum iterations number
    Output: eigenvalues as the diagonal of the returned matrix DiagD
            eigenvectors V
    */

     double t,c,s;
     int p,q,icount,state;
     int [] ColRowOfElMax, RowOfElMax;
     double [][] Arrtemp,D;
     double [] MaxElColRow, MaxElRow;
     MaxElColRow=new double[NumColRow];
     ColRowOfElMax=new int[NumColRow];
     RowOfElMax=new int[1];
     MaxElRow=new double[1];
     D=new double [NumColRow][NumColRow];
     Arrtemp=new double[NumColRow][NumColRow];
     double [][] DminusDiagD=new double [NumColRow][NumColRow];
     double [][] AbsDminusDiagD=new double [NumColRow][NumColRow];
     double [][] Rot=new double[2][2];
     double [][] RotT=new double[2][2];
     matrixops.unit(V,NumColRow);//makes V into a unit matrix
     matrixops.copy2d(A,D,NumColRow);  //copies A to D
     matrixops.diag(D,DiagD,NumColRow);//outputs DiagD=diagonal of D
     matrixops.minus2d(D,DiagD,DminusDiagD,NumColRow);     //does D-DiagD
     matrixops.abs2d(DminusDiagD,AbsDminusDiagD,NumColRow);//does abs(D-DiagD)
     matrixops.MaxMatrix2D(AbsDminusDiagD,NumColRow,ColRowOfElMax,MaxElColRow);
                                            //outputs ColRowOfElMax,MaxElColRow
     matrixops.MaxMatrix1D(MaxElColRow,NumColRow,RowOfElMax,MaxElRow);
                                            //outputs RowOfElMax, MaxElRow
     q=RowOfElMax[0];
     p=ColRowOfElMax[q];
     icount=0;
     state=1;
     while(state==1 && icount<icmax){
       icount = icount + 1;
       if (D[q][q] == D[p][p]) { //check to prevent t from diverging
         D[q][q] = D[p][p] + 1.e-10;
       }
       t = D[p][q] / (D[q][q] - D[p][p]);
       c = 1 / Math.sqrt(t * t + 1);
       s = c * t;
       Rot[0][0] = c;
       Rot[0][1] = s;
       Rot[1][0] = -s;
       Rot[1][1] = c;
       matrixops.transpose(Rot, RotT, 2);//RotT=transpose(Rot)
       for (int i = 0; i < NumColRow; i++) {
         Arrtemp[p][i] = RotT[0][0] * D[p][i] + RotT[0][1] * D[q][i];
         Arrtemp[q][i] = RotT[1][0] * D[p][i] + RotT[1][1] * D[q][i];
         D[p][i] = Arrtemp[p][i];
         D[q][i] = Arrtemp[q][i];
       }
       for (int i = 0; i < NumColRow; i++) {
         Arrtemp[i][p] = D[i][p] * Rot[0][0] + D[i][q] * Rot[1][0];
         Arrtemp[i][q] = D[i][p] * Rot[0][1] + D[i][q] * Rot[1][1];
         D[i][p] = Arrtemp[i][p];
         D[i][q] = Arrtemp[i][q];
       }
       for (int i = 0; i < NumColRow; i++) {
         Arrtemp[i][p] = V[i][p] * Rot[0][0] + V[i][q] * Rot[1][0];
         Arrtemp[i][q] = V[i][p] * Rot[0][1] + V[i][q] * Rot[1][1];
         V[i][p] = Arrtemp[i][p];
         V[i][q] = Arrtemp[i][q];
       }
     //find the new q, p element array values that need to be changed
       matrixops.diag(D, DiagD, NumColRow); //outputs DiagD=diagonal of D
       matrixops.minus2d(D, DiagD, DminusDiagD, NumColRow); //does D-DiagD
       matrixops.abs2d(DminusDiagD, AbsDminusDiagD, NumColRow); //does abs(D-DiagD)
       matrixops.MaxMatrix2D(AbsDminusDiagD, NumColRow, ColRowOfElMax, MaxElColRow);
                                               //outputs ColRowOfElMax,MaxElColRow
       matrixops.MaxMatrix1D(MaxElColRow, NumColRow, RowOfElMax, MaxElRow);
                                               //outputs RowOfElMax, MaxElRow
       q = RowOfElMax[0];
       p = ColRowOfElMax[q];
       if(Math.abs(D[p][q]) <
       tol*Math.sqrt(matrixops.SumDiagElSq(DiagD,NumColRow))/NumColRow){
       state=0;}
     }
     //matrixops.printMat2D(A,NumColRow,"EigenJacobi-Input Matrix");
     //matrixops.printMat2D(DiagD,NumColRow,"EigenJacobi-Eigenvalues");
     //matrixops.printMat2D(V,NumColRow,"EigenJacobi-Eigenvectors");
     if(icount>=icmax){
       System.out.println("iterations limit reached="+icount);
     }
    }
  }