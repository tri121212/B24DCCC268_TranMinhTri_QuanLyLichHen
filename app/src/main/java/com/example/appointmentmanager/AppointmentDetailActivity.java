package com.example.appointmentmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appointmentmanager.database.DatabaseHelper;
import com.example.appointmentmanager.model.Appointment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AppointmentDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvLocation, tvDateTime;
    private Switch switchReminder;
    private Button btnBack, btnDelete;
    private DatabaseHelper databaseHelper;
    private int appointmentId = -1;
    private Appointment appointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_appointment);

        databaseHelper = new DatabaseHelper(this);

        tvTitle = findViewById(R.id.tvTitle);
        tvLocation = findViewById(R.id.tvLocation);
        tvDateTime = findViewById(R.id.tvDateTime);
        switchReminder = findViewById(R.id.switchReminder);
        btnBack = findViewById(R.id.btnBack);
        btnDelete = findViewById(R.id.btnDelete);

        appointmentId = getIntent().getIntExtra("APPOINTMENT_ID", -1);
        if (appointmentId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin lịch hẹn", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadAppointmentDetails();

        switchReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isOn = switchReminder.isChecked();
                databaseHelper.updateReminder(appointmentId, isOn);
                updateReminderLabel(isOn);
                Toast.makeText(AppointmentDetailActivity.this, "Đã cập nhật nhắc nhở", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseHelper.deleteAppointment(appointmentId);
                Toast.makeText(AppointmentDetailActivity.this, "Đã xóa lịch hẹn", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void loadAppointmentDetails() {
        appointment = databaseHelper.getAppointmentById(appointmentId);
        if (appointment != null) {
            tvTitle.setText(appointment.getTitle());
            tvLocation.setText(appointment.getLocation());

            String formattedDate = appointment.getDate();
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date d = inputFormat.parse(appointment.getDate());
                if (d != null) {
                    formattedDate = outputFormat.format(d);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            tvDateTime.setText(formattedDate + " · " + appointment.getTime());

            boolean isReminderOn = appointment.isReminderOn();
            switchReminder.setChecked(isReminderOn);
            updateReminderLabel(isReminderOn);
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin lịch hẹn", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void updateReminderLabel(boolean isOn) {
        if (isOn) {
            switchReminder.setText(R.string.reminder_on_label);
        } else {
            switchReminder.setText(R.string.reminder_off_label);
        }
    }
}
