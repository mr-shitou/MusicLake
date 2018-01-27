package com.cyl.musiclake.data.source.download;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.cyl.musiclake.MyApplication;
import com.cyl.musiclake.data.source.db.DBHelper;
import com.liulishuo.filedownloader.util.FileDownloadUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yonglong on 2018/1/23.
 */

public class TasksManagerDBController {
    public final static String TABLE_NAME = "download_manger";
    private final SQLiteDatabase db;

    public TasksManagerDBController() {
        DBHelper openHelper = DBHelper.getInstance(MyApplication.mContext);

        db = openHelper.getWritableDatabase();
    }

    public List<TasksManagerModel> getAllTasks(int finish) {
        final Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME + " where "
                + TasksManagerModel.FINISH + "=" + finish, null);

        final List<TasksManagerModel> list = new ArrayList<>();
        try {
            if (!c.moveToLast()) {
                return list;
            }

            do {
                TasksManagerModel model = new TasksManagerModel();
                model.setId(c.getInt(c.getColumnIndex(TasksManagerModel.ID)));
                model.setMid(c.getString(c.getColumnIndex(TasksManagerModel.MID)));
                model.setName(c.getString(c.getColumnIndex(TasksManagerModel.NAME)));
                model.setUrl(c.getString(c.getColumnIndex(TasksManagerModel.URL)));
                model.setPath(c.getString(c.getColumnIndex(TasksManagerModel.PATH)));
                model.setFinish(c.getInt(c.getColumnIndex(TasksManagerModel.FINISH)) == 1);
                list.add(model);
            } while (c.moveToPrevious());
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return list;
    }

    public TasksManagerModel addTask(final String mid, final String name, final String url, final String path) {
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(path)) {
            return null;
        }

        // have to use FileDownloadUtils.generateId to associate TasksManagerModel with FileDownloader
        final int id = FileDownloadUtils.generateId(url, path);

        TasksManagerModel model = new TasksManagerModel();
        model.setId(id);
        model.setMid(mid);
        model.setName(name);
        model.setUrl(url);
        model.setPath(path);
        model.setFinish(false);

        final boolean succeed = db.insert(TABLE_NAME, null, model.toContentValues()) != -1;
        return succeed ? model : null;
    }

    public void finishTask(TasksManagerModel model) {
        model.setFinish(true);
        db.update(TABLE_NAME, model.toContentValues(), TasksManagerModel.FINISH + "=?", new String[]{"1"});
    }
}