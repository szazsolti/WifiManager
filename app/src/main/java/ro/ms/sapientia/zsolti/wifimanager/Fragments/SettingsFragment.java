package ro.ms.sapientia.zsolti.wifimanager.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import ro.ms.sapientia.zsolti.wifimanager.Communication.Client;
import ro.ms.sapientia.zsolti.wifimanager.R;
import yuku.ambilwarna.AmbilWarnaDialog;

public class SettingsFragment extends Fragment {

    private Context context;
    private ConstraintLayout constraintLayout;
    private Button userColor;
    private Button onlineUsersColor;
    private Button referencePointsColor;
    private Switch aSwitch;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public SettingsFragment(Context context){
        this.context = context;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        constraintLayout = view.findViewById(R.id.settingLayout);
        userColor = view.findViewById(R.id.selectUserColorButton);
        onlineUsersColor = view.findViewById(R.id.selectOnlineUserColorButton);
        referencePointsColor = view.findViewById(R.id.seleReferencePointsColorButton);
        aSwitch = view.findViewById(R.id.sw_autoFloorSwitch);
        aSwitch.setChecked(Client.getInstance().getAutoFloor());

        userColor.setTextColor(Client.getInstance().getClientDotColor());
        onlineUsersColor.setTextColor(Client.getInstance().getOnlineUsersDotColor());
        referencePointsColor.setTextColor(Client.getInstance().getReferencePointDotColor());

        userColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPickerUser();
            }
        });

        onlineUsersColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPickerUsers();
            }
        });

        referencePointsColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPickerReferencePoints();
            }
        });

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Client.getInstance().setAutoFloor(isChecked);
            }
        });

        return view;
    }


    public void openColorPickerUser(){
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(context, Client.getInstance().getClientDotColor(), new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                Client.getInstance().setClientDotColor(color);
                userColor.setTextColor(color);
            }
        });
        colorPicker.show();
    }

    public void openColorPickerUsers(){
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(context, Client.getInstance().getOnlineUsersDotColor(), new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                Client.getInstance().setOnlineUsersDotColor(color);
                onlineUsersColor.setTextColor(color);
            }
        });
        colorPicker.show();
    }

    public void openColorPickerReferencePoints(){
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(context, Client.getInstance().getReferencePointDotColor(), new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                Client.getInstance().setReferencePointDotColor(color);
                referencePointsColor.setTextColor(color);
            }
        });
        colorPicker.show();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
