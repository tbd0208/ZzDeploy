package zz.projectDistribute.util.io;

import java.io.File;

public interface FileSystemVisitor{
	boolean visitDirectory(File f);
	boolean filterFile(File f);
	boolean visitFile(File f);
}
