package com.crps_fisglobal.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

public class CprsInetUtils {

	static String tus400Ipv4;
	static String stp400Ipv4;
	static String malmusIpv4;
	static String eteamIipv4;
	static String tus400SerialNbr;
	static String stp400SerialNbr;
	static String tus400SystemName;
	static String stp400SystemName;

	static String tusmn1a4;
	static String stpmn1a4;
	static String tushd2ss;
	static String tushd1ss;
	static String malmus;
	static String eteam;
	
	static String prod400Ipv4;
	static String ha400Ipv4;
	static boolean ha400InService;
	
	static Properties cprsProperties;
	static boolean prod400Reachable;
	static boolean ha400Reachable;

	private static CprsInetUtils instance = new CprsInetUtils();
	/**
	 * Set up default values to use if cprs.properties is not available.
	 */
	private CprsInetUtils() {
		tus400Ipv4 = "172.21.80.1";
		stp400Ipv4 = "172.27.37.171";
		malmusIpv4 = "172.21.60.31";
		eteamIipv4 = "172.21.60.4";
		tus400SerialNbr = " 1026MVM";
		stp400SerialNbr = " 103B55C";
		tus400SystemName = "S1023703";
		stp400SystemName = "S103B55C";	
		tusmn1a4 = "tusmn1a4";
		stpmn1a4 = "stpmn1a4";
		tushd2ss = "tushd2ss";
		tushd1ss = "tushd1ss";
		eteam = "tushd1ss";
		malmus = "tushd2ss";

		prod400Ipv4 = tus400Ipv4;
		ha400Ipv4= stp400Ipv4;
		ha400InService = true;
		prod400Reachable = false;
		ha400Reachable = false;
	}
	
	private static void loadCprsProperties() {
		File cprsPropFile = new File ( CprsUtils.getCprsRoot() + "cprs.properties");
		try {
			FileInputStream fis = new FileInputStream(cprsPropFile);
			cprsProperties = new Properties();
			cprsProperties.load(fis);
			String propValue;
			propValue = cprsProperties.getProperty("cprs.tus400.ipv4");
			if ( propValue != null )
				tus400Ipv4 = propValue;
			propValue = cprsProperties.getProperty("cprs.stp400.ipv4");
			if ( propValue != null )
				stp400Ipv4 = propValue;
			propValue = cprsProperties.getProperty("cprs.malmus.ipv4");
			if ( propValue != null )
				malmusIpv4 = propValue;
			propValue = cprsProperties.getProperty("cprs.eteam.ipv4");
			if ( propValue != null )
				eteamIipv4 = propValue;
			propValue = cprsProperties.getProperty("cprs.prod400.ipv4");
			if ( propValue != null )
				prod400Ipv4 = propValue;
			propValue = cprsProperties.getProperty("cprs.ha400.ipv4");
			if ( propValue != null )
				ha400Ipv4 = propValue;
			propValue = cprsProperties.getProperty("cprs.ha400.inService");
			if ( propValue != null )
				ha400InService = Boolean.valueOf(propValue);
			fis.close();
			fis = null;

		} catch (FileNotFoundException e) { 
		} catch (IOException e) { 
		}
	}
	
	public static boolean isProd400Reachable() {
		if ( cprsProperties == null )
			loadCprsProperties();
		
		if ( !prod400Reachable ) 
			getProd400Address();
		return prod400Reachable;
	}
	
	public static boolean  isHa400Reachable() {
		if ( cprsProperties == null )
			loadCprsProperties();
		
		// property file indicates HA system not in service
		if ( !ha400InService )
			return false;
		if ( !ha400Reachable ) 
			getProd400Address();
		return ha400Reachable;	
	}

	public static String getProd400Address() {
		if ( cprsProperties == null )
			loadCprsProperties();
		
		try {
			InetAddress address = InetAddress.getByName( prod400Ipv4 );
			if ( address.isReachable(3000) ) {
				prod400Reachable = true;
			} 
		} catch (UnknownHostException e) {
		} catch (IOException e) {
		}		
		return prod400Ipv4;
	}
	
	public static String getHa400Address() {
		if ( cprsProperties == null )
			loadCprsProperties();
		
		try {
			InetAddress address = InetAddress.getByName( ha400Ipv4 );
			if ( address.isReachable(3000) ) {
				ha400Reachable = true;
			} 
		} catch (UnknownHostException e) {
		} catch (IOException e) {
		}		
		return ha400Ipv4;
	}
	
public static void main ( String[] s ) {
	System.out.println( getProd400Address() );
	System.out.println( getHa400Address() );
	System.out.println( isProd400Reachable() );
	System.out.println( isHa400Reachable() );

}

public static String getMalmusAddress() {
	return malmusIpv4;
}

public static String getEteamAddress() {
	return eteamIipv4;
}

public static String getMalmus() {
	return malmus;
}

public static String getEteam() {
	return eteam;
}
}
