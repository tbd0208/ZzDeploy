package zz.projectDistribute.util.io;

import java.io.File;

public interface FileConverter<T>{
	public T converte(File f);
}