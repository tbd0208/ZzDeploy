package zz.projectDistribute.util.eclipse;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class WorkingsetsReader {
	
	private final String path;
//	public static Map<String,ProjectFile> projectFileMap = new HashMap<>();
	
	public WorkingsetsReader(String path) {
		this.path=path;
	}

	private Document getDoc(){
		try {return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(path);}catch (Exception e){e.printStackTrace();}
		return null;
	}
	
	public Map<String, Workingset> getWorkingset(String matches){
		int idSeq = 0;
		Map<String, Workingset> map = new LinkedHashMap<String, Workingset>();
		
		Document doc = getDoc();
		if(doc==null) return map;
		
		NodeList childNodes = doc.getDocumentElement().getElementsByTagName("workingSet");
		for(int i=0,l=childNodes.getLength();i<l;i++){
			Element item = (Element)childNodes.item(i);
			
			NamedNodeMap attributes = item.getAttributes();
			Node editPageId = attributes.getNamedItem("editPageId");
			if(editPageId==null || !attributes.getNamedItem("name").getTextContent().matches(matches)) continue;
			
			Workingset w = new Workingset();
			w.setId(attributes.getNamedItem("id").getTextContent());
			w.setName(attributes.getNamedItem("name").getTextContent());
			map.put(w.getId(),w);
			
			final List<ProjectFile> proejctFiles = new ArrayList<>();
			
			NodeList childNodes2 = item.getElementsByTagName("item");
			for(int j=0;j<childNodes2.getLength();j++){
				Element item2 = (Element)childNodes2.item(j);
				NamedNodeMap attributes2 = item2.getAttributes();
				Node path = attributes2.getNamedItem("path");
				if(path!=null){
					// ex : /AiMgr/src/com/autoinside/admin/service/entry/ae/EntryAuctionDao.xml
					ProjectFile projectFile = new ProjectFile();
					projectFile.setId("" + ++idSeq);
					projectFile.setPath(path.getTextContent().substring(1));
//					projectFileMap.put(projectFile.getId(),projectFile);
					proejctFiles.add(projectFile);
				}else{
					String textContent = attributes2.getNamedItem("elementID").getTextContent();
					// ex : =AiMgr/src<com.autoinside.admin.controller.entry.ae{EntryAuctionController.java
					
					char[] url = textContent.toCharArray();
					char[] toUrl = new char[url.length];
					int ci = 1;
					int ci2 = 1;
					if(textContent.contains("[")){ // default class - same file sub class
						for(char ch=url[ci2];ch!='<';ch=url[++ci2]) toUrl[ci++] = ch;
						toUrl[ci++] = '/';
						ci2++;
						for(char ch=url[ci2];ch!='{';ch=url[++ci2]) toUrl[ci++] = ch=='.'?'/':ch;
						toUrl[ci++] = '/';
						ci2++;
						for(char ch=url[ci2];ch!='[';ch=url[++ci2]) ;
						ci2++;
						for(;ci2<url.length;) toUrl[ci++] = url[ci2++];
						
						ProjectFile projectFile = new ProjectFile();
						projectFile.setId("" + ++idSeq);
						projectFile.setPath(new String(toUrl,1,ci-1)+".java");
//						projectFileMap.put(projectFile.getId(),projectFile);
						proejctFiles.add(projectFile);
						
						continue;
					}else if(textContent.endsWith(".java")){
						for(char ch=url[ci2];ch!='<';ch=url[++ci2]) toUrl[ci++] = ch;
						toUrl[ci++] = '/';
						ci2++;
						for(char ch=url[ci2];ch!='{';ch=url[++ci2]) toUrl[ci++] = ch=='.'?'/':ch;
						toUrl[ci++] = '/';
						ci2++;
					}else{
						int lastIndexOf = textContent.lastIndexOf("\\");
						for(char ch=url[ci2];ci2<=lastIndexOf;ch=url[++ci2]) if(ch!='\\') toUrl[ci++] = ch;
					}
					for(;ci2<url.length;) toUrl[ci++] = url[ci2++];
					
					ProjectFile projectFile = new ProjectFile();
					projectFile.setId("" + ++idSeq);
					projectFile.setPath(new String(toUrl,1,ci-1));
//					projectFileMap.put(projectFile.getId(),projectFile);
					proejctFiles.add(projectFile);
				}
			}
			
//			sortedProjectFileMap.putAll(projectFileMap);
			w.setProjectFiles(proejctFiles);
			
//			Collections.sort(map, new Comparator<Workingset>() {
//				public int compare(Workingset paramT1, Workingset paramT2) {
//					return paramT1.getName().compareTo(paramT2.getName());
//				}
//			});
		}
		return map;
	}
	
}
