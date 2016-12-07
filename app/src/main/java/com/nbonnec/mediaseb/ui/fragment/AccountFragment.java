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

package com.nbonnec.mediaseb.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.nbonnec.mediaseb.R;
import com.nbonnec.mediaseb.data.Rx.RxUtils;
import com.nbonnec.mediaseb.data.services.MSSService;
import com.nbonnec.mediaseb.models.Account;
import com.nbonnec.mediaseb.ui.event.LoginEvent;
import com.nbonnec.mediaseb.ui.event.LogoutSuccessEvent;
import com.squareup.otto.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import timber.log.Timber;

public class AccountFragment extends BaseFragment {

    private static final String STATE_ACCOUNT = "state_account";
    private static final String STATE_PAGE_LOADED = "state_page_loaded";

    @Inject
    MSSService mssService;

    @Inject
    RxUtils rxUtils;

    @Bind(R.id.account_flipper_view)
    ViewFlipper flipperView;

    @Bind(R.id.account_swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.account_content_layout)
    View contentView;

    @Bind(R.id.account_not_logged_layout)
    View notLoggedView;

    @Bind(R.id.account_progress_bar_layout)
    View loadingView;

    @Bind(R.id.account_error_layout)
    View errorView;

    @Bind(R.id.account_name)
    TextView accountNameView;

    @Bind(R.id.account_surname)
    TextView accountSurnameView;

    @Bind(R.id.account_address)
    TextView accountAddressView;

    @Bind(R.id.account_postal_code)
    TextView accountPostalCodeView;

    @Bind(R.id.account_city)
    TextView accountCityView;

    @Bind(R.id.account_birthdate)
    TextView accountBirthdateView;

    @Bind(R.id.account_mail)
    TextView accountMailView;

    @Bind(R.id.account_phone_number)
    TextView accountPhoneNumberView;

    @Bind(R.id.account_loan_number)
    TextView accountLoanNumberView;

    @Bind(R.id.account_reservation_number)
    TextView accountReservationView;

    @Bind(R.id.account_available_reservation_number)
    TextView accountAvailableReservationView;

    @Bind(R.id.account_card_number)
    TextView accountCardNumberView;

    @Bind(R.id.account_fare)
    TextView accountFareView;

    @Bind(R.id.account_balance)
    TextView accountBalanceView;

    @Bind(R.id.account_renew_date)
    TextView accountRenewDateView;

    private Account account;

    private boolean pageLoaded;

    private OnClickListener onClickListener;

    private OnIsSignedInListener onIsSignedInListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            account = savedInstanceState.getParcelable(STATE_ACCOUNT);
            pageLoaded = savedInstanceState.getBoolean(STATE_PAGE_LOADED);
        } else {
            pageLoaded = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_account, container, false);

        ButterKnife.bind(this, rootView);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadAccount();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (onIsSignedInListener.onIsSignedIn()) {
            if (pageLoaded) {
                setViews();
                showContentView();
            } else if (errorView.getVisibility() == View.GONE) {
                showLoadingView();
            }
        } else {
            showNotLoggedView();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity;

        if (context instanceof Activity) {
            activity = (Activity) context;
            try {
                onClickListener = (AccountFragment.OnClickListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnClickListener!");
            }

            try {
                onIsSignedInListener = (AccountFragment.OnIsSignedInListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnIsSignedInListener!");
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        onClickListener = null;
        onIsSignedInListener = null;

    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);

        saveInstanceState.putParcelable(STATE_ACCOUNT, account);
        saveInstanceState.putBoolean(STATE_PAGE_LOADED, pageLoaded);
    }

    @OnClick(R.id.account_not_logged_button)
    public void onClickNotLoggedButton() {
        onClickListener.onNotLoggedButtonClicked();
    }

    @OnClick(R.id.account_reload_button)
    public void onClickReloadButton() {
        showLoadingView();
        onClickListener.onReloadButtonClicked();
    }

    @Subscribe
    public void onLoginSuccessEvent(LoginEvent event) {
        if (pageLoaded) {
            setViews();
            showContentView();
        } else if (event.isSuccess()) {
            showLoadingView();
            loadAccount();
        } else {
            showErrorView();
        }
    }

    @Subscribe
    public void onLogoutSuccessEvent(LogoutSuccessEvent event) {
        showNotLoggedView();
    }

    private void setViews() {
        accountNameView.setText(account.name());
        accountSurnameView.setText(account.surname());
        accountAddressView.setText(account.address());
        accountPostalCodeView.setText(account.postalCode());
        accountCityView.setText(account.city());
        accountBirthdateView.setText(account.birthDate());
        accountMailView.setText(account.mail());
        accountPhoneNumberView.setText(account.phoneNumber());
        accountLoanNumberView.setText(account.loanNumber());
        accountReservationView.setText(account.reservation());
        accountAvailableReservationView.setText(account.availableReservation());
        accountCardNumberView.setText(account.cardNumber());
        accountFareView.setText(account.fare());
        accountBalanceView.setText(account.balance());

        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
        accountRenewDateView.setText(fmt.format(account.renewDate()));
    }

    private void loadAccount() {
        Timber.d("Loading account infos.");
        Observable<Account> getAccountObservable = mssService
                .getAccountDetails()
                .compose(rxUtils.<Account>applySchedulers());
        addSubscription(getAccountObservable.subscribe(new Observer<Account>() {
            @Override
            public void onCompleted() {
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(Throwable e) {
                swipeRefreshLayout.setRefreshing(false);
                showErrorView();
            }

            @Override
            public void onNext(Account a) {
                account = a;
                pageLoaded = true;
                setViews();
                showContentView();
            }

        }));
    }

    private void showLoadingView() {
        Timber.d("Showing loading view '%s'", this.toString());
        flipperView.setDisplayedChild(flipperView.indexOfChild(loadingView));
    }

    private void showNotLoggedView() {
        flipperView.setDisplayedChild(flipperView.indexOfChild(notLoggedView));
    }

    private void showContentView() {
        Timber.d("Showing content view '%s'", this.toString());
        flipperView.setDisplayedChild(flipperView.indexOfChild(swipeRefreshLayout));
    }

    private void showErrorView() {
        Timber.d("Showing error view '%s'", this.toString());
        flipperView.setDisplayedChild(flipperView.indexOfChild(errorView));
    }

    public interface OnClickListener {
        void onNotLoggedButtonClicked();

        void onReloadButtonClicked();
    }

    public interface OnIsSignedInListener {
        boolean onIsSignedIn();
    }
}
