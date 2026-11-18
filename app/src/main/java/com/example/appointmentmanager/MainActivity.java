package com.example.appointmentmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appointmentmanager.adapter.AppointmentAdapter;
import com.example.appointmentmanager.database.DatabaseHelper;
import com.example.appointmentmanager.model.Appointment;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvStats, tvTotal, tvReminders;
    private ListView lvAppointments;
    private Button btnAddAppointment;
    private DatabaseHelper databaseHelper;
    private AppointmentAdapter adapter;
    private List<Appointment> appointmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        tvStats = findViewById(R.id.tvStats);
        tvTotal = findViewById(R.id.tvTotal);
        tvReminders = findViewById(R.id.tvReminders);
        lvAppointments = findViewById(R.id.lvAppointments);
        btnAddAppointment = findViewById(R.id.btnAddAppointment);

        appointmentList = databaseHelper.getAllAppointments();
        adapter = new AppointmentAdapter(this, appointmentList);
        lvAppointments.setAdapter(adapter);

        updateStats();

        lvAppointments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Appointment selectedApt = appointmentList.get(position);
                Intent intent = new Intent(MainActivity.this, AppointmentDetailActivity.class);
                intent.putExtra("APPOINTMENT_ID", selectedApt.getId());
                startActivity(intent);
            }
        });

        btnAddAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddAppointmentActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        appointmentList = databaseHelper.getAllAppointments();
        adapter.updateData(appointmentList);
        updateStats();
    }

    private void updateStats() {
        int total = databaseHelper.countAll();
        int reminders = databaseHelper.countReminders();
        if (tvTotal != null) {
            tvTotal.setText(String.valueOf(total));
        }
        if (tvReminders != null) {
            tvReminders.setText(String.valueOf(reminders));
        }
        if (tvStats != null) {
            String statsText = getString(R.string.stats_format, total, reminders);
            tvStats.setText(statsText);
        }
    }
}
