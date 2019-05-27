package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.Bug;

public class XMLParser {

	public static ArrayList<Bug> getFixedBugsFromXML(File bugFile) {
		ArrayList<Bug> bugList = new ArrayList<Bug>();
		String repo = "";
		try {
			String content = FileUtil.readFileContent(bugFile);
			Document dom = DocumentHelper.parseText(content);
			Element rootElt = dom.getRootElement();
			List<Element> bugEle = rootElt.elements("bug");
			for (Element bug : bugEle) {
				String bid = bug.attributeValue("id");
				String openDate = bug.attributeValue("opendate");
				String fixDate = bug.attributeValue("fixdate");
				String resol = bug.attributeValue("resolution");
				Element bugInfo = bug.element("buginformation");
				String summary = bugInfo.elementText("summary");
				String description = bugInfo.elementText("description");
				String version = bugInfo.elementText("version");
				String fixedVersion = bugInfo.elementText("fixedVersion");
				String type = bugInfo.elementText("type");
				ArrayList<String> fileList = new ArrayList<String>();
				List<Element> files = bug.element("fixedFiles").elements("file");
				for (Element file : files) {
					String fileName = file.getStringValue();
					fileList.add(fileName);
				}

				HashMap<String, String> linkList = new HashMap<String, String>();
				try {
					List<Element> relations = bug.element("links").elements("link");
					for (Element rel : relations) {
						String relType = rel.attributeValue("type");
						String relDesc = rel.attributeValue("description");
						String relBug = rel.getStringValue();
						linkList.put(relBug, relType + ":" + relDesc);
					}
				} catch (NullPointerException e) {
					;
				}

				Bug bugEntity = new Bug(bid, openDate, fixDate, resol, summary, description, version, fixedVersion,
						type, fileList, linkList);
				bugList.add(bugEntity);
			}

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return bugList;
	}
	
	public static HashMap<String, Bug> getFixedBugsFromXMLToMap(File bugFile) {
		HashMap<String, Bug> bugList = new HashMap<String, Bug>();
		String repo = "";
		try {
			String content = FileUtil.readFileContent(bugFile);
			Document dom = DocumentHelper.parseText(content);
			Element rootElt = dom.getRootElement();
			List<Element> bugEle = rootElt.elements("bug");
			for (Element bug : bugEle) {
				String bid = bug.attributeValue("id");
				String openDate = bug.attributeValue("opendate");
				String fixDate = bug.attributeValue("fixdate");
				String resol = bug.attributeValue("resolution");
				Element bugInfo = bug.element("buginformation");
				String summary = bugInfo.elementText("summary");
				String description = bugInfo.elementText("description");
				String version = bugInfo.elementText("version");
				String fixedVersion = bugInfo.elementText("fixedVersion");
				String type = bugInfo.elementText("type");
				ArrayList<String> fileList = new ArrayList<String>();
				List<Element> files = bug.element("fixedFiles").elements("file");
				for (Element file : files) {
					String fileName = file.getStringValue();
					fileList.add(fileName);
				}

				HashMap<String, String> linkList = new HashMap<String, String>();
				try {
					List<Element> relations = bug.element("links").elements("link");
					for (Element rel : relations) {
						String relType = rel.attributeValue("type");
						String relDesc = rel.attributeValue("description");
						String relBug = rel.getStringValue();
						linkList.put(relBug, relType + ":" + relDesc);
					}
				} catch (NullPointerException e) {
					;
				}

				Bug bugEntity = new Bug(bid, openDate, fixDate, resol, summary, description, version, fixedVersion,
						type, fileList, linkList);
				bugList.put(bid,bugEntity);
			}

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return bugList;
	}
	
	public static ArrayList<Bug> getFixedBugsFromSismanXML(String bugFile) {
		ArrayList<Bug> bugList = new ArrayList<Bug>();
		String repo = "";
		try {
			String content = FileUtil.readFileContent(new File(bugFile));
			Document dom = DocumentHelper.parseText(content);
			Element rootElt = dom.getRootElement();
			List<Element> bugEle = rootElt.elements("bug");
			for (Element bug : bugEle) {
				String bid = bug.attributeValue("id");
				ArrayList<String> fileList = new ArrayList<String>();
				List<Element> files = bug.element("fixedfiles").elements("file");
				for (Element file : files) {
					String fileName = file.getStringValue();
					fileList.add(fileName);
				}
				Bug bugEntity = new Bug();
				bugEntity.setBugID(bid);
				bugEntity.setFileList(fileList);
				
				bugList.add(bugEntity);
			}

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return bugList;
	}

	public static ArrayList<Bug> getFixedBugsFromXMLWithMethod(File bugFile) {
		ArrayList<Bug> bugList = new ArrayList<Bug>();
		String content = FileUtil.readFileContent(bugFile);
		String repo = "";
		try {
			Document dom = DocumentHelper.parseText(content);
			Element rootElt = dom.getRootElement();
			List<Element> bugEle = rootElt.elements("bug");
			for (Element bug : bugEle) {
				String bid = bug.attributeValue("id");
				String openDate = bug.attributeValue("opendate");
				String fixDate = bug.attributeValue("fixdate");
				String resol = bug.attributeValue("resolution");
				Element bugInfo = bug.element("buginformation");
				String summary = recoverXMlNoise(bugInfo.elementText("summary"));
				String description = recoverXMlNoise(bugInfo.elementText("description"));
				String version = bugInfo.elementText("version");
				String fixedVersion = bugInfo.elementText("fixedVersion");
				String type = bugInfo.elementText("type");
				ArrayList<String> fileList = new ArrayList<String>();
				HashMap<String, HashSet<String>> fileMethodMap = new HashMap<String, HashSet<String>>();
				List<Element> files = bug.element("fixedFiles").elements("file");
				for (Element file : files) {
					String fileName = file.attributeValue("name");
					fileList.add(fileName);
					List<Element> methods = file.elements("method");
					HashSet<String> methodSet = new HashSet<String>();
					for (Element method : methods) {
						String methodName = recoverXMlNoise(method.getStringValue());
						methodSet.add(methodName);
					}
					fileMethodMap.put(fileName, methodSet);
				}

				HashMap<String, String> linkList = new HashMap<String, String>();
				try {
					List<Element> relations = bug.element("links").elements("link");
					for (Element rel : relations) {
						String relType = rel.attributeValue("type");
						String relDesc = rel.attributeValue("description");
						String relBug = rel.getStringValue();
						linkList.put(relBug, relType + ":" + relDesc);
					}
				} catch (NullPointerException e) {
					;
				}

				Bug bugEntity = new Bug(bid, openDate, fixDate, resol, summary, description, version, fixedVersion,
						type, fileList, linkList, fileMethodMap);
				bugList.add(bugEntity);
			}

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			System.err.println(bugFile);
			System.err.println(content);
			e1.printStackTrace();
		}
		return bugList;
	}

	public static void writeBugXML(ArrayList<Bug> bugList, String filePath, String project) {

		try {
			File file = new File(filePath.replace(".xml", ""));
			if (!file.exists()) {
				file.mkdirs();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
			bw.write("<?xml version = \"1.0\" encoding = \"UTF-8\" ?>\n");
			bw.write("<bugrepository name=\"" + project + "\">\n");
			for (int i = 0; i < bugList.size(); i++) {
				Bug bug = bugList.get(i);
				bw.write("<bug id=\"" + bug.getBugID() + "\" " + "opendate=\"" + bug.getOpenDate() + "\" fixdate=\""
						+ bug.getFixData() + "\" resolution=\"" + bug.getResolution() + "\">\n");
				bw.write("<buginformation>\n");
				bw.write("<summary>" + removeXMlNoise(bug.getSum()) + "</summary>\n");
				bw.write("<description>" + removeXMlNoise(bug.getDesc()) + "</description>\n");
				bw.write("<version>" + bug.getVer() + "</version>\n");
				bw.write("<fixedVersion>" + bug.getFixVer() + "</fixedVersion>\n");
				bw.write("<type>" + bug.getType() + "</type>\n");
				bw.write("</buginformation>\n");

				bw.write("<links>\n");
				HashMap<String, String> relList = bug.getRelationList();
				Iterator<String> relIter = relList.keySet().iterator();
				while (relIter.hasNext()) {
					String relID = relIter.next();
					String type = relList.get(relID).split(":")[0];
					String desc = relList.get(relID).split(":")[1];
					bw.write("<link type=\"" + type + "\" description=\"" + desc + "\">" + relID + "</link>\n");
				}
				bw.write("</links>\n");

				ArrayList<String> fileList = bug.getFileList();
				bw.write("<fixedFiles>\n");
				for (int j = 0; j < fileList.size(); j++) {
					String fileName = fileList.get(j);
					bw.write("<file type=\"M\" name = \"" + fileName + "\">");
					bw.write("</file>\n");
				}
				bw.write("</fixedFiles>\n");
				bw.write("</bug>\n");
			}
			bw.write("</bugrepository>\n");
			file = new File(filePath.replace(".xml", ""));
			file.delete();

			bw.close();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public static void writeBugXMLWithMethod(ArrayList<Bug> bugList, String filePath, String project) {

		try {			
			System.out.println(filePath);
			BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
			bw.write("<?xml version = \"1.0\" encoding = \"UTF-8\" ?>\n");
			bw.write("<bugrepository name=\"" + project + "\">\n");
			for (int i = 0; i < bugList.size(); i++) {
				Bug bug = bugList.get(i);
				bw.write("<bug id=\"" + bug.getBugID() + "\" " + "opendate=\"" + bug.getOpenDate() + "\" fixdate=\""
						+ bug.getFixData() + "\" resolution=\"" + bug.getResolution() + "\">\n");
				bw.write("<buginformation>\n");
				bw.write("<summary>" + removeXMlNoise(bug.getSum()) + "</summary>\n");
				bw.write("<description>" + removeXMlNoise(bug.getDesc()) + "</description>\n");
				bw.write("<version>" + bug.getVer() + "</version>\n");
				bw.write("<fixedVersion>" + bug.getFixVer() + "</fixedVersion>\n");
				bw.write("<type>" + bug.getType() + "</type>\n");
				bw.write("</buginformation>\n");

				bw.write("<links>\n");
				HashMap<String, String> relList = bug.getRelationList();
				Iterator<String> relIter = relList.keySet().iterator();
				while (relIter.hasNext()) {
					String relID = relIter.next();
					String type = relList.get(relID).split(":")[0];
					String desc = relList.get(relID).split(":")[1];
					bw.write("<link type=\"" + type + "\" description=\"" + desc + "\">" + relID + "</link>\n");
				}
				bw.write("</links>\n");
//				System.out.println(bug);
				ArrayList<String> fileList = bug.getFileList();
				HashMap<String, HashSet<String>> fileMethodMap = bug.getFileMethodMap();
				bw.write("<fixedFiles>\n");
				if(fileMethodMap != null) {
					Iterator<String> fileIter = fileMethodMap.keySet().iterator();
					while(fileIter.hasNext()) {
						String fileName = fileIter.next();
						bw.write("<file type=\"M\" name=\"" + fileName + "\">\n");
						if (fileMethodMap != null && fileMethodMap.containsKey(fileName)) {
							HashSet<String> methods = fileMethodMap.get(fileName);
							Iterator<String> iter = methods.iterator();
							while (iter.hasNext()) {
								bw.write("<method>" + removeXMlNoise(iter.next()) + "</method>\n");
							}
						}
						bw.write("</file>\n");
					}
				}else {
					for (int j = 0; j < fileList.size(); j++) {
						String fileName = fileList.get(j);
						bw.write("<file type=\"M\" name = \"" + fileName + "\">");
						bw.write("</file>\n");
					}
				}
				bw.write("</fixedFiles>\n");
				bw.write("</bug>\n");
			}
			bw.write("</bugrepository>\n");

			bw.close();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private static String removeXMlNoise(String text) {
		text = text.replaceAll("&", "&amp;");
		text = text.replaceAll("<", "&lt;");
		text = text.replaceAll(">", "&gt;");
		text = text.replaceAll("\"", "&quot;");
		text = text.replace("\n"," ");
		return text;
	}		

	private static String recoverXMlNoise(String text) {
		text = text.replaceAll("&amp;","&");
		text = text.replaceAll("&lt;", "<");
		text = text.replaceAll("&gt;", ">");
		text = text.replaceAll("&quot;", "\"");
		return text;
	}		
}
