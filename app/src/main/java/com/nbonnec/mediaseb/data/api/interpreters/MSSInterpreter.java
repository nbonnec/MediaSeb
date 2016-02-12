package com.nbonnec.mediaseb.data.api.interpreters;

import com.nbonnec.mediaseb.models.MediaList;

/**
 * Created by nbonnec on 19/01/2016.
 */
public interface MSSInterpreter {
    public MediaList interpretMediaResultsFromHtml(String html);
}
