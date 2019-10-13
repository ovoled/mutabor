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
import java.util.Random;

/**
 * @author Aleksej Kozlov
 */
public class MutaborTest {
	
	protected static final int N_BIG = 100000;
	protected static final int N_SMALL = 1000;
	
	protected static Random random = new Random();
	
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
		List<Long> listOriginal = makeArrayList(N_BIG);
		int size = listOriginal.size();
		
		ImmutableList<Long> listConverted = Mutabor.convertToImmutableList(listOriginal);
		testImmutableIteratorStep(listConverted, size, 0);
		
		int from1 = random.nextInt(100) + 1;
		int to1 = size - 1 - random.nextInt(100);
		ImmutableList<Long> subList1 = listConverted.subList(from1, to1);
		testImmutableIteratorStep(subList1, to1 - from1, from1);
		
		int from2 = random.nextInt(100) + 1;
		int to2 = to1 - from1 - 1 - random.nextInt(100);
		ImmutableList<Long> subList2 = subList1.subList(from2, to2);
		testImmutableIteratorStep(subList2, to2 - from2, from1 + from2);
		
		int from3 = random.nextInt(100) + 1;
		int to3 = to2 - from2; //to end of the list
		ImmutableList<Long> subList3 = subList2.subList(from3, to3);
		testImmutableIteratorStep(subList3, to3 - from3, from1 + from2 + from3);
		
		int from4 = 0; //from begin of the list
		int to4 = to3 - from3 - 1 - random.nextInt(100);
		ImmutableList<Long> subList4 = subList3.subList(from4, to4);
		testImmutableIteratorStep(subList4, to4 - from4, from1 + from2 + from3 + from4);
		
		int from5 = 0; //from begin of the list
		int to5 = to4 - from4; //to end of the list
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
		testMutableIteratorStep(makeArrayList(N_BIG));
		testMutableIteratorStep(makeLinkedList(N_SMALL));
	}
	
	protected static void testMutableIteratorStep(List<Long> listOriginal) {
		int size = listOriginal.size();
		
		MutableList<Long> listConverted = Mutabor.convertToMutableList(listOriginal);
		testMutableIteratorSubStep(listConverted, size, 0);
		
		int from1 = random.nextInt(100) + 1;
		int to1 = size - 1 - random.nextInt(100);
		MutableList<Long> subList1 = listConverted.subList(from1, to1);
		testMutableIteratorSubStep(subList1, to1 - from1, from1);
		
		int from2 = random.nextInt(100) + 1;
		int to2 = to1 - from1 - 1 - random.nextInt(100);
		MutableList<Long> subList2 = subList1.subList(from2, to2);
		testMutableIteratorSubStep(subList2, to2 - from2, from1 + from2);
		
		int from3 = random.nextInt(100) + 1;
		int to3 = to2 - from2; //to end of the list
		MutableList<Long> subList3 = subList2.subList(from3, to3);
		testMutableIteratorSubStep(subList3, to3 - from3, from1 + from2 + from3);
		
		int from4 = 0; //from begin of the list
		int to4 = to3 - from3 - 1 - random.nextInt(100);
		MutableList<Long> subList4 = subList3.subList(from4, to4);
		testMutableIteratorSubStep(subList4, to4 - from4, from1 + from2 + from3 + from4);
		
		int from5 = 0; //from begin of the list
		int to5 = to4 - from4; //to end of the list
		MutableList<Long> subList5 = subList4.subList(from5, to5);
		testMutableIteratorSubStep(subList5, to5 - from5, from1 + from2 + from3 + from4 + from5);
	}
	
	protected static void testMutableIteratorSubStep(MutableList<Long> list, int size, int fOffset) {
		Assert.assertEquals(size, list.size());
		checkListByGet(list, fOffset);
		checkListByIterator(list, fOffset);
		checkListIteratorForward(list.listIterator(), size, fOffset);
		checkListIteratorBackward(list.listIterator(size), size, fOffset);
	}
	
	@SuppressWarnings("static-method")
	@Test
	public void testImmutableSerialize() throws IOException, ClassNotFoundException {
		testImmutableSerializeStep(makeArrayList(N_BIG));
		testImmutableSerializeStep(makeLinkedList(N_BIG));
	}
	
	protected static void testImmutableSerializeStep(List<Long> listOriginal) throws IOException, ClassNotFoundException {
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
		testMutableSerializeStep(makeArrayList(N_BIG));
		testMutableSerializeStep(makeLinkedList(N_SMALL)); //serialization of big LinkedLists is slow
	}
	
	protected static void testMutableSerializeStep(List<Long> listOriginal) throws IOException, ClassNotFoundException {
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
		List<Long> listOriginal = makeArrayList(N_SMALL);
		ImmutableList<Long> listConverted = Mutabor.copyToImmutableList(listOriginal);
		Assert.assertEquals(listOriginal, listConverted.toList());
		Assert.assertEquals(listConverted.toList(), listOriginal);
		
		int from = random.nextInt(10) + 1;
		int to = listOriginal.size() - 1 - random.nextInt(10);
		List<Long> subListOriginal = listOriginal.subList(from, to);
		ImmutableList<Long> subListConverted = listConverted.subList(from, to);
		Assert.assertEquals(subListOriginal, subListConverted.toList());
		Assert.assertEquals(subListConverted.toList(), subListOriginal);
	}
	
	@SuppressWarnings("static-method")
	@Test
	public void testImmutableToMutable() {
		List<Long> listOriginal = makeArrayList(N_SMALL);
		ImmutableList<Long> listImmutable = Mutabor.copyToImmutableList(listOriginal);
		MutableList<Long> listMutable = listImmutable.mutable();
		Assert.assertTrue(listImmutable.contentEquals(listMutable));
		
		listMutable.add(Long.valueOf(-1)); //here MutableList should realize its mutability
		listMutable.remove(Long.valueOf(-1));
		Assert.assertTrue(listImmutable.contentEquals(listMutable));
		
		int from1 = random.nextInt(10) + 1;
		int to1 = listOriginal.size() - 1 - random.nextInt(10);
		ImmutableList<Long> subListImmutable1 = listImmutable.subList(from1, to1);
		MutableList<Long> subListMutable1 = listMutable.subList(from1, to1);
		Assert.assertTrue(subListImmutable1.contentEquals(subListMutable1));
		
		int from2 = random.nextInt(10) + 1;
		int to2 = to1 - from1 - 1 - random.nextInt(10);
		ImmutableList<Long> subListImmutable2 = subListImmutable1.subList(from2, to2);
		MutableList<Long> subListMutable2 = subListMutable1.subList(from2, to2);
		Assert.assertTrue(subListImmutable2.contentEquals(subListMutable2));
		
		MutableList<Long> subListMutable2a = subListImmutable2.mutable();
		Assert.assertTrue(subListImmutable2.contentEquals(subListMutable2a));
		Assert.assertTrue(subListMutable2.contentEquals(subListMutable2a));
		subListMutable2a.add(Long.valueOf(-1)); //here MutableList should realize its mutability
		subListMutable2a.remove(Long.valueOf(-1));
		Assert.assertTrue(subListImmutable2.contentEquals(subListMutable2a));
		Assert.assertTrue(subListMutable2.contentEquals(subListMutable2a));
	}
	
	@SuppressWarnings("static-method")
	@Test
	public void testEqualsHashCode() {
		List<Long> listOriginal = makeArrayList(N_BIG);
		List<Long> listOriginalLinked = makeLinkedList(N_BIG);
		ImmutableList<Long> listImmutable1 = Mutabor.copyToImmutableList(listOriginal);
		ImmutableList<Long> listImmutable2 = Mutabor.copyToImmutableList(listOriginal);
		
		testEqualsHashCodeStep(
				listOriginal,
				listImmutable1, listImmutable2,
				Mutabor.copyToMutableList(listOriginal),
				Mutabor.copyToMutableList(listOriginal));
		testEqualsHashCodeStep(
				listOriginal,
				listImmutable1, listImmutable2,
				Mutabor.convertToMutableList(listOriginal),
				Mutabor.convertToMutableList(listOriginal));
		testEqualsHashCodeStep(
				listOriginalLinked,
				listImmutable1, listImmutable2,
				Mutabor.convertToMutableList(listOriginalLinked),
				Mutabor.convertToMutableList(listOriginalLinked));
		testEqualsHashCodeStep(
				listOriginal,
				listImmutable1, listImmutable2,
				Mutabor.convertToMutableList(listOriginal),
				Mutabor.convertToMutableList(listOriginalLinked));
		testEqualsHashCodeStep(
				listOriginalLinked,
				listImmutable1, listImmutable2,
				Mutabor.convertToMutableList(listOriginal),
				Mutabor.convertToMutableList(listOriginalLinked));
	}
	
	protected static void testEqualsHashCodeStep(
			List<Long> listOriginal,
			ImmutableList<Long> listImmutable1, ImmutableList<Long> listImmutable2,
			MutableList<Long> listMutable1, MutableList<Long> listMutable2) {
		
		listMutable2.add(Long.valueOf(-1)); //here MutableList should realize its mutability
		listMutable2.remove(Long.valueOf(-1));
		
		Assert.assertTrue(listImmutable1.contentEquals(listImmutable1));
		Assert.assertTrue(listMutable1.contentEquals(listMutable1));
		Assert.assertTrue(listImmutable1.contentEquals(listImmutable2));
		Assert.assertTrue(listMutable1.contentEquals(listMutable2));
		Assert.assertTrue(listImmutable1.contentEquals(listMutable1));
		Assert.assertTrue(listMutable1.contentEquals(listImmutable1));
		Assert.assertTrue(listImmutable1.contentEquals(listOriginal));
		Assert.assertTrue(listMutable1.contentEquals(listOriginal));
		Assert.assertFalse(listImmutable1.contentEquals(null));
		Assert.assertFalse(listMutable1.contentEquals(null));
		
		Assert.assertEquals(listImmutable1, listImmutable1);
		Assert.assertEquals(listMutable1, listMutable1);
		Assert.assertEquals(listImmutable1, listImmutable2);
		Assert.assertEquals(listMutable1, listMutable2);
		Assert.assertNotEquals(listImmutable1, listMutable1);
		Assert.assertNotEquals(listMutable1, listImmutable1);
		Assert.assertNotEquals(listImmutable1, listOriginal);
		Assert.assertNotEquals(listOriginal, listImmutable1);
		Assert.assertEquals(listMutable1, listOriginal);
		Assert.assertEquals(listOriginal, listMutable1);
		Assert.assertNotEquals(listImmutable1, null);
		Assert.assertNotEquals(listMutable1, null);
		
		Assert.assertEquals(listImmutable1.hashCode(), listImmutable2.hashCode());
		Assert.assertEquals(listMutable1.hashCode(), listMutable2.hashCode());
		Assert.assertEquals(listImmutable1.hashCode(), listMutable1.hashCode());
		Assert.assertEquals(listOriginal.hashCode(), listImmutable1.hashCode());
		Assert.assertEquals(listOriginal.hashCode(), listMutable1.hashCode());
		
		int from = random.nextInt(10) + 1;
		int to = listOriginal.size() - 1 - random.nextInt(10);
		List<Long> subListOriginal = listOriginal.subList(from, to);
		ImmutableList<Long> subListImmutable = listImmutable1.subList(from, to);
		MutableList<Long> subListMutable = listMutable1.subList(from, to);
		
		Assert.assertEquals(subListOriginal.hashCode(), subListImmutable.hashCode());
		Assert.assertEquals(subListOriginal.hashCode(), subListMutable.hashCode());
	}
	
	@SuppressWarnings("static-method")
	@Test
	public void testMutableSnapshot() {
		List<Long> listOriginal1 = makeArrayList(N_BIG);
		testMutableSnapshotStep(Mutabor.copyToMutableList(listOriginal1));
		testMutableSnapshotStep(Mutabor.convertToMutableList(listOriginal1));
		
		List<Long> listOriginal2 = makeLinkedList(N_BIG);
		testMutableSnapshotStep(Mutabor.copyToMutableList(listOriginal2));
		testMutableSnapshotStep(Mutabor.convertToMutableList(listOriginal2));
	}
	
	protected static void testMutableSnapshotStep(MutableList<Long> listMutable) {
		ImmutableList<Long> snapshot1 = listMutable.snapshot();
		Assert.assertTrue(listMutable.contentEquals(snapshot1));
		ImmutableList<Long> snapshot2 = listMutable.snapshot();
		Assert.assertTrue(snapshot1 == snapshot2);
		listMutable.releaseSnapshot();
		ImmutableList<Long> snapshot3 = listMutable.snapshot();
		Assert.assertTrue(snapshot2 != snapshot3);
		Assert.assertTrue(snapshot2.contentEquals(snapshot3));
		listMutable.add(Long.valueOf(-1)); //here MutableList should realize its mutability
		listMutable.remove(Long.valueOf(-1));
		ImmutableList<Long> snapshot4 = listMutable.snapshot();
		Assert.assertTrue(snapshot3 != snapshot4);
		Assert.assertTrue(snapshot3.contentEquals(snapshot4));
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
