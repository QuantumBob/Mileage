package com.smalldoor.rwk.mileage;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;

import java.util.Calendar;

import static com.smalldoor.rwk.mileage.DeliveriesFragment.DATE_PICKED_RESULT_CODE;
import static com.smalldoor.rwk.mileage.DeliveriesFragment.RETURN_DATE;

/**
 * pops up the date picker
 */
public class DatePickerDeliveriesFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        /* Create a new instance of DatePickerDialog and return it */
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {

        String dateStr = Integer.toString(year) + "-" + Integer.toString(month + 1) + "-" + Integer.toString(day);

        /* return the date to DeliveriesFragment */
        Intent intent = new Intent();
        intent.putExtra(RETURN_DATE, dateStr);
        getTargetFragment().onActivityResult(getTargetRequestCode(), DATE_PICKED_RESULT_CODE, intent);

        InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}

