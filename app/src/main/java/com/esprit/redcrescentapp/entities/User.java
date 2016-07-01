package com.esprit.redcrescentapp.entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by haith on 21/11/2015.
 */
public class User {
    //Basic
    private String FirstName;
    private String LastName;
    private String Email;
    private Date BirthDate;
    //Extended
    private String NationalId;
    private String Password;
    private String BloodType;
    private String PhoneNumber;

    //Sharing
    private String ImageFile;
    private Bitmap Image;
    private String Username;
    private String FacebookId;
    private Adresse Adress;

    private Boolean IsMember;
    private Boolean IsAdmin;
    private Location LastRecordedLocation;
    private String Id;

    public User(JSONObject jsonObject) {

        try {
            if (jsonObject.has("id"))
                Id = jsonObject.getString("id");
            if (jsonObject.has("FirstName"))
                FirstName = jsonObject.getString("FirstName");
            if (jsonObject.has("LastName"))
                LastName = jsonObject.getString("LastName");
            if (jsonObject.has("IsMember"))
                IsMember = jsonObject.getBoolean("IsMember");
            if (jsonObject.has("Email"))
                Email = jsonObject.getString("Email");
            if (jsonObject.has("BirthDate")) {
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    BirthDate = format.parse(jsonObject.getString("BirthDate"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (jsonObject.has("NationalId"))
                NationalId = jsonObject.getString("NationalId");
            if (jsonObject.has("PhoneNumber"))
                PhoneNumber = jsonObject.getString("PhoneNumber");
            if (jsonObject.has("Username"))
                Username = jsonObject.getString("Username");
            if (jsonObject.has("ImageFile")) {
                ImageFile = jsonObject.getString("ImageFile").replace("\\/", "\\");
                final URL url = new URL(ImageFile);
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
            if (jsonObject.has("IsAdmin"))
                IsAdmin = jsonObject.getBoolean("IsAdmin");
            if (jsonObject.has("BloodType"))
                BloodType = jsonObject.getString("BloodType");
            if (jsonObject.has("FacebookId"))
                FacebookId = jsonObject.getString("FacebookId");
            if (jsonObject.has("Address")) {
                Adress = new Adresse();
                JSONObject jAddress = jsonObject.getJSONObject("Address");
                if (jAddress.has("StreetAddress"))
                    Adress.StreetAddress = jAddress.getString("StreetAddress");
                if (jAddress.has("City"))
                    Adress.City = jAddress.getString("City");
                if (jAddress.has("ZipCode"))
                    Adress.ZipCode = jAddress.getInt("ZipCode");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    public String getFirstName() {
        if (FirstName == null)
            FirstName = "";
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        if (LastName == null)
            LastName = "";
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getEmail() {
        if (Email == null)
            Email = "";
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public Date getBirthDate() {
        return BirthDate;
    }

    public void setBirthDate(Date birthDate) {
        BirthDate = birthDate;
    }

    public String getNationalId() {
        if (Email == null)
            NationalId = "";
        return NationalId;
    }

    public void setNationalId(String nationalId) {
        NationalId = nationalId;
    }

    public String getPhoneNumber() {
        if (PhoneNumber == null)
            PhoneNumber = "";
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public User.Adresse getAdress() {
        return Adress;
    }

    public void setAdress(User.Adresse adress) {
        Adress = adress;
    }

    public String getPassword() {
        if (Password == null)
            Password = "";
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getFacebookId() {

        if (FacebookId == null)
            FacebookId = "";
        return FacebookId;
    }

    public void setFacebookId(String facebookId) {
        FacebookId = facebookId;
    }

    public String getUsername() {
        if (Username == null)
            Username = "";
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getImageFile() {
        return ImageFile;
    }

    public void setImageFile(String imageFile) {
        ImageFile = imageFile;
    }

    public Boolean getIsMember() {
        return IsMember;
    }

    public void setIsMember(Boolean isMember) {
        IsMember = isMember;
    }

    public Boolean getIsAdmin() {
        return IsAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        IsAdmin = isAdmin;
    }

    public String getBloodType() {
        if (BloodType == null)
            BloodType = "";
        return BloodType;
    }

    public void setBloodType(String bloodType) {
        BloodType = bloodType;
    }

    public Location getLastRecordedLocation() {
        return LastRecordedLocation;
    }

    public Bitmap getImage() {
        return Image;
    }

    public void setImage(Bitmap image) {
        Image = image;
    }

    public void setLastRecordedLocation(Location lastRecordedLocation) {
        LastRecordedLocation = lastRecordedLocation;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public void setAdress(String adress) {
        if (!adress.isEmpty()) {
            String[] a = adress.split(",", 3);
            Adress.setCity(a[0]);
            Adress.setStreetAddress(a[0]);
            Adress.setZipCode(Integer.getInteger(a[0]));
        }
    }

    public class Adresse {
        private String StreetAddress;
        private String City;
        private int ZipCode;


        public String getStreetAddress() {
            return StreetAddress;
        }

        public void setStreetAddress(String streetAddress) {
            StreetAddress = streetAddress;
        }

        public String getCity() {
            return City;
        }

        public void setCity(String city) {
            City = city;
        }

        public int getZipCode() {
            return ZipCode;
        }

        public void setZipCode(int zipCode) {
            ZipCode = zipCode;
        }
    }
}
