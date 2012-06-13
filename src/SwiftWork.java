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
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpException;

import com.rackspacecloud.client.cloudfiles.*;

public class SwiftWork {
	// constants
	protected SwiftDataObject dataObject;
	private FilesClient client;

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

	
	private FilesClient swiftLogin() {
		//TODO: read these from the configuration file.
		client = new FilesClient("admin:admin", "admin", "http://158.38.172.243:8080/auth/v1.0");
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
	 * 1- file is generate
	 * 2- file's checksum is available
	 * 3- connection is authenticated, and client is ready
	 * 4- container is created
	 * @param file
	 * @return etag (file's MD5)
	 */
	public String putFile(String container, String file, String mimeType){
		String etag = null;
		try {
			etag = client.storeObject(container, new File(file), mimeType);
			
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
		SwiftWork work = new SwiftWork(null);
		generateRandomFile("1024", "8.0");
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
	
	// Utils
	private String generateContainerName(String addition) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSSS");
		return "test-container-" + addition + "-" + sdf.format(new Date(System.currentTimeMillis()));
	}
	
	private static String generateFileName(String addition) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSSS");
		return "test-file-" + addition + "-" + sdf.format(new Date(System.currentTimeMillis()));
	}
	
	
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
			return randfilename;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
		return null;
	}	
	
}
