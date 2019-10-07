package mutabor.internal;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

import mutabor.ImmutableList;
import mutabor.internal.ImmutableListImpl;

/**
 * Internal utils for mutable & immutable lists.
 * @author Aleksej Kozlov
 */
public class InternalUtils {
	
	protected static final Object[] EMPTY_ARRAY = new Object[0];
	
	protected static final Field data_ArrayList; //TODO check thread safety
	protected static final Class<?> class_Arrays$ArrayList;
	protected static final Field data_Arrays$ArrayList;
	static {
		try {
			data_ArrayList = ArrayList.class.getDeclaredField("elementData");
			data_ArrayList.setAccessible(true);
			
			class_Arrays$ArrayList = Class.forName("java.util.Arrays$ArrayList");
			data_Arrays$ArrayList = class_Arrays$ArrayList.getDeclaredField("a");
			data_Arrays$ArrayList.setAccessible(true);
		} catch (NoSuchFieldException | SecurityException | ClassNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public static <E> ImmutableList<E> copyToImmutableList(E[] original) {
		Object[] arr;
		if (original == null) {
			arr = EMPTY_ARRAY;
		} else {
			arr = new Object[original.length];
			System.arraycopy(original, 0, arr, 0, original.length);
		}
		
		return new ImmutableListImpl<>(arr);
	}
	
	public static <E> ImmutableList<E> convertToImmutableList(Collection<? extends E> original) {
		Object[] arr = stealDataArray(original);
		
		if (arr == null) {
			//либо получили не ArrayList, либо отобрать массив не получилось - копируем
			arr = original.toArray();
		}
		
		return new ImmutableListImpl<>(arr);
	}
	
	protected static Object[] stealDataArray(Collection<?> original) {
		Object [] arr;
		if (original instanceof ArrayList) {
			//грязный хак - отбираем у переданного списка его массив;
			//переданный список намеренно делаем неработоспособным
			
			try {
				arr = (Object[]) data_ArrayList.get(original);
				data_ArrayList.set(original, null);
			} catch (@SuppressWarnings("unused") IllegalArgumentException | IllegalAccessException e) {
				arr = null;
			}
		} else if (class_Arrays$ArrayList.isInstance(original)) {
			//грязный хак - отбираем у переданного списка его массив;
			//переданный список намеренно делаем неработоспособным
			
			try {
				arr = (Object[]) data_Arrays$ArrayList.get(original);
				data_Arrays$ArrayList.set(original, null);
			} catch (@SuppressWarnings("unused") IllegalArgumentException | IllegalAccessException e) {
				arr = null;
			}
		} else {
			arr = null;
		}
		return arr;
	}
	
	private InternalUtils() {
	}
}
