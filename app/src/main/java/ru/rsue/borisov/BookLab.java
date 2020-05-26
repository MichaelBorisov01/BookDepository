package ru.rsue.borisov;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import database.BookBaseHelper;
import database.BookCursorWrapper;
import database.BookDbSchema;

class BookLab {
    private static BookLab sBookLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    static BookLab get(Context context) {
        if (sBookLab == null) {
            sBookLab = new BookLab(context);
        }
        return sBookLab;
    }

    private BookLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new BookBaseHelper(mContext)
                .getWritableDatabase();


    }

    void addBook(Book b) {
        ContentValues values = getContentValues(b);
        mDatabase.insert(BookDbSchema.BookTable.NAME, null, values);
    }

    List<Book> getBooks() {
        List<Book> books = new ArrayList<>();
        try (BookCursorWrapper cursor = queryBooks(null, null)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                books.add(cursor.getBook());
                cursor.moveToNext();
            }
        }
        return books;
    }

    Book getBook(UUID id) {
        try (BookCursorWrapper cursor = queryBooks(
                BookDbSchema.BookTable.Cols.UUID + "= ?",
                new String[]{id.toString()}
        )) {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getBook();
        }

    }

    File getPhotoFile(Book book) {
        File externalFileDir = mContext
                .getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (externalFileDir == null) {
            return null;
        }
        return new File(externalFileDir, book.getPhotoFilename());
    }

    void updateBook(Book book) {
        String uuidString = book.getId().toString();
        ContentValues values = getContentValues(book);
        mDatabase.update(BookDbSchema.BookTable.NAME, values,
                BookDbSchema.BookTable.Cols.UUID + "= ?",
                new String[]{
                        uuidString
                });
    }

    private static ContentValues getContentValues(Book book) {
        ContentValues values = new ContentValues();
        values.put(BookDbSchema.BookTable.Cols.UUID, book.getId().toString());
        values.put(BookDbSchema.BookTable.Cols.TITLE, book.getTitle());
        values.put(BookDbSchema.BookTable.Cols.DATE, book.getDate().getTime());
        values.put(BookDbSchema.BookTable.Cols.READED, book.isReaded());
        return values;
    }

    private BookCursorWrapper queryBooks(String whereClause, String[]
            whereArgs) {
        Cursor cursor = mDatabase.query(
                BookDbSchema.BookTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null

        );
        return new BookCursorWrapper(cursor);
    }

    void deleteBook(Book book) {
        String uuidString = book.getId().toString();
        mDatabase.delete(BookDbSchema.BookTable.NAME, BookDbSchema.BookTable.Cols.UUID + "= ?", new String[]{uuidString});

    }
}
