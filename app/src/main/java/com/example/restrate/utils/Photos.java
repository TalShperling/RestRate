package com.example.restrate.utils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.fragment.app.FragmentActivity;

import com.example.restrate.model.GenericEventListenerWithNoParam;
import com.example.restrate.model.GenericEventListenerWithParam;

public class Photos {
    public static final int SELECT_PICTURE_FROM_CAMERA = 0;
    public static final int SELECT_PICTURE_FROM_GALLERY = 1;

    public static void editImage(FragmentActivity activity, GenericEventListenerWithNoParam onTakePictureListener, GenericEventListenerWithParam<Intent> onGalleryPictureListener) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    onTakePictureListener.onComplete();

                } else if (options[item].equals("Choose from Gallery")) {
                    imageChooser(onGalleryPictureListener);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private static void imageChooser(GenericEventListenerWithParam<Intent> onGalleryPictureListener) {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        onGalleryPictureListener.onComplete(i);
    }
}
