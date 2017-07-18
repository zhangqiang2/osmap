package com.zte.hacker.fetchchangeinfo;

public interface JobConServiceInterface {
	void start();
	void stop();
	String getPrjBasePath();
	String getShellExecutePath();
}
