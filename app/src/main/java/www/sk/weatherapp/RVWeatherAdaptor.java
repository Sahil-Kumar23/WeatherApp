package www.sk.weatherapp;

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

public class RVWeatherAdaptor extends RecyclerView.Adapter<RVWeatherAdaptor.ViewHolder> {

    private final Context context;
    private ArrayList<RVWeatherModal> rvWeatherModalArrayList;

    public RVWeatherAdaptor(Context context, ArrayList<RVWeatherModal> rvWeatherModalArrayList) {
        this.context = context;
        this.rvWeatherModalArrayList = rvWeatherModalArrayList;
    }

    @NonNull
    @Override
    public RVWeatherAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_item_weather,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        RVWeatherModal modal = rvWeatherModalArrayList.get(position);
        holder.tvtemperature.setText(modal.getTemperature() + "°c");
        Picasso.get().load("http:".concat(modal.getIcon())).into(holder.ivcondition);
        holder.tvwind.setText(modal.getWindSpeed() + "Km/h");
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");
        try{
            Date t = input.parse(modal.getTime());
            holder.tvtime.setText(output.format(t));
        }catch (ParseException e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return rvWeatherModalArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvwind , tvtemperature , tvtime;
        private ImageView ivcondition;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvwind = itemView.findViewById(R.id.tvwindspeed);
            tvtemperature = itemView.findViewById(R.id.tvTemperature);
            tvtime = itemView.findViewById(R.id.tvTime);
            ivcondition = itemView.findViewById(R.id.ivCondition);
        }
    }
}
