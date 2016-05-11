package com.rambo.marketposter.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.rambo.marketposter.R;
import com.rambo.marketposter.activity.Base.BaseActivity;
import com.rambo.marketposter.application.ApiConst;
import com.rambo.marketposter.application.AppUtils;
import com.rambo.marketposter.application.Constants;
import com.rambo.marketposter.data.bean.Dish;
import com.rambo.marketposter.data.bean.OrderInfo;
import com.rambo.marketposter.data.feeds.OrderListFeed;
import com.rambo.marketposter.network.response.OrderGenerateResponse;
import com.rambo.marketposter.utils.ActivityUtils;
import com.rambo.marketposter.utils.Mylog;
import com.rambo.marketposter.utils.StringUtil;
import com.rambo.marketposter.utils.ToastUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by windy on 16/4/6.
 * 商品购物车
 */
public class GoodsCarActivity extends BaseActivity {


    public static final int HANDLER_LIST_REFRESH = 0;
    public static final int HANDLE_MSG_GET_DATA_SUCCESS = 1;


    @ViewInject(R.id.listViewDish)
    ListView listViewDish;

    @ViewInject(R.id.cbSelectAll)
    CheckBox cbSelctedAll;

    @ViewInject(R.id.tvCalcTotal)
    TextView tvCalcTotal;

    @ViewInject(R.id.tvDelDish)
    TextView tvDelDish;
    @ViewInject(R.id.btnPayConfirm)
    Button btnPayConfirm;

    @ViewInject(R.id.tvAdHotLine)
    TextView tvAdHotLine;
    @ViewInject(R.id.tvAdAddress)
    TextView tvAdAddress;

    private DishAdapter dishAdapter;
    private OrderListFeed orderListFeed;

    ArrayList<Dish> dishBuyList = new ArrayList<Dish>();//所有菜品在选中类型的

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_car);
        ViewUtils.inject(this);
        initView();

    }

    private void initView() {
        initData();
//        listViewDish.setPullRefreshEnable(false);
//        listViewDish.setAutoLoadEnable(false);
//        listViewDish.setPullLoadEnable(false);
//        listViewDish.setRefreshTime(StringUtil.getTime());


        dishAdapter = new DishAdapter(GoodsCarActivity.this);
        initListViewFooter();
        listViewDish.setAdapter(dishAdapter);
//

        cbSelctedAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selectAllDish(isChecked);
            }
        });


//        tvDelDish.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                delSelectedDish();
//            }
//        });

        calcTotalPrice();


        cbSelctedAll.performClick();

//        listViewDish.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                dishBuyList.get(position).setSelectedClicked();
//            }
//        });
    }

    private void initData() {
        initBottomAD();
        dishBuyList.addAll(ShopListActivity.getCurrentDishList());
        for (int i = 0; i < dishBuyList.size(); i++) {
            dishBuyList.get(i).isSelected = true;
            myHandler.sendEmptyMessage(HANDLER_LIST_REFRESH);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
//        if (listViewDish.getFooterViewsCount() == 0) {
//            initListViewFooter();
//        }
    }

    public void initListViewFooter() {
        LinearLayout footer = (LinearLayout) LayoutInflater.from(GoodsCarActivity.this).inflate(R.layout.listview_empty_footer, null);
//        footer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT));
        listViewDish.addFooterView(footer);
    }


    @OnClick({R.id.btnPayConfirm, R.id.btnBackToFront, R.id.tvDelDish})
    public void OnClicked(View view) {
        switch (view.getId()) {
            case R.id.btnPayConfirm:
                if (!checkValid()) return;
                requestGenerateOrder();
                break;
            case R.id.btnBackToFront:
                //把当前的数据回传回去
                ShopListActivity.updateAllDishList(dishBuyList);
                finish();
                break;
            case R.id.tvDelDish:
                delSelectedDish();
                break;
        }
    }

    //判断是否可以去购买按钮
    public boolean checkValid() {
        if (calcTotalPrice()) {

            return true;
        }

        ToastUtil.show(GoodsCarActivity.this, "请先选择菜品");
        return false;
    }

    class DishAdapter extends BaseAdapter {

        private Context mContext;
        private LayoutInflater mInflater;

        public DishAdapter(Context context) {
            this.mContext = context;
            mInflater = (LayoutInflater) context
                    .getSystemService(context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return dishBuyList != null ? dishBuyList.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return dishBuyList != null ? dishBuyList.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.list_view_item_dish, null);
                holder.imgDishPic = (ImageView) convertView
                        .findViewById(R.id.imgDishPic);
                holder.tvDisahDanJia = (TextView) convertView
                        .findViewById(R.id.tvDisahDanJia);
                holder.tvDisahGuiGe = (TextView) convertView
                        .findViewById(R.id.tvDisahGuiGe);
                holder.tvDishJiaGe = (TextView) convertView
                        .findViewById(R.id.tvDisahJiaGe);
                holder.tvDishJiaGe2 = (TextView) convertView
                        .findViewById(R.id.tvDisahJiaGe2);
                holder.tvDisahName = (TextView) convertView
                        .findViewById(R.id.tvDisahName);
                holder.tvAdd = (Button) convertView
                        .findViewById(R.id.tvAdd);
                holder.tvDel = (Button) convertView
                        .findViewById(R.id.tvDel);
                holder.tvDishResult = (TextView) convertView
                        .findViewById(R.id.tvDishResult);
                holder.tvCounter = (TextView) convertView
                        .findViewById(R.id.tvCounter);
                holder.cbDelDish = (CheckBox) convertView.findViewById(R.id.cbDelDish);
                holder.cbDelDish.setVisibility(View.VISIBLE);
                holder.rlFavor = (RelativeLayout) convertView.findViewById(R.id.rlFavor);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            final Dish dish = dishBuyList.get(position);
            if (dish != null) {
                holder.tvDisahName.setText(dish.dishName);
                holder.tvDisahDanJia.setText(dish.getDanJia());
                holder.tvDisahGuiGe.setText(dish.getGuiGe());
                holder.tvDishJiaGe.setText(dish.getJiaGe());
                holder.tvDishJiaGe2.setText(dish.getJiaGe2());
                if (!dish.isJiaGeEqualOC()) {
                    holder.tvDishJiaGe2.setVisibility(View.VISIBLE);
                    holder.tvDishJiaGe.setText(Html.fromHtml(dish.getJiaGeHtmlULine()));
//                    holder.tvDishJiaGe.setBackgroundDrawable(getDrawable(R.drawable.bg_text_middle_line));
                    holder.tvDishJiaGe.setBackgroundResource(R.drawable.bg_text_middle_line);
                } else {
                    holder.tvDishJiaGe2.setVisibility(View.GONE);
//                    holder.tvDishJiaGe.setBackgroundDrawable(null);
                    holder.tvDishJiaGe.setBackgroundResource(R.drawable.transpanet);
                }

                holder.tvCounter.setText(String.valueOf(dish.dishCount));
                holder.tvDishResult.setText(dish.getResult());
                holder.tvDishResult.setVisibility(StringUtil.isEmpty(dish.getResult()) ? View.GONE : View.VISIBLE);
                holder.cbDelDish.setChecked(dish.isSelected);
                holder.rlFavor.setVisibility(View.GONE);
                holder.cbDelDish.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        setDishSelected(position, isChecked);
                        myHandler.sendEmptyMessage(HANDLER_LIST_REFRESH);
                    }
                });

                holder.imgDishPic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dishBuyList.get(position).setSelectedClicked();
                        myHandler.sendEmptyMessage(HANDLER_LIST_REFRESH);
                    }
                });
                Picasso.with(mContext).load(dish.dishPic).placeholder(R.drawable.solid_gray).error(R.drawable.solid_gray).into(holder.imgDishPic);
            }

            holder.tvAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dish.addCount();
                    myHandler.sendEmptyMessage(HANDLER_LIST_REFRESH);
                }
            });
            holder.tvDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dish.delCount();
                    myHandler.sendEmptyMessage(HANDLER_LIST_REFRESH);
                }
            });
            return convertView;
        }

        public class ViewHolder {
            public ImageView imgDishPic;
            public TextView tvDisahName;
            public TextView tvDisahDanJia;
            public TextView tvDisahGuiGe;
            public TextView tvDishJiaGe;
            public TextView tvDishJiaGe2;
            public Button tvDel;
            public Button tvAdd;
            public TextView tvDishResult;
            public TextView tvCounter;
            public CheckBox cbDelDish;
            public RelativeLayout rlFavor;
        }

    }


    private void refreshDishList() {
        if (dishAdapter != null) {
            dishAdapter.notifyDataSetChanged();
        }
        calcTotalPrice();
    }

    private void selectAllDish(boolean selected) {
        for (int i = 0; i < dishBuyList.size(); i++) {
            dishBuyList.get(i).isSelected = selected;
        }
        refreshDishList();
    }

    private void delSelectedDish() {
        for (int i = dishBuyList.size() - 1; i >= 0; i--) {
            if (dishBuyList.get(i).isSelected) {
                dishBuyList.get(i).dishCount = 0;//先置0 后UI 删除
                dishBuyList.remove(i);
            }
        }
        refreshDishList();
        calcTotalPrice();
    }


    //计算总费用
    private boolean calcTotalPrice() {//购物车总计：￥6.00元
        double total = 0;

        for (int i = 0; i < dishBuyList.size(); i++) {
            Dish dish = dishBuyList.get(i);
            if (dish.dishCount > 0 && dish.isSelected) {
                total += dish.getTotalPrice();
            }
        }

        tvCalcTotal.setText("购物车总计：￥" + StringUtil.getDouble(total) + "元");
        return total != 0;
    }

    private void setDishSelected(int position, boolean isSelected) {
        dishBuyList.get(position).isSelected = isSelected;

    }

    private ArrayList<Dish> getValidBuyList() {
        ArrayList<Dish> result = new ArrayList<>();

        for (int i = 0; i < dishBuyList.size(); i++) {
            if (dishBuyList.get(i).isSelected) {
                result.add(dishBuyList.get(i));
            }
        }

        return result;
    }

    private void requestGenerateOrder() {
        final String url = ApiConst.API_SERVER_IP + ApiConst.API_GENERATEORDER;

        final HashMap<String, String> map = new HashMap<String, String>();

        map.put("data", AppUtils.getOrderDishJsonString(getValidBuyList()));

        httpService.postStringRequest(url, map, new Response.Listener<String>() {
            @Override
            public void onResponse(String resp) {
                OrderGenerateResponse rs = new OrderGenerateResponse();
                orderListFeed = new OrderListFeed();
                rs.parseResponse(orderListFeed, resp);
                myHandler.sendEmptyMessage(HANDLE_MSG_GET_DATA_SUCCESS);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Mylog.d("requestPrise:", volleyError.toString());
            }
        });

    }

    public Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case HANDLER_LIST_REFRESH:
                    if (listViewDish != null) {
                        refreshDishList();
                    }

                    calcTotalPrice();
                    break;

                case HANDLE_MSG_GET_DATA_SUCCESS:

                    if (orderListFeed.resCode == 0) {
                        Intent intent = new Intent(GoodsCarActivity.this, OrderConfirmActivity.class);
                        OrderInfo orderInfo = orderListFeed.orderInfos.get(0);
                        intent.putExtra(OrderConfirmActivity.BUNDLE_ORDER_INFO, orderInfo);
                        ActivityUtils.startActivity(GoodsCarActivity.this, intent);
                    } else {
                        ToastUtil.show(GoodsCarActivity.this, orderListFeed.resMsg);
                    }

                    break;

            }
        }
    };

    private void initBottomAD() {
        tvAdHotLine.setText(Constants.APP_NUMBER);
        tvAdAddress.setText(Constants.APP_CONFIG_URL);
    }

}
