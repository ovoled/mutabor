package mutabor;

import java.util.Collection;

import mutabor.internal.InternalUtils;

/**
 * Utils for mutable & immutable lists.
 * @author Aleksej Kozlov
 */
public class Mutabor {
	
	/**
	 * Creates {@link ImmutableList} by copying original array.
	 * If original array is null, returns empty list.
	 * Copying procedure may be slow for big arrays.
	 * @param original original array
	 * @return immutable list
	 */
	public static <E> ImmutableList<E> copyToImmutableList(E[] original) {
		return InternalUtils.copyToImmutableList(original);
	}
	
	/**
	 * Creates {@link ImmutableList} by copying original collection.
	 * If original collection is null, returns empty list.
	 * Copying procedure may be slow for big collections.
	 * @param original original collection
	 * @return immutable list
	 */
	public static <E> ImmutableList<E> copyToImmutableList(Collection<? extends E> original) {
		return InternalUtils.copyToImmutableList(original);
	}
	
	/**
	 * Creates {@link ImmutableList} by converting original collection.
	 * If original collection is null, returns empty list.
	 * Converting procedure is fast but applicable only for {@link java.util.ArrayList} and {@link java.util.Arrays.ArrayList}.
	 * After conversion original collection will be cleared and should no longer be used.
	 * If fast conversion cannot be done, (slow) copying procedure will be used.
	 * @param original original collection
	 * @return immutable list
	 */
	public static <E> ImmutableList<E> convertToImmutableList(Collection<? extends E> original) {
		return convertToImmutableList(original, false);
	}
	
	/**
	 * Creates {@link ImmutableList} by converting original collection.
	 * If original collection is null, returns empty list.
	 * Converting procedure is fast but applicable only for {@link java.util.ArrayList} and {@link java.util.Arrays.ArrayList}.
	 * After conversion original collection will be cleared and should no longer be used.
	 * If fast conversion cannot be done, behaviour id determined by {@code strictFast} flag:
	 * when {@code strictFast == false}, (slow) copying procedure will be used,
	 * when {@code strictFast == true}, method returns null.
	 * @param original original collection
	 * @param strictFast flag: never copy
	 * @return immutable list or {@code null}
	 */
	public static <E> ImmutableList<E> convertToImmutableList(Collection<? extends E> original, boolean strictFast) {
		ImmutableList<E> res = InternalUtils.convertToImmutableList(original);
		if (res != null) {
			return res;
		}
		
		return strictFast ? null : InternalUtils.copyToImmutableList(original);
	}
	
	private Mutabor() {
	}
}
