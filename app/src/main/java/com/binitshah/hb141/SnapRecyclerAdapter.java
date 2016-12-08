package com.binitshah.hb141;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlaceTypes;
import com.google.android.gms.location.places.Places;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

class SnapRecyclerAdapter extends RecyclerView.Adapter<SnapRecyclerAdapter.ReyclerViewHolder> {

    private LayoutInflater layoutInflater;
    private Context context;
    private ArrayList<Establishment> establishments;
    GoogleApiClient mGoogleApiClient;
    private static final String TAG = "HB141Log";

    public SnapRecyclerAdapter(Context context, ArrayList<Establishment> establishments, GoogleApiClient mGoogleApiClient) {
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.establishments = establishments;
        this.mGoogleApiClient = mGoogleApiClient;
    }

    @Override
    public ReyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = layoutInflater.inflate(R.layout.establishment_card, parent, false);
        return new ReyclerViewHolder(item);
    }

    @Override
    public void onBindViewHolder(final ReyclerViewHolder holder, int position) {
        final Establishment establishment = establishments.get(position);

        holder.establishmentCardView.setCardBackgroundColor(Color.WHITE);
        holder.establishmentInspectButton.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        holder.establishmentInspectButton.setTextColor(Color.WHITE);
        holder.establishmentInspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        new DownloadPlacePhoto(holder.establishmentBackground, holder.establishmentAttribution).execute(establishment.getId());

        holder.establishmentName.setText(establishment.getName());

        String typesMessage = "";
        List<Integer> establishmentTypes = establishment.getPlaceTypes();
        Integer thirtyfour = new Integer(34);
        establishmentTypes.remove(thirtyfour);
        Integer thousandthirteen = new Integer(1013);
        establishmentTypes.remove(thousandthirteen);
        Integer zero = new Integer(0);
        establishmentTypes.remove(zero);
        int typesSize = establishmentTypes.size();

        for(int i = 0; i < typesSize; i++) {
            String tempHolder = PlaceType.convertConstantToString(establishmentTypes.get(i));
            if((i+1) == typesSize){
                typesMessage += tempHolder;
            }
            else {
                typesMessage += tempHolder + " | ";
            }
        }
        Log.d(TAG, "Message: " + typesMessage);
        holder.establishmentTypes.setText(typesMessage);
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
        private CardView establishmentCardView;

        private ReyclerViewHolder(final View v) {
            super(v);

            establishmentBackground = (ImageView) v.findViewById(R.id.establishment_background);
            establishmentAttribution = (TextView) v.findViewById(R.id.establishment_attribution);
            establishmentInspectButton = (Button) v.findViewById(R.id.establishment_inspect_button);
            establishmentName = (TextView) v.findViewById(R.id.establishment_name);
            establishmentTypes = (TextView) v.findViewById(R.id.establishment_types);
            establishmentCardView = (CardView) v.findViewById(R.id.establishment_cardview);
        }
    }

    private class DownloadPlacePhoto extends AsyncTask<String, Void, DownloadPlacePhoto.AttributedPhoto> {
        ImageView establishment_background;
        TextView establishment_attribution;
        String placeId;

        DownloadPlacePhoto(ImageView establishment_background_holder, TextView establishment_attribution_holder) {
            this.establishment_background = establishment_background_holder;
            this.establishment_attribution = establishment_attribution_holder;
        }

        protected AttributedPhoto doInBackground(String... urls) {
            placeId = urls[0];
            AttributedPhoto downloadedPlacePhoto = null;

            PlacePhotoMetadataResult result = Places.GeoDataApi
                    .getPlacePhotos(mGoogleApiClient, placeId).await();

            if (result.getStatus().isSuccess()) {
                PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();
                if (photoMetadataBuffer.getCount() > 0 && !isCancelled()) {
                    // Get the first bitmap and its attributions.
                    PlacePhotoMetadata photo = photoMetadataBuffer.get(0);
                    CharSequence attribution = photo.getAttributions();
                    // Load a scaled bitmap for this photo.
                    Bitmap image = photo.getPhoto(mGoogleApiClient).await().getBitmap();

                    downloadedPlacePhoto = new AttributedPhoto(attribution, image);
                }
                // Release the PlacePhotoMetadataBuffer.
                photoMetadataBuffer.release();
            }

            return downloadedPlacePhoto;
        }

        protected void onPostExecute(AttributedPhoto result) {
            if(result != null) {
                establishment_background.setImageBitmap(result.bitmap);
                if (result.attribution == null) {
                    establishment_attribution.setVisibility(View.GONE);
                }
                else {
                    establishment_attribution.setVisibility(View.VISIBLE);
                    String attributionMessage = result.attribution.toString();
                    attributionMessage = attributionMessage.substring(attributionMessage.indexOf(">") + 1, attributionMessage.indexOf("</"));
                    attributionMessage = "Photo by " + attributionMessage;
                    Log.d(TAG, "Attribution: " + attributionMessage);
                    establishment_attribution.setText(attributionMessage);
                }
            }
        }

        class AttributedPhoto {
            final CharSequence attribution;
            final Bitmap bitmap;

            AttributedPhoto(CharSequence attribution, Bitmap bitmap) {
                this.attribution = attribution;
                this.bitmap = bitmap;
            }
        }
    }
}
