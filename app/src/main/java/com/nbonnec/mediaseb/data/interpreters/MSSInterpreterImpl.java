package com.nbonnec.mediaseb.data.interpreters;

import com.nbonnec.mediaseb.models.MediaList;

/**
 * Created by nbonnec on 19/01/2016.
 */
public final class MSSInterpreterImpl implements MSSInterpreter {
    public static final String TAG = MSSInterpreterImpl.class.getSimpleName();

    public MSSInterpreterImpl() {}

    @Override
    public MediaList interpretMediaResultsFromHtml(String html) {
        final String TITLE_ELEMENT = "span.titre_complet";
        final String EDITOR_ELEMENT = "span.editeur";
        final String COLLECTION_ELEMENT = "span.collection";
        final String YEAR_ELEMENT = "span.date_edi";
        final String NEXT_URL_ELEMENT = "a[title=Page:Suivant]";

        final MediaList mediaList = new MediaList(html);
        return mediaList;
    }
}
