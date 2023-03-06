package zz.projectDistribute.util.eclipse;

import java.util.List;

public class Workingset implements Comparable<Workingset>{

	private String id;
	private String name;
	private List<ProjectFile> projectFiles;
	
	public String getId(){
		return id;
	}
	public void setId(String id){
		this.id = id;
	}
	public String getName(){
		return name;
	}
	public void setName(String name){
		this.name = name;
	}
	
	public List<ProjectFile> getProjectFiles() {
		return projectFiles;
	}
	public void setProjectFiles(List<ProjectFile> projectFiles) {
		this.projectFiles = projectFiles;
	}
	
	@Override
	public int compareTo(Workingset o){
		return this.name.compareTo(o.name);
	}
	@Override
	public String toString() {
		return this.name;
	}
	
	
}
