package com.example.appointmentmanager;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appointmentmanager.database.DatabaseHelper;
import com.example.appointmentmanager.model.Appointment;

import java.util.Calendar;
import java.util.Locale;

public class AddAppointmentActivity extends AppCompatActivity {

    private EditText etTitle, etLocation;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Switch switchReminder;
    private Button btnSave;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);

        databaseHelper = new DatabaseHelper(this);

        etTitle = findViewById(R.id.etTitle);
        etLocation = findViewById(R.id.etLocation);
        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        switchReminder = findViewById(R.id.switchReminder);
        btnSave = findViewById(R.id.btnSave);

        timePicker.setIs24HourView(true);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etTitle.getText().toString().trim();
                String location = etLocation.getText().toString().trim();

                if (TextUtils.isEmpty(title)) {
                    etTitle.setError(getString(R.string.error_empty_fields));
                    return;
                }

                if (TextUtils.isEmpty(location)) {
                    etLocation.setError(getString(R.string.error_empty_fields));
                    return;
                }

                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int day = datePicker.getDayOfMonth();

                int hour = 0;
                int minute = 0;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    hour = timePicker.getHour();
                    minute = timePicker.getMinute();
                } else {
                    hour = timePicker.getCurrentHour();
                    minute = timePicker.getCurrentMinute();
                }

                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(year, month, day, hour, minute, 0);

                Calendar currentCalendar = Calendar.getInstance();

                if (selectedCalendar.before(currentCalendar)) {
                    Toast.makeText(AddAppointmentActivity.this, R.string.error_invalid_time, Toast.LENGTH_LONG).show();
                    return;
                }

                String dateStr = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, (month + 1), day);
                String timeStr = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
                int reminder = switchReminder.isChecked() ? 1 : 0;

                Appointment appointment = new Appointment(title, location, dateStr, timeStr, reminder);
                long id = databaseHelper.insertAppointment(appointment);

                if (id != -1) {
                    Toast.makeText(AddAppointmentActivity.this, "Thêm lịch hẹn thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddAppointmentActivity.this, "Lỗi khi lưu lịch hẹn", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
