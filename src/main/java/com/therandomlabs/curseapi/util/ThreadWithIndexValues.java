package com.therandomlabs.curseapi.util;

import com.therandomlabs.utils.misc.Duple;
import com.therandomlabs.utils.runnable.RunnableWithInput;

//For internal use only
public class ThreadWithIndexValues extends Thread {
	private static int threadCount;
	private final Duple<Integer, Integer> indexes;
	private final RunnableWithInput<Duple<Integer, Integer>> runnable;

	public ThreadWithIndexValues(int threadIndex, int startIndex,
			RunnableWithInput<Duple<Integer, Integer>> runnable) {
		super("CurseAPI-Thread-" + ++threadCount);
		indexes = new Duple<>(threadIndex, startIndex);
		this.runnable = runnable;
	}

	@Override
	public void run() {
		runnable.run(indexes);
	}
}
