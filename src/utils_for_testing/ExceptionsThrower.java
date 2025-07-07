package utils_for_testing;

public class ExceptionsThrower {

	public static void ThrowException() throws Exception {
		throw new Exception("Made up error");
	}

	public static void ThrowMultipleExceptions(int amount) throws Exception {
		for (int i = 0; i < amount; i++)
			ThrowException();
	}
}
