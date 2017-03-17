package com.leanote.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leanote.android.base.SingleFragmentActivity;
import com.leanote.android.model.Note;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xiongxingxing on 17/3/5.
 */

public class NotesListActivity extends SingleFragmentActivity {

    public static final String ARG_NOTE = "note";

    @Override
    protected Fragment createFragment() {
        return NotesListFragment.newInstance((Note) getIntent().getSerializableExtra(ARG_NOTE));
    }

    public static void showNotesList(Context context, Note note) {
        Intent intent = new Intent(context, NotesListActivity.class);
        intent.putExtra(ARG_NOTE, note);
        context.startActivity(intent);
    }

    public static class NotesListFragment extends Fragment {

        @BindView(R.id.recycler_view)
        RecyclerView mRecyclerView;
        @BindView(R.id.create_note_book)
        TextView mCreateNoteBook;
        @BindView(R.id.select_note_book)
        TextView mSelectNoteBook;
        private Note mNote;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mNote = (Note) getArguments().getSerializable(ARG_NOTE);
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_notes_list, container, false);
            ButterKnife.bind(this, view);
            return view;
        }

        public static NotesListFragment newInstance(Note note) {
            Bundle args = new Bundle();
            NotesListFragment fragment = new NotesListFragment();
            args.putSerializable(ARG_NOTE, note);
            fragment.setArguments(args);
            return fragment;
        }

        @OnClick({R.id.create_note_book, R.id.select_note_book})
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.create_note_book:
                    break;
                case R.id.select_note_book:
                    break;
            }
        }
    }

}
