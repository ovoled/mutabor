package extlist;

import java.util.Collection;

public interface ReadOnlyCollection<E> extends Iterable<E> {
	int size();
	boolean isEmpty();
	boolean contains(Object o);
	Object[] toArray();
	<T> T[] toArray(T[] a);
	boolean containsAll(Collection<?> c);
}
