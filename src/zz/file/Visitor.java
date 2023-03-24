package zz.file;

public interface Visitor<T> {
	boolean visite(T t);
}