package com.qemasoft.alhabibshop.app.view.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.kofigyan.stateprogressbar.StateProgressBar;
import com.qemasoft.alhabibshop.app.AppConstants;
import com.qemasoft.alhabibshop.app.Preferences;
import com.qemasoft.alhabibshop.app.R;
import com.qemasoft.alhabibshop.app.controller.CartDetailAdapter;
import com.qemasoft.alhabibshop.app.model.Address;
import com.qemasoft.alhabibshop.app.model.MyCartDetail;
import com.qemasoft.alhabibshop.app.model.ShippingMethod;
import com.qemasoft.alhabibshop.app.view.activities.FetchData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.qemasoft.alhabibshop.app.AppConstants.ADDRESS_BOOK_REQUEST_CODE;
import static com.qemasoft.alhabibshop.app.AppConstants.ADD_ORDER_REQUEST_CODE;
import static com.qemasoft.alhabibshop.app.AppConstants.CONFIRM_CHECKOUT_REQUEST_CODE;
import static com.qemasoft.alhabibshop.app.AppConstants.CUSTOMER_KEY;
import static com.qemasoft.alhabibshop.app.AppConstants.DEFAULT_STRING_VAL;
import static com.qemasoft.alhabibshop.app.AppConstants.FORCED_CANCEL;
import static com.qemasoft.alhabibshop.app.AppConstants.LEFT;
import static com.qemasoft.alhabibshop.app.AppConstants.PAYMENT_METHOD_REQUEST_CODE;
import static com.qemasoft.alhabibshop.app.AppConstants.RIGHT;
import static com.qemasoft.alhabibshop.app.AppConstants.SHIPPING_METHOD_REQUEST_CODE;
import static com.qemasoft.alhabibshop.app.AppConstants.UNIQUE_ID_KEY;
import static com.qemasoft.alhabibshop.app.AppConstants.appContext;

public class FragCheckout extends MyBaseFragment implements View.OnClickListener {

    private List<Address> addressList;
    private Bundle bundle;
    private StateProgressBar stateProgressBar;
    private RadioGroup radioGroupShippingMethod, radioGroupPaymentMethod;
    private Button selectDeliveryAddress, backBtn, nextBtn;
    private CheckBox termsCB;
    private LinearLayout step1, step2, step3, step4, step5;
    private List<String> list;
    private TextView confirmOrderTV, subTotalValTV, grandTotalValTV;
    private int selectedAddressIndex;

    public FragCheckout() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.frag_checkout, container, false);
        initViews(view);
        initUtils();

        String[] descriptionData = {"Delivery", "Shipping", "Payment", "Confirm"};
        stateProgressBar.setStateDescriptionData(descriptionData);
        backBtn.setOnClickListener(this);
        selectDeliveryAddress.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        bundle = new Bundle();
        getAddresses();
        setDrawables();

        return view;
    }

    private void setDrawables() {
        utils.setCompoundDrawable(backBtn, LEFT, R.drawable.ic_navigate_back);
        utils.setCompoundDrawable(nextBtn, RIGHT, R.drawable.ic_navigate_next);
        utils.setCompoundDrawable(selectDeliveryAddress, RIGHT, R.drawable.ic_menu_more);
    }

    private void getAddresses() {
        AppConstants.setMidFixApi("getAddresses");
        Map<String, String> map = new HashMap<>();
        map.put("customer_id", Preferences.getSharedPreferenceString(appContext
                , CUSTOMER_KEY, DEFAULT_STRING_VAL));
        bundle.putBoolean("hasParameters", true);
        bundle.putSerializable("parameters", (Serializable) map);
        Intent intent = new Intent(getContext(), FetchData.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, ADDRESS_BOOK_REQUEST_CODE);
    }

    private void initViews(View view) {
        stateProgressBar = view.findViewById(R.id.state_progress_bar);
        backBtn = view.findViewById(R.id.back_btn);
        step1 = view.findViewById(R.id.step1);
        selectDeliveryAddress = view.findViewById(R.id.select_delivery_address_btn);
        step2 = view.findViewById(R.id.step2);
        radioGroupShippingMethod = view.findViewById(R.id.rg_shipping_method);
        step3 = view.findViewById(R.id.step3);
        radioGroupPaymentMethod = view.findViewById(R.id.rg_payment_method);
        step4 = view.findViewById(R.id.step4);
        step5 = view.findViewById(R.id.step5);
        confirmOrderTV = view.findViewById(R.id.confirm_order_tv);
        subTotalValTV = view.findViewById(R.id.sub_total_val_tv);
        grandTotalValTV = view.findViewById(R.id.grand_total_val_tv);

        termsCB = view.findViewById(R.id.terms_cb);
        nextBtn = view.findViewById(R.id.next_btn);
        mRecyclerView = view.findViewById(R.id.cart_detail_recycler_view);
    }

    @Override
    public void onClick(View v) {
        list = new ArrayList<>();
        if (!addressList.isEmpty()) {
            for (int i = 0; i < addressList.size(); i++) {
                list.add(addressList.get(i).getAddress());
            }
        }
        int rgPaymentCount = radioGroupPaymentMethod.getChildCount();
        int rgShippingCount = radioGroupPaymentMethod.getChildCount();
        switch (v.getId()) {
            case R.id.select_delivery_address_btn:
                if (addressList.isEmpty()) {
                    utils.showAlertDialog("Alert!",
                            "You need to add an address");
                } else {
                    utils.showRadioAlertDialog(selectDeliveryAddress
                            , "Select Address", list, 0, null);
                    if (selectedAddressIndex < 0) {
                        selectedAddressIndex = 0;
                    }
                }
                break;
            case R.id.next_btn:
                if (step1.getVisibility() == View.VISIBLE) {
                    AppConstants.setMidFixApi("shippingMethod");
                    Map<String, String> map = new HashMap<>();
                    map.put("customer_id", Preferences.getSharedPreferenceString(appContext
                            , CUSTOMER_KEY, DEFAULT_STRING_VAL));
                    map.put("address_id", addressList.get(selectedAddressIndex).getId());
                    bundle.putBoolean("hasParameters", true);
                    bundle.putSerializable("parameters", (Serializable) map);
                    Intent intent = new Intent(getContext(), FetchData.class);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, SHIPPING_METHOD_REQUEST_CODE);
                    step1.setVisibility(View.GONE);
                    step2.setVisibility(View.VISIBLE);
                    backBtn.setVisibility(View.VISIBLE);
                    stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                    if (rgShippingCount > 0) radioGroupShippingMethod.check(0);
                } else if (step2.getVisibility() == View.VISIBLE) {
                    AppConstants.setMidFixApi("paymentMethod");
                    Map<String, String> map = new HashMap<>();
                    map.put("customer_id", Preferences.getSharedPreferenceString(appContext
                            , CUSTOMER_KEY, DEFAULT_STRING_VAL));
                    map.put("address_id", addressList.get(selectedAddressIndex).getId());
                    map.put("session_id", Preferences.getSharedPreferenceString(appContext
                            , UNIQUE_ID_KEY, DEFAULT_STRING_VAL));
                    bundle.putBoolean("hasParameters", true);
                    bundle.putSerializable("parameters", (Serializable) map);
                    Intent intent = new Intent(getContext(), FetchData.class);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, PAYMENT_METHOD_REQUEST_CODE);
                    step2.setVisibility(View.GONE);
                    step3.setVisibility(View.VISIBLE);
                    stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
                    if (rgPaymentCount > 0) radioGroupPaymentMethod.check(0);
                } else if (step3.getVisibility() == View.VISIBLE) {
                    AppConstants.setMidFixApi("confirm");
                    Map<String, String> map = new HashMap<>();
                    map.put("customer_id", Preferences.getSharedPreferenceString(appContext
                            , CUSTOMER_KEY, DEFAULT_STRING_VAL));
                    map.put("address_id", addressList.get(selectedAddressIndex).getId());
                    map.put("session_id", Preferences.getSharedPreferenceString(appContext
                            , UNIQUE_ID_KEY, DEFAULT_STRING_VAL));
                    bundle.putBoolean("hasParameters", true);
                    bundle.putSerializable("parameters", (Serializable) map);
                    Intent intent = new Intent(getContext(), FetchData.class);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, CONFIRM_CHECKOUT_REQUEST_CODE);
                    if (isTermsCBChecked()) {
                        step3.setVisibility(View.GONE);
                        step4.setVisibility(View.VISIBLE);
                        nextBtn.setText(R.string.confirm_text);
                        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FOUR);
                    } else {
                        utils.showAlertDialog("Read Terms", "You have to Accept Terms and Condition to Continue");
                    }
                } else if (step4.getVisibility() == View.VISIBLE) {
                    backBtn.setVisibility(View.GONE);
                    nextBtn.setText(R.string.view_order_history_text);
                    step4.setVisibility(View.INVISIBLE);
                    step5.setVisibility(View.VISIBLE);
                    stateProgressBar.setAllStatesCompleted(true);
                    confirmOrderTV.setText(String.valueOf("You have placed your order successfully." +
                            "You can view your order history with the below button OR go to" +
                            "My Account => Order History"));
                }
                break;

            case R.id.back_btn:
                if (step4.getVisibility() == View.VISIBLE) {
                    step4.setVisibility(View.GONE);
                    step3.setVisibility(View.VISIBLE);
                    nextBtn.setText(R.string.next_text);
                    stateProgressBar.setAllStatesCompleted(false);
                    stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
                } else if (step3.getVisibility() == View.VISIBLE) {
                    step3.setVisibility(View.GONE);
                    step2.setVisibility(View.VISIBLE);
                    stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                } else if (step2.getVisibility() == View.VISIBLE) {
                    step2.setVisibility(View.GONE);
                    step1.setVisibility(View.VISIBLE);
                    backBtn.setVisibility(View.GONE);
                    stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
                }
                break;

        }
    }

    private boolean isTermsCBChecked() {
        return termsCB.isChecked();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        JSONObject response = null;
        try {
            response = new JSONObject(data.getStringExtra("result"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (response != null) {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == ADDRESS_BOOK_REQUEST_CODE) {
                    JSONArray addresses = response.optJSONArray("addresses");
                    addressList = new ArrayList<>();
                    for (int i = 0; i < addresses.length(); i++) {
                        JSONObject addressObj = addresses.optJSONObject(i);
                        addressList.add(new Address(addressObj.optString("address_id"),
                                        addressObj.optString("firstname"),
                                        addressObj.optString("lastname"),
                                        addressObj.optString("company"),
                                        addressObj.optString("address_1"),
                                        addressObj.optString("city"),
                                        addressObj.optString("postcode"),
                                        addressObj.optString("country"),
                                        addressObj.optString("zone"),
                                        addressObj.optBoolean("default_address")
                                )
                        );
                        if (addressList.size() > 0) {
                            selectDeliveryAddress.setHint(addressList.get(0).getAddress());
                        }
                    }
                } else if (requestCode == SHIPPING_METHOD_REQUEST_CODE) {
                    JSONArray shippingMethods = response.optJSONArray("shippingMethods");
                    List<ShippingMethod> shippingMethodList = new ArrayList<>();
                    List<String> keysList = new ArrayList<>();
                    radioGroupShippingMethod.removeAllViews();
                    if (shippingMethods == null || shippingMethods.toString().isEmpty()) {
                        utils.showErrorDialog("No Shipping Method Available");
                        return;
                    }
                    for (int i = 0; i < shippingMethods.length(); i++) {
                        JSONObject shippingObj = shippingMethods.optJSONObject(i);
                        Iterator<?> keys = shippingObj.keys();

                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            keysList.add(key);
                            utils.printLog("KeyStr", key);
                        }
                        utils.printLog("KeyStr", "Size = " + keysList.size());
                        JSONObject shippingMethod = shippingObj.optJSONObject(keysList.get(i));
                        shippingMethodList.add(new ShippingMethod(shippingMethod.optString("code")
                                , shippingMethod.optString("cost")
                                , shippingMethod.optString("text")
                                , shippingMethod.optString("title"))
                        );
                        RadioButton radioButton = new RadioButton(getActivity());
                        radioButton.setText(shippingMethodList.get(i).getTitle()
                                .concat(" - ").concat(symbol).concat(shippingMethodList.get(i).getCost()));
                        radioButton.setId(i);
                        RadioGroup.LayoutParams rgParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT,
                                RadioGroup.LayoutParams.WRAP_CONTENT);
                        radioGroupShippingMethod.addView(radioButton, rgParams);
                    }
                    radioGroupShippingMethod.check(0);
                } else if (requestCode == PAYMENT_METHOD_REQUEST_CODE) {
                    JSONArray paymentMethods = response.optJSONArray("paymentMethods");
                    List<ShippingMethod> paymentMethodList = new ArrayList<>();
                    List<String> keysList = new ArrayList<>();
                    radioGroupPaymentMethod.removeAllViews();
                    if (paymentMethods == null || paymentMethods.toString().isEmpty()) {
                        utils.showErrorDialog("No Payment Method Available");
                        return;
                    }
                    for (int i = 0; i < paymentMethods.length(); i++) {
                        JSONObject paymentObj = paymentMethods.optJSONObject(i);
                        Iterator<?> keys = paymentObj.keys();

                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            keysList.add(key);
                            utils.printLog("KeyStr", key);
                        }
                        utils.printLog("KeyStr", "Size = " + keysList.size());
                        JSONObject paymentMethod = paymentObj.optJSONObject(keysList.get(i));
                        paymentMethodList.add(new ShippingMethod(paymentMethod.optString("code")
                                , paymentMethod.optString("title")
                                , paymentMethod.optString("terms"))
                        );
                        RadioButton radioButton = new RadioButton(getActivity());
                        radioButton.setText(paymentMethodList.get(i).getTitle());
                        radioButton.setId(i);
                        RadioGroup.LayoutParams rgParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT,
                                RadioGroup.LayoutParams.WRAP_CONTENT);
                        radioGroupPaymentMethod.addView(radioButton, rgParams);
                    }
                    radioGroupPaymentMethod.check(0);
                } else if (requestCode == CONFIRM_CHECKOUT_REQUEST_CODE) {

                    JSONArray cartProducts = response.optJSONArray("cartProducts");
                    List<MyCartDetail> cartDetailList = new ArrayList<>();
                    if (cartProducts == null || cartProducts.toString().isEmpty()) {
                        utils.showErrorDialog("You have no products in cart");
                        return;
                    }
                    for (int i = 0; i < cartProducts.length(); i++) {
                        JSONObject objectCP = cartProducts.optJSONObject(i);
                        cartDetailList.add(new MyCartDetail(objectCP.optString("product_id"),
                                objectCP.optString("name"),
                                objectCP.optString("model"),
                                objectCP.optString("quantity"),
                                objectCP.optString("price"),
                                objectCP.optString("total")));
                    }
                    JSONArray cartTotals = response.optJSONArray("carttotals");
                    List<String> totalsList = new ArrayList<>();
                    for (int j = 0; j < cartTotals.length(); j++) {
                        JSONObject object = cartTotals.optJSONObject(j);
                        totalsList.add(object.optString("value"));
                    }
                    subTotalValTV.setText(totalsList.get(0));
                    grandTotalValTV.setText(totalsList.get(1));


                    CartDetailAdapter cartDetailAdapter = new CartDetailAdapter(cartDetailList
                            , true);
                    RecyclerView.LayoutManager mLayoutManager =
                            new LinearLayoutManager(context
                                    , LinearLayoutManager.VERTICAL, false);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    String className = FragCheckout.class.getSimpleName();
                    utils.printLog(className + "Adapter", "Before Cart list Adapter");
                    if (!cartDetailList.isEmpty() || cartDetailList.size() > 0)
                        mRecyclerView.setAdapter(cartDetailAdapter);

                } else if (requestCode == ADD_ORDER_REQUEST_CODE) {
                    System.out.println("I");
                }
            } else if (resultCode == FORCED_CANCEL) {
                String message = response.optString("message");
                if (!message.isEmpty()) {
                    utils.showAlertDialog("Message", message);
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                utils.showErrorDialog("Unable to Get Data From Server");
            }
        } else utils.showErrorDialog("Response is null");
    }
}
