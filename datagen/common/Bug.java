package common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class Bug {
	
	String bugID;
	String openDate;
	String fixDate;
	String resolution;
	
	String sum;
	String desc;
	String ver;
	String fixVer;
	String type;
	
	ArrayList<String> fileList;
	HashMap<String, String> relationList;
	HashMap<String, HashSet<String>> fileMethodMap;
	
	public Bug(String bugID, String openDate, String fixDate, String resolution,
			String sum, String desc, String ver,
			String fixVer, String type, ArrayList<String> fileList,
			HashMap<String, String> relationList) {
		super();
		this.bugID = bugID;
		this.openDate = openDate;
		this.fixDate = fixDate;
		this.resolution = resolution;
		this.sum = sum;
		this.desc = desc;
		this.ver = ver;
		this.fixVer = fixVer;
		this.type = type;
		this.fileList = fileList;
		this.relationList = relationList;
	}
	
	
	public Bug(String bugID, String openDate, String fixDate, String resolution,
			String sum, String desc, String ver,
			String fixVer, String type, ArrayList<String> fileList,
			HashMap<String, String> relationList,
			HashMap<String, HashSet<String>> fileMethodMap) {
		super();
		this.bugID = bugID;
		this.openDate = openDate;
		this.fixDate = fixDate;
		this.resolution = resolution;
		this.sum = (sum);
		this.desc = (desc);
		this.ver = ver;
		this.fixVer = fixVer;
		this.type = type;
		this.fileList = fileList;
		this.relationList = relationList;
		this.fileMethodMap = fileMethodMap;
	}
	public Bug() {
		super();
	}


	public String getBugID() {
		return bugID;
	}
	public void setBugID(String bugID) {
		this.bugID = bugID;
	}
	public String getOpenDate() {
		return openDate;
	}
	public void setOpenDate(String openDate) {
		this.openDate = openDate;
	}
	public String getFixData() {
		return fixDate;
	}
	public void setFixData(String fixData) {
		this.fixDate = fixData;
	}
	public String getResolution() {
		return resolution;
	}
	public void setResolution(String resolution) {
		this.resolution = resolution;
	}
	public String getSum() {
		return sum;
	}
	public void setSum(String sum) {
		this.sum = sum;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getVer() {
		return ver;
	}
	public void setVer(String ver) {
		this.ver = ver;
	}
	public String getFixVer() {
		return fixVer;
	}
	public void setFixVer(String fixVer) {
		this.fixVer = fixVer;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public ArrayList<String> getFileList() {
		return fileList;
	}
	public void setFileList(ArrayList<String> fileList) {
		this.fileList = fileList;
	}
	public HashMap<String, String> getRelationList() {
		return relationList;
	}
	public void setRelationList(HashMap<String, String> relationList) {
		this.relationList = relationList;
	}
	public HashMap<String, HashSet<String>> getFileMethodMap() {
		return fileMethodMap;
	}
	public void setFileMethodMap(HashMap<String, HashSet<String>> fileMethodMap) {
		this.fileMethodMap = fileMethodMap;
	}
	public void addFileMethodMap(HashMap<String, HashSet<String>> fileMethodMap) {
		if(this.fileMethodMap == null) {
			this.fileMethodMap = fileMethodMap;
		}else {
			Iterator<String> fileIter = fileMethodMap.keySet().iterator();
			while(fileIter.hasNext()) {
				String fileName = fileIter.next();
//				if(!fileList.contains(fileName)) {
//					System.err.println("WARN: NO FILE in Bench4BL "+fileName+" "+fileMethodMap.get(fileName));
//					continue;
//				}
				if(this.fileMethodMap.containsKey(fileName)) {
					HashSet<String> methods = this.fileMethodMap.get(fileName);
					methods.addAll(fileMethodMap.get(fileName));
					this.fileMethodMap.replace(fileName, methods);
				}else {
					this.fileMethodMap.put(fileName, fileMethodMap.get(fileName));
				}
			}
		}
	}
	
	@Override
	public String toString() {
		return "Bug [bugID=" + bugID + ", openDate=" + openDate + ", fixData=" + fixDate + ", resolution=" + resolution
				+ ", sum=" + sum + ", desc=" + desc + ", ver=" + ver + ", fixVer=" + fixVer + ", type=" + type
				+ ", fileList=" + fileList + ", relationList=" + relationList + ", fileMethodMap=" + fileMethodMap
				+ "]";
	}
	
	

}
