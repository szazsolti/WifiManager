package ro.ms.sapientia.zsolti.wifimanager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class WifiListAdapter extends RecyclerView.Adapter<WifiListAdapter.ViewHolder> {

    List<ListWifiItem> listWifiItems;
    private Context context;

    public WifiListAdapter(List<ListWifiItem> listWifiItems, Context context) {
        this.listWifiItems = listWifiItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item,viewGroup,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        ListWifiItem listWifiItem = listWifiItems.get(i);
        viewHolder.textViewdescription.setText(listWifiItem.getDescription());
    }

    @Override
    public int getItemCount() {
        return listWifiItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewdescription;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewdescription = itemView.findViewById(R.id.description);

        }
    }
}
