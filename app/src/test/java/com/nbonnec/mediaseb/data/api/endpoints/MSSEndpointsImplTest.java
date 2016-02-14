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

package com.nbonnec.mediaseb.data.api.endpoints;

import android.os.Build;

import com.nbonnec.mediaseb.BaseTestCase;
import com.nbonnec.mediaseb.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class MSSEndpointsImplTest extends BaseTestCase {
    @Test
    public void test_simpleSearchUrl() {
        MSSEndpoints mssEndpoints = new MSSEndpointsImpl();
        String expected;
        String result;

        expected = "http://mediatheque.saintsebastien.fr/recherche/facettes/walking+dead/ligne?limit=10";
        result = mssEndpoints.simpleSearchUrl("walking dead");
        assertThat(result).isEqualTo(expected);

        expected = "http://mediatheque.saintsebastien.fr/recherche/facettes/walking/ligne?limit=10";
        result = mssEndpoints.simpleSearchUrl("walking&\"'(-_@)'");
        assertThat(result).isEqualTo(expected);

        expected = "http://mediatheque.saintsebastien.fr/recherche/facettes/123walking/ligne?limit=10";
        result = mssEndpoints.simpleSearchUrl("123walking");
        assertThat(result).isEqualTo(expected);

        expected = "http://mediatheque.saintsebastien.fr/recherche/facettes/abc/ligne?limit=10";
        result = mssEndpoints.simpleSearchUrl("%");
        assertThat(result).isEqualTo(expected);

        expected = "http://mediatheque.saintsebastien.fr/recherche/facettes/abc/ligne?limit=10";
        result = mssEndpoints.simpleSearchUrl("?/§");
        assertThat(result).isEqualTo(expected);

        expected = "http://mediatheque.saintsebastien.fr/recherche/facettes/toto+tata/ligne?limit=10";
        result = mssEndpoints.simpleSearchUrl("toto / tata");
        assertThat(result).isEqualTo(expected);

        expected = "http://mediatheque.saintsebastien.fr/recherche/facettes/abc/ligne?limit=10";
        result = mssEndpoints.simpleSearchUrl(",;:");
        assertThat(result).isEqualTo(expected);
    }
}
