package com.crps_fisglobal.common.running;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import com.crps_fisglobal.common.util.CprsUtils;

/* TODO make work for AS400
 *   get process list of running Java jobs # which 
 */
public class RunningMonitor {

	/** ArrayList of running Java/image server programs */
	ProcessList runningServers = null;
	/** ArrayList of running programs on system */
	ProcessList processList = null;
	
	boolean updateRunningServers;
	File file; 
	FileChannel channel;
	FileLock lock;
	RandomAccessFile raf;
	/**
	 * Has readObject() been retried?  getRunningJavaServers() can be
	 * retried once if deserializing failed.
	 */
	boolean readObjectRetried = false;
	/** 
	 * Signal whichever method called getRunningJavaServers() to retry 
	 * because deserializing failed first time.
	 */
	boolean retryReadObject = false;

	
	private boolean openAndLockRunningServers() {
		file = new File ( System.getProperty("cprs.runningJavaServer"), 
				CprsUtils.getCprsRoot() + "runningJavaServer.serial");
		boolean opened = false;
		int elapsedTime = 0;
		do {
			try {
				raf = new RandomAccessFile( file, "rw" );
				opened = true;
			} catch ( FileNotFoundException e ) {
				try {
					Thread.sleep(2000);
					elapsedTime += 2000;
					if ( elapsedTime > 2 * 60 * 1000 ) {
//						throw new Exception("Waiting too long for runningJavaServer serial file.");
						return false;
					}  
				} catch (InterruptedException e1) {	}
			}
		} while (! opened );
		return true;
	}
	
	/**
	 * Load java server instance objects (ProcessObject) from serialized object.  Remove java server 
	 * instance objects that are not on the list of running tasks retrieved from Windows." 
	 * @throws Exception 
	 * 
	 */
	private void getRunningJavaServers() throws Exception {


		if ( file.exists() && file.length() > 0L ) {
			
			// serialized file exists and has data, read the running servers ProcessList
			channel = raf.getChannel();
	        boolean locked = false;
			do {
				lock = channel.tryLock(0L, Long.MAX_VALUE, false);
				if ( lock == null ) {
//					System.out.println("File locked, wait 2 secs");
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) { }
				} else {
					locked = true;
				} 
			} while ( ! locked );

			FileInputStream in = new FileInputStream( raf.getFD() );
			try {
				ObjectInputStream ois = new ObjectInputStream(in);
				runningServers = (ProcessList) ois.readObject();
			} catch (Exception e) {
				// can't instantiate serialized ProcessList.  Blow it away
				// and try one more time.
				if ( !readObjectRetried ) {
					unlockRunningServers();	
					new File ( System.getProperty( "cprs.runningJavaServer" ), 
							CprsUtils.getCprsRoot() + "runningJavaServer.serial").delete();
					retryReadObject = true;
					readObjectRetried = true;
					return;
				} else {
					throw e;
				}
			}

			if ( runningServers == null ) {
				// running servers ProcessList not restored
				// create new one and force update
				runningServers = new ProcessList();
				updateRunningServers = true;
			}
				
			if ( !runningServers.isEmpty() ) {
				// running servers ProcessList has elements
	
				updateRunningServers = false;
	
				// loop through runningServers removing every entry in the list of
				// running java/image servers that are no on the list of 
				// running processes from Windows.  Keep looping until no 
				// obsolete java/images server are found.  Cleans up any duplicates.
				boolean noneFound = false;
				do {
					int removeIndex = -1;
					if ( !runningServers.isEmpty() ) {
						for (ProcessObject po : runningServers) {
//							System.out.println(po.getImageName() + " -- " + po.getIsiName());
							if (!processList.contains(po)) {
								removeIndex = runningServers.indexOf(po);
//								System.out.println( "drop this one, index " + removeIndex);
								updateRunningServers = true;
							}
						}
						if (removeIndex > -1)
							runningServers.remove(removeIndex);
						else
							noneFound = true;
					} else
						noneFound = true;
				} while (!noneFound);
			}
		} else {
			runningServers = new ProcessList();			
		}

		return;
	}

	/**
	 * Check ProcessList of running java/image server instances for this server
	 * instance running in this JVM.  Process id acquired from CprsUtils.getPidString().
	 * 
	 * @param javaServerInstance Java/image server instance name
	 * @return <code> true means java/image server instance was not found to be
	 * running and has been added to the list of running java/image server instances.
	 * @throws Exception 
	 */
	public boolean okayToRunJavaServer ( String javaServerInstance ) throws Exception {
		return okayToRunJavaServer (  javaServerInstance, CprsUtils.getPidString() );
	}

	/**
	 * Check ProcessList of running java/image server instances for this server
	 * instance.  If this instance is not running add to the java/image server
	 * ProcessList and serialize it.
	 * 
	 * @param javaServerInstance Java/image server instance name
	 * @param pid process id string. In Windows this is PID, in AS400 this is job number. 
	 * @return <code> true means java/image server instance was not found to be
	 * running and has been added to the list of running java/image server instances.
	 * @throws Exception 
	 */
	public boolean okayToRunJavaServer ( String javaServerInstance, String pid ) throws Exception {
		
		if ( !CprsUtils.isWindows() )
			throw new Exception ( "This utility works only with Windows.");
		
		if ( !openAndLockRunningServers() )
			throw new Exception ( "Unable to obtain lock on runningServers serialized object.");
		
		boolean okayToRun = true;

		// get list of currently running Windows processes
    	getProcessList();
    	
		getRunningJavaServers();
		if ( retryReadObject ) {
			if ( !openAndLockRunningServers() )
				throw new Exception ( "Unable to obtain lock on runningServers serialized object.");
			getRunningJavaServers();
		}			
		
        for ( ProcessObject po : runningServers ) {
        	if ( po.getJsiName().equalsIgnoreCase(javaServerInstance ) ) {
//        		System.out.println(po.getIsiName() +" Found one !!!!!!!!!!!!!!!");
        		okayToRun = false;
        		break;
        	}
        }
        if ( okayToRun ) {
            for ( ProcessObject po : processList ) {
            	if ( po.getPid().equalsIgnoreCase( pid ) ) {
            		ProcessObject javaServerProcess = new ProcessObject();
            		javaServerProcess.setImageName( po.getImageName() );
            		javaServerProcess.setJsiName( javaServerInstance) ;
            		javaServerProcess.setPid( pid );
            		javaServerProcess.setUserName(new String(po.getUserName()) );
            		runningServers.add( javaServerProcess );
            		updateRunningServers = true;
//            		System.out.println( " Running server list updated!");
            		break;
            	}
            }
        }
        
        if ( updateRunningServers ) {
        	updateRunningJavaServers();
        }
        unlockRunningServers();
        
        return okayToRun;
		
	}

	/**
	 * Check ProcessList of running java/image server instances for this server
	 * instance.  If found remove from ProcessList and serialize it.
	 * 
	 * @param javaServerInstance
	 * @return <code> true means java/image server instance was not found to be
	 * running and has been added to the list of running java/image server instances.
	 * @throws Exception 
	 */
	public void removeJavaServer ( String javaServerInstance ) throws Exception {

		if ( !CprsUtils.isWindows() )
			throw new Exception ( "This utility works only with Windows.");
		
		if ( !openAndLockRunningServers() )
			throw new Exception ( "Unable to obtain lock on runningServers serialized object.");
		
    	getProcessList();

    	getRunningJavaServers();
		
		int removeIndex = -1;
        for ( ProcessObject po : runningServers ) {
        	if ( po.getJsiName().equalsIgnoreCase(javaServerInstance ) ) {
//        		System.out.println(po.getIsiName() +" Found one !!!!!!!!!!!!!!!");
        		removeIndex = runningServers.indexOf(po);
        		break;
        	}
        }
        
        if ( removeIndex > -1 ) {
        	runningServers.remove( removeIndex );
        	updateRunningServers = true;
        }
        
        if ( updateRunningServers ) {
        	updateRunningJavaServers();
        }
        
        unlockRunningServers();
        
	}

	
	
	
	private void updateRunningJavaServers() throws IOException {
 
    	if ( channel == null )
    		channel = raf.getChannel();
    	if ( lock == null ) {
            boolean locked = false;
    		do {
    			lock = channel.tryLock(0L, Long.MAX_VALUE, false);
    			if ( lock == null ) {
//    				System.out.println("File locked, wait 2 secs");
    				try {
    					Thread.sleep(2000);
    				} catch (InterruptedException e) { }
    			} else {
    				locked = true;
    			} 
    		} while ( ! locked );
    	}
    	if ( !runningServers.isEmpty() ) { 
    		// serialize ProcessList object
    		raf.seek(0L);
    		FileOutputStream out = new FileOutputStream( raf.getFD() );   
    		ObjectOutputStream oos = new ObjectOutputStream(out);
    		oos.writeObject( runningServers );
    		oos.flush();
    	} else {
    		// no element so clear file
    		raf.setLength(0);
    	}
	}

	
	private void unlockRunningServers() throws IOException {

		
        if ( lock != null ) {
        	lock.release();
//       	System.out.println( " lock final release ");
        }
        
        runningServers = null; 
        processList = null;   
        lock = null;
        channel = null;
       raf.close();
        raf = null;
        file = null;       
	}
	
	private void getProcessList() throws IOException {
		Process p;
		processList = new ProcessList();
		Runtime runTime;
		String process = null;
//		try {
//			System.out.println("Processes Reading is started...");

			// Get Runtime environment of System
			runTime = Runtime.getRuntime();

			// Execute command thru Runtime
			p = runTime.exec( new String[] {
					"tasklist.exe",
					"/V",
					"/FO",
					"CSV",
					"/NH"
			}); // For Windows
			

			
			// Create Inputstream for Read Processes
			InputStream inputStream = p.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);
			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			// Read the processes from system and add & as delimiter for
			// tokenize the output
			String line = bufferedReader.readLine();
			// System.out.print(++i  + " " + line);
			process = "&";
			while (line != null) {
				String[] tokens = line.split("\",\"");
			//	System.out.println( " -- tokens: " + tokens.length);
				if ( tokens.length == 9 ) {
					ProcessObject po = new ProcessObject();
					po.setImageName( tokens[0].substring(1));
					po.setPid( tokens[1]);
					po.setUserName(tokens[6]);
					processList.add(po);
				}
				line = bufferedReader.readLine();
			//	System.out.print(++i  + " " + line);
				process += line + "&";
			}

			// Close the Streams
			bufferedReader.close();
			inputStreamReader.close();
			inputStream.close();

//		} catch (IOException e) {
//			System.out.println("Exception arise during the read Processes");
//			e.printStackTrace();
//		}
		return;
	}


}
