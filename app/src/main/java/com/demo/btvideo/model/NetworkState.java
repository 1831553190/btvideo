package com.demo.btvideo.model;

public class NetworkState {
	private static final int IDLE = -1;
	public static final int RUNNING = 0;
	public static final int SUCCESS = 1;
	public static final int FAILED = 2;

	private int mState = IDLE;

	public static NetworkState create() {
		return new NetworkState();
	}

	private NetworkState() {
	}

	public void setState(int state) {
		if (mState != state) {
			this.mState = state;
		}
	}

	public int getState() {
		return mState;
	}

	public boolean isLoaded() {
		return mState == SUCCESS;
	}

	public boolean isIdle() {
		return mState == IDLE;
	}

	boolean isLoading() {
		return mState == RUNNING;
	}

	public boolean isLoadFailed() {
		return mState == FAILED;
	}
}