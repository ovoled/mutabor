package mutabor.test;

import org.junit.Assert;
import org.junit.Test;

import mutabor.ImmutableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ImmutableListTest {
	
	@SuppressWarnings("static-method")
	@Test
	public void test() {
		List<Integer> mutable1 = Arrays.asList(Integer.valueOf(5), Integer.valueOf(4), Integer.valueOf(3), Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(0));
		List<Integer> mutable2 = new ArrayList<>(mutable1);
		List<Integer> mutable3 = new LinkedList<>(mutable1);
		
		testStep(mutable1, true);
		testStep(mutable2, true);
		testStep(mutable3, false);
	}
	
	protected static void testStep(Collection<?> mutable, boolean expectConversion) {
		System.out.println("checking for " + mutable.getClass().getName());
		dump("mutable", mutable);
		
		ImmutableList<?> immutable = new ImmutableList<>(mutable);
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
	//TODO тесты subList (и особенно subList от subList)
}
