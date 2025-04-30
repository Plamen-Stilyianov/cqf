function n = normcdfM(x)

%Gives the normal cumulative density function probabilities
%%Author: Sivakumar Batthala
%MBA candidate
%Chicago Graduate School of Business
%University of chicago
%Date:02/23/2005
%Please email sbatthal@gsb.uchicago.edu for any clarifications or errors.

a1 = 0.319381530; a2=-0.356563782; a3=1.781477937; a4 = -1.821255978;
a5=1.330274429;
gamma = 0.2316419;

k = 1/(1+(gamma*x));
nprime = (1/sqrt(2*pi))* (exp(-(x^2)/2));

if (x >= 0)
    n = 1 - (nprime * ([a1*k] + [a2*(k^2)] + [a3*(k^3)] + [a4*(k^4)] + [a5*(k^5)]));
else
    n = 1 - normcdfM(-x);
end

    
