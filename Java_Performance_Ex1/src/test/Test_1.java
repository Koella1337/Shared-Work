package test;

import test2.Test_3;
import test2.Test_Uninstrumented;

public class Test_1 {
	
	public static void main(String[] args) {
		for (int i = 0; i < 1000000; i++);
		
		Test_1 x = new Test_1("hello", ": hello 2");
		x.nonStaticEmpty("objectref call");
		
		return5();
		
		testmethod2('k');
		System.currentTimeMillis();
		
		recursive(3);

		System.out.println(new StringBuilder("tezzt").append(" test"));
		
		new Test_1();
		
		testmethod2('x');
		
		Test_1 subclass = new Test_2();
		((Test_2) subclass).testmethod3();
		System.out.println(subclass.toString());
		
		Test_3.otherPackageStaticMethod(3.49f);
		Test_3 y = new Test_3();
		y.otherPackageMethod(4.5f);
		
		Test_Uninstrumented.uninstrumentedSleep(1234);
	}
	
	private static int recursive(int n) {
		if (n > 0) {
			recursive(n-1);
		}
		return n;
	}
	
	public static int return5() {
		return 5;
	}
	
	public Test_1() {
		
		//does not work? why?
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		
		System.out.println("test constr");
		nonStaticEmpty("call nonStaticEmpty");
		
	}
	
	public Test_1(String x, String y) {
		
		System.out.println("test constr2: " + x + y);
		
	}
	
	protected static void testmethod2(char c) {
		
		
		System.out.println(c);
	}
	
	protected void nonStaticEmpty(String x) {
		
	}
	
}
