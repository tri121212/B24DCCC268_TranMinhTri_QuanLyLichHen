package com.example.appointmentmanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.appointmentmanager.R;
import com.example.appointmentmanager.model.Appointment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AppointmentAdapter extends BaseAdapter {

    private Context context;
    private List<Appointment> appointmentList;
    private LayoutInflater inflater;

    public AppointmentAdapter(Context context, List<Appointment> appointmentList) {
        this.context = context;
        this.appointmentList = appointmentList;
        this.inflater = LayoutInflater.from(context);
    }

    public void updateData(List<Appointment> newList) {
        this.appointmentList = newList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return appointmentList.size();
    }

    @Override
    public Object getItem(int position) {
        return appointmentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return appointmentList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_appointment, parent, false);
            holder = new ViewHolder();
            holder.tvTitle = convertView.findViewById(R.id.tvTitle);
            holder.tvLocation = convertView.findViewById(R.id.tvLocation);
            holder.tvDateTime = convertView.findViewById(R.id.tvDateTime);
            holder.tvReminder = convertView.findViewById(R.id.tvReminder);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Appointment apt = appointmentList.get(position);
        holder.tvTitle.setText(apt.getTitle());
        holder.tvLocation.setText(apt.getLocation());

        // Format date from yyyy-MM-dd to dd/MM/yyyy
        String formattedDate = apt.getDate();
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date d = inputFormat.parse(apt.getDate());
            if (d != null) {
                formattedDate = outputFormat.format(d);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.tvDateTime.setText(formattedDate + " · " + apt.getTime());

        if (apt.getReminder() == 1) {
            holder.tvReminder.setText(context.getString(R.string.reminder_yes));
            holder.tvReminder.setTextColor(context.getResources().getColor(R.color.green_primary));
        } else {
            holder.tvReminder.setText(context.getString(R.string.reminder_no));
            holder.tvReminder.setTextColor(0xFF757575);
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView tvTitle, tvLocation, tvDateTime, tvReminder;
    }
}
