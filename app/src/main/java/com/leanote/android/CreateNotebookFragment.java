package com.leanote.android;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import com.leanote.android.api.ApiProvider;
import com.leanote.android.api.NotebookApi;
import com.leanote.android.model.BaseModel;
import com.leanote.android.model.Notebook;
import com.leanote.android.utils.DensityUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class CreateNotebookFragment extends DialogFragment {
    public static final String TAG = "CreateNotebookFragment";
    public static final String ARG_PARENT_NOTEBOOK_ID = "parentNotebookId";
    @BindView(R.id.edit_text)
    EditText mEditText;
    private NotebookApi mNotebookApi;
    private String mParentNotebookId;
    private OnAddNotebookSuccessListener mOnAddNotebookSuccessListener;

    public void setOnAddNotebookSuccessListener(OnAddNotebookSuccessListener onAddNotebookSuccessListener) {
        this.mOnAddNotebookSuccessListener = onAddNotebookSuccessListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppTheme_BottomSheet);
        mNotebookApi = ApiProvider.getInstance().getNotebookApi();
        mParentNotebookId = getArguments().getString(ARG_PARENT_NOTEBOOK_ID);
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    public static CreateNotebookFragment newInstance(String parentNotebookId) {
        Bundle args = new Bundle();
        CreateNotebookFragment fragment = new CreateNotebookFragment();
        args.putString(ARG_PARENT_NOTEBOOK_ID, parentNotebookId);
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
                if (TextUtils.isEmpty(mEditText.getText().toString())) {
                    return;
                }
                mNotebookApi.addNotebook(mEditText.getText().toString().trim(), mParentNotebookId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<BaseModel<Notebook>>() {
                            @Override
                            public void call(BaseModel<Notebook> notebookBaseModel) {
                                if (mOnAddNotebookSuccessListener != null) {
                                    mOnAddNotebookSuccessListener.onSuccess(notebookBaseModel.data);
                                }
                                dismiss();
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        });
                dismiss();
                break;
        }
    }

    public interface OnAddNotebookSuccessListener {
        void onSuccess(Notebook notebook);
    }
}
