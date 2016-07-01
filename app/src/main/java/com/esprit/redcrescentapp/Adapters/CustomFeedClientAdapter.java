package com.esprit.redcrescentapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.esprit.redcrescentapp.R;
import com.esprit.redcrescentapp.activites.MainActivity;
import com.esprit.redcrescentapp.entities.FeedItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by haith on 28/12/2015.
 */

public class CustomFeedClientAdapter extends ArrayAdapter<FeedItem> implements View.OnClickListener {

    FeedItem message;

    public CustomFeedClientAdapter(Context context, ArrayList<FeedItem> Messages) {
        super(context, 0, Messages);

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        message = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.feed_item, parent, false);
        }

        TextView TxMessageTitle = (TextView) convertView.findViewById(R.id.TxTitleFeed);
        TextView TxMessageLink = (TextView) convertView.findViewById(R.id.TxLinkFeed);
        TextView TxMessageDescription = (TextView) convertView.findViewById(R.id.TxDescriptionFeed);
        ImageView IvMessage = (ImageView) convertView.findViewById(R.id.IvPictureFeed);
        TextView TxMessageCaption = (TextView) convertView.findViewById(R.id.TxcaptionFeed);

        TxMessageLink.setOnClickListener(this);

        if (message.getMessage() != null)
            TxMessageTitle.setText(message.getMessage());

        if (message.getLink() != null)
            TxMessageLink.setText(message.getName());

        if (message.getDescription() != null)
            TxMessageDescription.setText(message.getDescription());
        if (message.getPicture() != null)
            IvMessage.setImageBitmap(message.getPicture());
        if (message.getCaption() != null)
            TxMessageCaption.setText(message.getCaption());


        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public void onClick(View v) {
        if (message != null) {
            if (message.getLink() != null) {
                Intent myWebLink = new Intent(android.content.Intent.ACTION_VIEW);
                myWebLink.setData(Uri.parse(message.getLink()));
                MainActivity.Main.startActivity(myWebLink);
            }
        }

    }
}
