package com.qnex.jpa.practice.util;

import net.ttddyy.dsproxy.QueryCountHolder;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionOperations;

public class LoggableTransactionTemplate implements TransactionOperations {

    private static final Logger LOG = Logger.getLogger(LoggableTransactionTemplate.class);


    private TransactionOperations txOperations;

    public LoggableTransactionTemplate(TransactionOperations txOperations) {
        this.txOperations = txOperations;
    }

    @Override
    public <T> T execute(TransactionCallback<T> action) throws TransactionException {
        QueryCountHolder.clear();
        String txName = Thread.currentThread().getName();
        LOG.debug("The transaction '" + txName + "' has been started");
        T result = txOperations.execute(action);
        LOG.debug("The transaction '" + txName + "' is being committed");
        return result;
    }
}
