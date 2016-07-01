package com.esprit.redcrescentapp.entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by haith on 17/12/2015.
 */
public class MessageItem {

    String Title;
    String ID;
    Bitmap Image;
    String Content;
    Date Startdate;
    Date EndDate;
    Location location;

    public MessageItem(JSONObject jsonObject) throws JSONException, ParseException, IOException {
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        if (jsonObject.has("Description")) {
            Content = jsonObject.getString("Description");
        }
        if (jsonObject.has("Title")) {
            Title = jsonObject.getString("Title");

        }
        if (jsonObject.has("id")) {
            ID = jsonObject.getString("id");

        }
        if (jsonObject.has("StartDate")) {
            Startdate = dateFormatter.parse(jsonObject.getString("StartDate").replace("T", " ").replace("Z", ""));
        }
        if (jsonObject.has("EndDate")) {
            EndDate = dateFormatter.parse(jsonObject.getString("EndDate").replace("T", " ").replace("Z", ""));

        }
        if (jsonObject.has("ImageFile")) {
            String ImageFile = jsonObject.getString("ImageFile");
            Log.d("IMAGE", ImageFile);
            URL url = null;

            url = new URL(ImageFile);
            //  url=new URL("https://crt-server-ibicha.c9users.io/images/messages/566ca22c969d5aacc1fe6e1e.png");
            final URL finalUrl = url;
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Image = BitmapFactory.decodeStream(finalUrl.openConnection().getInputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Bitmap getImage() {
        return Image;
    }

    public void setImage(Bitmap image) {
        Image = image;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public Date getStartdate() {
        return Startdate;
    }

    public void setStartdate(Date startdate) {
        Startdate = startdate;
    }

    public Date getEndDate() {
        return EndDate;
    }

    public void setEndDate(Date endDate) {
        EndDate = endDate;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
