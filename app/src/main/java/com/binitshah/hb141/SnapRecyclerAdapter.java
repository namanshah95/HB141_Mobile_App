package com.binitshah.hb141;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.PlaceTypes;

import java.util.ArrayList;
import java.util.List;

class SnapRecyclerAdapter extends RecyclerView.Adapter<SnapRecyclerAdapter.ReyclerViewHolder> {

    private LayoutInflater layoutInflater;
    private Context context;
    private ArrayList<Establishment> establishments;

    public SnapRecyclerAdapter(Context context, ArrayList<Establishment> establishments) {
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.establishments = establishments;
    }

    @Override
    public ReyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = layoutInflater.inflate(R.layout.establishment_card, parent, false);
        return new ReyclerViewHolder(item);
    }

    @Override
    public void onBindViewHolder(final ReyclerViewHolder holder, int position) {
        final Establishment establishment = establishments.get(position);

        //todo: holder.establishmentBackground.setImageResource(item.getDrawable());
        //holder.appName.setText(item.getName());
        holder.establishmentName.setText(establishment.getName());

//        String typesMessage = "";
//        List<Integer> establishmentTypes = establishment.getPlaceTypes();
//        int typesSize = establishmentTypes.size();
//        if(typesSize > 3) {
//            typesSize = 3;
//        }
        //this would take too long, we're gonna just ignore establishTypes for now. i need someone to create an enum of all the place type constants. that would be fantastic

        if(establishment.getAttributions() != null) {
            holder.establishmentAttribution.setText(establishment.getAttributions());
        }
        else {
            holder.establishmentAttribution.setVisibility(View.GONE);
        }

        holder.establishmentInspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "ID: " + establishment.getId(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return establishments.size();
    }

    class ReyclerViewHolder extends RecyclerView.ViewHolder {
        private ImageView establishmentBackground;
        private TextView establishmentAttribution;
        private Button establishmentInspectButton;
        private TextView establishmentName;
        private TextView establishmentTypes;

        private ReyclerViewHolder(final View v) {
            super(v);

            establishmentBackground = (ImageView) v.findViewById(R.id.establishment_background);
            establishmentAttribution = (TextView) v.findViewById(R.id.establishment_attribution);
            establishmentInspectButton = (Button) v.findViewById(R.id.establishment_inspect_button);
            establishmentName = (TextView) v.findViewById(R.id.establishment_name);
            establishmentTypes = (TextView) v.findViewById(R.id.establishment_types);
        }
    }
}
