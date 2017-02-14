package com.leanote.android.model;

import android.text.TextUtils;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by xiongxingxing on 17/1/18.
 */

public class FullTree {
    public static class Entry {
        public final Folder parent;
        public final TreeEntry entry;
        public final String name;

        private Entry() {
            this.parent = null;
            this.entry = null;
            this.name = null;
        }

        private Entry(TreeEntry entry, Folder parent) {
            this.entry = entry;
            this.parent = parent;
//            this.name = CommitUtils.getName(entry.getPath());
            this.name = "";
        }
    }

    public static class Folder extends Entry {
        public final Map<String, Folder> folders = new TreeMap<String, Folder>();

        public final Map<String, Entry> files = new TreeMap<String, Entry>();

        private Folder() {
            super();
        }

        private Folder(TreeEntry entry, Folder parent) {
            super(entry, parent);
        }

        private void addFile(TreeEntry entry, String[] pathSegments, int index) {
            if (index == pathSegments.length - 1) {
                Entry file = new Entry(entry, this);
                files.put(file.name, file);
            } else {
                Folder folder = folders.get(pathSegments[index]);
                if (folder != null)
                    folder.addFile(entry, pathSegments, index + 1);
            }
        }

        private void addFolder(TreeEntry entry, String[] pathSegments, int index) {
            if (index == pathSegments.length - 1) {
                Folder folder = new Folder(entry, this);
                folders.put(folder.name, folder);
            } else {
                Folder folder = folders.get(pathSegments[index]);
                if (folder != null)
                    folder.addFolder(entry, pathSegments, index + 1);
            }
        }

//        private void add(final TreeEntry entry) {
//            String type = entry.getType();
//            String path = entry.getPath();
//            if (TextUtils.isEmpty(path))
//                return;
//
//            if (TYPE_BLOB.equals(type)) {
//                String[] segments = path.split("/");
//                if (segments.length > 1) {
//                    Folder folder = folders.get(segments[0]);
//                    if (folder != null)
//                        folder.addFile(entry, segments, 1);
//                } else if (segments.length == 1) {
//                    Entry file = new Entry(entry, this);
//                    files.put(file.name, file);
//                }
//            } else if (TYPE_TREE.equals(type)) {
//                String[] segments = path.split("/");
//                if (segments.length > 1) {
//                    Folder folder = folders.get(segments[0]);
//                    if (folder != null)
//                        folder.addFolder(entry, segments, 1);
//                } else if (segments.length == 1) {
//                    Folder folder = new Folder(entry, this);
//                    folders.put(folder.name, folder);
//                }
//            }
//        }
    }


}
