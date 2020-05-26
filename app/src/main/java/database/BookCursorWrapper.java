package database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

import ru.rsue.borisov.Book;

public class BookCursorWrapper extends CursorWrapper {
    public BookCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Book getBook() {
        String uuidString = getString(getColumnIndex(BookDbSchema.BookTable.Cols.UUID));
        String title = getString(getColumnIndex(BookDbSchema.BookTable.Cols.TITLE));
        long date = getLong(getColumnIndex(BookDbSchema.BookTable.Cols.DATE));
        int isReaded = getInt(getColumnIndex(BookDbSchema.BookTable.Cols.READED));
        Book book = new Book(UUID.fromString(uuidString));
        book.setTitle(title);
        book.setDate(new Date(date));
        book.setReaded(isReaded != 0);
        return book;
    }
}
