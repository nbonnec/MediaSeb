package com.nbonnec.mediaseb.ui.activity;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.nbonnec.mediaseb.R;
import com.nbonnec.mediaseb.account.AccountGeneral;
import com.nbonnec.mediaseb.data.Rx.RxUtils;
import com.nbonnec.mediaseb.data.services.MSSService;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import rx.Observer;
import timber.log.Timber;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity {
    private AccountAuthenticatorResponse accountAuthenticatorResponse = null;
    private Bundle resultBundle = null;

    @Inject
    MSSService mssService;

    @Bind(R.id.name)
    TextInputEditText nameView;
    @Bind(R.id.card_number)
    TextInputEditText cardNumberView;
    @Bind(R.id.login_progress)
    View progressView;
    @Bind(R.id.login_form)
    View loginView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        accountAuthenticatorResponse =
                getIntent().getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);

        if (accountAuthenticatorResponse != null) {
            accountAuthenticatorResponse.onRequestContinued();
        }
    }

    @OnEditorAction(R.id.card_number)
    protected boolean passwordEditorAction(TextView textView, int id, KeyEvent keyEvent) {
        if (id == EditorInfo.IME_ACTION_GO || id == EditorInfo.IME_NULL) {
            attemptLogin();
            return true;
        }
        return false;
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors, the
     * errors are presented and no actual login attempt is made.
     */
    @OnClick(R.id.sign_in_button)
    protected void attemptLogin() {
        // Reset errors.
        nameView.setError(null);
        cardNumberView.setError(null);

        // Store values at the time of the login attempt.
        final String name = nameView.getText().toString();
        final String cardNumber = cardNumberView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a card number, if the user entered one.
        if (isCardNumberTooShort(cardNumber)) {
            cardNumberView.setError(getString(R.string.error_invalid_card_number));
            focusView = cardNumberView;
            cancel = true;
        } else if (!isCardNumberCorrect(cardNumber)) {
            cardNumberView.setError(getString(R.string.error_incorrect_card_number));
            focusView = cardNumberView;
            cancel = true;
        }

        // Check for a valid name.
        if (TextUtils.isEmpty(name)) {
            nameView.setError(getString(R.string.error_field_required));
            focusView = nameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            hideSoftKeyboard();
            showProgress(true);
            addSubscription(mssService.login(name, cardNumber)
                    .compose(RxUtils.<Boolean>applySchedulers())
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onCompleted() {
                            showProgress(false);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Timber.e(e, "Unknown error while logging");
                        }

                        @Override
                        public void onNext(Boolean loginOk) {
                            Account account = new Account(name, AccountGeneral.ACCOUNT_TYPE);
                            AccountManager accountManager = AccountManager.get(getApplicationContext());

                            if (loginOk) {
                                accountManager.addAccountExplicitly(account, cardNumber, null);
                                accountManager.setAuthToken(account, cardNumber, null);
                                finish();
                            } else {
                                showLoginError();
                            }
                        }
                    }));
        }
    }

    /**
     * Card number must be 14 character length.
     * @param cardNumber card number.
     * @return true if too short.
     */
    private boolean isCardNumberTooShort(String cardNumber) {
        return cardNumber.length() < 14;
    }

    /**
     * Only numbers.
     * @param cardNumber card number.
     * @return true if correct.
     */
    private boolean isCardNumberCorrect(String cardNumber) {
        return cardNumber.matches("[0-9]+");
    }

    private void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * Show a snackbar.
     */
    private void showLoginError() {
        View view = findViewById(R.id.login_form);

        if (view != null) {
            Snackbar snackbar = Snackbar.make(view, getString(R.string.error_ids), Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     * @param show true to show.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            loginView.setVisibility(show ? View.GONE : View.VISIBLE);
            loginView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loginView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            loginView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * From {@link android.accounts.AccountAuthenticatorActivity AccountAuthenicatorActivity}
     * Set the result that is to be sent as the result of the request that caused this
     * Activity to be launched. If result is null or this method is never called then
     * the request will be canceled.
     * @param result this is returned as the result of the AbstractAccountAuthenticator request
     */
    public final void setAccountAuthenticatorResult(Bundle result) {
        resultBundle = result;
    }

    /**
     * From {@link android.accounts.AccountAuthenticatorActivity AccountAuthenicatorActivity}
     * Sends the result or a Constants.ERROR_CODE_CANCELED error if a result isn't present.
     */
    public void finish() {
        if (accountAuthenticatorResponse != null) {
            // send the result bundle back if set, otherwise send an error.
            if (resultBundle != null) {
                accountAuthenticatorResponse.onResult(resultBundle);
            } else {
                accountAuthenticatorResponse.onError(AccountManager.ERROR_CODE_CANCELED,
                        "canceled");
            }
            accountAuthenticatorResponse = null;
        }
        super.finish();
    }
}

