package mutabor.internal;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import mutabor.ImmutableList;
import mutabor.MutableList;
import mutabor.ReadOnlyList;
import mutabor.internal.ImmutableListImpl;

/**
 * Internal utils for mutable & immutable lists.
 * @author Aleksej Kozlov
 */
public class InternalUtils {
	
	protected static final Object[] EMPTY_ARRAY = new Object[0];
	
	protected static final Field data_ArrayList;
	protected static final Field size_ArrayList;
	static {
		Field data;
		Field size;
		try {
			data = ArrayList.class.getDeclaredField("elementData");
			size = ArrayList.class.getDeclaredField("size");
			data.setAccessible(true);
			size.setAccessible(true);
		} catch (@SuppressWarnings("unused") Exception e) {
			data = null;
			size = null;
		}
		data_ArrayList = data;
		size_ArrayList = size;
	}
	
	protected static final Class<?> class_Arrays$ArrayList;
	protected static final Field data_Arrays$ArrayList;
	static {
		Class<?> clazz;
		Field data;
		try {
			clazz = Class.forName("java.util.Arrays$ArrayList");
			data = clazz.getDeclaredField("a");
			data.setAccessible(true);
		} catch (@SuppressWarnings("unused") Exception e) {
			clazz = null;
			data = null;
		}
		class_Arrays$ArrayList = clazz;
		data_Arrays$ArrayList = data;
	}
	
	public static <E> ImmutableList<E> copyToImmutableList(E[] original) {
		if (original == null) {
			return new ImmutableListImpl<>(EMPTY_ARRAY);
		}
		
		Object[] arr = new Object[original.length];
		System.arraycopy(original, 0, arr, 0, original.length);
		return new ImmutableListImpl<>(arr);
	}
	
	public static <E> ImmutableList<E> copyToImmutableList(Collection<? extends E> original) {
		if (original == null) {
			return new ImmutableListImpl<>(EMPTY_ARRAY);
		}
		
		return new ImmutableListImpl<>(original.toArray());
	}
	
	public static <E> ImmutableList<E> convertToImmutableList(Collection<? extends E> original) {
		if (original == null) {
			return new ImmutableListImpl<>(EMPTY_ARRAY);
		}
		
		Collection<? extends E> c = original;
		if (c instanceof MutableListImpl<?>) {
			@SuppressWarnings("unchecked")
			MutableListImpl<E> mutable = (MutableListImpl<E>) original;
			if (mutable.immutable != null) {
				mutable.list = null;
				return mutable.immutable;
			}
			c = mutable.list;
		}
		
		int size = c.size();
		Object[] arr = stealDataArray(c);
		if (arr == null) {
			return null;
		}
		
		return new ImmutableListImpl<>(arr, size);
	}
	
	public static <E> MutableList<E> copyToMutableList(Collection<? extends E> original) {
		return new MutableListImpl<>(new ArrayList<>(original));
	}
	
	public static <E> MutableList<E> convertToMutableList(List<E> original) {
		if (original instanceof MutableList<?>) {
			return (MutableList<E>) original;
		}
		
		return new MutableListImpl<>(original);
	}
	
	public static int hashCodeIterable(Iterable<?> iterable) {
		//iterable must be ordered
		
		int hashCode = 1;
		for (Object obj : iterable) {
			hashCode = 31 * hashCode + (obj == null ? 0 : obj.hashCode());
		}
		return hashCode;
	}
	
	public static boolean equalIterables(Iterable<?> iterable1, Iterable<?> iterable2) {
		//iterables must be ordered
		
		Iterator<?> iterator1 = iterable1.iterator();
		Iterator<?> iterator2 = iterable2.iterator();
		while (iterator1.hasNext() && iterator2.hasNext()) {
			Object o1 = iterator1.next();
			Object o2 = iterator2.next();
			if (!(o1 == null ? o2 == null : o1.equals(o2))) {
				return false;
			}
		}
		return !(iterator1.hasNext() || iterator2.hasNext());
	}
	
	public static boolean equalLists(List<?> list1, Object list2) {
		if (list1 == list2) {
			return true;
		}
		if (!(list2 instanceof List<?> || list2 instanceof ReadOnlyList<?>)) {
			return false;
		}
		return equalIterables(list1, (Iterable<?>) list2);
	}
	
	public static boolean equalLists(ReadOnlyList<?> list1, Object list2) {
		if (list1 == list2) {
			return true;
		}
		if (!(list2 instanceof List<?> || list2 instanceof ReadOnlyList<?>)) {
			return false;
		}
		return equalIterables(list1, (Iterable<?>) list2);
	}
	
	protected static Object[] stealDataArray(Collection<?> original) {
		if (original instanceof ArrayList<?>) {
			if (data_ArrayList != null) {
				try {
					Object [] arr = (Object[]) data_ArrayList.get(original);
					data_ArrayList.set(original, EMPTY_ARRAY);
					size_ArrayList.set(original, Integer.valueOf(0));
					return arr;
				} catch (@SuppressWarnings("unused") Exception e) {
					return null;
				}
			}
		}
		
		if (class_Arrays$ArrayList != null) {
			try {
				if (class_Arrays$ArrayList.isInstance(original)) {
					Object [] arr = (Object[]) data_Arrays$ArrayList.get(original);
					data_Arrays$ArrayList.set(original, EMPTY_ARRAY);
					return arr;
				}
			} catch (@SuppressWarnings("unused") Exception e) {
				return null;
			}
		}
		
		return null;
	}
	
	private InternalUtils() {
	}
}
