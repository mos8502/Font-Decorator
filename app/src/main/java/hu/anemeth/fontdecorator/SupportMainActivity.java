package hu.anemeth.fontdecorator;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

/**
 * Created by nemi on 2015.06.28..
 */
public class SupportMainActivity extends AppCompatActivity {

    FontDecoratingLayoutInflater layoutInflater;
    FrameLayout fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentContainer = (FrameLayout)findViewById(R.id.fragment_container);
        if(savedInstanceState == null) {
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().add(R.id.fragment_container, new DummySupportFragment()).commitAllowingStateLoss();
        }
    }

    @Override
    public Object getSystemService(String name) {
        if(Context.LAYOUT_INFLATER_SERVICE.equals(name)) {
            if(layoutInflater == null) {
                layoutInflater = new FontDecoratingLayoutInflater(this, new AssetTypeFaceProvider(getAssets()));
            }

            return layoutInflater;
        }

        return super.getSystemService(name);
    }
}
