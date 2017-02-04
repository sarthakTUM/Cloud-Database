package app_kvServer;

public interface QueueWrapper<T> {

	void push(T t);
	void push(T t, long ID);
	
}
