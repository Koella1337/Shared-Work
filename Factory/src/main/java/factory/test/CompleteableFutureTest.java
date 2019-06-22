package factory.test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CompleteableFutureTest {

	public static void main(String[] args) {
		test1();
		test2();
	}

	private static void test1() {
		System.out.println("---- test 1");
		try {
			System.out.println("create future");
			Future<String> completableFuture = calculateAsync();

			System.out.println("calling get");
			String result = completableFuture.get();
			System.out.println(result);

			System.out.println("finished 1");
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	private static Future<String> calculateAsync() throws InterruptedException {
		CompletableFuture<String> completableFuture = new CompletableFuture<>();

		Executors.newCachedThreadPool().submit(() -> {
			Thread.sleep(1000);
			completableFuture.complete("Hello");
			return null;
		});

		return completableFuture;
	}

	private static void test2() {
		System.out.println("---- test 2");
		CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> "Hello");
		CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return "blabla";
		});
		CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> "World");

		CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(future1, future2, future3);

		System.out.println("calling get");

		try {
			combinedFuture.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		System.out.println(future1.isDone());
		System.out.println(future2.isDone());
		System.out.println(future3.isDone());
		System.out.println("finished 2");
	}



}
