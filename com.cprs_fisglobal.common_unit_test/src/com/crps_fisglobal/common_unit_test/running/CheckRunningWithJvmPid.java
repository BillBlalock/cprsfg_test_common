package com.crps_fisglobal.common_unit_test.running;

import java.lang.management.ManagementFactory;

import com.crps_fisglobal.common.running.RunningMonitor;
import com.crps_fisglobal.common.util.CprsUtils;


public class CheckRunningWithJvmPid {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		String pid = CprsUtils.getPidString();
		System.out.println( "Testing on PID: " + pid );
		RunningMonitor rm = new RunningMonitor();
		boolean ok = rm.okayToRunJavaServer("REPACK" + pid );
//		rm = null;
		
		System.out.println("Value returned is: " + ok + " -- Waiting 15 seconds ");
		Thread.sleep(15000);
		
//		RunningMonitor newRm = new RunningMonitor();
		ok = rm.okayToRunJavaServer("REPACK" + pid );
//		newRm = null;
		System.out.println("2nd try value returned is: " + ok + " -- Waiting 5 seconds ");
		
		Thread.sleep(5000);
		
//		RunningMonitor newRm3 = new RunningMonitor();
		ok = rm.okayToRunJavaServer("REPACK" + pid  );
		System.out.println("3rd try value returned is: " + ok + " -- Waiting 5 seconds then remove ");
		
		Thread.sleep(5000);
		
		rm.removeJavaServer("REPACK" + pid );
		
		rm = null;
	}

}
