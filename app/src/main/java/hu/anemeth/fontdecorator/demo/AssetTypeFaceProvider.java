package hu.anemeth.fontdecorator.demo;

import android.content.res.AssetManager;
import android.graphics.Typeface;

import hu.anemeth.fontdecorator.TypefaceProvider;

/**
 * Created by nemi on 2015.06.28..
 */
public class AssetTypeFaceProvider implements TypefaceProvider {
    private final AssetManager assetManager;

    public AssetTypeFaceProvider(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    @Override
    public Typeface get(String name, int style) {
        if(name == null) {
            return null;
        }

        return Typeface.createFromAsset(assetManager, name);
    }
}
