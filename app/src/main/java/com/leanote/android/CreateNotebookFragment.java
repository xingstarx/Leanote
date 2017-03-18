package com.leanote.android;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import com.leanote.android.utils.DensityUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CreateNotebookFragment extends DialogFragment {
    public static final String TAG = "CreateNotebookFragment";
    @BindView(R.id.edit_text)
    EditText mEditText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppTheme_BottomSheet);
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(DensityUtils.dp2px(getContext(), 300f), WindowManager.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams p = getDialog().getWindow().getAttributes();
        p.gravity = Gravity.CENTER;
        getDialog().getWindow().setAttributes(p);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_create_notebook, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    public static CreateNotebookFragment newInstance() {
        Bundle args = new Bundle();
        CreateNotebookFragment fragment = new CreateNotebookFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @OnClick({R.id.cancel, R.id.ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                dismiss();
                break;
            case R.id.ok:
                dismiss();
                break;
        }
    }
}
