package com.hctpbl.biovoiceapp.api;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;

/**
 * Error handler for a retrofit connection problem
 */
public class RetroFitErrorHandler implements ErrorHandler{
    @Override
    public Throwable handleError(RetrofitError cause) {
        if (cause.isNetworkError()) {
            return new APIConnException();
        }
        return null;
    }
}
