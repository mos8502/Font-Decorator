package hu.anemeth.fontdecorator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.FragmentManager;
import android.widget.FrameLayout;

import com.synchronoss.how2test.R;

/**
 * Created by nemi on 2015.06.28..
 */
@SuppressLint("NewApi")
public class MainActivity extends Activity {

    FontDecoratingLayoutInflater layoutInflater;
    FrameLayout fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentContainer = (FrameLayout)findViewById(R.id.fragment_container);
        if(savedInstanceState == null) {
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction().add(R.id.fragment_container, new DummyFragment()).commitAllowingStateLoss();
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
