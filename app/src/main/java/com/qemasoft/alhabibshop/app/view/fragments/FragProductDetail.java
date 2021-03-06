package com.qemasoft.alhabibshop.app.view.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.qemasoft.alhabibshop.app.AppConstants;
import com.qemasoft.alhabibshop.app.Preferences;
import com.qemasoft.alhabibshop.app.R;
import com.qemasoft.alhabibshop.app.controller.ProductOptionsAdapter;
import com.qemasoft.alhabibshop.app.model.Options;
import com.qemasoft.alhabibshop.app.model.Product;
import com.qemasoft.alhabibshop.app.model.ProductOptionValueItem;
import com.qemasoft.alhabibshop.app.view.activities.FetchData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

import static com.qemasoft.alhabibshop.app.AppConstants.FORCED_CANCEL;
import static com.qemasoft.alhabibshop.app.AppConstants.ITEM_COUNTER;
import static com.qemasoft.alhabibshop.app.AppConstants.PRODUCT_DETAIL_REQUEST_CODE;
import static com.qemasoft.alhabibshop.app.AppConstants.appContext;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragProductDetail extends MyBaseFragment implements View.OnClickListener {

    private List<Options> optionsList;
    ProductOptionsAdapter.ProductOptionsAdapterInterface adapterInterface
            = new ProductOptionsAdapter.ProductOptionsAdapterInterface() {
        @Override
        public void OnItemClicked(int adapterPosition, String val) {
            utils.printLog("Interface Bridge Working! Id = " + adapterPosition
                    + "\nItem Value = " + val);
            AppConstants.optionsList.add(new ProductOptionValueItem(optionsList.get(adapterPosition)
                    .getProductOptionId(), val));
            for (int i = 0; i < AppConstants.optionsList.size(); i++) {
                utils.printLog("Option = " + AppConstants.optionsList.get(i)
                        .getOptionValueId() + " value = " + AppConstants.optionsList.get(i).getName());
                utils.printLog(" List Size = " + AppConstants.optionsList.size());
            }
            // Do whatever you wants to do with this data that is coming from your adapter
        }
    };
    private ViewPager mPager;
    private CircleIndicator indicator;
    private ProgressBar pb;
    //    private List<Reviews> reviewsList;
    private Product product;
    private TextView productTitleTV, productModelTV, manufacturerTV, productDescriptionTV,
            productPriceTV, productSpecialPriceTV, discountTV, percentDiscTV,
            stockStatusTV, dateAddedTV, optionsTV;
    //    writeReviewTV, postReviewTV, productQtyTV;
    private Button addToCartBtn;// submitReviewBtn;
    private RatingBar ratingBarOverall;//, ratingBarPost;
    private RecyclerView mRecyclerViewOptions; //mRecyclerViewRating;
//    private EditText yourNameET, reviewCommentET;
//    private TabHost tabHost;
//    private ScrollView scrollView;

    public FragProductDetail() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.frag_product_detail, container, false);
        initViews(view);
        initUtils();


        Bundle bundle = getArguments();
        if (bundle != null) {
            String id = getArguments().getString("id");
            requestData(id);
        } else {
            utils.showErrorDialog("No Data to Show");
        }

        addToCartBtn.setOnClickListener(this);


        return view;
    }

    private void initViews(View view) {

//        scrollView = view.findViewById(R.id.sv);

        mPager = view.findViewById(R.id.pager);
        indicator = view.findViewById(R.id.indicator);
        pb = view.findViewById(R.id.loading);

//        tabHost = view.findViewById(R.id.tabHost);
        productTitleTV = view.findViewById(R.id.product_title_val_tv);
        productModelTV = view.findViewById(R.id.product_model_val_tv);
        manufacturerTV = view.findViewById(R.id.maker_company_tv);
        productDescriptionTV = view.findViewById(R.id.product_desc_val_tv);
        productPriceTV = view.findViewById(R.id.product_price_val_tv);
        productSpecialPriceTV = view.findViewById(R.id.product_special_price_val_tv);
        discountTV = view.findViewById(R.id.product_disc_val_tv);
        percentDiscTV = view.findViewById(R.id.disc_percent_val_tv);
//        ratingBarOverall = view.findViewById(R.id.ratingBar);
        stockStatusTV = view.findViewById(R.id.stock_status_val_tv);
//        productQtyTV = view.findViewById(R.id.product_qty_tv);
        dateAddedTV = view.findViewById(R.id.added_date_val_tv);
        optionsTV = view.findViewById(R.id.options_available_tv);

        addToCartBtn = view.findViewById(R.id.add_to_cart_btn);

//        mRecyclerViewRating = view.findViewById(R.id.author_recycler_view);
        mRecyclerViewOptions = view.findViewById(R.id.product_options_recycler_view);
//        postReviewTV = view.findViewById(R.id.review_comment_tv);
//        yourNameET = view.findViewById(R.id.name_et);
//        reviewCommentET = view.findViewById(R.id.review_comment_et);
//        ratingBarPost = view.findViewById(R.id.post_rating_bar);
//
//        submitReviewBtn = view.findViewById(R.id.submit_btn);
    }

    private void requestData(String id) {

        AppConstants.setMidFixApi("getProduct/product_id/" + id);

        Bundle bundle = new Bundle();
        Intent intent = new Intent(getContext(), FetchData.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, PRODUCT_DETAIL_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PRODUCT_DETAIL_REQUEST_CODE) {
                try {
                    final JSONObject response = new JSONObject(data.getStringExtra("result"));
                    utils.printLog("ResponseInFragProDetail", response.toString());
                    JSONObject proObj = response.optJSONObject("product");

                    product = new Product(proObj.optString("id")
                            , proObj.optString("name")
                            , proObj.optString("model")
                            , proObj.optString("price")
                            , proObj.optString("special")
                            , proObj.optString("quantity")
                            , proObj.optString("description")
                            , proObj.optString("stock_status")
                            , proObj.optString("manufacturer")
                            , proObj.optString("disc")
                            , proObj.optString("disc_percent")
                            , proObj.optString("date_added")
                            , proObj.optString("rating")
                            , proObj.optString("review_total")
                    );
                    JSONArray slideShow = proObj.optJSONArray("slideshow");
                    AppConstants.setSlideshowExtra(slideShow.toString());
                    utils.setupSlider(mPager, indicator, pb, false, false);
                    productTitleTV.setText(product.getName());
                    if (!product.getManufacturer().isEmpty())
                        manufacturerTV.setText(product.getManufacturer());
                    productModelTV.setText(product.getModel());
                    productDescriptionTV.setText(product.getProductDescription());
                    productPriceTV.setText(product.getPrice().concat(" ").concat(symbol));
                    productSpecialPriceTV.setText(product.getSpacialPrice().concat(" ").concat(symbol));
                    if (!product.getDiscPercent().isEmpty()) {
                        percentDiscTV.setText(product.getDiscPercent().concat(" ").concat(symbol));
                    }

                    stockStatusTV.setText(product.getStockStatus());
//                    productQtyTV.setText(product.getQuantity());
                    dateAddedTV.setText(product.getDateAdded());

                    JSONArray optionsArray = proObj.optJSONArray("options");
                    optionsList = new ArrayList<>();
                    for (int j = 0; j < optionsArray.length(); j++) {
                        JSONObject optionsObj = optionsArray.getJSONObject(j);
                        JSONArray subOptionsArray = optionsObj.optJSONArray("product_option_value");
                        List<ProductOptionValueItem> subOptionsList = new ArrayList<>();
                        for (int k = 0; k < subOptionsArray.length(); k++) {
                            JSONObject subOptionsObj = subOptionsArray.getJSONObject(k);
                            subOptionsList.add(new ProductOptionValueItem(
                                    subOptionsObj.optString("option_value_id")
                                    , subOptionsObj.optString("name")));
                            String val = subOptionsList.get(k).getName();
                            utils.printLog("Color Value = ", val);
                        }
                        optionsList.add(new Options(optionsObj.optString("product_option_id")
                                , subOptionsList
                                , optionsObj.optString("name")
                                , optionsObj.optString("option_id")));
                    }
                    if (optionsList.isEmpty() || optionsList.size()<1) {
                        optionsTV.setVisibility(View.GONE);
                        return;
                    }
                    RecyclerView.LayoutManager mLayoutManagerOptions =
                            new LinearLayoutManager(getActivity()
                                    , LinearLayoutManager.VERTICAL, false);
                    mRecyclerViewOptions.setLayoutManager(mLayoutManagerOptions);
                    mRecyclerViewOptions.setAdapter(
                            new ProductOptionsAdapter(optionsList, adapterInterface));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        } else if (resultCode == FORCED_CANCEL) {
            utils.showToast("Request Cancelled by User");
        } else if (resultCode == Activity.RESULT_CANCELED) {
            utils.showErrorDialog("Error Getting Data From Server!");
        }


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.add_to_cart_btn:
                TextView itemCountTV = getActivity().findViewById(R.id.actionbar_notification_tv);
                int val = Preferences.getSharedPreferenceInt(appContext, ITEM_COUNTER, 0);
                val++;
                itemCountTV.setText(String.valueOf(val));
                Preferences.setSharedPreferenceInt(appContext, ITEM_COUNTER,
                        Integer.parseInt(itemCountTV.getText().toString()));
                Bundle bundle = new Bundle();
                bundle.putString("id", product.getProductId());
                utils.printLog("ProductId", "ID=" + product.getProductId());
                bundle.putString("midFix", "addCart");
                utils.switchFragment(new FragCartDetail(), bundle);
                break;
        }
    }
}
