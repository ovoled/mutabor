package mutabor.test;

import org.junit.Assert;
import org.junit.Test;

import mutabor.ImmutableList;
import mutabor.Mutabor;
import mutabor.ReadOnlyList;

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
public class ImmutableListTest {
	
	@SuppressWarnings("static-method")
	@Test
	public void testConversion() {
		List<Integer> mutable1 = Arrays.asList(Integer.valueOf(5), Integer.valueOf(4), Integer.valueOf(3), Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(0));
		List<Integer> mutable2 = new ArrayList<>(mutable1);
		List<Integer> mutable3 = new LinkedList<>(mutable1);
		
		testConversionStep(mutable1, true);
		testConversionStep(mutable2, true);
		testConversionStep(mutable3, false);
	}
	
	protected static void testConversionStep(Collection<?> mutable, boolean expectConversion) {
		System.out.println("checking for " + mutable.getClass().getName());
		dump("mutable", mutable);
		
		ImmutableList<?> immutable = Mutabor.convertToImmutableList(mutable);
		dump("immutable", immutable);
		
		//проверяем, что исходный список более неработоспособен, если expectConversion
		try {
			dump("mutable", mutable);
			if (expectConversion) {
				Assert.fail("expected NullPointerException");
			}
		} catch (NullPointerException e) {
			if (expectConversion) {
				System.out.println();
				System.out.println(e + " - as expected");
			} else {
				throw e;
			}
		}
		System.out.println();
	}
	
	@SuppressWarnings("static-method")
	@Test
	public void testIterator() {
		List<Long> listOriginal = makeList(100000);
		int size = listOriginal.size();
		
		ImmutableList<Long> listConverted = Mutabor.convertToImmutableList(listOriginal);
		testIteratorStep(listConverted, size, 0);
		
		int from1 = 300;
		int to1 = 99900;
		ImmutableList<Long> subList1 = listConverted.subList(from1, to1);
		testIteratorStep(subList1, to1 - from1, from1);
		
		int from2 = 50;
		int to2 = 99000;
		ImmutableList<Long> subList2 = subList1.subList(from2, to2);
		testIteratorStep(subList2, to2 - from2, from1 + from2);
		
		int from3 = 77;
		int to3 = to2 - from2;
		ImmutableList<Long> subList3 = subList2.subList(from3, to3);
		testIteratorStep(subList3, to3 - from3, from1 + from2 + from3);
		
		int from4 = 0;
		int to4 = 80000;
		ImmutableList<Long> subList4 = subList3.subList(from4, to4);
		testIteratorStep(subList4, to4 - from4, from1 + from2 + from3 + from4);
		
		int from5 = 0;
		int to5 = to4 - from4;
		ImmutableList<Long> subList5 = subList4.subList(from5, to5);
		testIteratorStep(subList5, to5 - from5, from1 + from2 + from3 + from4 + from5);
	}
	
	protected static void testIteratorStep(ImmutableList<Long> list, int size, int fOffset) {
		Assert.assertEquals(size, list.size());
		checkListByGet(list, fOffset);
		checkListByIterator(list, fOffset);
		checkListIteratorForward(list.listIterator(), size, fOffset);
		checkListIteratorBackward(list.listIterator(size), size, fOffset);
	}
	
	@SuppressWarnings("static-method")
	@Test
	public void testSerialize() throws IOException, ClassNotFoundException {
		List<Long> listOriginal = makeList(100000);
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
	}
	
	protected static List<Long> makeList(int size) {
		List<Long> list = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			list.add(Long.valueOf(f(i)));
		}
		return list;
	}
	
	protected static void checkListByGet(ReadOnlyList<Long> list, int fOffset) {
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
	
	//TODO тесты сериализации
}
