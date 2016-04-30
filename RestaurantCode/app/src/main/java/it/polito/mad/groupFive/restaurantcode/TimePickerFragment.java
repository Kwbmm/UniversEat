package it.polito.mad.groupFive.restaurantcode;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public interface Listener {
        public void setTime(int hourOfDay, int minute);
    }

    // hold the listener
    private Listener mListener;

    // used by Activity to set itself as the listener for the fragment
    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (mListener != null) mListener.setTime(hourOfDay, minute);
    }
}