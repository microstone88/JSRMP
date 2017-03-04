/**
 * 
 */
package jrmp.srmp.math;

/**
 * @author micro
 *
 */
public class Factorial {

	/**
	 * calculate the value of x!
	 * 
	 * @param x
	 * @return x!
	 */
	public static int fac(int x) {
		
        if (x < 0) {
            throw new IllegalArgumentException("x must be>=0");
        }
        if (x <= 1) {
            return 1;
        } else
            return x * fac(x - 1);
    }

}
