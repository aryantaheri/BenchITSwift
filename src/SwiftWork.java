/********************************************************************
 * BenchIT - Performance Measurement for Scientific Applications
 * Contact: developer@benchit.org
 *
 * $Id: SkeletonWork.java 1 2009-09-11 12:26:19Z william $
 * $URL: svn+ssh://william@rupert.zih.tu-dresden.de/svn-base/benchit-root/BenchITv6/kernel/utilities/skeleton/Java/0/0/0/SkeletonWork.java $
 * For license details see COPYING in the package base directory
 *******************************************************************/
/* Kernel: java kernel skeleton
 * this file: the WORK algorithm
 *******************************************************************/
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpException;

import com.rackspacecloud.client.cloudfiles.FilesAuthorizationException;
import com.rackspacecloud.client.cloudfiles.FilesClient;
import com.rackspacecloud.client.cloudfiles.FilesConstants;
import com.rackspacecloud.client.cloudfiles.FilesContainerNotEmptyException;
import com.rackspacecloud.client.cloudfiles.FilesException;
import com.rackspacecloud.client.cloudfiles.FilesInvalidNameException;
import com.rackspacecloud.client.cloudfiles.FilesNotFoundException;

public class SwiftWork {

	
	protected SwiftDataObject dataObject;
	private FilesClient client;

	final private static String LOCAL_TEMP_LOG_FILE = "local_temp_files.log";
	final private static String REMOTE_TEMP_LOG_FILE = "remote_temp_files.log";
	
	public SwiftWork(SwiftDataObject dataObject) {
		this.dataObject = dataObject;
	}
	
	/**
	 * Generate and store the file, make everything ready a pure "GET" benchmark 
	 * @return return the file name to be used for GET operation
	 */
	public String initForGet() {
		
		if (client == null || !client.isLoggedin()){
			client = this.swiftLogin();
		}
		
		String randfilename = null;
		if (dataObject.getFileSize() < 1024){
			randfilename = generateRandomFile(String.valueOf(dataObject.getFileSize()), "1");
		}else{
			randfilename = generateRandomFile("1024", String.valueOf((int) Math.ceil(dataObject.getFileSize()/1024)));
		}

		String randfileaddr = getBenchITTemp() + "/" + randfilename;
		String orig_md5 = null;
		try {
			orig_md5 = FilesClient.md5Sum(new File(randfileaddr));
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Processing file: " + randfileaddr);
		String uploaded_md5 = putFile("benchit_container", randfileaddr, FilesConstants.getMimetype(""));
		
		//TODO: use assert
		if (!orig_md5.equals(uploaded_md5)) {
			System.out.println("I should terminate the process, and upload the file again...");
		}
		
		return randfilename;
			
	}
	
	public void cleanForGet(){
		
	}

	
	private FilesClient swiftLogin() {
		client = new FilesClient(dataObject.getUsername(), dataObject.getPassword(), dataObject.getAuthUrl());
		boolean success;
		try {
			success = client.login();
			
			if (success) {
				System.out.println("username: " + client.getUserName());
				System.out.println("url: " + client.getStorageURL());
				System.out.println("token: " + client.getStorageToken());
				return client;
			} else {
				System.out.println("login failed.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (HttpException e) {
			e.printStackTrace();
		}
		return null;
	}



	
//	public void work_1() { /* here is defined the job to do */
//	}
//
//	public void work_2() { /* here is defined the job to do */
//	}

	/**
	 * Simple get
	 * @param container name
	 * @param file name
	 * @return downloaded file in byte[] form
	 */
	public byte[] getFile(String container, String file) {
		byte[] file_ba = null;
		try {
			file_ba = client.getObject(container, file);
		} catch (FilesAuthorizationException e) {
			e.printStackTrace();
		} catch (FilesInvalidNameException e) {
			e.printStackTrace();
		} catch (FilesNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (HttpException e) {
			e.printStackTrace();
		}
		return file_ba;
		
	}

	/**
	 * Simple put
	 * Assumptions:
	 * 1- file is generated
	 * 2- file's checksum is available
	 * 3- connection is authenticated, and client is ready
	 * 4- container is created
	 * @param file
	 * @return etag (file's MD5)
	 */
	public String putFile(String container, String file, String mimeType){
		String etag = null;
		try {
			File upload = new File(file);
			etag = client.storeObject(container, upload, mimeType);
			appendToRemoteTmpList(container + ":" + upload.getName(), true);
		} catch (FilesException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (HttpException e) {
			e.printStackTrace();
		}
		return etag;
	}
	
	/**
	 * Simple delete
	 * @param container
	 * @param file
	 */
	public void deleteFile(String container, String file){
		try {
			client.deleteObject(container, file);
		} catch (FilesNotFoundException e) {
			e.printStackTrace();
		} catch (FilesException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (HttpException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Creating the given container
	 * @param container
	 */
	public void createContainer(String container){
		try {
			if (!client.containerExists(container)){
				client.createContainer(container);
			}
		} catch (FilesAuthorizationException e) {
			e.printStackTrace();
		} catch (FilesException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (HttpException e) {
			e.printStackTrace();
		}
	}
	
	public boolean deleteContainer(String contianer){
		boolean bol = false;
		try {
			bol = client.deleteContainer(contianer);
		} catch (FilesAuthorizationException e) {
			e.printStackTrace();
		} catch (FilesInvalidNameException e) {
			e.printStackTrace();
		} catch (FilesNotFoundException e) {
			e.printStackTrace();
		} catch (FilesContainerNotEmptyException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (HttpException e) {
			e.printStackTrace();
		}
		return bol;
	}
	
	public static void main(String[] args) throws IOException {
		File tmp = new File("/tmp/benchit_tmp/test-file-swift-2012_08_13_13_30_33_0807");
		boolean s = tmp.delete();
		System.out.println(s);
//		for (int i = 0; i < 10; i++) {
//			appendToLocalTmpList(i+"", true);
//			appendToRemoteTmpList("container:"+i+"file", true);
//			
//		}
		//		SwiftWork work = new SwiftWork(null);
//		generateRandomFile("1024", "8.0");
//		toByte(5, "M");
//		long[] s = generateLogarithmicFileSizeSeries(2, 10, "M", 1, "G");
//		long[] s = generateLinearFileSizeSeries(30, "K", 1, "K", 10, "M");
//		for (int i = 0; i < s.length; i++) {
//			System.out.println(humanReadableByteCount(s[i], false));
//			System.out.println(s[i]);
//		}
//		work.swiftLogin();
//		String container = "benchit_container";
//		work.createContainer(container);
//		try {
//			work.client.createFullPath(container, "tmp/benchit_tmp/");
//		} catch (FilesException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (HttpException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		String etag = work.putFile(container, "/tmp/benchit_tmp/test-file-swift-2012_06_08_10_24_47_0976", FilesConstants.getMimetype(""));
//		System.out.println("Uploaded MD5: " + etag);
//		byte[] downloaded_file = work.getFile(container, "test-file-swift-2012_06_08_10_24_47_0976");
//		String downloaded_md5 = FilesClient.md5Sum(downloaded_file);
//		System.out.println("Downloaded MD5: " + downloaded_md5);
//		String md5 = FilesClient.md5Sum(new File("/tmp/benchit_tmp/test-file-swift-2012_06_08_10_24_47_0976"));
//		System.out.println("Actual MD5: " + md5);
//		work.deleteFile(container, "test-file-swift-2012_06_08_10_24_47_0976");
//		if (md5.equals(etag)) System.out.println("Yohuuuuuuuuu");
//		else {
//			System.out.println("Ridiiiiiiiii");
//		}
	}
	
	// logarithmic 
	public static long[] generateLogarithmicFileSizeSeries(int base, int min_value, String min_metric, int max_value, String max_metric){
		long min_byte = toByte(min_value, min_metric);
		long max_byte = toByte(max_value, max_metric);
		
		int lmin = (int) (Math.log(min_byte) / Math.log(base));
		int lmax = (int) (Math.log(max_byte) / Math.log(base));
		
		long[] series = new long[lmax - lmin + 1];
		for (int i = lmin; i <= lmax; i++) {
			series[i - lmin] = (long) Math.pow(base, i);
		}
		
		return series;
	}
	
	// linear 
	public static long[] generateLinearFileSizeSeries(int inc_value, String inc_metric, int min_value, String min_metric, int max_value, String max_metric){
		long min_byte = toByte(min_value, min_metric);
		long max_byte = toByte(max_value, max_metric);
		long inc_byte = toByte(inc_value, inc_metric);
				
		int num_inc = (int) ((Math.ceil((max_byte - min_byte) / inc_byte ) + 1));
		long[] series = new long[num_inc];
		for (int i = 0; i < num_inc; i++) {
			series[i] = min_byte + (i * inc_byte);
		}
		
		return series;
	}
	
	
	/**
	 * 
	 * @param size, e.g 1M, 1K, 1G
	 * @return size in byte, e.g 1024^3 
	 */
	public static long toByte(int value, String metric){
		String metrics = "BKMGTPE";
		int exp = metrics.indexOf(metric.toUpperCase());
//		System.out.println((long) (value * Math.pow(1024, exp)));
		return (long) (value * Math.pow(1024, exp));
				
	}
	
	public static String humanReadableByteCount(long bytes, boolean si) {
	    int unit = si ? 1000 : 1024;
	    if (bytes < unit) return bytes + " B";
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
	    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
	
	
	/**
	 * 
	 * @param byteps byte per second
	 * @return X bit per second
	 */
	public static double convertRateToXbps(double byteps, SwiftGetKernelMain.SizeUnit unit){
		switch (unit) {
		case b:
			return (byteps * 8);
			
		case K:
			return (byteps * 8) / (1024);
			
		case M:
			return (byteps * 8) / (1024 * 1024);
			
		case G:
			return (byteps * 8) / (1024 * 1024 * 1024);

		default:
			return byteps;
		}
	}
	
	/**
	 * @param file size in byte
	 * @return file size in unit
	 */
	public static double convertFileSize(double sizeInByte, SwiftGetKernelMain.SizeUnit unit){
		switch (unit) {
		case b:
			return (sizeInByte * 8);
			
		case B:
			return sizeInByte;
			
		case K:
			return (sizeInByte) / (1024);
			
		case M:
			return (sizeInByte) / (1024 * 1024);
			
		case G:
			return (sizeInByte) / (1024 * 1024 * 1024);

		default:
			return sizeInByte;
		}
	}

		
	
	
	// Utils
	private String generateContainerName(String addition) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSSS");
		return "test-container-" + addition + "-" + sdf.format(new Date(System.currentTimeMillis()));
	}
	
	private static String generateFileName(String addition) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSSS");
		return "test-file-" + addition + "-" + sdf.format(new Date(System.currentTimeMillis()));
	}
	
	
	/**
	 * 
	 * @return BenchIT temp directory
	 */
	public static String getBenchITTemp(){
		return System.getProperty("java.io.tmpdir") + "/benchit_tmp";
	}
	
	/**
	 * Generate a random file of size: blocksize * count 
	 * @param blocksize c =1, w =2, b =512, K =1024, M =1024*1024, G =1024*1024*1024,
	 * @param count c =1, w =2, b =512, K =1024, M =1024*1024, G =1024*1024*1024,
	 * @return file name
	 */
	public static String generateRandomFile(String blocksize, String count) {
		Process p;
		File tmpdir = new File(getBenchITTemp());
		if (!tmpdir.isDirectory())
			if(!tmpdir.mkdirs()) System.out.println("BenchIT tmpdir is not created. Going to blow up!");
		String randfilename = generateFileName("swift");
		String randfileaddr = tmpdir.getPath() + "/" + randfilename;
		
		try {
			String command = "/bin/dd if=/dev/urandom of=" + randfileaddr + " bs=" + blocksize + " count=" + count;
			p = Runtime.getRuntime().exec(command);
			p.waitFor(); 
			System.out.println("Random File:" + randfileaddr + " with size: (" + blocksize + " x " + count + ") is created successfully");
			appendToLocalTmpList(randfileaddr, true);
			return randfilename;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
		return null;
	}	


	private static void appendToRemoteTmpList(String object, boolean close){
		File logFile = new File(getBenchITTemp(), REMOTE_TEMP_LOG_FILE);
		try {
			FileWriter fileWriter = new FileWriter(logFile, true);
			fileWriter.append(object + '\n');
			if (close) fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void appendToLocalTmpList(String file, boolean close){
		File logFile = new File(getBenchITTemp(), LOCAL_TEMP_LOG_FILE);
		try {
			FileWriter fileWriter = new FileWriter(logFile, true);
			fileWriter.append(file + '\n' );
			if (close) fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private static void cleanLocalTemp() {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(getBenchITTemp() + "/" + LOCAL_TEMP_LOG_FILE));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("Local temp log file is not found, skipping the clean up process.");
			return;
		}
		String line = null;
		boolean res = false;
		
		try {
			while ((line = reader.readLine()) != null){
				File tmp = new File(line);
				if(tmp.delete()){
					System.out.println("File " + line + " is deleted successfully");
				} else {
					System.err.println("File " + line + " is NOT deleted successfully");
					// In case of a locking
					linuxDeleteFile(line);
				}
				
			}
			// Removing the actual local log file
			reader.close();
			File local_log = new File(getBenchITTemp(), LOCAL_TEMP_LOG_FILE);
			local_log.delete();
			
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Something wrong with the local log file, skipping the cleanup process.");
			return;
		}
	}
	
	private void cleanRemoteTemp() {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(getBenchITTemp() + "/" + REMOTE_TEMP_LOG_FILE));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("Remote temp log file is not found, skipping the clean up process.");
			return;
		}
		String line = null;
		boolean res = false;
		File tmp = null;
		
		try {
			while ((line = reader.readLine()) != null){
//				tmp = new File(getBenchITTemp(), line);
//				if (tmp.delete()) {
//					System.out.println("File " + line + " is deleted successfully"); 
//				} else {
//					System.err.println("File " + line + " is NOT deleted successfully"); 
//				}
				if (!client.isLoggedin()) swiftLogin();
				// container:filename
				String[] params = line.split(":");
				try {
					client.deleteObject(params[0], params[1]);
					System.out.println("Object " + line + " is removed from storage");
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("Unable to delete container:file " + line);
					System.err.println("Skipping to the next container:file");
				}
				
			}
			
			// Removing the actual remote log file
			reader.close();
			File remote_log = new File(getBenchITTemp(), REMOTE_TEMP_LOG_FILE);
			remote_log.delete();
		
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Something wrong with the remote log file, skipping the cleanup process.");
			return;
		}

	}

	public void cleanUp(int remove_local_tmp, int remove_remote_tmp,	String filename, int max_problem_size, int current_problem_size) {
		
//		# 0 -> don't remove
//		# 1 -> remove files after each run
//		# 2 -> remove files when the measurement is done
		switch (remove_local_tmp) {
		case 0:
			break;
		case 1:
			// do clean
			
			break;
		case 2:
			if (max_problem_size == current_problem_size){
				//do clean
				cleanLocalTemp();
			}
			break;

			
		default:
			break;
		}
		
//		# 0 -> don't remove
//		# 1 -> remove files after each run
//		# 2 -> remove files when the measurement is done
		switch (remove_remote_tmp) {
		case 0:
			break;
		case 1:
			// do clean
			
			break;
		case 2:
			if (max_problem_size == current_problem_size){
				//do clean
				cleanRemoteTemp();
			}
			break;

			
		default:
			break;
		}
		
		
	}
	
	
	
	/**
	 * LINUX Operations
	 */
	
	private static void linuxDeleteFile(String file) {
		String command = "/bin/rm -f " + file;
		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor(); 
			System.out.println("File :" + file + " is deleted successfully");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("File :" + file + " is NOT deleted successfully");
		} 

	}

	
}
