package com.leanote.android.model;

import com.leanote.android.database.AppDataBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiongxingxing on 17/1/18.
 */

public class FullTree {
    public static class Entry {
        public final Folder parent;
        public final TreeEntry entry;

        private Entry() {
            this.parent = null;
            this.entry = null;
        }

        private Entry(TreeEntry entry, Folder parent) {
            this.entry = entry;
            this.parent = parent;
        }
    }

    public static class Folder extends Entry {
        public final List<Folder> folders = new ArrayList<>();

        public final List<Entry> files = new ArrayList<>();

        public Folder() {
            super();
        }

        public Folder(TreeEntry entry, Folder parent) {
            super(entry, parent);
        }

        public Folder initFullTree(List<Notebook> notebooks, String userId) {
            for (Notebook notebook : notebooks) {
                Folder folder = new Folder(new TreeEntry(notebook.title, notebook.notebookId, notebook.updateTime, true), this);
                folders.add(folder);
            }
            iteratorFolders(folders, userId);
            return this;
        }

        private void iteratorFolders(List<Folder> folders, String userId) {
            for (Folder folder : folders) {
                List<Notebook> childNotebooks = AppDataBase.getChildNotebook(folder.entry.notebookId, userId);
                List<Note> childNotes = AppDataBase.getChildNote(folder.entry.notebookId, userId);
                generateTree(folder, childNotebooks, childNotes, userId);
            }
        }

        private void generateTree(Folder folder, List<Notebook> childNotebooks, List<Note> childNotes, String userId) {
            for (Notebook notebook : childNotebooks) {
                Folder childFolder = new Folder(new TreeEntry(notebook.title, notebook.notebookId, notebook.updateTime, true), folder);
                folder.folders.add(childFolder);
            }
            for (Note note : childNotes) {
                Entry childEntry = new Entry(new TreeEntry(note.title, note.noteId, note.noteAbstract, note.updatedTime, note.isMarkDown, false), folder);
                folder.files.add(childEntry);
            }
            iteratorFolders(folder.folders, userId);
        }

    }


}
