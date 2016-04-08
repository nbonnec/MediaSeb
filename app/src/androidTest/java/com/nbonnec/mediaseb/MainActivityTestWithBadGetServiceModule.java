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
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.nbonnec.mediaseb.di.modules.BadGetMSSServiceModule;
import com.nbonnec.mediaseb.di.modules.MediasebModule;
import com.nbonnec.mediaseb.ui.activity.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.Visibility;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static junit.framework.Assert.assertNotNull;

/**
 * The @Before annotation is called before tests and not before lauchning activity on default
 * behavior.
 * Use launchActivity=false for {@link android.support.test.rule.ActivityTestRule#ActivityTestRule(Class, boolean, boolean)}.
 * @see <a href="https://jabknowsnothing.wordpress.com/2015/11/05/activitytestrule-espressos-test-lifecycle/">ActivityTestRule: Espresso’s Test “Lifecycle”</a>
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTestWithBadGetServiceModule {
    public static final String TAG = MainActivityTestWithBadGetServiceModule.class.getSimpleName();

    @Rule
    public final ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<>(MainActivity.class, false, false);

    @Before
    public void setup() {
        MediasebApp app = MediasebApp.get(InstrumentationRegistry.getTargetContext());
        app.setObjectGraph(new MediasebModule(app), new BadGetMSSServiceModule());
    }

    @Test
    public void testProgressBarViewIsInvisible() {
        activityRule.launchActivity(null);
        onView(withId(R.id.media_list_progress_bar_layout)).check(matches(withEffectiveVisibility(Visibility.GONE)));
        onView(withId(R.id.media_list_error_layout)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)));
        onView(withId(R.id.media_list_content_layout)).check(matches(withEffectiveVisibility(Visibility.GONE)));
    }
}
