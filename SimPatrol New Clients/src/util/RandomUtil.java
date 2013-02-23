package util;

import java.util.Random;

public class RandomUtil {
	
	private static Random rand = new Random(); 

	/**
	 * Recebe um array que é interpretado como tamanhos de intervalos.
	 * Escolhe um dos intervalos aleatoriamente, atribuindo a cada um deles 
	 * uma probabilidade proporcional ao seu tamanhos. 
	 * Retorna o índice do intervalo escolhido, 
	 */
	public static int randomProportionalChoice(double[] intervalSizes) {
		double sum = 0.0d;
		for (int i = 0; i < intervalSizes.length; i++) {
			sum += intervalSizes[i];
		}
		
		double choice = sum * rand.nextDouble(); //choice is in interval [0;sum)
		
		double partialSum = 0.0d;
		for (int i = 0; i < intervalSizes.length; i++) {
			partialSum += intervalSizes[i];
			if (choice <= partialSum) {
				return i;
			}
		}
		
		return intervalSizes.length - 1; //não deveria acontecer!
	}

}
