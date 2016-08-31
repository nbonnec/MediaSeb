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

package com.nbonnec.mediaseb.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;

import com.nbonnec.mediaseb.R;
import com.nbonnec.mediaseb.models.Media;
import com.nbonnec.mediaseb.ui.fragment.DetailsFragmentBuilder;

// TODO manage parent activity
public class DetailsActivity extends ToolbarActivity {
    public static final String TAG = DetailsActivity.class.getSimpleName();

    public static final String MEDIA = "media";

    private static final String DETAILS_FRAGMENT_TAG = "details_fragment_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base);

        Media media = getIntent().getParcelableExtra(MEDIA);

        if (savedInstanceState == null && findViewById(R.id.container) != null) {

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container,
                            new DetailsFragmentBuilder(media).build(),
                            DETAILS_FRAGMENT_TAG)
                    .commit();
            /* we want the fragment quickly for lollipop transitions. */
            // TODO maybe postponeEnterTransition
            getSupportFragmentManager().executePendingTransactions();
        }

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle("");
        }
    }
}
