package hu.anemeth.fontdecorator;

import android.graphics.Typeface;

/**
 * Created by nemi on 2015.06.28..
 */
public interface TypefaceProvider {
    Typeface get(String name, int style);
}
