package mutabor.test;

import org.junit.Assert;
import org.junit.Test;

import mutabor.ImmutableList;
import mutabor.MutableList;
import mutabor.Mutabor;
import mutabor.ReadOnlyList;
import mutabor.internal.InternalUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Aleksej Kozlov
 */
public class MutaborTest {
	
	@SuppressWarnings("static-method")
	@Test
	public void testImmutableConversion() {
		List<Integer> list0 = Arrays.asList(Integer.valueOf(5), Integer.valueOf(4), Integer.valueOf(3), Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(0));
		List<Integer> list1 = Arrays.asList(Integer.valueOf(5), Integer.valueOf(4), Integer.valueOf(3), Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(0));
		List<Integer> list2 = new ArrayList<>(list1);
		List<Integer> list3 = new LinkedList<>(list1);
		
		list2.add(Integer.valueOf(-1));
		list2.remove(Integer.valueOf(-1));
		list3.add(Integer.valueOf(-1));
		list3.remove(Integer.valueOf(-1));
		
		testImmutableConversionStep(list1, list0, true);
		testImmutableConversionStep(list2, list0, true);
		testImmutableConversionStep(list3, list0, false);
	}
	
	protected static void testImmutableConversionStep(Collection<?> original, Collection<?> compareTarget, boolean expectConversion) {
		System.out.println("checking for " + original.getClass().getName());
		dump("original", original);
		
		ImmutableList<?> immutable = Mutabor.convertToImmutableList(original);
		dump("immutable", immutable);
		
		dump("original", original);
		Assert.assertTrue(InternalUtils.equalIterables(compareTarget, immutable));
		//проверяем, что исходный список очищен, если expectConversion
		Assert.assertTrue(expectConversion ? original.isEmpty() : InternalUtils.equalIterables(original, immutable));
		
		System.out.println();
	}
	
	@SuppressWarnings("static-method")
	@Test
	public void testImmutableIterator() {
		List<Long> listOriginal = makeArrayList(100000);
		int size = listOriginal.size();
		
		ImmutableList<Long> listConverted = Mutabor.convertToImmutableList(listOriginal);
		testImmutableIteratorStep(listConverted, size, 0);
		
		int from1 = 300;
		int to1 = 99900;
		ImmutableList<Long> subList1 = listConverted.subList(from1, to1);
		testImmutableIteratorStep(subList1, to1 - from1, from1);
		
		int from2 = 50;
		int to2 = 99000;
		ImmutableList<Long> subList2 = subList1.subList(from2, to2);
		testImmutableIteratorStep(subList2, to2 - from2, from1 + from2);
		
		int from3 = 77;
		int to3 = to2 - from2;
		ImmutableList<Long> subList3 = subList2.subList(from3, to3);
		testImmutableIteratorStep(subList3, to3 - from3, from1 + from2 + from3);
		
		int from4 = 0;
		int to4 = 80000;
		ImmutableList<Long> subList4 = subList3.subList(from4, to4);
		testImmutableIteratorStep(subList4, to4 - from4, from1 + from2 + from3 + from4);
		
		int from5 = 0;
		int to5 = to4 - from4;
		ImmutableList<Long> subList5 = subList4.subList(from5, to5);
		testImmutableIteratorStep(subList5, to5 - from5, from1 + from2 + from3 + from4 + from5);
	}
	
	protected static void testImmutableIteratorStep(ImmutableList<Long> list, int size, int fOffset) {
		Assert.assertEquals(size, list.size());
		checkListByGet(list, fOffset);
		checkListByIterator(list, fOffset);
		checkListIteratorForward(list.listIterator(), size, fOffset);
		checkListIteratorBackward(list.listIterator(size), size, fOffset);
	}
	
	@SuppressWarnings("static-method")
	@Test
	public void testMutableIterator() {
		List<Long> listOriginal = makeArrayList(100000);
		int size = listOriginal.size();
		
		MutableList<Long> listConverted = Mutabor.convertToMutableList(listOriginal);
		testMutableIteratorStep(listConverted, size, 0);
		
		int from1 = 300;
		int to1 = 99900;
		MutableList<Long> subList1 = listConverted.subList(from1, to1);
		testMutableIteratorStep(subList1, to1 - from1, from1);
		
		int from2 = 50;
		int to2 = 99000;
		MutableList<Long> subList2 = subList1.subList(from2, to2);
		testMutableIteratorStep(subList2, to2 - from2, from1 + from2);
		
		int from3 = 77;
		int to3 = to2 - from2;
		MutableList<Long> subList3 = subList2.subList(from3, to3);
		testMutableIteratorStep(subList3, to3 - from3, from1 + from2 + from3);
		
		int from4 = 0;
		int to4 = 80000;
		MutableList<Long> subList4 = subList3.subList(from4, to4);
		testMutableIteratorStep(subList4, to4 - from4, from1 + from2 + from3 + from4);
		
		int from5 = 0;
		int to5 = to4 - from4;
		MutableList<Long> subList5 = subList4.subList(from5, to5);
		testMutableIteratorStep(subList5, to5 - from5, from1 + from2 + from3 + from4 + from5);
	}
	
	protected static void testMutableIteratorStep(MutableList<Long> list, int size, int fOffset) {
		Assert.assertEquals(size, list.size());
		checkListByGet(list, fOffset);
		checkListByIterator(list, fOffset);
		checkListIteratorForward(list.listIterator(), size, fOffset);
		checkListIteratorBackward(list.listIterator(size), size, fOffset);
	}
	
	@SuppressWarnings("static-method")
	@Test
	public void testImmutableSerialize() throws IOException, ClassNotFoundException {
		List<Long> listOriginal = makeArrayList(100000);
		ImmutableList<Long> listConverted = Mutabor.convertToImmutableList(listOriginal);
		byte[] data = serialize(listConverted);
		@SuppressWarnings("unchecked")
		ImmutableList<Long> listDeserialized = (ImmutableList<Long>) deserialize(data);
		
		int sizeConverted = listConverted.size();
		int sizeDeserialized = listDeserialized.size();
		Assert.assertEquals(sizeConverted, sizeDeserialized);
		for (int i = 0; i < sizeConverted; i++) {
			Assert.assertEquals(listConverted.get(i), listDeserialized.get(i));
		}
		
		Assert.assertEquals(listConverted, listDeserialized);
	}
	
	@SuppressWarnings("static-method")
	@Test
	public void testMutableSerialize() throws IOException, ClassNotFoundException {
		List<Long> listOriginal = makeArrayList(100000);
		MutableList<Long> listConverted = Mutabor.convertToMutableList(listOriginal);
		byte[] data = serialize(listConverted);
		@SuppressWarnings("unchecked")
		MutableList<Long> listDeserialized = (MutableList<Long>) deserialize(data);
		
		int sizeConverted = listConverted.size();
		int sizeDeserialized = listDeserialized.size();
		Assert.assertEquals(sizeConverted, sizeDeserialized);
		for (int i = 0; i < sizeConverted; i++) {
			Assert.assertEquals(listConverted.get(i), listDeserialized.get(i));
		}
		
		Assert.assertEquals(listConverted, listDeserialized);
	}
	
	@SuppressWarnings("static-method")
	@Test
	public void testImmutableToList() {
		List<Long> listOriginal = makeArrayList(100);
		ImmutableList<Long> listConverted = Mutabor.copyToImmutableList(listOriginal);
		Assert.assertEquals(listOriginal, listConverted.toList());
		
		List<Long> subListOriginal = listOriginal.subList(10, 90);
		ImmutableList<Long> subListConverted = listConverted.subList(10, 90);
		Assert.assertEquals(subListOriginal, subListConverted.toList());
	}
	
	@SuppressWarnings("static-method")
	@Test
	public void testImmutableToMutable() {
		List<Long> listOriginal = makeArrayList(100);
		ImmutableList<Long> listImmutable = Mutabor.copyToImmutableList(listOriginal);
		MutableList<Long> listMutable = listImmutable.mutable();
		Assert.assertEquals(listImmutable, listMutable);
		
		listMutable.add(Long.valueOf(-1)); //here MutableList should realize its mutability
		listMutable.remove(Long.valueOf(-1));
		Assert.assertEquals(listImmutable, listMutable);
		
		ImmutableList<Long> subListImmutable1 = listImmutable.subList(10, 90);
		MutableList<Long> subListMutable1 = listMutable.subList(10, 90);
		Assert.assertEquals(subListImmutable1, subListMutable1);
		
		ImmutableList<Long> subListImmutable2 = subListImmutable1.subList(5, 65);
		MutableList<Long> subListMutable2 = subListMutable1.subList(5, 65);
		Assert.assertEquals(subListImmutable2, subListMutable2);
		
		MutableList<Long> subListMutable2a = subListImmutable2.mutable();
		Assert.assertEquals(subListImmutable2, subListMutable2a);
		Assert.assertEquals(subListMutable2, subListMutable2a);
		subListMutable2a.add(Long.valueOf(-1)); //here MutableList should realize its mutability
		subListMutable2a.remove(Long.valueOf(-1));
		Assert.assertEquals(subListImmutable2, subListMutable2a);
		Assert.assertEquals(subListMutable2, subListMutable2a);
	}
	
	@SuppressWarnings("static-method")
	@Test
	public void testEqualsHashCode() {
		List<Long> listOriginal = makeArrayList(100000);
		ImmutableList<Long> listImmutable1 = Mutabor.copyToImmutableList(listOriginal);
		ImmutableList<Long> listImmutable2 = Mutabor.copyToImmutableList(listOriginal);
		MutableList<Long> listMutable1 = Mutabor.copyToMutableList(listOriginal);
		MutableList<Long> listMutable2 = Mutabor.copyToMutableList(listOriginal);
		
		Assert.assertEquals(listImmutable1, listImmutable2);
		Assert.assertEquals(listMutable1, listMutable2);
		Assert.assertEquals(listImmutable1, listMutable1);
		Assert.assertEquals(listMutable1, listImmutable1);
		Assert.assertEquals(listImmutable1, listOriginal); //listImmutable1.equals(listOriginal) but not listOriginal.equals(listImmutable1)
		Assert.assertEquals(listMutable1, listOriginal);
		
		Assert.assertEquals(listImmutable1.hashCode(), listImmutable2.hashCode());
		Assert.assertEquals(listMutable1.hashCode(), listMutable2.hashCode());
		Assert.assertEquals(listImmutable1.hashCode(), listMutable1.hashCode());
		Assert.assertEquals(listOriginal.hashCode(), listImmutable1.hashCode());
		Assert.assertEquals(listOriginal.hashCode(), listMutable1.hashCode());
		
		List<Long> subListOriginal = listOriginal.subList(10, listOriginal.size() - 10);
		ImmutableList<Long> subListImmutable = listImmutable1.subList(10, listImmutable1.size() - 10);
		MutableList<Long> subListMutable = listMutable1.subList(10, listMutable1.size() - 10);
		
		Assert.assertEquals(subListOriginal.hashCode(), subListImmutable.hashCode());
		Assert.assertEquals(subListOriginal.hashCode(), subListMutable.hashCode());
	}
	
	@SuppressWarnings("static-method")
	@Test
	public void testMutableSnapshot() {
		List<Long> listOriginal1 = makeArrayList(100000);
		testMutableSnapshotStep(Mutabor.copyToMutableList(listOriginal1));
		testMutableSnapshotStep(Mutabor.convertToMutableList(listOriginal1));
		
		List<Long> listOriginal2 = makeLinkedList(100000);
		testMutableSnapshotStep(Mutabor.copyToMutableList(listOriginal2));
		testMutableSnapshotStep(Mutabor.convertToMutableList(listOriginal2));
	}
	
	protected static void testMutableSnapshotStep(MutableList<Long> listMutable) {
		ImmutableList<Long> snapshot1 = listMutable.snapshot();
		Assert.assertEquals(listMutable, snapshot1);
		ImmutableList<Long> snapshot2 = listMutable.snapshot();
		Assert.assertTrue(snapshot1 == snapshot2);
		listMutable.releaseSnapshot();
		ImmutableList<Long> snapshot3 = listMutable.snapshot();
		Assert.assertTrue(snapshot2 != snapshot3);
		Assert.assertEquals(snapshot2, snapshot3);
		listMutable.add(Long.valueOf(-1)); //here MutableList should realize its mutability
		listMutable.remove(Long.valueOf(-1));
		ImmutableList<Long> snapshot4 = listMutable.snapshot();
		Assert.assertTrue(snapshot3 != snapshot4);
		Assert.assertEquals(snapshot3, snapshot4);
	}
	
	protected static List<Long> makeArrayList(int size) {
		List<Long> list = new ArrayList<>(size);
		fillList(list, size);
		return list;
	}
	
	protected static List<Long> makeLinkedList(int size) {
		List<Long> list = new LinkedList<>();
		fillList(list, size);
		return list;
	}
	
	protected static void fillList(List<Long> list, int size) {
		for (int i = 0; i < size; i++) {
			list.add(Long.valueOf(f(i)));
		}
	}
	
	protected static void checkListByGet(ReadOnlyList<Long> list, int fOffset) {
		int size = list.size();
		for (int i = 0; i < size; i++) {
			Long item = list.get(i);
			Assert.assertNotNull(item);
			Assert.assertEquals(f(i + fOffset), item.longValue());
		}
	}
	
	protected static void checkListByGet(List<Long> list, int fOffset) {
		int size = list.size();
		for (int i = 0; i < size; i++) {
			Long item = list.get(i);
			Assert.assertNotNull(item);
			Assert.assertEquals(f(i + fOffset), item.longValue());
		}
	}
	
	protected static void checkListByIterator(Iterable<Long> list, int fOffset) {
		int i = 0;
		for (Long item : list) {
			Assert.assertNotNull(item);
			Assert.assertEquals(f(i + fOffset), item.longValue());
			i++;
		}
	}
	
	protected static void checkListIteratorForward(ListIterator<Long> iter, int size, int fOffset) {
		int i = 0;
		while (iter.hasNext()) {
			Long item = iter.next();
			Assert.assertNotNull(item);
			Assert.assertEquals(f(i + fOffset), item.longValue());
			i++;
		}
		Assert.assertEquals(size, i);
	}
	
	protected static void checkListIteratorBackward(ListIterator<Long> iter, int size, int fOffset) {
		int i = size - 1;
		while (iter.hasPrevious()) {
			Long item = iter.previous();
			Assert.assertNotNull(item);
			Assert.assertEquals(f(i + fOffset), item.longValue());
			i--;
		}
		Assert.assertEquals(-1, i);
	}
	
	protected static long f(long x) {
		return x * 59 + 348957;
	}
	
	protected static byte[] serialize(Object obj) throws IOException {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
				oos.writeObject(obj);
				oos.flush();
			}
			return baos.toByteArray();
		}
	}
	
	protected static Object deserialize(byte[] data) throws ClassNotFoundException, IOException {
		try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {
			try (ObjectInputStream ois = new ObjectInputStream(bais)) {
				return ois.readObject();
			}
		}
	}
	
	protected static void dump(String title, Iterable<?> list) {
		System.out.print(title);
		System.out.print(": ");
		if (list == null) {
			System.out.println("null");
		} else {
			System.out.print("{");
			for (Object obj : list) {
				System.out.print(" ");
				System.out.print(obj);
			}
			System.out.println(" }");
		}
	}
}
