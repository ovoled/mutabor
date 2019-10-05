package extlist;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

public class ImmutableList<E> implements ReadOnlyList<E>, RandomAccess, Cloneable, Serializable {
	
	private static final long serialVersionUID = 39387334160562704L;
	
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
	
	protected final Object[] data;
	
	//конструктор, аналогичный ArrayList
	public ImmutableList() {
		this.data = EMPTY_ARRAY;
	}
	
	//конструктор, аналогичный ArrayList
	//преобразующий, если original instanceof ArrayList, иначе копирующий
	public ImmutableList(Collection<? extends E> original) {
		Object[] arr = null;
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
		}
		
		if (arr == null) {
			//либо получили не ArrayList, либо отобрать массив не получилось - копируем
			arr = original.toArray();
		}
		
		this.data = arr;
	}
	
	//копирующий конструктор
	public ImmutableList(E[] original) {
		if (original == null) {
			this.data = EMPTY_ARRAY;
		} else {
			this.data = new Object[original.length];
			System.arraycopy(original, 0, this.data, 0, original.length);
		}
	}
	
	@Override
	public int size() {
		return data.length;
	}
	
	@Override
	public boolean isEmpty() {
		return data.length <= 0;
	}
	
	@Override
	public boolean contains(Object o) {
		return indexOf(o) >= 0;
	}
	
	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object e : c) {
			if (!contains(e)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public int indexOf(Object o) {
		if (o == null) {
			for (int i = 0; i < data.length; i++) {
				if (data[i] == null) {
					return i;
				}
			}
		} else {
			for (int i = 0; i < data.length; i++) {
				if (o.equals(data[i])) {
					return i;
				}
			}
		}
		return -1;
	}
	
	@Override
	public int lastIndexOf(Object o) {
		if (o == null) {
			for (int i = data.length - 1; i >= 0; i--) {
				if (data[i] == null) {
					return i;
				}
			}
		} else {
			for (int i = data.length - 1; i >= 0; i--) {
				if (o.equals(data[i])) {
					return i;
				}
			}
		}
		return -1;
	}
	
	@Override
	public Object[] toArray() {
		return Arrays.copyOf(data, data.length);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		if (a.length < data.length) {
			return (T[]) Arrays.copyOf(data, data.length, a.getClass());
		}
		System.arraycopy(data, 0, a, 0, data.length);
		if (a.length > data.length) {
			a[data.length] = null;
		}
		return a;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public E get(int index) {
		return (E) data[index];
	}
	
	@Override
	public ListIterator<E> listIterator(int index) {
		if (index < 0 || index > data.length) {
			throw new IndexOutOfBoundsException("Index: "+index);
		}
		return new ListItr(0, data.length, index);
	}
	
	@Override
	public ListIterator<E> listIterator() {
		return new ListItr(0, data.length, 0);
	}
	
	@Override
	public Iterator<E> iterator() {
		return new Itr(0, data.length);
	}
	
	protected class Itr implements Iterator<E> {
		protected int fromIndex;
		protected int toIndex;
		protected int cursor;
		
		protected Itr(int fromIndex, int toIndex) {
			this.fromIndex = fromIndex;
			this.toIndex = toIndex;
			this.cursor = fromIndex;
		}
		
		@Override
		public boolean hasNext() {
			return cursor < toIndex;
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public E next() {
			if (cursor >= toIndex) {
				throw new NoSuchElementException();
			}
			return (E) data[cursor++];
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	protected class ListItr extends Itr implements ListIterator<E> { //TODO проверить - вероятно, работает неправильно
		
		protected ListItr(int fromIndex, int toIndex, int index) {
			super(fromIndex, toIndex);
			cursor = index;
		}
		
		@Override
		public boolean hasPrevious() {
			return cursor > fromIndex;
		}
		
		@Override
		public int nextIndex() {
			return cursor;
		}
		
		@Override
		public int previousIndex() {
			return cursor - 1;
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public E previous() {
			if (cursor < fromIndex + 1) {
				throw new NoSuchElementException();
			}
			return (E) data[cursor];
		}
		
		@Override
		public void set(E e) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void add(E e) {
			throw new UnsupportedOperationException();
		}
	}
	
	@Override
	public ReadOnlyList<E> subList(int fromIndex, int toIndex) {
		subListRangeCheck(fromIndex, toIndex, data.length);
		return new SubList(fromIndex, toIndex);
	}
	
	protected static void subListRangeCheck(int fromIndex, int toIndex, int size) {
		if (fromIndex < 0) {
			throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
		}
		if (toIndex > size) {
			throw new IndexOutOfBoundsException("toIndex = " + toIndex);
		}
		if (fromIndex > toIndex) {
			throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
		}
	}
	
	protected class SubList implements ReadOnlyList<E>, RandomAccess {
		protected final int fromIndex;
		protected final int toIndex;
		protected final int size;
		
		protected SubList(int fromIndex, int toIndex) {
			this.fromIndex = fromIndex;
			this.toIndex = toIndex;
			this.size = toIndex - fromIndex;
		}
		
		@Override
		public int size() {
			return this.size;
		}
		
		@Override
		public boolean isEmpty() {
			return this.size == 0;
		}
		
		@Override
		public boolean contains(Object o) {
			return indexOf(o) >= 0;
		}
		
		
		@Override
		public boolean containsAll(Collection<?> c) {
			for (Object e : c) {
				if (!contains(e)) {
					return false;
				}
			}
			return true;
		}
		
		@Override
		public int indexOf(Object o) {
			if (o == null) {
				for (int i = fromIndex; i < toIndex; i++) {
					if (data[i] == null) {
						return i;
					}
				}
			} else {
				for (int i = fromIndex; i < toIndex; i++) {
					if (o.equals(data[i])) {
						return i;
					}
				}
			}
			return -1;
		}
		
		@Override
		public int lastIndexOf(Object o) {
			if (o == null) {
				for (int i = toIndex - 1; i >= fromIndex; i--) {
					if (data[i] == null) {
						return i;
					}
				}
			} else {
				for (int i = toIndex - 1; i >= fromIndex; i--) {
					if (o.equals(data[i])) {
						return i;
					}
				}
			}
			return -1;
		}
		
		@Override
		public Object[] toArray() {
			return Arrays.copyOfRange(data, fromIndex, toIndex);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public <T> T[] toArray(T[] a) {
			if (a.length < size) {
				return (T[]) Arrays.copyOfRange(data, fromIndex, toIndex, a.getClass());
			}
			System.arraycopy(data, fromIndex, a, 0, size);
			if (a.length > size) {
				a[size] = null;
			}
			return a;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public E get(int index) {
			rangeCheck(index);
			return (E) data[fromIndex + index];
		}
		
		@Override
		public Iterator<E> iterator() {
			return listIterator();
		}
		
		@Override
		public ListIterator<E> listIterator() {
			return listIterator(0);
		}
		
		@Override
		public ListIterator<E> listIterator(int index) {
			return new ListItr(fromIndex, toIndex, index);
		}
		
		@Override
		public ReadOnlyList<E> subList(int fromIndexSub, int toIndexSub) {
			subListRangeCheck(fromIndexSub, toIndexSub, size);
			return new SubList(fromIndex + fromIndexSub, fromIndex + toIndexSub);
		}
		
		protected void rangeCheck(int index) {
			if (index < 0 || index >= this.size) {
				throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.size);
			}
		}
	}
}
