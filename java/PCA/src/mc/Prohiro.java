package mc;

/**
 * @(#) Prohiro.java 1.0 24/08/2007
 *
 * Copyright (c) 2007 Mälardalen University
 * Högskoleplan Box 883, 721 23 Västerås, Sweden.
 * All Rights Reserved.
 *
 * The copyright to the computer program(s) herein
 * is the property of Mälardalen University.
 * The program(s) may be used and/or copied only with
 * the written permission of Mälardalen University
 * or in accordance with the terms and conditions
 * stipulated in the agreement/contract under which
 * the program(s) have been supplied.
 *
 * Description: Class essential for the realization of Java Applet HJM
 * @version 1.0 Aug 07
 * @author Michail Kalavrezos
 * Mail: michail_kalavrezos@yahoo.se
 */

import java.util.Random;

public class Prohiro {

    //The class has two static variables
    public double[] knowntime;
    public double[] knownrate;

    // the class has one constructor
    public Prohiro() {
    }

    int i;
    int p = 0;
    int s = 0;
    int r = 0;
    int n = 0;
    //number of days, we use the convention 1 year=360 days
    double deltati;
    double[] discreteforwardrate;
    double[] fhatsim;
    double[] taxi;
    double[] fHatSpot;
    //The class has three methods

    //the following method creates an array of double numbers representing
    //the simulated spot rates for the whole time period of the cap

    public double[] getTimePoints(int periodscovered, int simulationintervals, int resetperiod) {
        this.p = periodscovered;
        this.s = simulationintervals;
        this.r = resetperiod;
        double[] timePoints = new double[p * s + 2];
        // the simulation interval's length ,we assume that we have at least one  caplet
        double deltati = r / s;
        int l;
        for (l = 0; l < p * s + 2; l++) {
            //this is the array of time points expressed in years that we plot
            timePoints[l] = (l * deltati) / 360;
        }
        return (timePoints);
    }

    //this method returns the simulated spot rates for the whole time period of the cap
    public double[] getfHatSpot(int periodscovered, int simulationintervals, int resetperiod, double sigmaone,
                                double sigmatwo, double alphaone, double alphatwo, int numberofiterations,
                                double[] knownTimePoints, double[] forwardRates
    ) {
        this.n = numberofiterations;
        this.p = periodscovered;
        this.s = simulationintervals;
        this.r = resetperiod;
        int l = 0;
        this.deltati = r / s;
        double[] matchedforward = new double[(s * p) + 2];
        double[] moments = new double[s * p + 2];
        fHatSpot = new double[(s * p) + 1];
        discreteforwardrate = new double[s * p + 1];

        // Spline interpolation
        // We have points knownTimePoints[i] on x axis
        // and points forwardRates[i] on y axis
        // first, we estimate the derivative at the point standardTimes[0]
        double ypFirst = (forwardRates[1] - forwardRates[0]) / (knownTimePoints[1] - knownTimePoints[0]);

        // create spline object
 //       Spline1 spline = new Spline1(knownTimePoints, forwardRates, ypFirst, 0.0);

        // fill arrays
        discreteforwardrate[0] = forwardRates[0];
        for (int i = 1; i < s * p + 1; i++) {
            moments[i] = i * deltati / 360;
 //           discreteforwardrate[i] = spline.splint(moments[i]);
        }

        //create an array of the discrete forward rate from the  matched forward rate at the simulation points
        fHatSpot[0] = discreteforwardrate[0];

        //create the arrays where the sigmas are going to be stored(or maybe locally !!!!)
        double[][] sigmaonehat = new double[s * p + 1][s * p + 1];
        sigmaonehat[0][0] = 0.00;
        double[][] sigmatwohat = new double[s * p + 1][s * p + 1];
        sigmatwohat[0][0] = 0.00;
        double summaone = 0.00;
        double summatwo = 0.00;
        double[][] miouonehat = new double[s * p + 1][s * p + 1];
        double[][] mioutwohat = new double[s * p + 1][s * p + 1];
        taxi = new double[s * p + 1];

        //calculate the sigmas
        for (i = 1; i < s * p + 1; i++) {
            Random generator = new Random();
            double gen = generator.nextGaussian();
            int k = 0;
            for (k = i; k < s * p + 1; k++) {
                sigmaonehat[i - 1][k] = sigmaone * (Math.exp(-alphaone * (k - i + 1) * deltati / 360));
                summaone = summaone + sigmaonehat[i - 1][k];
                miouonehat[i - 1][k] = ((deltati / 360) / 2) * ((Math.pow(summaone, 2)) - (1 / 2) * (Math.pow((summaone - sigmaonehat[i - 1][k]), 2)));
                sigmatwohat[i - 1][k] = sigmatwo * (Math.exp(-alphatwo * (k - i + 1) * deltati / 360));
                summatwo = summatwo + sigmatwohat[i - 1][k];
                mioutwohat[i - 1][k] = ((deltati / 360) / 2) * ((Math.pow(summatwo, 2)) - (1 / 2) * (Math.pow((summatwo - sigmatwohat[i - 1][k]), 2)));

                //calculate the first row of the forward rates after f(i- 1, l)and save
                // the very first each time, which is an instantaneous rate f(l, l)
                //We can do the Monte carlo simulations here
                int M = s * p + 1;
                fhatsim = new double[M - i];
                //in the following loop we do the simulations
                if (k == i && i == 1) {
                    double tr = 0.00;
                    for (int d = 1; d < n + 1; d++) {
                        fhatsim[0] = discreteforwardrate[k] + (deltati / 360) * (miouonehat[i - 1][k] + mioutwohat[i - 1][k]) +
                                (Math.sqrt(deltati / 360) * (sigmaonehat[i - 1][k] * Math.random() + sigmatwohat[i - 1][k] * gen));
                        tr += fhatsim[0];
                    }
                    fHatSpot[i] = ((double) tr) / n;
                } else if (k > i && i == 1) {
                    double kr = 0.00;
                    for (int d = 1; d < n + 1; d++) {
                        fhatsim[k - i] = discreteforwardrate[k] + (deltati / 360) * (miouonehat[i - 1][k] + mioutwohat[i - 1][k]) +
                                (Math.sqrt(deltati / 360) * (sigmaonehat[i - 1][k] * Math.random() + sigmatwohat[i - 1][k] * gen));
                        kr += fhatsim[k - i];
                    }

                    //taxi[] is the array whose values are recalculated within each simulation loop
                    taxi[k] = ((double) kr) / n;
                } else if (k == i && k > 1) {
                    double tr = 0.00;
                    for (int d = 1; d < n + 1; d++) {
                        fhatsim[0] = taxi[k] + (deltati / 360) * (miouonehat[i - 1][k] + mioutwohat[i - 1][k]) +
                                (Math.sqrt(deltati / 360) * (sigmaonehat[i - 1][k] * Math.random() + sigmatwohat[i - 1][k] * gen));
                        //tr=tr+fhatsim[0];
                        //fhatsim[0]+=fhatsim[0];
                        tr += fhatsim[0];
                    }
                    fHatSpot[i] = ((double) tr) / n;
                } else if (k > i && i > 1) {
                    double kr = 0.00;
                    for (int d = 1; d < n + 1; d++) {
                        fhatsim[k - i] = taxi[k] + (deltati / 360) * (miouonehat[i - 1][k] + mioutwohat[i - 1][k]) +
                                (Math.sqrt(deltati / 360) * (sigmaonehat[i - 1][k] * Math.random() + sigmatwohat[i - 1][k] * gen));
                        kr += fhatsim[k - i];
                    }
                    taxi[k] = ((double) kr) / n;
                }
            }
        }
        return (fHatSpot);
    }

    //the following method returns the price of the cap with principal=1
    public double getPrice(double[] fHatSpot, int periodscovered, int resetperiod, int simulationintervals, double caprate) {
        double c = caprate;
        int r = resetperiod;
        this.p = periodscovered;
        this.s = simulationintervals;
        this.deltati = r / s;
        double PV = 0.00;
        double[] presentValue = new double[p + 1];
        presentValue[0] = 0.00;
        double[] D = new double[p + 1];
        D[0] = 0.00;
        double[] Fhat = new double[p + 1];
        Fhat[0] = fHatSpot[0] / 100;
        int i = 0;
        for (i = 1; i < p + 1; i++) {

            //the following statement assigns the simulated spot rate on the reset day to the array Fhat[i]
            Fhat[i] = fHatSpot[i * s] / 100;

            //we need to create a double variable to express the sum
            //of the simulated spot rates between two reset days
            double sumOfSpot = 0.00;
            double[] P = new double[p + 1];

            //A loop to add the spots, for each caplet the number of
            //spots is equal to (no of caplet*simulation intervals)
            //in other words this is the discount factor for each (discounted
            //at the maturity) caplet payment
            for (int k = 0; k < (i * s); k++) {
                sumOfSpot = sumOfSpot + fHatSpot[k];
            }

            P[i] = 1 * ((double) r) / 360 * (Fhat[i] - c * 0.01) / (1 + ((double) Fhat[i] * r) / 360);
            D[i] = Math.exp(-(sumOfSpot / 100) * deltati / 360);

            //this is the present value at current time zero
            presentValue[i] = Math.max((P[i] * D[i]), 0);
            PV = PV + presentValue[i];
        }
        return (PV);
    }
}
