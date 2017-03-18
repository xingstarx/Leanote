package com.leanote.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leanote.android.base.SingleFragmentActivity;
import com.leanote.android.database.AppDataBase;
import com.leanote.android.model.Account;
import com.leanote.android.model.Note;
import com.leanote.android.model.Notebook;
import com.leanote.android.utils.TimeUtils;

import java.util.List;
import java.util.Stack;

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

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (fragment instanceof NotesListFragment && !((NotesListFragment) fragment).onBackPressed()) {
            super.onBackPressed();
        }
    }

    public static class NotesListFragment extends Fragment {

        @BindView(R.id.recycler_view)
        RecyclerView mRecyclerView;
        @BindView(R.id.create_note_book)
        TextView mCreateNoteBook;
        @BindView(R.id.select_note_book)
        TextView mSelectNoteBook;
        private Note mNote;
        private List<Notebook> mNotebooks;
        private NotebookAdapter mNotebookAdapter;
        private Account mCurrentAccount;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mNote = (Note) getArguments().getSerializable(ARG_NOTE);
            mCurrentAccount = AppDataBase.getAccountWithToken();
            mNotebooks = AppDataBase.getRootNotebooks(mCurrentAccount.userId);
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_notes_list, container, false);
            ButterKnife.bind(this, view);
            return view;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            mNotebookAdapter = new NotebookAdapter();
            mNotebookAdapter.setNotebooks(mNotebooks);
            mNotebookAdapter.setAccount(mCurrentAccount);
            mRecyclerView.setAdapter(mNotebookAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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
                    CreateNotebookFragment fragment = CreateNotebookFragment.newInstance(mNotebookAdapter.getParentNotebookId());
                    fragment.setOnAddNotebookSuccessListener(new CreateNotebookFragment.OnAddNotebookSuccessListener() {
                        @Override
                        public void onSuccess(Notebook notebook) {
                            mNotebookAdapter.addNotebook(notebook);
                        }
                    });
                    fragment.show(getChildFragmentManager(), CreateNotebookFragment.TAG);
                    break;
                case R.id.select_note_book:
                    break;
            }
        }

        public boolean onBackPressed() {
            if (!mNotebookAdapter.isRoot()) {
                mNotebookAdapter.onBackPressed();
                return true;
            }
            return false;
        }
    }

    static class NotebookAdapter extends RecyclerView.Adapter {

        private List<Notebook> notebooks;
        private Account account;
        private Stack<List<Notebook>> listStack = new Stack<>();
        private Stack<Notebook> stack = new Stack<>();

        public boolean isRoot() {
            return listStack.size() == 0;
        }

        public void addNotebook(Notebook notebook) {
            notebooks.add(0, notebook);
            notifyDataSetChanged();
        }

        public String getParentNotebookId() {
            return stack.size() == 0 ? null : stack.peek().notebookId;
        }


        public void setNotebooks(List<Notebook> notebooks) {
            this.notebooks = notebooks;
        }

        public void setAccount(Account account) {
            this.account = account;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_note_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            final Notebook notebook = notebooks.get(position);
            viewHolder.titleView.setText(notebook.title);
            viewHolder.updateTimeView.setText(TimeUtils.toYearFormat(TimeUtils.toTimestamp(notebook.updateTime)));
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<Notebook> childNotebooks = AppDataBase.getChildNotebook(notebook.notebookId, account.userId);
                    stack.push(notebook);
                    listStack.push(notebooks);
                    setNotebooks(childNotebooks);
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return notebooks.size();
        }

        public void onBackPressed() {
            stack.pop();
            List<Notebook> notebooks = listStack.pop();
            setNotebooks(notebooks);
            notifyDataSetChanged();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView titleView;
        @BindView(R.id.update_time)
        TextView updateTimeView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
