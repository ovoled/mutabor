package mutabor.internal;

import java.util.List;
import java.util.RandomAccess;

import mutabor.ImmutableList;

/**
 * Immutable list implementation (with random access marker).
 * @param <E> the type of elements in this list
 * @author Aleksej Kozlov {@literal <ovoled@gmail.com>}
 */
public class RandomAccessMutableListImpl<E> extends MutableListImpl<E> implements RandomAccess {
	
	private static final long serialVersionUID = 5455093627290146672L;
	
	protected RandomAccessMutableListImpl(ImmutableList<E> immutable) {
		super(immutable);
	}
	
	protected RandomAccessMutableListImpl(List<E> list) {
		super(list);
	}
}
