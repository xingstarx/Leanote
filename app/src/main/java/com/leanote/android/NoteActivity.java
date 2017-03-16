package com.leanote.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.leanote.android.base.SingleFragmentActivity;
import com.leanote.android.editor.Editor;
import com.leanote.android.editor.RichTextEditor;
import com.leanote.android.model.Note;
import com.leanote.android.utils.ContextUtils;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xiongxingxing on 17/3/5.
 */

public class NoteActivity extends SingleFragmentActivity {

    public static final String ARG_NOTE = "note";
    @Override
    protected Fragment createFragment() {
        return NoteFragment.newInstance((Note) getIntent().getSerializableExtra(ARG_NOTE));
    }

    public static void showPreNote(Context context, Note note) {
        Intent intent = new Intent(context, NoteActivity.class);
        intent.putExtra(ARG_NOTE, note);
        context.startActivity(intent);
    }

    public static class NoteFragment extends Fragment implements Editor.EditorListener {

        @BindView(R.id.web_view)
        WebView mWebView;
        private Note mNote;
        private Editor mEditor;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mNote = (Note) getArguments().getSerializable(ARG_NOTE);
            if (mNote.isMarkDown) {

            } else {
                mEditor = new RichTextEditor(this);
            }
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_note, container, false);
            ButterKnife.bind(this, view);
            mEditor.init(mWebView);
            return view;
        }

        public static NoteFragment newInstance(Note note) {
            Bundle args = new Bundle();
            NoteFragment fragment = new NoteFragment();
            args.putSerializable(ARG_NOTE, note);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onPageLoaded() {
            mEditor.setTitle(mNote.title);
            mEditor.setContent(mNote.content);
            mEditor.setEditingEnabled(false);
        }

        @Override
        public void onClickedLink(String title, String url) {

        }

        @Override
        public void onStyleChanged(Editor.Format style, boolean enabled) {

        }

        @Override
        public void onFormatChanged(Map<Editor.Format, Object> enabledFormats) {

        }

        @Override
        public void onCursorChanged(Map<Editor.Format, Object> enabledFormats) {

        }

        @Override
        public void linkTo(String url) {
            ContextUtils.openUrl(getContext(), url);
        }

        @Override
        public void onClickedImage(String url) {

        }
    }

}
