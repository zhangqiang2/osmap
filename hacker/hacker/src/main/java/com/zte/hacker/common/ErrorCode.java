package com.zte.hacker.common;

public abstract class ErrorCode {
	// sql错误码
	public static class SqlErrorCode {
		private SqlErrorCode() {
		}

		public static final Integer SUCCESS = 0;

		public static final Integer SQL_EXCEPTION = 2010;

	}

}
