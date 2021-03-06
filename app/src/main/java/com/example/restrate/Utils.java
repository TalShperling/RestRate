package com.example.restrate;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.navigation.Navigation;

public class Utils {
    public static void returnBack(View view) {
        Navigation.findNavController(view).popBackStack();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean isValidEmail(String target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static String costMeterTextConverter(String costMeter) {
        String costMeterStringified = "";

        for (int i = 0; i < Integer.parseInt(costMeter); i++) {
            costMeterStringified = costMeterStringified.concat("$");
        }

        return costMeterStringified;
    }
}
