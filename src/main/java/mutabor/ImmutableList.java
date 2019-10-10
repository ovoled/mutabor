package mutabor;

import java.util.List;

/**
 * Immutable list.
 * @param <E> the type of elements in this list
 * @author Aleksej Kozlov
 */
public interface ImmutableList<E> extends ReadOnlyList<E> {
	
	/**
	 * Returns a view of the portion of this list between the specified
	 * <tt>fromIndex</tt>, inclusive, and <tt>toIndex</tt>, exclusive. (If
	 * <tt>fromIndex</tt> and <tt>toIndex</tt> are equal, the returned list is
	 * empty.) The returned list is backed by this list.<p>
	 *
	 * @param fromIndex low endpoint (inclusive) of the subList
	 * @param toIndex high endpoint (exclusive) of the subList
	 * @return a view of the specified range within this list
	 * @throws IndexOutOfBoundsException for an illegal endpoint index value
	 * (<tt>fromIndex &lt; 0 || toIndex &gt; size ||
	 * fromIndex &gt; toIndex</tt>)
	 */
	@Override
	ImmutableList<E> subList(int fromIndex, int toIndex);
	
	/**
	 * Creates {@link java.util.List} representation of this list.
	 * Representation is still read-only, all of it's modification methods
	 * throws {@link java.lang.UnsupportedOperationException}.
	 * @return representation
	 */
	List<E> toList();
}
