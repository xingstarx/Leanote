package com.leanote.android;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by xiongxingxing on 17/1/10.
 */

public class BrowserFragment extends Fragment {

    public static BrowserFragment newInstance() {
        Bundle args = new Bundle();
        BrowserFragment fragment = new BrowserFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_browser, container, false);
        return view;
    }
}
