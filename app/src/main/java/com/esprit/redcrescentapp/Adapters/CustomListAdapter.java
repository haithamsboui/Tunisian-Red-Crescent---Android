package com.esprit.redcrescentapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.esprit.redcrescentapp.R;
import com.esprit.redcrescentapp.entities.MessageItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by haith on 17/12/2015.
 */
public class CustomListAdapter extends ArrayAdapter<MessageItem> {


    public CustomListAdapter(Context context, ArrayList<MessageItem> Messages) {
        super(context, 0, Messages);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        MessageItem message = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_item, parent, false);
        }

        TextView TxMessageTitle = (TextView) convertView.findViewById(R.id.TxMessageTitle);
        TextView TxMessageDateStart = (TextView) convertView.findViewById(R.id.TxMessageDateStart);
        TextView TxMessageDateEnd = (TextView) convertView.findViewById(R.id.TxMessageDateEnd);
        ImageView IvMessage = (ImageView) convertView.findViewById(R.id.IvMessage);
        TextView TxMessageDescritption = (TextView) convertView.findViewById(R.id.TxMessageDescritption);


        TxMessageTitle.setText(message.getTitle());
        if (message.getStartdate() != null)
            TxMessageDateStart.setText(dateFormatter.format(message.getStartdate().getTime()));
        if (message.getEndDate() != null)
            TxMessageDateEnd.setText(dateFormatter.format(message.getEndDate().getTime()));
        IvMessage.setImageBitmap(message.getImage());
        TxMessageDescritption.setText(message.getContent());

        // Return the completed view to render on screen
        return convertView;
    }
}
