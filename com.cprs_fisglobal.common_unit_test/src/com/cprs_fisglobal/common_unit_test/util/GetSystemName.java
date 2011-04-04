package com.cprs_fisglobal.common_unit_test.util;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.Properties;


public class GetSystemName {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Properties props = System.getProperties();
		props.list(System.out);
		String computerName = InetAddress.getLocalHost().getHostName();
		System.out.println(computerName);
		File file = new File ("//C://temp//foobar.txt");
//		File file = new File ("//TUSHD2SS/D:/image_processes_test/repackage_pid.txt");
		if (file.exists() )
			System.out.println("found File object");
		System.out.println("File getPath() " + file.getPath().replaceAll("\\\\", "/"));
		System.out.println("File getAbsolutePath() " + file.getAbsolutePath().replaceAll("\\\\", "/"));
		System.out.println("File getCanonicalPath() " + file.getCanonicalPath().replaceAll("\\\\", "/"));
		System.out.println("URI toString() " + file.toURI().toString());
		System.out.println("URI toString() " + file.toURI().toString());
		System.out.println("URI getHost() " + file.toURI().getHost());
		System.out.println("URI getRawPath() " + file.toURI().getRawPath());
		System.out.println("URL toString() " + file.toURI().toURL().toString());
		System.out.println("URL getHost() " + file.toURI().toURL().getHost());
	}

}
