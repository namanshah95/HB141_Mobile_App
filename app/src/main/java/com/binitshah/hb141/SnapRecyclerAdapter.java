package com.binitshah.hb141;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LruCache;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

class SnapRecyclerAdapter extends RecyclerView.Adapter<SnapRecyclerAdapter.RecyclerViewHolder> {

    private LayoutInflater layoutInflater;
    private Context context;
    private ArrayList<Establishment> establishments;
    GoogleApiClient mGoogleApiClient;
    private static final String TAG = "HB141Log";
    private LruCache<String, Bitmap> mMemoryCache;

    public SnapRecyclerAdapter(Context context, ArrayList<Establishment> establishments, GoogleApiClient mGoogleApiClient) {
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.establishments = establishments;
        this.mGoogleApiClient = mGoogleApiClient;

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 2;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null  && bitmap != null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = layoutInflater.inflate(R.layout.establishment_card, parent, false);
        return new RecyclerViewHolder(item);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
        if(establishments.size() == position) {
            holder.specificEstablishmentView.setVisibility(View.GONE);
            holder.generalEstablishmentView.setVisibility(View.VISIBLE);
            holder.establishmentCardView.setCardBackgroundColor(Color.WHITE);
            //holder.generalEstablishInspectButton.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
            holder.generalEstablishInspectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Generic Action", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            final Establishment establishment = establishments.get(position);

            holder.specificEstablishmentView.setVisibility(View.VISIBLE);
            holder.generalEstablishmentView.setVisibility(View.GONE);
            holder.establishmentCardView.setCardBackgroundColor(Color.WHITE); //todo: change color to something using the name here and then set background image to transparent if no image is found.
            //holder.establishmentInspectButton.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
            holder.establishmentInspectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*Intent intent = new Intent(context, ReportActivity.class);
                    intent.putExtra("establishment", establishment);
                    context.startActivity(intent);*/
                    Toast.makeText(context, establishment.getName(), Toast.LENGTH_SHORT).show();
                }
            });

            loadBackgroundImage(establishment, holder.establishmentBackground, holder.establishmentAttribution);

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

            for (int i = 0; i < typesSize; i++) {
                String tempHolder = PlaceType.convertConstantToString(establishmentTypes.get(i));
                if ((i + 1) == typesSize) {
                    typesMessage += tempHolder;
                } else {
                    typesMessage += tempHolder + " | ";
                }
            }

            holder.establishmentTypes.setText(typesMessage);
        }
    }

    public void loadBackgroundImage(Establishment establishment, ImageView background, TextView attribution) {
        final Bitmap bitmap = getBitmapFromMemCache(establishment.getId());

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int usedMemory = (int) ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024);
        final float percentage = (float) usedMemory / (float) maxMemory;
        Log.d(TAG, "Total Memory: " + maxMemory + " | Used Memory: " + usedMemory + " | Percentage used: " + percentage);

        if(bitmap != null) {
            Log.d(TAG, "Pulling from cache for " + establishment.getName());
            background.setVisibility(View.VISIBLE);
            background.setImageBitmap(bitmap);
            attribution.setText(establishment.getAttributions());
        } else {
            Log.d(TAG, "Pulling from online for " + establishment.getName());
            new DownloadPlacePhoto(establishment, background, attribution).execute(establishment.getId());
        }
    }

    @Override
    public int getItemCount() {
        return establishments.size() + 1;
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private ImageView establishmentBackground;
        private TextView establishmentAttribution;
        private ImageView establishmentInspectButton;
        private TextView establishmentName;
        private TextView establishmentTypes;
        private CardView establishmentCardView;
        private RelativeLayout specificEstablishmentView;
        private RelativeLayout generalEstablishmentView;
        private ImageView generalEstablishInspectButton;

        private RecyclerViewHolder(final View v) {
            super(v);

            establishmentBackground = (ImageView) v.findViewById(R.id.establishment_background);
            establishmentAttribution = (TextView) v.findViewById(R.id.establishment_attribution);
            establishmentInspectButton = (ImageView) v.findViewById(R.id.establishment_inspect_button);
            establishmentName = (TextView) v.findViewById(R.id.establishment_name);
            establishmentTypes = (TextView) v.findViewById(R.id.establishment_types);
            establishmentCardView = (CardView) v.findViewById(R.id.establishment_cardview);
            specificEstablishmentView = (RelativeLayout) v.findViewById(R.id.specific_establishment);
            generalEstablishmentView = (RelativeLayout) v.findViewById(R.id.general_establishment);
            generalEstablishInspectButton = (ImageView) v.findViewById(R.id.general_establishment_inspect_button);
        }
    }

    private class DownloadPlacePhoto extends AsyncTask<String, Void, Bitmap> {
        Establishment establishment;
        ImageView establishment_background;
        TextView establishment_attribution;
        String placeId;

        DownloadPlacePhoto(Establishment establishment, ImageView establishment_background_holder, TextView establishment_attribution_holder) {
            this.establishment = establishment;
            this.establishment_background = establishment_background_holder;
            this.establishment_attribution = establishment_attribution_holder;
        }

        protected Bitmap doInBackground(String... placeIds) {
            placeId = placeIds[0];
            Bitmap downloadedPlaceBitmap = null;

            PlacePhotoMetadataResult result = Places.GeoDataApi
                    .getPlacePhotos(mGoogleApiClient, placeId).await();

            if (result.getStatus().isSuccess()) {
                PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();
                if (photoMetadataBuffer.getCount() > 0 && !isCancelled()) {
                    //Get photo
                    PlacePhotoMetadata photo = photoMetadataBuffer.get(0);
                    //get attribution and set it to establishment
                    CharSequence attribution = photo.getAttributions();
                    if(attribution != null) {
                        //process it to our standards for attribution. none of that html link bs
                        String raw = attribution.toString();
                        raw = raw.substring(raw.indexOf(">") + 1, raw.indexOf("</"));
                        raw = "Photo by " + raw;
                        establishment.setAttributions(raw);
                    } else {
                        establishment.setAttributions(null);
                    }

                    // Load a scaled bitmap for this photo.
                    downloadedPlaceBitmap = photo.getPhoto(mGoogleApiClient).await().getBitmap();
                }
                // Release the PlacePhotoMetadataBuffer.
                photoMetadataBuffer.release();
            }

            addBitmapToMemoryCache(establishment.getId(), downloadedPlaceBitmap);
            return downloadedPlaceBitmap;
        }

        protected void onPostExecute(Bitmap result) {
            //handle the background image
            if(result != null) {
                establishment_background.setVisibility(View.VISIBLE);
                establishment_background.setImageBitmap(result);
            } else {
                establishment_background.setVisibility(View.GONE);
            }

            //handle the attribution text
            if (establishment.getAttributions() == null) {
                establishment_attribution.setVisibility(View.GONE);
            }
            else {
                establishment_attribution.setVisibility(View.VISIBLE);
                establishment_attribution.setText(establishment.getAttributions());
            }
        }
    }
}
