package zz.projectDistribute.util.eclipse;

public class ProjectFile implements Comparable<ProjectFile>{

	private String id;
	private String path;
	private String editDt;
	private long size;
	private long lastModified;
	private String note;
	
	public String getId(){
		return id;
	}
	public void setId(String id){
		this.id = id;
	}
	public String getPath(){
		return path;
	}
	public void setPath(String path){
		this.path = path;
	}
	public String getEditDt(){
		return editDt;
	}
	public void setEditDt(String editDt){
		this.editDt = editDt;
	}	
	public long getSize(){
		return size;
	}
	public void setSize(long size){
		this.size = size;
	}
	public long getLastModified(){
		return lastModified;
	}
	public void setLastModified(long lastModified){
		this.lastModified = lastModified;
	}
	public String getNote(){
		return note;
	}
	public void setNote(String note){
		this.note = note;
	}
	
	@Override
	public int compareTo(ProjectFile arg0) {
		return path.compareTo(arg0.getPath());
	}
	@Override
	public String toString() {
		return path;
	}
}