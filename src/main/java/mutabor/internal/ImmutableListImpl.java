package mutabor.internal;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

import mutabor.ImmutableList;
import mutabor.MutableList;

/**
 * Immutable list implementation.
 * Based on sources of {@link java.util.ArrayList} and {@link java.util.AbstractList}.
 * @param <E> the type of elements in this list
 * @author Aleksej Kozlov {@literal <ovoled@gmail.com>}
 */
public class ImmutableListImpl<E> implements ImmutableList<E>, RandomAccess, Cloneable, Serializable {
	
	private static final long serialVersionUID = 39387334160562704L;
	
	protected final Object[] data;
	protected final int size;
	
	protected ImmutableListImpl(Object[] data) {
		this(data, data.length);
	}
	
	protected ImmutableListImpl(Object[] data, int size) {
		this.data = data;
		this.size = size;
	}
	
	@Override
	public int size() {
		return size;
	}
	
	@Override
	public boolean isEmpty() {
		return size <= 0;
	}
	
	@Override
	public boolean contains(Object o) {
		return indexOf(o) >= 0;
	}
	
	@Override
	public boolean containsAll(Iterable<?> c) {
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
			for (int i = 0; i < size; i++) {
				if (data[i] == null) {
					return i;
				}
			}
		} else {
			for (int i = 0; i < size; i++) {
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
			for (int i = size - 1; i >= 0; i--) {
				if (data[i] == null) {
					return i;
				}
			}
		} else {
			for (int i = size - 1; i >= 0; i--) {
				if (o.equals(data[i])) {
					return i;
				}
			}
		}
		return -1;
	}
	
	@Override
	public Object[] toArray() {
		return Arrays.copyOf(data, size);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		if (a.length < size) {
			return (T[]) Arrays.copyOf(data, size, a.getClass());
		}
		System.arraycopy(data, 0, a, 0, size);
		if (a.length > size) {
			a[size] = null;
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
		if (index < 0 || index > size) {
			throw new IndexOutOfBoundsException("Index: " + index);
		}
		return new ListItr(0, size, index);
	}
	
	@Override
	public ListIterator<E> listIterator() {
		return new ListItr(0, size, 0);
	}
	
	@Override
	public Iterator<E> iterator() {
		return new Itr(0, size);
	}
	
	protected class Itr implements Iterator<E> {
		protected final int fromIndex;
		protected final int toIndex;
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
	
	protected class ListItr extends Itr implements ListIterator<E> {
		
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
			cursor--;
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
	public ImmutableList<E> subList(int fromIndex, int toIndex) {
		subListRangeCheck(fromIndex, toIndex, size);
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
	
	protected class SubList implements ImmutableList<E>, RandomAccess {
		protected final int fromIndex;
		protected final int toIndex;
		protected final int subSize;
		
		protected SubList(int fromIndex, int toIndex) {
			this.fromIndex = fromIndex;
			this.toIndex = toIndex;
			this.subSize = toIndex - fromIndex;
		}
		
		@Override
		public int size() {
			return this.subSize;
		}
		
		@Override
		public boolean isEmpty() {
			return this.subSize <= 0;
		}
		
		@Override
		public boolean contains(Object o) {
			return indexOf(o) >= 0;
		}
		
		@Override
		public boolean containsAll(Iterable<?> c) {
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
			if (a.length < subSize) {
				return (T[]) Arrays.copyOfRange(data, fromIndex, toIndex, a.getClass());
			}
			System.arraycopy(data, fromIndex, a, 0, subSize);
			if (a.length > subSize) {
				a[subSize] = null;
			}
			return a;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public E get(int index) {
			if (index < 0 || index >= this.subSize) {
				throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.subSize);
			}
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
			return new ListItr(fromIndex, toIndex, fromIndex + index);
		}
		
		@Override
		public ImmutableList<E> subList(int fromIndexSub, int toIndexSub) {
			subListRangeCheck(fromIndexSub, toIndexSub, subSize);
			return new SubList(fromIndex + fromIndexSub, fromIndex + toIndexSub);
		}
		
		@Override
		public boolean equals(Object o) {
			return InternalUtils.equalLists(this, o);
		}
		
		@Override
		public int hashCode() {
			return InternalUtils.hashCodeIterable(this);
		}
		
		@Override
		public boolean contentEquals(Iterable<? extends E> iterable) {
			return InternalUtils.equalIterables(this, iterable);
		}
		
		@Override
		public List<E> toList() {
			return new ListRepresentation<>(this);
		}
		
		@Override
		public MutableList<E> mutable() {
			return new RandomAccessMutableListImpl<>(this);
		}
	}
	
	@Override
	public boolean equals(Object o) {
		return InternalUtils.equalLists(this, o);
	}
	
	@Override
	public int hashCode() {
		return InternalUtils.hashCodeIterable(this);
	}
	
	@Override
	public boolean contentEquals(Iterable<? extends E> iterable) {
		return InternalUtils.equalIterables(this, iterable);
	}
	
	@Override
	public List<E> toList() {
		return new ListRepresentation<>(this);
	}
	
	@Override
	public MutableList<E> mutable() { 
		return new RandomAccessMutableListImpl<>(this);
	}
}
