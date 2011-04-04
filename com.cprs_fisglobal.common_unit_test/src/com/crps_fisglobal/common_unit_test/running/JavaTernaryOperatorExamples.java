package com.crps_fisglobal.common_unit_test.running;

/**
 * Example from http://www.devdaily.com/java/edu/pj/pj010018/
 * @author alvin
 *
 */
public class JavaTernaryOperatorExamples
{
  /**
   * Examples using the Java ternary operator
   * @author alvin alexander, devdaily.com
   */
  public static void main(String[] args)
  {
    // min value example
    int minVal, a=3, b=2;
    minVal = a < b ? a : b;
    System.out.println("min = " + minVal);
    
    // absolute value example
    a = -10;
    int absValue = (a < 0) ? -a : a;
    System.out.println("abs = " + absValue);

    // result is assigned the value 1.0
    float result = true ? 1.0f : 2.0f;
    System.out.println("float = " + result);
 
    // result is assigned the value "Sorry Dude, it's false"
    String s = false ? "Dude, that was true" : "Sorry Dude, it's false";
    System.out.println(s);
    
    // example using the ternary operator on the rhs, in a string
    cookies(5);
    cookies(1);

  }
  static void cookies(int x) {
	    String out = "There " + (x > 1 ? "are " + x + " cookies" : "is one cookie") + " in the jar.";
	    System.out.println(out);
	  
  }
}
