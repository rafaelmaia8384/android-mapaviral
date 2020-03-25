package br.com.smarttoolsapps.mapaviral;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.paolorotolo.appintro.ISlidePolicy;

import br.com.smarttoolsapps.mapaviral.R;

public class FragmentIntro4 extends Fragment implements ISlidePolicy {

    private boolean canContinue = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_intro4, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        final Switch switchGPS = getActivity().findViewById(R.id.switchGPS);

        switchGPS.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (switchGPS.isChecked()) {

                    checkPermission();
                }
            }
        });
    }

    public void checkPermission() {

        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, IntroActivity.CODE_PERMISSION_REQUEST);
            }
        }
    }

    @Override
    public boolean isPolicyRespected() {

        return canContinue;
    }

    @Override
    public void onUserIllegallyRequestedNextPage() {

    }
}
