/*
 * Copyright 2016 nbonnec
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nbonnec.mediaseb.data.api.interpreters;

import android.os.Build;

import com.nbonnec.mediaseb.BaseTestCase;
import com.nbonnec.mediaseb.BuildConfig;
import com.nbonnec.mediaseb.data.api.endpoints.MSSEndpoints;
import com.nbonnec.mediaseb.data.api.endpoints.MSSEndpointsImpl;
import com.nbonnec.mediaseb.data.factories.DefaultFactory;
import com.nbonnec.mediaseb.models.Media;
import com.nbonnec.mediaseb.models.MediaList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class MSSInterpreterImplTest extends BaseTestCase {
    MSSEndpoints mssEndpoints;

    public MSSInterpreterImplTest() {
        this.mssEndpoints = new MSSEndpointsImpl();
    }

    @Test
    public void test_resultsParsing() throws IOException {
        MSSInterpreterImpl interpreter = new MSSInterpreterImpl(mssEndpoints);
        MediaList mediaList = interpreter.interpretMediaResultsFromHtml(readAssetFile("results.html"));
        List<Media> medias = mediaList.getMedias();

        assertThat(mediaList.getNextPageUrl()).isEqualTo("http://mediatheque.saintsebastien.fr/recherche/facettes/walking+dead/ligne?start=10");
        assertThat(medias.get(0).getTitle()).isEqualTo("Dead man walking : bande originale du film");
        assertThat(medias.get(0).getAuthor()).isEqualTo("Robbins, Tim");
        assertThat(medias.get(0).getEditor()).isEqualTo("Sony Music");
        assertThat(medias.get(0).getYear()).isEqualTo(("P 1995"));
        assertThat(medias.get(0).getCollection()).isEqualTo(DefaultFactory.Media.EMPTY_FIELD_COLLECTION);
        assertThat(medias.get(0).getImageUrl()).isEqualTo("http://mediatheque.saintsebastien.fr/templates/c3rb_alpha_25/html/com_opac/assets/images/icones_support/ico_sup_03.png");
        assertThat(medias.get(0).getNoticeUrl()).isEqualTo("http://mediatheque.saintsebastien.fr/recherche/notice/520486958-103");
    }

    @Test
    public void test_noResultsParsing() throws IOException {
        MSSInterpreterImpl interpreter = new MSSInterpreterImpl(mssEndpoints);
        MediaList mediaList = interpreter.interpretMediaResultsFromHtml(readAssetFile("no-results.html"));

        assertThat(mediaList.getMedias()).isEmpty();
        assertThat(mediaList.getNextPageUrl()).isEqualTo(DefaultFactory.MediaList.EMPTY_FIELD_NEXT_PAGE_URL);
    }

    @Test
    public void test_noticeParsing() throws IOException, ParseException {
        MSSInterpreterImpl interpreter = new MSSInterpreterImpl(mssEndpoints);
        Media media = interpreter.interpretNoticeFromHtml(readAssetFile("details-walking-dead.html"));

        assertThat(media.getSummary()).isEqualTo("Les amis en visite à Alexandria sont un renfort" +
                " appréciable dans l'affrontement contre les ennemis présents" +
                " dans les rangs des rôdeurs. Electre 2015.");
        assertThat(media.getType()).isEqualTo("Bd Adulte");
        assertThat(media.getSection()).isEqualTo("Espace adulte");
        assertThat(media.getLocation()).isEqualTo("BD");
        assertThat(media.getRating()).isEqualTo("");
        assertThat(media.isAvailable()).isEqualTo(false);
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd", Locale.FRENCH);
        assertThat(media.getReturnDate()).isEqualTo(fmt.parse("2016/03/25"));

        media = interpreter.interpretNoticeFromHtml(readAssetFile("details-endymion.html"));
        assertThat(media.getSummary()).isEqualTo(DefaultFactory.Media.EMPTY_FIELD_SUMMARY);
        assertThat(media.getType()).isEqualTo("Livre");
        assertThat(media.getSection()).isEqualTo("Espace adulte");
        assertThat(media.getLocation()).isEqualTo("Réserve");
        assertThat(media.getRating()).isEqualTo("R SIM");
        assertThat(media.isAvailable()).isEqualTo(true);
    }
}
