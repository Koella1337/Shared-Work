package factory.test;

import io.reactivex.Observable;

public class RxJavaTest {
	static String result = "test";

	public static void main(String[] args) {
//        result = "";
//        Observable<String> observer = Observable.just("Hello"); // provides datea
//        observer.subscribe(s -> result=s); // Callable as subscriber
//        System.out.println(result);
		testSimpleObservable();
		testSimpleObservable();
	}

	private static void testSimpleObservable() {
		System.out.println("test");
		
		System.out.println("creating emitter");
		Observable<Object> simpleObservable = Observable.create(emitter -> {
			emitter.onNext(new MyEvent("a"));
			emitter.onNext(new MyEvent("b"));
			emitter.onNext(new MyEvent("c"));
			emitter.onNext(new MyEvent("d"));
		}).doOnError(error -> System.out.println("doOnError"))
				.doOnSubscribe(disp -> System.out.println("doOnSubscribe"))
				.doOnNext(cons -> System.out.println("doOnNext"))
				.doOnEach(cons -> System.out.println("doOnEach"));
				
				
		System.out.println("subscribe first time");
		simpleObservable.subscribe();

		System.out.println("subscribe second time");
		simpleObservable.subscribe();

	}
}