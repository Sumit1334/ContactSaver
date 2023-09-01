package com.sumit.contactsaver;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;
import androidx.core.content.ContextCompat;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;
import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.runtime.EventDispatcher;


public class ContactSaver extends AndroidNonvisibleComponent {
    private static final String TAG = "ContactSaver";
    private final Context context;

    public ContactSaver(ComponentContainer container) {
        super(container.$form());
        this.context = container.$context();
    }

    public void createContact(Context context, String name, String phoneNumber) {
        try {
            ContentResolver contentResolver = context.getContentResolver();

            // Create a new raw contact
            ContentValues values = new ContentValues();
            Uri rawContactUri = contentResolver.insert(RawContacts.CONTENT_URI, values);
            long rawContactId = ContentUris.parseId(rawContactUri);

            // Insert contact name
            values.clear();
            values.put(Data.RAW_CONTACT_ID, rawContactId);
            values.put(Data.MIMETYPE, CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
            values.put(CommonDataKinds.StructuredName.DISPLAY_NAME, name);
            contentResolver.insert(Data.CONTENT_URI, values);

            // Insert contact phone number
            values.clear();
            values.put(Data.RAW_CONTACT_ID, rawContactId);
            values.put(Data.MIMETYPE, CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            values.put(CommonDataKinds.Phone.NUMBER, phoneNumber);
            values.put(CommonDataKinds.Phone.TYPE, CommonDataKinds.Phone.TYPE_MOBILE);
            contentResolver.insert(Data.CONTENT_URI, values);
        } catch (Exception e) {
            ContactSaver.this.ErrorOccurred(e.getMessage());
            Log.e(TAG, "createContact: ", e);
        }
    }

    @SimpleFunction
    public void AskPermission(){
        form.AskForPermission(Manifest.permission.WRITE_CONTACTS);
    }

    @SimpleFunction
    public boolean HavePermission(){
        return ContextCompat.checkSelfPermission(context,Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }

    @SimpleFunction
    public void CreateContact(String name, String phoneNumber) {
        createContact(context, name, phoneNumber);
    }

    @SimpleEvent
    public void ErrorOccurred(String error) {
        EventDispatcher.dispatchEvent(this, "ErrorOccurred", error);
    }
}
