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
import com.nbonnec.mediaseb.models.MediaList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import static org.assertj.core.api.Assertions.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class MSSInterpreterImplTest extends BaseTestCase {
    @Test
    public void test_resultsParsing() throws IOException {
        MSSInterpreterImpl interpreter = new MSSInterpreterImpl();
        MediaList medias = null;

        medias = interpreter.interpretMediaResultsFromHtml(readAssetFile("results.html"));
        assertThat(medias.getNextPageUrl()).isEqualTo("/recherche/facettes/walking+dead/ligne?start=10");
    }
}
