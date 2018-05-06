package com.therandomlabs.curseapi;

import java.util.List;
import com.therandomlabs.utils.collection.TRLList;
import com.therandomlabs.utils.runnable.RunnableWithInput;

public final class CurseEventHandling {
	public static final CurseEventHandler DEFAULT_EVENT_HANDLER = new CurseEventHandler() {};

	private static final List<CurseEventHandler> eventHandlers = new TRLList<>(5);

	static {
		register(DEFAULT_EVENT_HANDLER);
	}

	private CurseEventHandling() {}

	public static void register(CurseEventHandler eventHandler) {
		eventHandlers.add(eventHandler);
	}

	public static void unregister(CurseEventHandler eventHandler) {
		eventHandlers.remove(eventHandler);
	}

	public static void forEach(RunnableWithInput<CurseEventHandler> runnable) {
		eventHandlers.forEach(runnable::run);
	}
}
