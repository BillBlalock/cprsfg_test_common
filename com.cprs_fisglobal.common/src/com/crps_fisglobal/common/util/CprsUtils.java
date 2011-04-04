package com.crps_fisglobal.common.util;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class CprsUtils {
	
	static String tus400Ipv4 = "172.21.80.1";
	static String stp400Ipv4 = "172.27.37.171";
	static String malmusIpv4 = "172.21.60.31";
	static String eteamIipv4 = "172.21.60.4";

	static String tusmn1a4 = "tusmn1a4";
	static String stpmn1a4 = "stpmn1a4";
	static String tushd2ss = "tushd2ss";
	static String tushd1ss = "fushd1ss";

	/** CPRS primary Windows server **/
	private static final String MALMUS = "tushd2ss";
	
	public static boolean isWindows(){
		 
		String os = System.getProperty("os.name").toLowerCase();
		//windows
	    return (os.indexOf( "win" ) >= 0); 
 
	}
 
	public static boolean isMac(){
 
		String os = System.getProperty("os.name").toLowerCase();
		//Mac
	    return (os.indexOf( "mac" ) >= 0); 
 
	}
 
	public static boolean isUnix(){
 
		String os = System.getProperty("os.name").toLowerCase();
		//linux or unix
	    return (os.indexOf( "nix") >=0 || os.indexOf( "nux") >=0);
 
	}

	public static boolean isAs400(){
		 
		String os = System.getProperty("os.name").toLowerCase();
		//linux or unix
		return ( os.indexOf("400") > 0 );
	}

	/**
	 * 
	 * @return path to folder where CPRS objects are stored.  
	 * String ends in slash (forward or back)
	 */
	
	public static String getCprsRoot() {
		String cprsRoot = System.getProperty("cprs.root");
		if ( cprsRoot == null ) {
			if ( isAs400() ) {
				return "/java/";
			} else if ( isWindows() ) {
				try {
					cprsRoot = "C:/";
					if ( InetAddress.getLocalHost().getHostName().equalsIgnoreCase(MALMUS) )
						cprsRoot = "D:/Applications/";
					else if ( new File("C:/cprs").exists() && new File("C:/cprs/").isDirectory() )
						cprsRoot = "C:/cprs/";
					else cprsRoot = "C:/";
				} catch (UnknownHostException e) {
					cprsRoot = "C:/";
				}
			} else cprsRoot = "/";
		}
		// make sure string ends with / or \
		if ( cprsRoot.charAt(cprsRoot.length()-1) != '/' && cprsRoot.charAt(cprsRoot.length()-1) != '\\')
			cprsRoot += "/";
		
		return cprsRoot;
	}

	/**
	 * 
	 * @return process ID string from ManagementFactory.getRuntimeMXBean().getName().  
	 * On Windows will be string value of integer.  On AS400 will be 6 character
	 * String of digits (which will include leading zeros).
	 */
	public static String getPidString() {
		String pid = "";
		if ( isWindows() || isAs400() ) {
			int atPos = -1;
			String beanName = ManagementFactory.getRuntimeMXBean().getName();
			if ( ( atPos=beanName.indexOf('@') ) >= 0 )
				pid = beanName.substring(0, atPos);
		}
		return pid;
	}


	/**
	 * Unit test
	 * @param args
	 */
	public static void main ( String[] args ) {
		System.out.println( "Process ID string: " + getPidString());
		System.out.println( "This is windows? " + isWindows() );
		System.out.println( "This is AS400?   " + isAs400() );
		System.out.println( "Cprs root folder: " + getCprsRoot() );	
		
	     try {
	       InetAddress address = InetAddress.getByName("172.21.60.4");
	       System.out.println("Name: " + address.getHostName());
	       System.out.println("Addr: " + address.getHostAddress());
	       System.out.println("Reach: " + address.isReachable(3000));
	     }
	     catch (UnknownHostException e) {
	       System.err.println("Unable to lookup web.mit.edu");
	     }
	     catch (IOException e) {
	       System.err.println("Unable to reach web.mit.edu");
	     }
	   }

	
}
