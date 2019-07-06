import java.util.*;
import java.math.BigInteger;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

public class PrimeGen{
	private static int n_primes = 0, digits = 0;
    private static ArrayList<Long> prime_list = new ArrayList<Long>();
    private static Semaphore mutex = new Semaphore(1);

    public PrimeGen(int n_primes, int digits){
    	this.n_primes = n_primes;
    	this.digits = digits;
    }


	/** -------- Miller-Rabin Primality Check Functions -------- **/
	/** Function to check if prime or not **/
	/** Source: https://www.sanfoundry.com/java-program-miller-rabin-primality-test-algorithm/ **/
    public static boolean isPrime(long n, int iteration)
    {
        /** base case **/
        if (n == 0 || n == 1)
            return false;
        /** base case - 2 is prime **/
        if (n == 2)
            return true;
        /** an even number other than 2 is composite **/
        if (n % 2 == 0)
            return false;
 
        long s = n - 1;
        while (s % 2 == 0)
            s /= 2;
 
        Random rand = new Random();
        for (int i = 0; i < iteration; i++)
        {
            long r = Math.abs(rand.nextLong());            
            long a = r % (n - 1) + 1, temp = s;
            long mod = modPow(a, temp, n);
            while (temp != n - 1 && mod != 1 && mod != n - 1)
            {
                mod = mulMod(mod, mod, n);
                temp *= 2;
            }
            if (mod != n - 1 && temp % 2 == 0)
                return false;
        }
        return true;        
    }
    /** Function to calculate (a ^ b) % c **/
    public static long modPow(long a, long b, long c)
    {
        long res = 1;
        for (int i = 0; i < b; i++)
        {
            res *= a;
            res %= c; 
        }
        return res % c;
    }
    /** Function to calculate (a * b) % c **/
    public static long mulMod(long a, long b, long mod) 
    {
        return BigInteger.valueOf(a).multiply(BigInteger.valueOf(b)).mod(BigInteger.valueOf(mod)).longValue();
    }
    /** -------- End of Miller-Rabin functions -------- **/

    private void generatePrimes(){
		long prime_num = 1;

		// generate initial prime value
		// for the future: random number with the minimum being the digit length
		for(int i=0; i<digits-1; i++){
			prime_num *= 10;
		}

		// create n_prime prime numbers
		while(prime_list.size() < n_primes){
			// check primality of a number 20 times
			while(!isPrime(prime_num, 20)){
				prime_num = getNextNum(prime_num);
			}
			// enqueue prime number to list
			try{
    			mutex.acquire();
    			if(!prime_list.contains(prime_num)){
    				prime_list.add(prime_num);
    			}
    			mutex.release();
    		}
    		catch(Exception e){
    			e.printStackTrace();
    		}

    		// increment prime_num
    		prime_num = getNextNum(prime_num);
		}
    }

    private static long getNextNum(long value){
    	try{
    		mutex.acquire();
    		value++;
    		mutex.release();
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    	return value;
    }

    public static void main(String[] args){
    	int n_primes = 0, digits = 0;

    	// Verify number of arguments
    	if(args.length<2){
    		System.out.println("Accepts 2 parameters");
    	}

    	// try to convert arguments to ints
    	try{
    		n_primes = Integer.parseInt(args[0]);
    		digits = Integer.parseInt(args[1]);
    	}
    	catch(Exception e){
    		System.out.println("Only accepts integer arguments!");
    		System.exit(1);
    	}

    	// check if n_primes is 0 or negative
    	if(n_primes <= 0){
    		System.exit(0);
    	}

    	//set global var digits, n_primes
    	PrimeGen primeObj = new PrimeGen(n_primes, digits);
    	primeObj.run();

    	for(Long num: prime_list){
    		System.out.println(num);
    	}
    	
    }

    private void run(){
    	try{
    		Thread t1 = new Thread(this::generatePrimes);
    		Thread t2 = new Thread(this::generatePrimes);
    		Thread t3 = new Thread(this::generatePrimes);
    		Thread t4 = new Thread(this::generatePrimes);
    		t1.start();
    		t2.start();
    		t3.start();
    		t4.start();
    		t1.join();
    		t2.join();
    		t3.join();
    		t4.join();
    	}
    	catch(Exception e){
    		System.err.println("fatal error, unexpected interrupt exception");
			System.exit(2);
    	}
    }
}