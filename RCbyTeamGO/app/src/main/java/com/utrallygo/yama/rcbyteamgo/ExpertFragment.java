package com.utrallygo.yama.rcbyteamgo;


import android.app.Activity;
import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExpertFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExpertFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SECTION_NUMBER = "section_number";

    // Instance of root DataHolder
    DataHolder data = new DataHolder();
    ClockHandler clock = new ClockHandler();
    SubClass sub = new SubClass(data, clock);


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ExpertFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExpertFragment newInstance(int sectionNumber) {
        ExpertFragment fragment = new ExpertFragment();
        Bundle args = new Bundle();
        //set number of this fragment
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ExpertFragment() {
        // not used constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_expert, container, false);

        // get Display instance
        for (int i = 0; i < 5; ++i) {
            data.nDisplay[i] = (TextView) v.findViewById(data.id_nDisplay[i]);
        }

        // get number_Button instance
        for (int i = 0; i < 16; ++i) {
            data.nButton[i] = (Button) v.findViewById(data.id_nButton[i]);
        }

        // set TextView fontType
        setType(data);
        setButtonsListener(data);

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // set actionbar indicator
        ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onResume() {
        super.onResume();
        Thread thread = new Thread(sub, "ClockHandler");
        thread.start();
    }

    /**
     * From this, define the methods
     *
     */

    // this function make text type of some of TextView Digital
    private void setType(DataHolder data) {
        for (int i = 0; i < 5; ++i) {
            data.nDisplay[i].setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Let's go Digital Regular.ttf"));
        }
    }

    // set ButtonsListener
    private void setButtonsListener(DataHolder data) {

        // give DataHolder instance to ButtonsListener
        ButtonsListener b = new ButtonsListener(data);

        for (int i =0; i < 16; ++i) {
            data.nButton[i].setOnClickListener(b);
        }
    }

}
