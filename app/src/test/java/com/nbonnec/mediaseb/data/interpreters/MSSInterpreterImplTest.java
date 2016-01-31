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

package com.nbonnec.mediaseb.data.interpreters;

import android.os.Build;

import com.nbonnec.mediaseb.BaseTestCase;
import com.nbonnec.mediaseb.BuildConfig;
import com.nbonnec.mediaseb.data.api.interpreters.MSSInterpreterImpl;
import com.nbonnec.mediaseb.data.factories.DefaultFactory;
import com.nbonnec.mediaseb.models.Media;
import com.nbonnec.mediaseb.models.MediaList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class MSSInterpreterImplTest extends BaseTestCase {
    @Test
    public void test_resultsParsing() throws IOException {
        MSSInterpreterImpl interpreter = new MSSInterpreterImpl();
        MediaList mediaList = interpreter.interpretMediaResultsFromHtml(readAssetFile("results.html"));
        List<Media> medias = mediaList.getMedias();

        assertThat(mediaList.getNextPageUrl()).isEqualTo("/recherche/facettes/walking+dead/ligne?start=10");
        assertThat(medias.get(0).getTitle()).isEqualTo("Dead man walking : bande originale du film");
        assertThat(medias.get(0).getAuthor()).isEqualTo("Robbins, Tim");
        assertThat(medias.get(0).getEditor()).isEqualTo("Sony Music");
        assertThat(medias.get(0).getYear()).isEqualTo(("P 1995"));
        assertThat(medias.get(0).getCollection()).isEqualTo(DefaultFactory.Media.EMPTY_FIELD_COLLECTION);
        assertThat(medias.get(0).getImageUrl()).isEqualTo("/templates/c3rb_alpha_25/html/com_opac/assets/images/icones_support/ico_sup_03.png");
    }

    @Test
    public void test_noResultsParsing() throws IOException {
        MSSInterpreterImpl interpreter = new MSSInterpreterImpl();
        MediaList mediaList = interpreter.interpretMediaResultsFromHtml(readAssetFile("no-results.html"));

        assertThat(mediaList.getMedias()).isEmpty();
        assertThat(mediaList.getNextPageUrl()).isEqualTo(DefaultFactory.MediaList.EMPTY_FIELD_NEXT_PAGE_URL);
    }
}
