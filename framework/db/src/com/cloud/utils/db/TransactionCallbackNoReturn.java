package com.cloud.utils.db;

public abstract class TransactionCallbackNoReturn implements TransactionCallback<Object> {

	@Override
	public Object doInTransaction(TransactionStatus status) {
		doInTransaction(status);
		return null;
	}

	public abstract void doInTransactionWithoutResult(TransactionStatus status);

}
