package com.leanote.android;

import android.app.Activity;
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

import com.leanote.android.api.ApiProvider;
import com.leanote.android.api.NoteApi;
import com.leanote.android.base.BaseFragment;
import com.leanote.android.base.SingleFragmentActivity;
import com.leanote.android.database.AppDataBase;
import com.leanote.android.model.Account;
import com.leanote.android.model.BaseModel;
import com.leanote.android.model.Note;
import com.leanote.android.model.Notebook;
import com.leanote.android.utils.NetWorkUtils;
import com.leanote.android.utils.TimeUtils;
import com.leanote.android.utils.ToastUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by xiongxingxing on 17/3/5.
 */

public class NotesListActivity extends SingleFragmentActivity {

    public static final String ARG_NOTE = "note";
    public static final int REQUEST_CODE = 11;
    public static final String INTENT_DATA = "intent_data";

    @Override
    protected Fragment createFragment() {
        return NotesListFragment.newInstance((Note) getIntent().getSerializableExtra(ARG_NOTE));
    }

    public static void showNotesList(Fragment fragment, Note note) {
        Intent intent = new Intent(fragment.getContext(), NotesListActivity.class);
        intent.putExtra(ARG_NOTE, note);
        fragment.startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (fragment instanceof BaseFragment && !((BaseFragment) fragment).onBackPressed()) {
            super.onBackPressed();
        }
    }

    public static class NotesListFragment extends BaseFragment {

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
        private NoteApi mNoteApi;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mNote = (Note) getArguments().getSerializable(ARG_NOTE);
            mCurrentAccount = AppDataBase.getAccountWithToken();
            mNotebooks = AppDataBase.getRootNotebooks(mCurrentAccount.userId);
            mNoteApi = ApiProvider.getInstance().getNoteApi();
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
                    if (mNotebookAdapter.isRoot()) {
                        ToastUtils.show(getContext(), "不能把笔记移动到根节点位置");
                    } else {
                        Map<String, String> map = new HashMap<>();
                        map.put("NoteId", mNote.noteId);
                        map.put("Usn", String.valueOf(mNote.usn));
                        map.put("NotebookId", mNotebookAdapter.getParentNotebookId());
                        if (NetWorkUtils.isConnected(getContext())) {
                            mNoteApi.updateNote(map).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Action1<BaseModel<Note>>() {
                                        @Override
                                        public void call(BaseModel<Note> noteBaseModel) {
                                            if (!noteBaseModel.isError()) {
                                                handleSelectEvent(noteBaseModel.data.noteId, noteBaseModel.data.noteBookId);
                                            }
                                        }
                                    }, new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {
                                            throwable.printStackTrace();
                                            ToastUtils.show(getContext(), "移动失败");
                                            getActivity().finish();
                                        }
                                    });
                        } else {
                            handleSelectEvent(mNote.noteId, mNotebookAdapter.getParentNotebookId());
                        }
                    }
                    break;
            }
        }

        private void handleSelectEvent(String noteId, String noteBookId) {
            Note localeNote = AppDataBase.getNoteByServerId(noteId);
            localeNote.noteBookId = noteBookId;
            localeNote.update();
            ToastUtils.show(getContext(), "移动成功");
            Intent intent = new Intent();
            intent.putExtra(INTENT_DATA, localeNote);
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
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
