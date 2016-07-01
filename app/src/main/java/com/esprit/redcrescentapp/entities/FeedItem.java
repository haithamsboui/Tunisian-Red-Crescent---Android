package com.esprit.redcrescentapp.entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

/**
 * Created by haith on 28/12/2015.
 */
public class FeedItem {
    Bitmap picture;
    String message;
    String link;
    String caption;
    String description;
    String name;

    public FeedItem(JSONObject jsonObject) throws JSONException, IOException {

        if (jsonObject.has("name")) {
            name = jsonObject.getString("name");
        }
        if (jsonObject.has("description")) {
            description = jsonObject.getString("description");

        }
        if (jsonObject.has("link")) {
            link = jsonObject.getString("link");

        }
        if (jsonObject.has("caption")) {
            caption = jsonObject.getString("caption");

        }
        if (jsonObject.has("message")) {
            message = jsonObject.getString("message");

        }
        if (jsonObject.has("picture")) {
            String ImageFile = jsonObject.getString("picture");
            Log.d("IMAGE", ImageFile);
            URL url = null;

            url = new URL(ImageFile);

            final URL finalUrl = url;
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        picture = BitmapFactory.decodeStream(finalUrl.openConnection().getInputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            //  url=new URL("https://crt-server-ibicha.c9users.io/images/messages/566ca22c969d5aacc1fe6e1e.png");
        }
    }


    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public void setPicture(String pictureLink) throws IOException {
        Log.d("IMAGE", pictureLink);
        URL url = null;

        url = new URL(pictureLink);
        picture = BitmapFactory.decodeStream(url.openConnection().getInputStream());
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
