package mutabor;

import java.util.Collection;

import mutabor.internal.InternalUtils;

/**
 * Utils for mutable & immutable lists.
 * @author Aleksej Kozlov
 */
public class Mutabor {
	
	public static <E> ImmutableList<E> copyToImmutableList(E[] original) {
		return InternalUtils.copyToImmutableList(original);
	}
	
	public static <E> ImmutableList<E> convertToImmutableList(Collection<? extends E> original) {
		return InternalUtils.convertToImmutableList(original);
	}
	
	private Mutabor() {
	}
}
