package hu.anemeth.fontdecorator.demo;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hu.anemeth.fontdecorator.demo.R;

/**
 * Created by nemi on 2015.06.28..
 */
@SuppressLint("NewApi")
public class DummyFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dummy, container, false);
    }
}
