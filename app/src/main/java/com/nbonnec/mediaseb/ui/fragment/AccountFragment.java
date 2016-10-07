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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.nbonnec.mediaseb.R;
import com.nbonnec.mediaseb.data.Rx.RxUtils;
import com.nbonnec.mediaseb.data.services.MSSService;
import com.nbonnec.mediaseb.models.Account;

import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;

public class AccountFragment extends BaseFragment {

    @Inject
    MSSService mssService;

    @Bind(R.id.account_flipper_view)
    ViewFlipper viewFlipper;

    @Bind(R.id.account_content_layout)
    View contentView;

    @Bind(R.id.account_not_logged_layout)
    View notLoggedView;

    @Bind(R.id.account_renew_date)
    TextView accountDateView;

    private Account account;

    private OnClickListener listener;

    private Observer<Account> getAccountObserver = new Observer<Account>() {
        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
        }

        @Override
        public void onNext(Account a) {
            account = a;
            setViews();
        }

    };

    public interface OnClickListener {
        void onNotLoggedButtonClicked();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_account, container, false);

        ButterKnife.bind(this, rootView);

        showNotLoggedView();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        loadAccount();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity;

        if (context instanceof Activity) {
            activity = (Activity) context;
            try {
                listener = (AccountFragment.OnClickListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnClickListener");
            }
        }
    }

    @OnClick(R.id.account_not_logged_button)
    public void onClickNotLoggedButton() {
        listener.onNotLoggedButtonClicked();
    }

    private void setViews() {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
        accountDateView.setText(fmt.format(account.getRenewDate()));
    }

    private void loadAccount() {

        Observable<Account> getAccountObservable = mssService
                .getAccountDetails()
                .compose(RxUtils.<Account>applySchedulers());
        addSubscription(getAccountObservable.subscribe(getAccountObserver));
    }

    private void showNotLoggedView() {
        viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(notLoggedView));
    }

    private void showContentView() {
        viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(contentView));
    }
}
