package com.hctpbl.biovoiceapp.api;

/**
 * Interface to implement by every class interested in
 * uploading a file and check its upload progress
 */
public interface ProgressListener {
    void transferred (long num);
}
