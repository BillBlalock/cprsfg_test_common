package com.crps_fisglobal.common.running;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

public class ProcessObject implements Serializable
 , Comparator<ProcessObject>
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String pid;
	private String imageName;
	private String userName;
	/** Java/image server instance name */
	private String jsiName;
	private Date createDate;
	
	public ProcessObject() {
		createDate = new Date();
	}
	
	/**
	 * @return the image server instance name
	 */
	public String getJsiName() {
		return jsiName;
	}
	/**
	 * @param jsiName the image server instance name to set
	 */
	public void setJsiName(String jsiName) {
		this.jsiName = jsiName;
	}
	/**
	 * @return the pid
	 */
	public String getPid() {
		return pid;
	}
	/**
	 * @param pid the pid to set
	 */
	public void setPid(int pid) {
		this.pid = new Integer(pid).toString();
	}
	/**
	 * @param pid the pid to set
	 */
	public void setPid(Integer pid) {
		this.pid = pid.toString();
	}
	/**
	 * @param pid the pid to set
	 */
	public void setPid(String pid) {
		this.pid = new String(pid);
	}
	/**
	 * @return the imageName
	 */
	public String getImageName() {
		return imageName;
	}
	/**
	 * @param imageName the imageName to set
	 */
	public void setImageName(String imageName) {
		this.imageName = new String(imageName);
	}
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = new String(userName);
	}
	
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = new Date( createDate.getTime() );
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((imageName == null) ? 0 : imageName.hashCode());
		result = prime * result + ((pid == null) ? 0 : pid.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProcessObject other = (ProcessObject) obj;
		if (imageName == null) {
			if (other.imageName != null)
				return false;
		} else if (!imageName.equals(other.imageName))
			return false;
		if (pid == null) {
			if (other.pid != null)
				return false;
		} else if (!pid.equals(other.pid))
			return false;
		return true;
	}
	
	@Override
	public int compare(ProcessObject arg0, ProcessObject arg1) {
		int compareReturn = arg0.getImageName().compareTo(arg1.getImageName());
		if ( compareReturn != 0 )
			return compareReturn;
		else
			return arg0.getPid().compareTo(arg1.getPid());
	}

}
