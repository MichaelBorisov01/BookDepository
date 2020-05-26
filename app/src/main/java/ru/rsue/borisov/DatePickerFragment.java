package ru.rsue.borisov;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

public class DatePickerFragment extends DialogFragment {


    private static final String ARG_DATE = "date";
    static final String EXTRA_DATE = "ru.rsue.borisov.bookdepository.date";
    private DatePicker mDatePicker;

    static DatePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        assert getArguments() != null;
        Date date = (Date) getArguments().getSerializable(ARG_DATE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        View view = inflater.inflate(R.layout.dialog_date, container, false);
        mDatePicker = view.findViewById(R.id.dialog_date_date_picker);
        mDatePicker.init(year, month, day, null);
        Button okButton = view.findViewById(R.id.dialog_date_ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = mDatePicker.getYear();
                int month = mDatePicker.getMonth();
                int day = mDatePicker.getDayOfMonth();
                Date date = new GregorianCalendar(year, month, day).getTime();
                sendResult(date);
            }
        });
        return view;
    }

    private void sendResult(Date date) {
        if (getTargetFragment() == null) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DATE, date);
            Objects.requireNonNull(getActivity()).setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);

    }

}