package ru.rsue.borisov;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.List;
import java.util.Objects;

public class BookListFragment extends Fragment {
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 4;
    static boolean isReadGranted = false;
    private RecyclerView mBookRecyclerView;
    private BookAdapter mAdapter;
    private int mPosition;
    private boolean mSubtitleVisible;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setHasOptionsMenu(true);
        requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);
        mBookRecyclerView = view.findViewById(R.id.book_recycle_view);
        mBookRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (saveInstanceState != null) {
            mSubtitleVisible =
                    saveInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }
        updateUI();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_book_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_book:
                Book book = new Book();
                BookLab.get(getActivity()).addBook(book);
                Intent intent = BookPagerActivity
                        .newIntent(getActivity(), book.getId());
                startActivity(intent);
                return true;
            case R.id.menu_item_show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                Objects.requireNonNull(getActivity()).invalidateOptionsMenu();
                updateSubtitle();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void updateSubtitle() {
        BookLab bookLab = BookLab.get(getActivity());
        int bookCount = bookLab.getBooks().size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, bookCount, bookCount);
        if (!mSubtitleVisible) {
            subtitle = null;
        }
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        assert activity != null;
        Objects.requireNonNull(activity.getSupportActionBar()).setSubtitle(subtitle);
    }

    private void updateUI() {
        BookLab bookLab = BookLab.get(getActivity());
        List<Book> books = bookLab.getBooks();
        if (books.size() == 0)
            Toast.makeText(getContext(), R.string.list_empty, Toast.LENGTH_SHORT).show();
        if (mAdapter == null) {
            mAdapter = new BookAdapter(books);
            mBookRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setBooks(books);
            mAdapter.notifyItemChanged(mPosition);
            mAdapter.notifyItemRemoved(books.size());
        }
        updateSubtitle();
    }

    private class BookHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mReadedCheckBox;
        private Book mBook;


        BookHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTitleTextView = itemView.findViewById(R.id.list_item_book_title_text_view);
            mDateTextView = itemView.findViewById(R.id.list_item_book_date_text_view);
            mReadedCheckBox = itemView.findViewById(R.id.list_item_book_readed_check_box);

        }

        void bindBook(Book book) {
            mBook = book;
            mTitleTextView.setText(book.getTitle());
            mDateTextView.setText(DateFormat.getDateInstance().format(mBook.getDate()));
            mReadedCheckBox.setChecked(book.isReaded());
        }

        @Override
        public void onClick(View view) {
            Intent intent = BookPagerActivity.newIntent(getActivity(), mBook.getId());
            startActivity(intent);
            mPosition = getAdapterPosition();
        }
    }

    private class BookAdapter extends RecyclerView.Adapter<BookHolder> {
        private List<Book> mBooks;

        BookAdapter(List<Book> books) {
            mBooks = books;
        }


        @NonNull
        @Override
        public BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_book, parent, false);
            return new BookHolder(view);
        }

        @Override
        public void onBindViewHolder(BookHolder holder, int position) {
            Book book = mBooks.get(position);
            holder.bindBook(book);
        }

        @Override
        public int getItemCount() {
            return mBooks.size();
        }

        void setBooks(List<Book> books) {
            mBooks = books;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_READ_EXTERNAL_STORAGE) {
            isReadGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }
    }
}
