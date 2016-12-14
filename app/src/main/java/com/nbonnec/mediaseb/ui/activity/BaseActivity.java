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

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OnAccountsUpdateListener;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

import com.nbonnec.mediaseb.MediasebApp;
import com.nbonnec.mediaseb.R;
import com.nbonnec.mediaseb.account.AccountGeneral;
import com.nbonnec.mediaseb.data.Rx.RxUtils;
import com.nbonnec.mediaseb.data.services.MSSService;
import com.nbonnec.mediaseb.misc.PermissionUtils;
import com.nbonnec.mediaseb.misc.Utils;
import com.nbonnec.mediaseb.ui.event.LoginEvent;
import com.nbonnec.mediaseb.ui.event.LogoutSuccessEvent;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import rx.Observer;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class BaseActivity extends AppCompatActivity {
    public static final String TAG = BaseActivity.class.getSimpleName();

    public static final int PERMISSION_GET_ACCOUNTS = 0;
    public static final int PERMISSION_ADD_ACCOUNT_UPDATED_LISTENER = 1;

    private CompositeSubscription subscriptions;

    private boolean signIn;

    private AccountManager am;

    private Account account;

    private Account[] copyAccounts;

    @Inject
    MSSService mssService;

    @Inject
    Bus bus;

    @Inject
    RxUtils rxUtils;

    /* clean up when deleting account from account phone menu */
    private OnAccountsUpdateListener accountsUpdateListener = new OnAccountsUpdateListener() {
        @Override
        public void onAccountsUpdated(Account[] accounts) {
            Timber.d("Account updated.");

            if (copyAccounts == null) {
                copyAccounts = accounts;
                return;
            }

            for (Account currAccount : copyAccounts) {
                boolean accountExists = false;
                for (Account account : accounts) {
                    if (account.equals(currAccount)) {
                        accountExists = true;
                    }
                }
                if (!accountExists) {
                    Timber.d("Deleting account.");

                    addSubscription(mssService.logout()
                            .compose(rxUtils.<Boolean>applySchedulers())
                            .subscribe(new Observer<Boolean>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    Timber.d("Can not log out!");
                                }

                                @Override
                                public void onNext(Boolean b) {
                                    Timber.d("Log out '%s'", b ? "successful!" : "failed!");
                                    if (b) {
                                        signIn = false;
                                        bus.post(new LogoutSuccessEvent());
                                    }
                                }
                            }));

                }
            }

            copyAccounts = accounts;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inject dependencies
        MediasebApp app = MediasebApp.get(getApplicationContext());
        app.inject(this);
        bus.register(this);

        signIn = false;

        am = AccountManager.get(this);
        addAccountUpdatedListener();
    }

    @Override
    public void setContentView(int layoutResId) {
        super.setContentView(layoutResId);
        ButterKnife.bind(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        subscriptions = new CompositeSubscription();
        getAccount();
    }

    @Override
    protected void onStop() {
        super.onStop();
        subscriptions.unsubscribe();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        am.removeOnAccountsUpdatedListener(accountsUpdateListener);
    }

    /**
     * From ioshed.
     * <p>
     * This utility method handles Up navigation intents by searching for a parent activity and
     * navigating there if defined. When using this for an activity make sure to define both the
     * native parentActivity as well as the AppCompat one when supporting API levels less than 16.
     * when the activity has a single parent activity. If the activity doesn't have a single parent
     * activity then don't define one and this method will use back button functionality. If "Up"
     * functionality is still desired for activities without parents then use
     * {@code syntheticParentActivity} to define one dynamically.
     * <p>
     * Note: Up navigation intents are represented by a back arrow in the top left of the Toolbar
     * in Material Design guidelines.
     *
     * @param currentActivity         Activity in use when navigate Up action occurred.
     * @param syntheticParentActivity Parent activity to use when one is not already configured.
     */
    public static void navigateUpOrBack(Activity currentActivity,
                                        Class<? extends Activity> syntheticParentActivity) {
        // Retrieve parent activity from AndroidManifest.
        Intent intent = NavUtils.getParentActivityIntent(currentActivity);

        // Synthesize the parent activity when a natural one doesn't exist.
        if (intent == null && syntheticParentActivity != null) {
            try {
                intent = NavUtils.getParentActivityIntent(currentActivity, syntheticParentActivity);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (intent == null) {
            // No parent defined in manifest. This indicates the activity may be used by
            // in multiple flows throughout the app and doesn't have a strict parent. In
            // this case the navigation up button should act in the same manner as the
            // back button. This will result in users being forwarded back to other
            // applications if currentActivity was invoked from another application.
            currentActivity.onBackPressed();
        } else {
            if (NavUtils.shouldUpRecreateTask(currentActivity, intent)) {
                // Need to synthesize a backstack since currentActivity was probably invoked by a
                // different app. The preserves the "Up" functionality within the app according to
                // the activity hierarchy defined in AndroidManifest.xml via parentActivity
                // attributes.
                TaskStackBuilder builder = TaskStackBuilder.create(currentActivity);
                builder.addNextIntentWithParentStack(intent);
                builder.startActivities();
            } else {
                // Navigate normally to the manifest defined "Up" activity.
                NavUtils.navigateUpTo(currentActivity, intent);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.GET_ACCOUNTS)) {
            switch (requestCode) {
                case PERMISSION_GET_ACCOUNTS:
                    getAccount();
                    break;
                case PERMISSION_ADD_ACCOUNT_UPDATED_LISTENER:
                    addAccountUpdatedListener();
                    break;
                default:
            }
        }
    }

    /**
     * Add shared elements to a list of predifined transitions.
     *
     * @param activity       The Activity whose window contains the shared elements.
     * @param sharedElements elements to add to transitions.
     * @return a bundle or null if version is low than Lollipop.
     */
    @Nullable
    public Bundle makeTransitions(Activity activity, Pair<View, String>... sharedElements) {

        if (Utils.isLollipopOrLater()) {
            List<Pair<View, String>> transitions = new ArrayList<>();

            if (sharedElements != null) {
                Collections.addAll(transitions, sharedElements);
            }

            transitions.add(Pair.create(findViewById(R.id.toolbar), getString(R.string.transition_name_toolbar)));
            transitions.add(Pair.create(findViewById(android.R.id.statusBarBackground), Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME));
            /* does not exists in landscape */
            if (findViewById(android.R.id.navigationBarBackground) != null) {
                transitions.add(Pair.create(findViewById(android.R.id.navigationBarBackground), Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME));
            }

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity, transitions.toArray(new Pair[transitions.size()])
            );

            return options.toBundle();
        }

        return null;
    }

    public boolean isSignIn() {
        return signIn;
    }

    public void logout() {
        if (account != null) {
            /* OnUpdatedAccountListener will be called after that */
            am.removeAccount(account, null, null);
        }
    }

    public void login() {
        addSubscription(mssService.login(account.name, am.getPassword(account))
                .compose(rxUtils.<Boolean>applySchedulers())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.d("Error logging in!");
                        bus.post(new LoginEvent(false));
                    }

                    @Override
                    public void onNext(Boolean login) {
                        bus.post(new LoginEvent(login));
                    }
                }));
    }

    /**
     * Send an email to app contact.
     */
    public void composeEmailContact() {
        String[] emails = {getString(R.string.contact_mail)};
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, emails);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /**
     * Add a RxJava subscription.
     * This subscription will be handled on onStop().
     *
     * @param s subscription to handle.
     */
    protected void addSubscription(Subscription s) {
        subscriptions.add(s);
    }

    private void addAccountUpdatedListener() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) !=
                PackageManager.PERMISSION_GRANTED) {
            Timber.d("Requesting GET_ACCOUNTS permission.");
            PermissionUtils.requestPermission(this, PERMISSION_ADD_ACCOUNT_UPDATED_LISTENER,
                    Manifest.permission.GET_ACCOUNTS, false);
        } else {
            am.addOnAccountsUpdatedListener(accountsUpdateListener, new Handler(), true);
        }
    }

    private void getAccount() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) !=
                PackageManager.PERMISSION_GRANTED) {
            Timber.d("Requesting GET_ACCOUNTS permission.");
            PermissionUtils.requestPermission(this, PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS, false);
        } else {
            Account[] accounts = am.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);

            if (accounts.length > 0) {
                account = accounts[0];
                Timber.d("Using '%s' account.", account.name);
                signIn = true;
                login();
            } else {
                Timber.d("No account.");
                signIn = false;
            }
        }
    }
}
