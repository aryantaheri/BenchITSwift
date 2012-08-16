/********************************************************************
 * BenchIT - Performance Measurement for Scientific Applications Contact:
 * developer@benchit.org
 * 
 * $Id: SkeletonDataObject.java 1 2009-09-11 12:26:19Z william $ $URL:
 * svn+ssh://
 * william@rupert.zih.tu-dresden.de/svn-base/benchit-root/BenchITv6/kernel
 * /utilities/skeleton/Java/0/0/0/SkeletonDataObject.java $ For license details
 * see COPYING in the package base directory
 *******************************************************************/
/*
 * Kernel: java kernel skeleton this file: a container class for the data
 * *****************************************************************
 */

public class SwiftDataObject {
	private long fileSize = -1;
	private String username, password, authUrl, container;
	private int maxProblemSize = -1;
	
	

	public SwiftDataObject() {
	}


	/**
	 * 
	 * @return byte value of the file size
	 */
	public long getFileSize() {
		return fileSize;
	}


	/**
	 * 
	 * @param byte value of the fileSize
	 */
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}


	public String getAuthUrl() {
		return authUrl;
	}


	public void setAuthUrl(String authUrl) {
		this.authUrl = authUrl;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getContainer() {
		return container;
	}


	public void setContainer(String container) {
		this.container = container;
	}


	public int getMaxProblemSize() {
		return maxProblemSize;
	}


	public void setMaxProblemSize(int maxProblemSize) {
		this.maxProblemSize = maxProblemSize;
	}
	
}
