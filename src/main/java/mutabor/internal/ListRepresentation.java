package mutabor.internal;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

import mutabor.ImmutableList;

/**
 * {@link java.util.List} representation of immutable list.
 * @param <E> the type of elements in this list
 * @author Aleksej Kozlov {@literal <ovoled@gmail.com>}
 */
public class ListRepresentation<E> implements List<E>, RandomAccess, Cloneable, Serializable {
	
	private static final long serialVersionUID = -140010839742243756L;
	
	protected ImmutableList<E> list;
	
	public ListRepresentation(ImmutableList<E> list) {
		this.list = list;
	}
	
	@Override
	public int size() {
		return list.size();
	}
	
	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}
	
	@Override
	public boolean contains(Object o) {
		return list.contains(o);
	}
	
	@Override
	public Iterator<E> iterator() {
		return list.iterator();
	}
	
	@Override
	public Object[] toArray() {
		return list.toArray();
	}
	
	@Override
	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}
	
	@Override
	public boolean add(E e) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}
	
	@Override
	public boolean addAll(Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public E get(int index) {
		return list.get(index);
	}
	
	@Override
	public E set(int index, E element) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void add(int index, E element) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public E remove(int index) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public int indexOf(Object o) {
		return list.indexOf(o);
	}
	
	@Override
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}
	
	@Override
	public ListIterator<E> listIterator() {
		return list.listIterator();
	}
	
	@Override
	public ListIterator<E> listIterator(int index) {
		return list.listIterator(index);
	}
	
	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return list.subList(fromIndex, toIndex).toList();
	}
	
	@Override
	public boolean equals(Object o) {
		return InternalUtils.equalLists(this, o);
	}
	
	@Override
	public int hashCode() {
		return InternalUtils.hashCodeIterable(this);
	}
}
