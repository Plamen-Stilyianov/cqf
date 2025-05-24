import java.util.Random;
import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * Author: Plamen Stilyianov
 * Title: MSc Financial Markets with Information Systems
 * Date: 02-Sep-2005
 * Time: 00:42:00
 * To change this template use File | Settings | File Templates.
 * @noinspection ALL
 */
public class ArithMonteCarloGeoContVar {
    // An European Arithmetic Asian Option with a Geometric Asian Option Control Variate

    public double valueMonteCarlo = 0.0;

    public ArithMonteCarloGeoContVar(double S, double K, double r, double div, double sig, double T) {
        int M = 100;    // Simulations
        int N = 1000;     // Time step

        valueMonteCarlo = modelMonteCarlo(S, K, r, div, sig, T, M, N);

    }

    private double modelMonteCarlo(double S, double K, double r, double div, double sig, double T, int M, int N){

        // Constant
        double dt = T / N;    //  Time step
        double nudt = (r - div - 0.5 * sig * sig) * dt;     //
        double sigsdt = sig * Math.sqrt(dt);   //

        double delta = 0.0;
        double St = S;
        double sumSt = 0.0;
        long productSt = 1;
        double rndNum = 0.0;
        double arithAvgOpt = 0.0;
        double geomAvgOpt = 0.0;
        double diffOVal = 0.0;
        double diffOValSum = 0.0;
        double diffOValSumSq = 0.0;
        double powProd = 1 / N;

        for (int j = 0; j < M; j++) {

            St = S;
            sumSt = 0.0;
            productSt = 1;

            for (int i = 0; i < N; i++) {
                rndNum = randPolarRejc();
                St = St * Math.exp(nudt + (sigsdt * rndNum));
                sumSt = sumSt + St;
                productSt = (long) (productSt* St);
            }

            arithAvgOpt = sumSt / N;
            geomAvgOpt = Math.pow(productSt, powProd);
            diffOVal = Math.max(0.0, arithAvgOpt - K) - Math.max(0.0, geomAvgOpt - K);
            diffOValSum = diffOValSum + diffOVal;
            diffOValSumSq = diffOValSumSq + Math.pow(diffOVal, 2);
            delta = delta + arithAvgOpt;

        }
        System.out.println(productSt);
        System.out.println(sumSt);
        System.out.println(arithAvgOpt);
        System.out.println(geomAvgOpt);
        System.out.println(diffOVal);
        System.out.println(diffOValSum);
        System.out.println(diffOValSumSq);

        double portfolioValue = (diffOValSum / M) * Math.exp(-r * T);
        System.out.println(portfolioValue);
        double vega = (diffOValSum / 2 * sig) * Math.exp(-r * T);
        double sdDev = Math.sqrt(diffOValSumSq - diffOValSum * (diffOValSum / M)) * Math.exp((-2 * r * T) / (M - 1));
        double sdErr = sdDev * Math.sqrt(M);
        double callValue = portfolioValue + geometricAsianOption(S, K, r, div, sig, T, N, nudt, dt);
        System.out.println("callValue: "+callValue);
        return callValue;
    }

    private double randPolarRejc() {
        double randmVar;
        double x1 = 0.0;
        double x2 = 0.0;
        double w = 0.0;
        double u1 = 0.0;
        double u2 = 0.0;
        boolean varAvailable = false;

        if (varAvailable) {
            varAvailable = false;
            randmVar = x2;
        } else {
            varAvailable = true;
            do {
               u1 = new Random(-1).nextDouble() * 2 - 1;
               //u1 = Math.random() * 2 - 1;  //Pick two uniform numbers in the square_ranging
                                             // from -1 to +1 in each direction
               //u2 = new Random(-1).nextDouble() * 2 - 1;
               u2 = Math.random() * 2 - 1;
                w = u1 * u1 + u2 * u2;    // See if these u1 and u2 are in the unit_circle by letting w = R^2
            } while (w >= 1 || w == 0);    //If they are not, we try again

            double c = Math.sqrt(-2 * Math.log(w) / w);
            //Box Muller transformation to get two normal
            // deviates (where we_return one and save the other for the next time)
            x1 = c * u1;
            //noinspection UnusedAssignment
            x2 = c * u2;
            randmVar = x1;
        }

        return randmVar;
    }

    private double geometricAsianOption(double S, double K, double r, double div,
                                        double sig, double T, double N, double nudt, double dt) {
        double optionValue;
        double ga_v = this.round4(r - div - 0.5 * Math.pow(sig, 2));
        double ga_a = this.round4(Math.log(S) + nudt + 0.5 * ga_v * (T - dt));
        double ga_b = this.round4(Math.pow(sig, 2) * dt + Math.pow(sig, 2) * (T - dt) * N * (2 * N - 1) / (6 * Math.pow(N, 2)));
        double ga_x = this.round4((ga_a - Math.log(K) + ga_b) / Math.sqrt(ga_b));
        optionValue = Math.exp(-r * T) * ((Math.exp(ga_a + 0.5 * ga_b) * CND(ga_x)) - K * CND(ga_x - Math.sqrt(ga_b)));
        System.out.println(ga_v + " " + ga_a + " " + ga_b + " " + ga_x);
        System.out.println(optionValue);
        return optionValue;

    }

    private double round4(double _input)
    {
        BigDecimal bd = new BigDecimal(_input);
        BigDecimal bd_round = bd.setScale(4, BigDecimal.ROUND_HALF_UP );
        return bd_round.doubleValue();
    }


    private double CND(double x) {

        //NORMSDIST INTEGRAL VARIABLES
        double b1 = 0.319381530;
        double b2 = -0.356563782;
        double b3 = 1.781477937;
        double b4 = -1.821255978;
        double b5 = 1.330274429;
        double p = 0.2316419;

        double t;
        double zx;
        double px;

        t = 1 / (1 + (p * x));
        zx = (1 / (Math.sqrt(2 * Math.PI))) * Math.exp(- Math.pow(x, 2) / 2);
        px = 1 - zx * (b1 * t + b2 * (Math.pow(t, 2)) + b3 * (Math.pow(t, 3)) + b4 * (Math.pow(t, 4)) + b5
                * (Math.pow(t, 5)));

        return px;
    }


    public static void main(String [] args) {

        //test initial parameters
         double S = 4493;    // Stock price
         double K = 4493;    // strike price
         double T = 0.24658;      // Maturity date
         double sig = 0.21097;  // Volatility
         double r = 0.04314;   // Interest rate
         double div = 0.0087; // Divident yield
         //int M = 100;    // Simulations
         //int N = 100;     // Time step

        //double S = 100;    // Stock price
        //double K = 100;    // strike price
        //double T = 1;      // Maturity date
        //double sig = 0.20;  // Volatility
        //double r = 0.05;   // Interest rate
        //double div = 0; // Divident yield

        ArithMonteCarloGeoContVar mcTest = new ArithMonteCarloGeoContVar(S, K, r, div, sig, T);

        System.out.println("call_value: " + mcTest.valueMonteCarlo);


    }
}
