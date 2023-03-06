package zz.projectDistribute.dm.srcMng;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import zz.projectDistribute.config.Config;
import zz.projectDistribute.util.StringUtils;
import zz.projectDistribute.util.eclipse.ProjectFile;
import zz.projectDistribute.util.eclipse.Workingset;
import zz.projectDistribute.util.eclipse.WorkingsetsReader;

public class WorkingsetMng{
	
	final private WorkingsetsReader workingsetsReader;
	
	public Map<String, Workingset> workingsetsMap = null;
	public Map<String, List<Workingset>> workingsetsGroupMap = null;
	
	public WorkingsetMng(String workingsetPath) {
		this.workingsetsReader = new WorkingsetsReader(workingsetPath);
	}
	
	public void init() {
		this.workingsetsGroupMap = new TreeMap<>();
		this.workingsetsMap = workingsetsReader.getWorkingset("^([^\\d]+\\s)?\\S{1}\\d{3}.+");

		Pattern pattern = Pattern.compile("^(([^\\d]+)\\s)\\S{1}\\d{3}.+"); // 4자리 숫자앞의 문자열
		Map<String,Workingset> workingsetsMap = this.workingsetsMap;
		for(Workingset workingset : workingsetsMap.values()){
			String name = workingset.getName();
			Matcher matcher = pattern.matcher(name);
			String group = matcher.find()?matcher.group(2):"";
			if(group.equals("")) group = "@NONE_GROUP";
			else {
				workingset.setName(name.substring(group.length()));
			}
			List<Workingset> list = workingsetsGroupMap.get(group);
			if(list==null){
				list = new ArrayList<>();
				workingsetsGroupMap.put(group,list);
			}
			list.add(workingset);
		}
		
		for(Entry<String,List<Workingset>> e : workingsetsGroupMap.entrySet()){
			List<Workingset> workinsets = e.getValue();
			Collections.sort(workinsets);
			
			for (Workingset workingset : workinsets) {
				List<ProjectFile> projectFiles = workingset.getProjectFiles();
				Collections.sort(projectFiles);
			}
		}
	}

	
	public List<Workingset> getWorkingsetGroup(String workgingsetGroupName) {
		List<Workingset> list = this.workingsetsGroupMap.get(workgingsetGroupName);
		for (Workingset workingset : list) {
			
			List<ProjectFile> projectFiles = workingset.getProjectFiles();
			projectFiles.sort(new Comparator<ProjectFile>(){
				public int compare(ProjectFile aa,ProjectFile bb){
					String a = StringUtils.geUrltLastSimpleName(aa.getPath());
					String b = StringUtils.geUrltLastSimpleName(bb.getPath());
					String ax = StringUtils.extension(aa.getPath());
					String bx = StringUtils.extension(bb.getPath());
					return ax.equals(bx)?a.compareTo(b):ax.compareTo(bx);
				}
			});
			
			for (ProjectFile projectFile : projectFiles) {
				File f = new File(Config.LOCAL_PROJECT_PRE_PATH+projectFile.getPath());
				projectFile.setSize(f.length());
				projectFile.setLastModified(f.lastModified());
			}
		}
		return list;
	}
	
}
