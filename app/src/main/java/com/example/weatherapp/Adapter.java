package com.example.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private Context context;
    private ArrayList<ModalWindow> modalWindowArrayList;

    public Adapter(Context context, ArrayList<ModalWindow> modalWindowArrayList) {
        this.context = context;
        this.modalWindowArrayList = modalWindowArrayList;
    }

    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
        ModalWindow modal = modalWindowArrayList.get(position);
        holder.tempTV.setText(modal.getTemperature() + "ºC");
        Picasso.get().load("https://".concat(modal.getIcon())).into(holder.conditionIV);
        holder.windTV.setText(modal.getWindSpeed() + "Km/h");
        SimpleDateFormat input = new SimpleDateFormat("yyyy-mm-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");

        try {
            Date date = input.parse(modal.getTime());
            holder.timeTV.setText(output.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return modalWindowArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView windTV, tempTV, timeTV;
        private ImageView conditionIV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            windTV = itemView.findViewById(R.id.idTVWind);
            tempTV = itemView.findViewById(R.id.idTVTemp);
            timeTV = itemView.findViewById(R.id.idTVTime);
            conditionIV = itemView.findViewById(R.id.idTVCondition);
        }
    }
}
