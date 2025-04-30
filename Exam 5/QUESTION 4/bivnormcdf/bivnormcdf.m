function p = bivnormcdf(a,b,rho)

%Gives the bivariate normal disribution function probabilities
%As given in Hull's textbook
%Author: Sivakumar Batthala
%MBA candidate
%Chicago Graduate School of Business
%University of chicago
%Date:02/23/2005
%Please email  sbatthal@gsb.uchicago.edu     for any clarifications or errors.

if(a <= 0 && b <= 0 && rho <= 0)
    
    aprime = a/(sqrt(2*(1-(rho.^2))));
    bprime = b/(sqrt(2*(1-(rho.^2))));
    A = [0.3253030 0.4211071 0.1334425 0.006374323];
    B = [0.1337764 0.6243247 1.3425378 2.2626645];
    
    F = 'exp(aprime*(2*x - aprime)+ (bprime*(2*y - bprime)) + (2*rho *(x - aprime)*(y-bprime)))'; 
    t = 0;
    
    for i=1:4
        for j=1:4
            x = B(i);
            y = B(j);
            t = t + A(i)*A(j)*eval(F);
            
        end
    end
   
    p = (sqrt(1-rho.^2)/pi) * t;
    
elseif (a * b * rho <= 0)
        
        if (a <=0 && b >=0 && rho >=0)
            p = normcdfM(a) - bivnormcdf(a,-b,-rho);
        elseif (a >=0 && b <=0 && rho >=0)
                p = normcdfM(b) - bivnormcdf(-a,b,-rho);
        elseif (a >=0 && b >=0 && rho <=0) %modified here at 1:45 AM
                p = normcdfM(a) + normcdfM(b) - 1 + bivnormcdf(-a,-b,rho);
        end

elseif  a*b*rho > 0;
    %Could not use the In-Built function sign(x) because it is +1 if x>=0
    %not just x>0 as in Matlab.
    
    if(a >= 0), 
        asign =1 ;
    else
        asign = -1;
    end
    
    if(b >= 0), 
        bsign =1 ;
    else
        bsign = -1;
    end
    
    rho1 = (rho*a - b)*asign/(sqrt(a.^2 - (2*rho*a*b) + b.^2));
    rho2 = (rho*b - a)*bsign/(sqrt(a.^2 - (2*rho*a*b) + b.^2));
    delta = (1-(asign*bsign))/4;
    
        p = bivnormcdf(a,0,rho1) + bivnormcdf(b,0,rho2) - delta ;
end
            

