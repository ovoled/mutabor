package mutabor;

import java.util.ListIterator;

public interface ReadOnlyList<E> extends ReadOnlyCollection<E> {
	E get(int index);
	int indexOf(Object o);
	int lastIndexOf(Object o);
	ListIterator<E> listIterator();
	ListIterator<E> listIterator(int index);
	ReadOnlyList<E> subList(int fromIndex, int toIndex);
}
