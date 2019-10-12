package mutabor.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

import mutabor.ImmutableList;
import mutabor.MutableList;

/**
 * Immutable list implementation.
 * @param <E> the type of elements in this list
 * @author Aleksej Kozlov {@literal <ovoled@gmail.com>}
 */
public class MutableListImpl<E> implements MutableList<E>, RandomAccess, Cloneable, Serializable {
	
	private static final long serialVersionUID = -4595544533083091008L;
	
	protected ImmutableList<E> immutable;
	protected List<E> list;
	
	protected MutableListImpl(ImmutableList<E> immutable) {
		this.immutable = immutable;
		this.list = null;
	}
	
	protected MutableListImpl(List<E> list) {
		this.immutable = null;
		this.list = list;
	}
	
	@Override
	public int size() {
		if (immutable != null) {
			return immutable.size();
		}
		return list.size();
	}
	
	@Override
	public boolean isEmpty() {
		if (immutable != null) {
			return immutable.isEmpty();
		}
		return list.isEmpty();
	}
	
	@Override
	public boolean contains(Object o) {
		if (immutable != null) {
			return immutable.contains(o);
		}
		return list.contains(o);
	}
	
	@Override
	public Iterator<E> iterator() {
		return listIterator();
	}
	
	@Override
	public Object[] toArray() {
		if (immutable != null) {
			return immutable.toArray();
		}
		return list.toArray();
	}
	
	@Override
	public <T> T[] toArray(T[] a) {
		if (immutable != null) {
			return immutable.toArray(a);
		}
		return list.toArray(a);
	}
	
	@Override
	public boolean add(E e) {
		beforeChange();
		return list.add(e);
	}
	
	@Override
	public boolean remove(Object o) {
		beforeChange();
		return list.remove(o);
	}
	
	@Override
	public boolean containsAll(Collection<?> c) {
		if (immutable != null) {
			return immutable.containsAll(c);
		}
		return list.containsAll(c);
	}
	
	@Override
	public boolean addAll(Collection<? extends E> c) {
		beforeChange();
		return list.addAll(c);
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		beforeChange();
		return list.addAll(index, c);
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		beforeChange();
		return list.removeAll(c);
	}
	
	@Override
	public boolean retainAll(Collection<?> c) {
		beforeChange();
		return list.retainAll(c);
	}
	
	@Override
	public void clear() {
		beforeChange();
		list.clear();
	}
	
	@Override
	public E get(int index) {
		if (immutable != null) {
			return immutable.get(index);
		}
		return list.get(index);
	}
	
	@Override
	public E set(int index, E element) {
		beforeChange();
		return list.set(index, element);
	}
	
	@Override
	public void add(int index, E element) {
		beforeChange();
		list.add(index, element);
	}
	
	@Override
	public E remove(int index) {
		beforeChange();
		return list.remove(index);
	}
	
	@Override
	public int indexOf(Object o) {
		if (immutable != null) {
			return immutable.indexOf(o);
		}
		return list.indexOf(o);
	}
	
	@Override
	public int lastIndexOf(Object o) {
		if (immutable != null) {
			return immutable.lastIndexOf(o);
		}
		return list.lastIndexOf(o);
	}
	
	@Override
	public ListIterator<E> listIterator() {
		return listIterator(0);
	}
	
	@Override
	public ListIterator<E> listIterator(int index) {
		return new ListItr(index);
	}
	
	@Override
	public MutableList<E> subList(int fromIndex, int toIndex) {
		if (immutable != null) {
			return new MutableListImpl<>(immutable.subList(fromIndex, toIndex));
		}
		return new MutableListImpl<>(list.subList(fromIndex, toIndex));
	}
	
	@Override
	public boolean equals(Object o) {
		if (immutable != null) {
			return InternalUtils.equalLists(immutable, o);
		}
		return InternalUtils.equalLists(list, o);
	}
	
	@Override
	public int hashCode() {
		if (immutable != null) {
			return InternalUtils.hashCodeIterable(immutable);
		}
		return InternalUtils.hashCodeIterable(list);
	}
	
	@Override
	public ImmutableList<E> snapshot() {
		if (immutable != null) {
			return immutable;
		}
		
		immutable = InternalUtils.convertToImmutableList(list);
		if (immutable != null) {
			list = null;
			return immutable;
		}
		
		immutable = InternalUtils.copyToImmutableList(list);
		return immutable; 
	}
	
	@Override
	public void releaseSnapshot() {
		beforeChange();
	}
	
	protected void beforeChange() {
		if (list == null) {
			list = new ArrayList<>(immutable.toList());
		}
		immutable = null;
	}
	
	protected class ListItr implements ListIterator<E> {
		protected ListIterator<E> iter;
		protected boolean iterIsImmutable;
		protected int cursor;
		
		protected ListItr(int index) {
			iterIsImmutable = (immutable != null);
			iter = iterIsImmutable ? immutable.listIterator(index) : list.listIterator(index);
			cursor = index;
		}
		
		@Override
		public boolean hasNext() {
			return iter.hasNext();
		}
		
		@Override
		public E next() {
			E res = iter.next();
			cursor++;
			return res;
		}
		
		@Override
		public boolean hasPrevious() {
			return iter.hasPrevious();
		}
		
		@Override
		public E previous() {
			E res = iter.previous();
			cursor--;
			return res;
		}
		
		@Override
		public int nextIndex() {
			return iter.nextIndex();
		}
		
		@Override
		public int previousIndex() {
			return iter.previousIndex();
		}
		
		@Override
		public void remove() {
			beforeChangeListItr();
			iter.remove();
		}
		
		@Override
		public void set(E e) {
			beforeChangeListItr();
			iter.set(e);
		}
		
		@Override
		public void add(E e) {
			beforeChangeListItr();
			iter.add(e);
		}
		
		protected void beforeChangeListItr() {
			beforeChange();
			if (iterIsImmutable) {
				iterIsImmutable = false;
				iter = list.listIterator(cursor);
			}
		}
	}
}
