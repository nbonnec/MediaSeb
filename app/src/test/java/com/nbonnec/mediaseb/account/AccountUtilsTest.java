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

package com.nbonnec.mediaseb.account;

import android.os.Build;

import com.nbonnec.mediaseb.BaseTestCase;
import com.nbonnec.mediaseb.BuildConfig;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.Scanner;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class AccountUtilsTest extends BaseTestCase {

    @Test
    public void test_isCardNumberTooShort() throws IOException {
        Scanner s = new Scanner(readAssetFile(("text/card-number-too-short.txt")));
        loopTestStringToBoolean(s,
                new StringToBoolean() {
                    @Override
                    public boolean func(String s) {
                        return AccountUtils.isCardNumberTooShort(s);
                    }
                });
    }

    @Test
    public void test_isCardNumberCorrect() throws IOException {
        Scanner s = new Scanner(readAssetFile("text/card-number-correct.txt"));
        loopTestStringToBoolean(s,
                new StringToBoolean() {
                    @Override
                    public boolean func(String s) {
                        return AccountUtils.isCardNumberCorrect(s);
                    }
                });
    }

    private void loopTestStringToBoolean(Scanner s, StringToBoolean stb) {
        while (s.hasNext()) {
            String[] cardNumber = s.nextLine().split(",\\s");
            assertThat(stb.func(cardNumber[0]))
                    .as("Testing %s", cardNumber[0])
                    .isEqualTo(Boolean.parseBoolean(cardNumber[1]));
        }
    }

    interface StringToBoolean {
        boolean func(String s);
    }
}
