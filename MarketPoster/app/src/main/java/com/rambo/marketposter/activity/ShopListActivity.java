package com.rambo.marketposter.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.rambo.marketposter.R;
import com.rambo.marketposter.activity.Base.BaseActivity;
import com.rambo.marketposter.application.ApiConst;
import com.rambo.marketposter.application.Constants;
import com.rambo.marketposter.data.bean.Dish;
import com.rambo.marketposter.data.bean.DishType;
import com.rambo.marketposter.data.feeds.DishListFeed;
import com.rambo.marketposter.network.response.DishListResponse;
import com.rambo.marketposter.utils.ActivityUtils;
import com.rambo.marketposter.utils.Mylog;
import com.rambo.marketposter.utils.StringUtil;
import com.rambo.marketposter.utils.ToastUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by windy on 16/4/6.
 * 商品列表页
 */
public class ShopListActivity extends BaseActivity {

    public static final int HANDLE_MSG_GET_DATA_SUCCESS = 1;
    public static final int HANDLE_MSG_GET_DATA_FAILED = 2;


    public static final int HANDLER_LIST_REFRESH = 0;

    private static ShopListActivity instence;
    private DishListFeed dishListFeed;

    @ViewInject(R.id.listViewDish)
    ListView listViewDish;
    @ViewInject(R.id.gridViewType)
    GridView gridViewType;

    @ViewInject(R.id.tvAdHotLine)
    TextView tvAdHotLine;
    @ViewInject(R.id.tvAdAddress)
    TextView tvAdAddress;

    @ViewInject(R.id.btnPayConfirm)
    Button btnPayConfirm;
    @ViewInject(R.id.tvTypeSelectedName)
    TextView tvTypeSelectedName;

    @ViewInject(R.id.viewLoading)
    View viewLoading;

    @ViewInject(R.id.llJinRiTeJiaDish)
    View llJinRiTeJiaDish;
    @ViewInject(R.id.llJinRiTeJiaDish2)
    View llJinRiTeJiaDish2;

    public Dish dishSpecial;
    public Dish dishSpecial_2;


    @ViewInject(R.id.btnBackToFront)
    Button btnBackToFront;

    @ViewInject(R.id.tvCalcTotal)
    TextView tvCalcTotal;

    @ViewInject(R.id.tvCalcCounter)
    TextView tvCalcCounter;

    @ViewInject(R.id.tvEmpty)
    TextView tvEmpty;

    ArrayList<Dish> dishNormallList = new ArrayList<Dish>();//普通菜列表
    public static ArrayList<Dish> dishArrayListInType = new ArrayList<Dish>();//所有菜品在选中类型的
    public ArrayList<DishType> dishTypeArrayList = new ArrayList<DishType>();//所有菜的大类
    public ArrayList<Dish> dishSpecialList = new ArrayList<Dish>();//特价菜列表


    private DishAdapter dishAdapter;
    private DishTypeAdapter dishTypeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instence = this;
        setContentView(R.layout.activity_shop_list);
        ViewUtils.inject(this);
//        initView();
        requestGetData();
        initBottomAD();
    }

    private void initView() {
        initData();
//        listViewDish.setPullRefreshEnable(false);
//        listViewDish.setAutoLoadEnable(false);
//        listViewDish.setPullLoadEnable(false);
//        listViewDish.setRefreshTime(StringUtil.getTime());
        dishAdapter = new DishAdapter(ShopListActivity.this);
        initListViewFooter();
        listViewDish.setAdapter(dishAdapter);
        listViewDish.setEmptyView(tvEmpty);

        dishTypeAdapter = new DishTypeAdapter(ShopListActivity.this);
        gridViewType.setAdapter(dishTypeAdapter);
        dishTypeArrayList.get(0).performSelected();
        dishTypeAdapter.notifyDataSetChanged();

        gridViewType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectType(position);
            }
        });

        initSpecialDish();//特价菜1
        initSpecialDish2();//特价菜2
    }

    public void initListViewFooter() {
        LinearLayout footer = (LinearLayout) LayoutInflater.from(ShopListActivity.this).inflate(R.layout.listview_empty_footer, null);
//        footer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT));
        listViewDish.addFooterView(footer);
    }


    public void setImageButton(ImageButton btnImage) {
        btnImage.setBackgroundResource(btnImage.isSelected() ? R.drawable.fav_selected : R.drawable.fav_unselected);
    }


    private void initData() {
        dishArrayListInType.clear();
        dishNormallList = dishListFeed.dishList.dishNormalItems;
        dishSpecialList = dishListFeed.dishList.dishSpcialItems;
        dishTypeArrayList = dishListFeed.dishList.dishTypes;
        dishArrayListInType.addAll(dishNormallList);
    }

    //判断是否可以去购买按钮
    public boolean checkValid() {
        if (calcTotalPrice()) {

            return true;
        }
        ToastUtil.show(ShopListActivity.this, "请先选择菜品");
        return false;
    }

    @OnClick({R.id.btnPayConfirm, R.id.btnBackToFront})
    public void OnClicked(View view) {
        switch (view.getId()) {
            case R.id.btnPayConfirm:
                if (!checkValid()) return;

                Intent intent = new Intent(ShopListActivity.this, GoodsCarActivity.class);
                ActivityUtils.startActivity(ShopListActivity.this, intent);

                break;

            case R.id.btnBackToFront:
                finish();
                break;
        }
    }


    private void refreshUI() {
        updateUIDishSpcial();
        updateUIDishSpcial_2();
        myHandler.sendEmptyMessage(HANDLER_LIST_REFRESH);
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
            return dishArrayListInType != null ? dishArrayListInType.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return dishArrayListInType != null ? dishArrayListInType.get(position) : null;
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
                holder.tvDishResult = (TextView) convertView.findViewById(R.id.tvDishResult);
                holder.tvCounter = (TextView) convertView
                        .findViewById(R.id.tvCounter);
                holder.tvFavNumber = (TextView) convertView.findViewById(R.id.tvFavNumber);
                holder.imgFavor = (ImageButton) convertView.findViewById(R.id.imgFav);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            final Dish dish = dishArrayListInType.get(position);
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
                    holder.tvDishJiaGe.setBackgroundDrawable(null);
                    holder.tvDishJiaGe.setBackgroundResource(R.drawable.transpanet);
                }


                holder.tvCounter.setText(String.valueOf(dish.dishCount));
                holder.tvDishResult.setText(dish.getResult());
                holder.tvDishResult.setTextColor(dish.getResultColor(ShopListActivity.this));
                holder.tvDishResult.setVisibility(StringUtil.isEmpty(dish.getResult()) ? View.GONE : View.VISIBLE);
                holder.tvFavNumber.setText(String.valueOf(dish.getDishPariseCount()) + "人喜欢");
                holder.imgFavor.setSelected(dish.isFav);

                holder.tvAdd.setEnabled(!dish.isSoldOver());
                holder.tvDel.setEnabled(!dish.isSoldOver());
                setImageButton(holder.imgFavor);


                holder.imgDishPic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDishQrCode(dish);
                    }
                });
                Picasso.with(mContext).load(dish.dishPic).placeholder(R.drawable.solid_gray).error(R.drawable.solid_gray).into(holder.imgDishPic);
            }

            holder.imgFavor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.isSelected()) {
                        return;
                    } else {
                        ((ImageButton) v).setSelected(!((ImageButton) v).isSelected());
                        dish.isFav = ((ImageButton) v).isSelected();
                        myHandler.sendEmptyMessage(HANDLER_LIST_REFRESH);
                        requesPrise(dish.dishId);
                    }

                }
            });

            holder.tvAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dish.addCount()) {
                        myHandler.sendEmptyMessage(HANDLER_LIST_REFRESH);
                        startAniForCounter();
                    }
                }
            });
            holder.tvDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (dish.delCount()) {
                        myHandler.sendEmptyMessage(HANDLER_LIST_REFRESH);
                        startAniForCounter();
                    }

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
            public TextView tvFavNumber;
            public ImageButton imgFavor;
        }


    }


    private void refreshDishList() {
        if (dishAdapter != null) {
            dishAdapter.notifyDataSetChanged();
        }
    }


    class DishTypeAdapter extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mInflater;

        public DishTypeAdapter(Context context) {
            this.mContext = context;
            mInflater = (LayoutInflater) context
                    .getSystemService(context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return dishTypeArrayList != null ? dishTypeArrayList.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return dishTypeArrayList != null ? dishTypeArrayList.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.grid_view_item_dish_type, null);

                holder.tvDisahTypeName = (TextView) convertView
                        .findViewById(R.id.tvDishTypeName);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            DishType dishType = dishTypeArrayList.get(position);
            if (dishType != null) {
                holder.tvDisahTypeName.setText(dishType.dishTypeName);
                holder.tvDisahTypeName.setTextColor(dishType.isSelected ? getResources().getColor(R.color.tomato) : getResources().getColor(R.color.text_black_alpha90));
            }
            return convertView;
        }

        public class ViewHolder {
            public TextView tvDisahTypeName;
        }
    }


    public void reloadDishData() {
        dishArrayListInType.clear();
        for (int i = 0; i < dishNormallList.size(); i++) {
            if (isValidType(dishNormallList.get(i))) {
                dishArrayListInType.add(dishNormallList.get(i));
            }
        }
        dishAdapter.notifyDataSetChanged();
        myHandler.sendEmptyMessage(HANDLER_LIST_REFRESH);

    }

    public boolean isValidType(Dish dishItem) {
        if (dishTypeArrayList.get(0).isSelected) return true;//如果是全部，则全选了。

        for (int i = 0; i < dishTypeArrayList.size(); i++) {
            if (dishTypeArrayList.get(i).isSelected && dishTypeArrayList.get(i).dishTypeId == dishItem.dishType) {
                return true;
            }
        }
        return false;
    }

    public void selectType(int position) {
        if (dishTypeArrayList != null && position >= 0 && position < dishTypeArrayList.size()) {
            for (int i = 0; i < dishTypeArrayList.size(); i++) {
                dishTypeArrayList.get(i).isSelected = false;
            }
            dishTypeArrayList.get(position).isSelected = true;
            dishTypeAdapter.notifyDataSetChanged();
            reloadDishData();
        }

        tvTypeSelectedName.setText(dishTypeArrayList.get(position).dishTypeName);
    }

    public static ArrayList<Dish> getCurrentDishList() {
        return instence.makeDishListForOrder();
    }

    //处理回传回来的菜品信息，在购物车的数据处理后同步到菜品列表页
    public static void updateAllDishList(ArrayList<Dish> dishList) {
//        instence.updateAllDishListInstance(dishList);
        instence.refreshUI();
    }

    public void updateAllDishListInstance(ArrayList<Dish> dishLis) {
        //step1:清空所有菜品数量
        for (int i = 0; i < dishNormallList.size(); i++) {
            dishNormallList.get(i).dishCount = 0;
        }
        if (dishSpecial != null) {
            dishSpecial.dishCount = 0;
        }
        if (dishSpecial_2 != null) {
            dishSpecial_2.dishCount = 0;
        }
        if (dishLis == null) return;
        //step2:把每个回传回来的菜品遍历传数量
        for (int i = 0; i < dishLis.size(); i++) {
            updateForOneDishCount(dishLis.get(i));
        }
    }

    public void updateForOneDishCount(Dish dish) {
        if (dishSpecial != null && dishSpecial.isSameDishId(dish)) {
            dishSpecial.dishCount = dish.dishCount;
        }
        if (dishSpecial_2 != null && dishSpecial_2.isSameDishId(dish)) {
            dishSpecial_2.dishCount = dish.dishCount;
        }
        for (int i = 0; i < dishNormallList.size(); i++) {
            if (dishNormallList.get(i).isSameDishId(dish)) {
                dishNormallList.get(i).dishCount = dish.dishCount;
            }
        }
    }

    public void requestGetData() {

        final String url = ApiConst.API_SERVER_IP + ApiConst.API_GET_PRODUCE_LIST;


        httpService.getStringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String resp) {
                DishListResponse rs = new DishListResponse();
                dishListFeed = new DishListFeed();
                rs.parseResponse(dishListFeed, resp);
                myHandler.sendEmptyMessage(HANDLE_MSG_GET_DATA_SUCCESS);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
    }

    private void requesPrise(int produceId) {
        final String url = ApiConst.API_SERVER_IP + ApiConst.API_PRODUCT_PRAISE;

        final HashMap<String, String> map = new HashMap<String, String>();

        map.put("produceId", String.valueOf(produceId));

        httpService.postStringRequest(url, map, new Response.Listener<String>() {
            @Override
            public void onResponse(String resp) {

                Mylog.d("requestPrise:", resp);

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

                case HANDLE_MSG_GET_DATA_SUCCESS:
                    initView();
                    viewLoading.setVisibility(View.GONE);
                    break;

                case HANDLER_LIST_REFRESH:
                    if (dishArrayListInType != null) {
                        refreshDishList();
                    }
                    calcTotalPrice();

                    break;
            }

        }
    };

    //计算总费用
    private boolean calcTotalPrice() {//购物车总计：￥6.00元
        double total = 0;
        int count = 0;

        ArrayList<Dish> list = makeDishListForOrder();
        for (int i = 0; i < list.size(); i++) {
            Dish dish = list.get(i);
            if (dish.dishCount > 0) {
                total += dish.getTotalPrice();
                count += dish.dishCount;
            }
        }

        if (count > 999) count = 999;//最大值显示999
        tvCalcTotal.setText("购物车总计：￥" + StringUtil.getDouble(total) + "元");
        if (count > 99) {
            tvCalcCounter.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.font_very_small2));
        } else {
            tvCalcCounter.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.font_very_small));
        }
        tvCalcCounter.setText("" + count);
        tvCalcCounter.setVisibility(count == 0 ? View.GONE : View.VISIBLE);
        return total != 0;
    }


    //计算出列表
    private ArrayList<Dish> makeDishListForOrder() {
        ArrayList<Dish> dishsBuyList = new ArrayList<>();
        for (int i = 0; i < dishNormallList.size(); i++) {
            Dish dish = dishNormallList.get(i);
            if (dish.dishCount > 0) {
                dishsBuyList.add(dish);
            }
        }

        if (dishSpecial != null && dishSpecial.dishCount > 0) {
            dishsBuyList.add(dishSpecial);
        }

        if (dishSpecial_2 != null && dishSpecial_2.dishCount > 0) {
            dishsBuyList.add(dishSpecial_2);
        }

        return dishsBuyList;
    }

    private void startAniForCounter() {
        Animation ani = new AnimationUtils().loadAnimation(ShopListActivity.this, R.anim.counter_add);
        tvCalcCounter.startAnimation(ani);
    }


    ImageView imgDishPic;
    TextView tvDisahDanJia;
    TextView tvDisahGuiGe;
    TextView tvDishJiaGe;
    TextView tvDishJiaGe2;
    TextView tvDisahName;
    Button tvAdd;
    Button tvDel;
    TextView tvCounter;
    TextView tvFavNumber;
    ImageButton imgFav;
    TextView tvDishResult;

    private void initSpecialDish() {
        if (dishSpecialList == null || dishSpecialList.size() == 0) {
            llJinRiTeJiaDish.setVisibility(View.GONE);
            return;
        }
        dishSpecial = dishSpecialList.get(0);
        if (dishSpecial != null) {

            imgDishPic = (ImageView) llJinRiTeJiaDish
                    .findViewById(R.id.imgDishPic);
            tvDisahDanJia = (TextView) llJinRiTeJiaDish
                    .findViewById(R.id.tvDisahDanJia);
            tvDisahGuiGe = (TextView) llJinRiTeJiaDish
                    .findViewById(R.id.tvDisahGuiGe);
            tvDishJiaGe = (TextView) llJinRiTeJiaDish
                    .findViewById(R.id.tvDisahJiaGe);
            tvDishJiaGe2 = (TextView) llJinRiTeJiaDish
                    .findViewById(R.id.tvDisahJiaGe2);
            tvDisahName = (TextView) llJinRiTeJiaDish
                    .findViewById(R.id.tvDisahName);
            tvAdd = (Button) llJinRiTeJiaDish
                    .findViewById(R.id.tvAdd);
            tvDel = (Button) llJinRiTeJiaDish
                    .findViewById(R.id.tvDel);
            tvCounter = (TextView) llJinRiTeJiaDish
                    .findViewById(R.id.tvCounter);

            tvDishResult = (TextView) llJinRiTeJiaDish
                    .findViewById(R.id.tvDishResult);

            tvFavNumber = (TextView) llJinRiTeJiaDish.findViewById(R.id.tvFavNumber);
            imgFav = (ImageButton) llJinRiTeJiaDish.findViewById(R.id.imgFav);

            tvAdd.setEnabled(!dishSpecial.isSoldOver());
            tvDel.setEnabled(!dishSpecial.isSoldOver());

            tvDishResult.setText(dishSpecial.getResult());
            tvDishResult.setVisibility(StringUtil.isEmpty(dishSpecial.getResult()) ? View.GONE : View.VISIBLE);
            tvDishResult.setTextColor(dishSpecial.getResultColor(ShopListActivity.this));

            if (dishSpecial != null) {
                tvDisahName.setText(dishSpecial.dishName);
                tvDisahDanJia.setText(dishSpecial.getDanJia());
                tvDisahGuiGe.setText(dishSpecial.getGuiGe());
                tvDishJiaGe.setText(dishSpecial.getJiaGe());
                tvDishJiaGe2.setText(dishSpecial.getJiaGe2());
                if (!dishSpecial.isJiaGeEqualOC()) {
                    tvDishJiaGe2.setVisibility(View.VISIBLE);
                    tvDishJiaGe.setText(Html.fromHtml(dishSpecial.getJiaGeHtmlULine()));
//                    tvDishJiaGe.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_text_middle_line));
                    tvDishJiaGe.setBackgroundResource(R.drawable.bg_text_middle_line);
                } else {
                    tvDishJiaGe2.setVisibility(View.GONE);
//                    tvDishJiaGe.setBackgroundDrawable(null);
                    tvDishJiaGe.setBackgroundResource(R.drawable.transpanet);
                }
                tvCounter.setText(String.valueOf(dishSpecial.dishCount));
                tvFavNumber.setText(String.valueOf(dishSpecial.dishPraisecount) + "人喜欢");
                setImageButton(imgFav);
                imgDishPic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDishQrCode(dishSpecial);
                    }
                });
                Picasso.with(ShopListActivity.this).load(dishSpecial.dishPic).placeholder(R.drawable.solid_gray).error(R.drawable.solid_gray).into(imgDishPic);
            }

            imgFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (imgFav.isSelected()) {
                        return;
                    } else {
                        imgFav.setSelected(!imgFav.isSelected());
                        setImageButton(imgFav);
                        dishSpecial.isFav = imgFav.isSelected();
                        requesPrise(dishSpecial.dishId);
                        tvFavNumber.setText(String.valueOf(dishSpecial.getDishPariseCount()) + "人喜欢");
                    }
                }
            });

            tvAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dishSpecial.addCount()) {
                        tvCounter.setText(String.valueOf(dishSpecial.dishCount));
                        tvDishResult.setText(dishSpecial.getResult());
                        tvDishResult.setVisibility(StringUtil.isEmpty(dishSpecial.getResult()) ? View.GONE : View.VISIBLE);
                        calcTotalPrice();
                        startAniForCounter();
                    }
                }
            });
            tvDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dishSpecial.delCount()) {
                        tvCounter.setText(String.valueOf(dishSpecial.dishCount));
                        tvDishResult.setText(dishSpecial.getResult());
                        tvDishResult.setVisibility(StringUtil.isEmpty(dishSpecial.getResult()) ? View.GONE : View.VISIBLE);
                        calcTotalPrice();
                        startAniForCounter();
                    }
                }
            });
        }
    }

    private void updateUIDishSpcial() {
        if (dishSpecial != null) {
            dishSpecial.isFav = imgFav.isSelected();
            tvFavNumber.setText(String.valueOf(dishSpecial.getDishPariseCount()) + "人喜欢");
            tvCounter.setText(String.valueOf(dishSpecial.dishCount));
            tvDishResult.setText(dishSpecial.getResult());
            tvDishResult.setVisibility(StringUtil.isEmpty(dishSpecial.getResult()) ? View.GONE : View.VISIBLE);
        }
    }

    private void updateUIDishSpcial_2() {
        if (dishSpecial_2 != null) {
            setImageButton(imgFav_2);
            tvFavNumber_2.setText(String.valueOf(dishSpecial_2.getDishPariseCount()) + "人喜欢");
            tvCounter_2.setText(String.valueOf(dishSpecial_2.dishCount));
            tvDishResult_2.setText(dishSpecial_2.getResult());
            tvDishResult_2.setVisibility(StringUtil.isEmpty(dishSpecial_2.getResult()) ? View.GONE : View.VISIBLE);
        }
    }

    ImageView imgDishPic_2;
    TextView tvDisahDanJia_2;
    TextView tvDisahGuiGe_2;
    TextView tvDishJiaGe_2;
    TextView tvDishJiaGe2_2;
    TextView tvDisahName_2;
    Button tvAdd_2;
    Button tvDel_2;
    TextView tvCounter_2;
    TextView tvFavNumber_2;
    ImageButton imgFav_2;
    TextView tvDishResult_2;

    private void initSpecialDish2() {
        if (dishSpecialList == null || dishSpecialList.size() < 2) {
            llJinRiTeJiaDish2.setVisibility(View.GONE);
            return;
        }
        dishSpecial_2 = dishSpecialList.get(1);
        if (dishSpecial_2 != null) {

            imgDishPic_2 = (ImageView) llJinRiTeJiaDish2
                    .findViewById(R.id.imgDishPic);
            tvDisahDanJia_2 = (TextView) llJinRiTeJiaDish2
                    .findViewById(R.id.tvDisahDanJia);
            tvDisahGuiGe_2 = (TextView) llJinRiTeJiaDish2
                    .findViewById(R.id.tvDisahGuiGe);
            tvDishJiaGe_2 = (TextView) llJinRiTeJiaDish2
                    .findViewById(R.id.tvDisahJiaGe);
            tvDishJiaGe2_2 = (TextView) llJinRiTeJiaDish2
                    .findViewById(R.id.tvDisahJiaGe2);
            tvDisahName_2 = (TextView) llJinRiTeJiaDish2
                    .findViewById(R.id.tvDisahName);
            tvAdd_2 = (Button) llJinRiTeJiaDish2
                    .findViewById(R.id.tvAdd);
            tvDel_2 = (Button) llJinRiTeJiaDish2
                    .findViewById(R.id.tvDel);
            tvCounter_2 = (TextView) llJinRiTeJiaDish2
                    .findViewById(R.id.tvCounter);

            tvDishResult_2 = (TextView) llJinRiTeJiaDish2
                    .findViewById(R.id.tvDishResult);

            tvFavNumber_2 = (TextView) llJinRiTeJiaDish2.findViewById(R.id.tvFavNumber);
            imgFav_2 = (ImageButton) llJinRiTeJiaDish2.findViewById(R.id.imgFav);

            tvAdd_2.setEnabled(!dishSpecial_2.isSoldOver());
            tvDel_2.setEnabled(!dishSpecial_2.isSoldOver());

            tvDishResult_2.setText(dishSpecial_2.getResult());
            tvDishResult_2.setVisibility(StringUtil.isEmpty(dishSpecial_2.getResult()) ? View.GONE : View.VISIBLE);
            tvDishResult_2.setTextColor(dishSpecial_2.getResultColor(ShopListActivity.this));

            if (dishSpecial_2 != null) {
                tvDisahName_2.setText(dishSpecial_2.dishName);
                tvDisahDanJia_2.setText(dishSpecial_2.getDanJia());
                tvDisahGuiGe_2.setText(dishSpecial_2.getGuiGe());
                tvDishJiaGe_2.setText(dishSpecial_2.getJiaGe());
                tvDishJiaGe2_2.setText(dishSpecial_2.getJiaGe2());
                if (!dishSpecial_2.isJiaGeEqualOC()) {
                    tvDishJiaGe2_2.setVisibility(View.VISIBLE);
                    tvDishJiaGe_2.setText(Html.fromHtml(dishSpecial_2.getJiaGeHtmlULine()));
//                    tvDishJiaGe_2.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_text_middle_line));
                    tvDishJiaGe_2.setBackgroundResource(R.drawable.bg_text_middle_line);
                } else {
                    tvDishJiaGe2_2.setVisibility(View.GONE);
                    tvDishJiaGe_2.setBackgroundDrawable(null);
                    tvDishJiaGe_2.setBackgroundResource(R.drawable.transpanet);
                }
                tvCounter_2.setText(String.valueOf(dishSpecial_2.dishCount));
                tvFavNumber_2.setText(String.valueOf(dishSpecial_2.dishPraisecount) + "人喜欢");
                setImageButton(imgFav_2);

                imgDishPic_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDishQrCode(dishSpecial_2);
                    }
                });
                Picasso.with(ShopListActivity.this).load(dishSpecial_2.dishPic).placeholder(R.drawable.solid_gray).error(R.drawable.solid_gray).into(imgDishPic_2);
            }

            imgFav_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (imgFav_2.isSelected()) {
                        return;
                    } else {
                        imgFav_2.setSelected(!imgFav_2.isSelected());
                        setImageButton(imgFav_2);
                        dishSpecial_2.isFav = imgFav_2.isSelected();
                        requesPrise(dishSpecial_2.dishId);
                        tvFavNumber_2.setText(String.valueOf(dishSpecial_2.getDishPariseCount()) + "人喜欢");
                    }
                }
            });

            tvAdd_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dishSpecial_2.addCount()) {
                        tvCounter_2.setText(String.valueOf(dishSpecial_2.dishCount));
                        tvDishResult_2.setText(dishSpecial_2.getResult());
                        tvDishResult_2.setVisibility(StringUtil.isEmpty(dishSpecial_2.getResult()) ? View.GONE : View.VISIBLE);
                        calcTotalPrice();
                        startAniForCounter();
                    }
                }
            });
            tvDel_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dishSpecial_2.delCount()) {
                        tvCounter_2.setText(String.valueOf(dishSpecial_2.dishCount));
                        tvDishResult_2.setText(dishSpecial_2.getResult());
                        tvDishResult_2.setVisibility(StringUtil.isEmpty(dishSpecial_2.getResult()) ? View.GONE : View.VISIBLE);
                        calcTotalPrice();
                        startAniForCounter();
                    }
                }
            });
        }
    }

    private void initBottomAD() {
        tvAdHotLine.setText(Constants.APP_NUMBER);
        tvAdAddress.setText(Constants.APP_CONFIG_URL);
    }


    //点击图片显示菜品二维码
    private void showDishQrCode(Dish dish) {

        AlertDialog alertDialog = new AlertDialog.Builder(ShopListActivity.this).create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.qr_dish);
        TextView tvDishName = (TextView) window.findViewById(R.id.tvDishName);
        TextView tvDishMark = (TextView) window.findViewById(R.id.tvDishMark);
        TextView tvDishDanJia = (TextView) window.findViewById(R.id.tvDishDanJia);
        TextView tvDishGuiGe = (TextView) window.findViewById(R.id.tvDishGuiGe);
        TextView tvDishJiaGe = (TextView) window.findViewById(R.id.tvDishJiaGe);
        TextView tvDishJiaGe2 = (TextView) window.findViewById(R.id.tvDishJiaGe2);
        ImageView imgQr = (ImageView) window.findViewById(R.id.imgQr);

        tvDishName.setText(dish.dishName);
        tvDishMark.setText(dish.getReMark());
        tvDishDanJia.setText(dish.getDanJia());
        tvDishGuiGe.setText(dish.getGuiGe());
        tvDishJiaGe.setText(dish.getJiaGe());
        tvDishJiaGe2.setText(dish.getJiaGe2());
        if (!dish.isJiaGeEqualOC()) {
            tvDishJiaGe2.setVisibility(View.VISIBLE);
            tvDishJiaGe.setText(Html.fromHtml(dish.getJiaGeHtmlULine()));
            tvDishJiaGe.setBackgroundResource(R.drawable.bg_text_middle_line);
        } else {
            tvDishJiaGe2.setVisibility(View.GONE);
            tvDishJiaGe.setBackgroundDrawable(null);
            tvDishJiaGe.setBackgroundResource(R.drawable.transpanet);
        }

        Picasso.with(ShopListActivity.this).load(dish.qrCodePath).placeholder(R.drawable.solid_gray).error(R.drawable.solid_gray).into(imgQr);
    }

}
