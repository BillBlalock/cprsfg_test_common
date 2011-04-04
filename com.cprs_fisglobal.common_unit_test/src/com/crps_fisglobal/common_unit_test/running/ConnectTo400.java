package com.crps_fisglobal.common_unit_test.running;

import java.io.IOException;

import com.crps_fisglobal.common.util.CprsInetUtils;
import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.Job;

public class ConnectTo400 {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws AS400SecurityException 
	 */
	public static void main(String[] args) throws AS400SecurityException, IOException {
		// TODO Auto-generated method stub
		System.out.println("prod400 reachable? " + CprsInetUtils.isProd400Reachable() );
//		AS400 prod400 = new AS400 ( "TUSMN1A4", "BLALOCK", "WHB2ND");
		AS400 prod400 = new AS400 ( CprsInetUtils.getHa400Address(), "BLALOCK", "WHB2ND");
		System.out.println(prod400.getSystemName());
		prod400.connectService(AS400.CENTRAL);
		Job[] jobs = prod400.getJobs(AS400.CENTRAL);
		System.out.println(jobs.length + " jobs");
		prod400.connectService(AS400.COMMAND);
		jobs = prod400.getJobs(AS400.CENTRAL);
		System.out.println(jobs.length + " jobs");
		System.out.println( "AS400.isConnected() = " + prod400.isConnected());
		prod400.disconnectAllServices();
	}

}
