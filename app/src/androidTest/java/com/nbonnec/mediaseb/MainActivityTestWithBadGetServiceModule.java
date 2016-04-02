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

package com.nbonnec.mediaseb;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;

import com.nbonnec.mediaseb.di.modules.BadGetMSSServiceModule;
import com.nbonnec.mediaseb.ui.activity.MainActivity;
import com.nbonnec.mediaseb.utils.ActivityRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.Visibility;
import static android.support.test.espresso.assertion.ViewAssertions.matches;

@RunWith(AndroidJUnit4.class)
public class MainActivityTestWithBadGetServiceModule {
    public static final String TAG = MainActivityTestWithBadGetServiceModule.class.getSimpleName();

    @Rule
    public final ActivityRule<MainActivity> mActivityRule = new ActivityRule<>(MainActivity.class);

    public MainActivity mainActivity;

    @Before
    public void setup() {
        MediasebApp app = MediasebApp.get(InstrumentationRegistry.getTargetContext());
        app.buildInitialObjectGraph();
        app.addModule(new BadGetMSSServiceModule());

        mainActivity = mActivityRule.get();
    }

    @After
    public void teardown() {
        mainActivity = null;
    }

    @Test
    public void testProgressBarViewIsInvisible() {
        onView(withId(R.id.media_list_progress_bar)).check(matches(withEffectiveVisibility(Visibility.GONE)));
    }
}
