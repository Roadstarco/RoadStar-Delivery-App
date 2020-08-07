package com.countrycodepicker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.shrinkcom.utils.SessionManager;
import com.shrinkcom.passenger.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class CountryPicker extends DialogFragment implements
        Comparator<Country> {

    private EditText searchEditText;
    private ListView countryListView;

    private CountryListAdapter adapter;

    private ArrayList<Country> allCountriesList;

    private ArrayList<Country> selectedCountriesList;

    private CountryPickerListener listener;
    private String Language_code = "";

    public void setListener(CountryPickerListener listener) {
        this.listener = listener;
    }

    public EditText getSearchEditText() {
        return searchEditText;
    }

    public ListView getCountryListView() {
        return countryListView;
    }

    private SessionManager sessionManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialog);
    }

    public static Currency getCurrencyCode(String countryCode) {
        try {
            return Currency.getInstance(new Locale("es", countryCode));
        } catch (Exception e) {

        }
        return null;
    }

    private ArrayList<Country> getAllCountries() {
        if (allCountriesList == null) {
            try {
                allCountriesList = new ArrayList<Country>();

                String allCountriesCode = readEncodedJsonString(getActivity());
//                String prabu = allCountriesCode.replaceAll("\r\n", "");
//                System.out.println("---prabu---" + prabu);

                    JSONArray countrArray = new JSONArray(allCountriesCode.toString());

                    for (int i = 0; i < countrArray.length(); i++) {
                        JSONObject jsonObject = countrArray.getJSONObject(i);
                        String countryName = jsonObject.getString("name");
                        String countryDialCode = jsonObject.getString("dial_code");
                        String countryCode = jsonObject.getString("code");

                        Country country = new Country();
                        country.setCode(countryCode);
                        country.setName(countryName);
                        country.setDialCode(countryDialCode);
                        allCountriesList.add(country);
                    }



                Collections.sort(allCountriesList, this);

                selectedCountriesList = new ArrayList<Country>();
                selectedCountriesList.addAll(allCountriesList);
                for (int j = 0; j < selectedCountriesList.size(); j++) {
                    System.out.println("----------selectedCountriesList name---------" + selectedCountriesList.get(j).getName());
                }

                // Return
                return allCountriesList;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static String readEncodedJsonString(Context context)
            throws java.io.IOException {
        String base64 = context.getResources().getString(R.string.countries_code);
        byte[] data = Base64.decode(base64, Base64.DEFAULT);
        return new String(data, "UTF-8");
    }

    /**
     * To support show as dialog
     *
     * @param dialogTitle
     * @return
     */
    public static CountryPicker newInstance(String dialogTitle) {
        CountryPicker picker = new CountryPicker();
        Bundle bundle = new Bundle();
        bundle.putString("dialogTitle", dialogTitle);
        picker.setArguments(bundle);
        return picker;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.country_picker, null);
        Locale locale = null;
        sessionManager = new SessionManager(getActivity());
        HashMap<String, String> language = sessionManager.getLanaguage();
        Language_code = language.get(SessionManager.KEY_Language);
        System.out.println("------------Language_code--------------"+Language_code);

        switch (Language_code){

            case "en":
                locale = new Locale("en");
                sessionManager.setlamguage("en","en");
                break;
            case "es":
                locale = new Locale("es");
                sessionManager.setlamguage("es","es");
                break;
            case "ta":
                locale = new Locale("ta");
                sessionManager.setlamguage("ta","ta");
                break;
            default:
                locale = new Locale("en");
                sessionManager.setlamguage("en","en");
                break;
        }


        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());


        getAllCountries();

        Bundle args = getArguments();
        if (args != null) {
            String dialogTitle = args.getString("dialogTitle");
            getDialog().setTitle(dialogTitle);

            int width = getResources().getDimensionPixelSize(
                    R.dimen.cp_dialog_width);
            int height = getResources().getDimensionPixelSize(
                    R.dimen.cp_dialog_height);
            getDialog().getWindow().setLayout(width, height);
        }

        searchEditText = (EditText) view
                .findViewById(R.id.country_code_picker_search);
        countryListView = (ListView) view
                .findViewById(R.id.country_code_picker_listview);

        adapter = new CountryListAdapter(getActivity(), selectedCountriesList);
        countryListView.setAdapter(adapter);

        countryListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                View view1 = getActivity().getCurrentFocus();
                if (view1 != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
                }
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                if (listener != null) {
                    Country country = selectedCountriesList.get(position);
                    listener.onSelectCountry(country.getName(),
                            country.getCode(), country.getDialCode());
                }
            }
        });

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    // close keyboard
                    InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
                }
                return false;
            }
        });


        searchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                search(s.toString());
            }
        });

        return view;
    }

    @SuppressLint("DefaultLocale")
    private void search(String text) {
        selectedCountriesList.clear();

        for (Country country : allCountriesList) {
            if (country.getName().toLowerCase(Locale.ENGLISH)
                    .contains(text.toLowerCase())) {
                selectedCountriesList.add(country);
            }
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public int compare(Country lhs, Country rhs) {
        return lhs.getName().compareTo(rhs.getName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        View view1 = getActivity().getCurrentFocus();
        if (view1 != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        View view1 = getActivity().getCurrentFocus();
        if (view1 != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
        }
    }
}
