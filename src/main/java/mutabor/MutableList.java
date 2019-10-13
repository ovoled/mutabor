package mutabor;

import java.util.List;

/**
 * Mutable list.
 * @param <E> the type of elements in this list
 * @author Aleksej Kozlov {@literal <ovoled@gmail.com>}
 */
public interface MutableList<E> extends List<E> {
	
	/**
	 * Returns a view of the portion of this list between the specified
	 * <tt>fromIndex</tt>, inclusive, and <tt>toIndex</tt>, exclusive. (If
	 * <tt>fromIndex</tt> and <tt>toIndex</tt> are equal, the returned list is
	 * empty.) The returned list is backed by this list, so non-structural
	 * changes in the returned list are reflected in this list, and vice-versa.
	 * The returned list supports all of the optional list operations supported
	 * by this list.<p>
	 *
	 * This method eliminates the need for explicit range operations (of
	 * the sort that commonly exist for arrays). Any operation that expects
	 * a list can be used as a range operation by passing a subList view
	 * instead of a whole list. For example, the following idiom
	 * removes a range of elements from a list:
	 * <pre>
	 *     list.subList(from, to).clear();
	 * </pre>
	 * Similar idioms may be constructed for <tt>indexOf</tt> and
	 * <tt>lastIndexOf</tt>, and all of the algorithms in the
	 * <tt>Collections</tt> class can be applied to a subList.<p>
	 *
	 * The semantics of the list returned by this method become undefined if
	 * the backing list (i.e., this list) is <i>structurally modified</i> in
	 * any way other than via the returned list. (Structural modifications are
	 * those that change the size of this list, or otherwise perturb it in such
	 * a fashion that iterations in progress may yield incorrect results.)
	 *
	 * @param fromIndex low endpoint (inclusive) of the subList
	 * @param toIndex high endpoint (exclusive) of the subList
	 * @return a view of the specified range within this list
	 * @throws IndexOutOfBoundsException for an illegal endpoint index value
	 * (<tt>fromIndex &lt; 0 || toIndex &gt; size ||
	 * fromIndex &gt; toIndex</tt>)
	 */
	@Override
	MutableList<E> subList(int fromIndex, int toIndex);
	
	/**
	 * Compares contents of this list to the specified {@link java.lang.Iterable}.
	 * The result is {@code true} if and only if this list represents the same
	 * sequence of objects as the specified {@link java.lang.Iterable}.
	 * @param iterable the {@link java.lang.Iterable} to compare this list against
	 * @return {@code true} if this list represents the same sequence of objects
	 * as the specified {@link java.lang.Iterable}, {@code false} otherwise
	 */
	boolean contentEquals(Iterable<? extends E> iterable);
	
	/**
	 * Creates immutable snapshot of this list.
	 * Snapshot is saved internally. Two calls of this method
	 * will return the same object if list was not changed between calls.
	 * Saved snapshot released when list is changed or
	 * {@link #releaseSnapshot} is called.
	 * @return immutable snapshot
	 */
	ImmutableList<E> snapshot();
	
	/**
	 * Releases internally saved copy of snapshot.
	 * May be used for reduce memory consumption.
	 */
	void releaseSnapshot();
}
