package com.example.qwexo.menubasket;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qwexo.menubasket.databinding.ActivityBasketBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BasketActivity extends AppCompatActivity {
    public static final int BASKET_MAX_LENGTH = 30;
    public static final int OPTION_MAX_LENGHT = 5;

    ActivityBasketBinding basketBinding;
    SharedPreferences shared;
    SharedPreferences.Editor editor;
    DatabaseReference fireDB;

    ViewGroup[] view = new ViewGroup[BASKET_MAX_LENGTH];
    TextView[] tagText = new TextView[BASKET_MAX_LENGTH];
    ImageButton[] closeButton = new ImageButton[BASKET_MAX_LENGTH];
    TextView[] menuName = new TextView[BASKET_MAX_LENGTH];
    TextView[] menuPriceText = new TextView[BASKET_MAX_LENGTH];
    ImageView[] imageView = new ImageView[BASKET_MAX_LENGTH];
    TextView[] minHapPriceText = new TextView[BASKET_MAX_LENGTH];
    EditText[] countEdit = new EditText[BASKET_MAX_LENGTH];
    TextView[] optionText = new TextView[BASKET_MAX_LENGTH];
    LinearLayout[] optionLayout = new LinearLayout[BASKET_MAX_LENGTH];

    MenuList[] menuList = new MenuList[BASKET_MAX_LENGTH];
    String[] menu = new String[BASKET_MAX_LENGTH];
    String userID;
    int tag = 0;
    int basketCount = 0;
    int hapPrice;
    LayoutInflater inflater;
    LinearLayout ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        basketBinding = DataBindingUtil.setContentView(this, R.layout.activity_basket);

        basketBinding.toolbar.setTitle("장바구니");
        basketBinding.toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(basketBinding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        shared = getSharedPreferences("basket", MODE_PRIVATE);
        editor = shared.edit();
        editor.putString("marketId", "slwVsecqtTO3RDjzPxBWrFekbEd2");
        editor.putString("menu0", "-KpEcsuBIg-8VmoqGl1f");
        editor.putString("menu1", "-KpEcsuBIg-8VmoqGl1f");
        editor.commit();

        userID = shared.getString("marketId", "");
        for (int i = 0; i < BASKET_MAX_LENGTH; i++) {
            menu[i] = shared.getString("menu" + i, null);
        }

        fireDB = FirebaseDatabase.getInstance().getReference().child("market").child(userID).child("menu");
        fireDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    menuList[count] = data.getValue(MenuList.class);
                    if (menuList[count].menuName != null) {
                        basketCreate(menuList[count]);
                        count++;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


//        fireDB = FirebaseDatabase.getInstance().getReference().child("market").child(userID).child("menu");
//        fireDB.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                int count = 0;
//                for (DataSnapshot data : dataSnapshot.getChildren()) {
//                    menuList[count] = data.getValue(MenuList.class);
//                    menuList[count].menuKey = data.getKey();
//                    count++;
//                }
//
//                for(int i=0; menu[i]!=null; i++){
//                    for(int j=0; j<count; j++){
//                        if(menu[i]==menuList[j].menuKey){
//                            basketCreate(menuList[j]);
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    View.OnClickListener closeListener = new View.OnClickListener() {   //닫기버튼
        @Override
        public void onClick(View v) {
            Object object = v.getTag();
            for (int i = 0; i < basketCount; i++) {
                if (object.equals(tagText[i].getTag())) {
                    alertDialog(i);
                    break;
                }
            }
        }
    };
    View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {   //edit 포커스 잡힐 시
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            Object object = v.getTag();
            for (int i = 0; i < basketCount; i++) {
                if (object.equals(tagText[i].getTag())) {
                    tag = i;
                    break;
                }
            }
        }
    };

    public void OnClicked(View v) {
        switch (v.getId()) {
            case R.id.orderButton:  //주문하기버튼
//                for (int i = 0; i < BASKET_MAX_LENGTH; i++) {
//                    Log.d("menu" + i, menu[i]);
//                }
                Log.d("asdasd", userID);
                Log.d("asdawdas", menu[1]);
                break;
        }
    }

    public void alertDialog(final int num) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("메뉴를 삭제하시겠습니까?")
                .setCancelable(false)
                .setNegativeButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        basketBinding.basketLayout.removeView(view[num]);
                        hapPrice -= Integer.parseInt(minHapPriceText[num].getText().toString());
                        basketBinding.hapPriceText.setText(String.valueOf(hapPrice));
                        Toast.makeText(BasketActivity.this, "메뉴를 삭제하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void basketCreate(MenuList menuList) {   //메뉴 불러오기
        ll = new LinearLayout(this);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view[basketCount] = (ViewGroup) inflater.inflate(R.layout.basket_fragment, ll, false);
        tagText[basketCount] = (TextView) view[basketCount].findViewById(R.id.tagText); //번지수 기억 텍스트
        tagText[basketCount].setTag(basketCount);
        closeButton[basketCount] = (ImageButton) view[basketCount].findViewById(R.id.closeButton);  //닫기버튼
        closeButton[basketCount].setOnClickListener(closeListener);
        closeButton[basketCount].setTag(basketCount);
        menuPriceText[basketCount] = (TextView) view[basketCount].findViewById(R.id.menuPriceText); //메뉴가격
        menuPriceText[basketCount].setText(menuList.menuPrice);
        menuName[basketCount] = (TextView) view[basketCount].findViewById(R.id.menuName);   //메뉴이름
        menuName[basketCount].setText(menuList.menuName);
        imageView[basketCount] = (ImageView) view[basketCount].findViewById(R.id.imageView);    //메뉴이미지
        new ImageGlide().getImage(getApplicationContext(), userID, menu[basketCount], imageView[basketCount]);
        countEdit[basketCount] = (EditText) view[basketCount].findViewById(R.id.countEdit);     //메뉴당 주문 갯수
        countEdit[basketCount].addTextChangedListener(textWatcher);
        countEdit[basketCount].setTag(basketCount);
        countEdit[basketCount].setOnFocusChangeListener(focusListener);
        minHapPriceText[basketCount] = (TextView) view[basketCount].findViewById(R.id.minHapPriceText);  //메뉴당 가격 소계
        //(메뉴가격 + 옵션1 + 옵션2 + 옵션3 + 옵션4 + 옵션5) * 수량
        minHapPriceText[basketCount].setText(String.valueOf((Integer.parseInt(menuList.menuPrice) + Integer.parseInt(menuList.option1Price)
                + Integer.parseInt(menuList.option2Price) + Integer.parseInt(menuList.option3Price) + Integer.parseInt(menuList.option4Price)
                + Integer.parseInt(menuList.option5Price)) * Integer.parseInt(countEdit[basketCount].getText().toString())));

        int price = Integer.parseInt(minHapPriceText[basketCount].getText().toString());    //소계
        hapPrice = Integer.parseInt(basketBinding.hapPriceText.getText().toString()) + price;   //합계금액
        basketBinding.hapPriceText.setText(String.valueOf(hapPrice));
        optionText[basketCount] = (TextView) view[basketCount].findViewById(R.id.optionText);   //옵션텍스트
        optionLayout[basketCount] = (LinearLayout) view[basketCount].findViewById(R.id.optionLayout);   //옵션레이아웃
        if (menuList.option5Name != null) {
            optionLayout[basketCount].setVisibility(View.VISIBLE);
            optionText[basketCount].setText(menuList.option1Name + "(+" + menuList.option1Price + "원)\n"
                    + menuList.option2Name + "(+" + menuList.option2Price + "원)\n"
                    + menuList.option3Name + "(+" + menuList.option3Price + "원)\n"
                    + menuList.option4Name + "(+" + menuList.option4Price + "원)\n"
                    + menuList.option5Name + "(+" + menuList.option5Price + "원)");
        } else if (menuList.option4Name != null) {
            optionLayout[basketCount].setVisibility(View.VISIBLE);
            optionText[basketCount].setText(menuList.option1Name + "(+" + menuList.option1Price + "원)\n"
                    + menuList.option2Name + "(+" + menuList.option2Price + "원)\n"
                    + menuList.option3Name + "(+" + menuList.option3Price + "원)\n"
                    + menuList.option4Name + "(+" + menuList.option4Price + "원)");
        } else if (menuList.option3Name != null) {
            optionLayout[basketCount].setVisibility(View.VISIBLE);
            optionText[basketCount].setText(menuList.option1Name + "(+" + menuList.option1Price + "원)\n"
                    + menuList.option2Name + "(+" + menuList.option2Price + "원)\n"
                    + menuList.option3Name + "(+" + menuList.option3Price + "원)");
        } else if (menuList.option2Name != null) {
            optionLayout[basketCount].setVisibility(View.VISIBLE);
            optionText[basketCount].setText(menuList.option1Name + "(+" + menuList.option1Price + "원)\n"
                    + menuList.option2Name + "(+" + menuList.option2Price + "원)");
        } else if (menuList.option1Name != null) {
            optionLayout[basketCount].setVisibility(View.VISIBLE);
            optionText[basketCount].setText(menuList.option1Name + "(+" + menuList.option1Price + "원)");
        }

        basketBinding.basketLayout.addView(view[basketCount]);

        basketCount++;
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int num;
            if (countEdit[tag].getText().toString().length() <= 0) {
                num = 0;
            } else {
                num = Integer.parseInt(countEdit[tag].getText().toString());
            }
            hapPrice -= Integer.parseInt(minHapPriceText[tag].getText().toString());
            //(메뉴가격 + 옵션1 + 옵션2 + 옵션3 + 옵션4 + 옵션5) * 수량
            minHapPriceText[tag].setText(String.valueOf((Integer.parseInt(menuList[tag].menuPrice) + Integer.parseInt(menuList[tag].option1Price)
                    + Integer.parseInt(menuList[tag].option2Price) + Integer.parseInt(menuList[tag].option3Price) + Integer.parseInt(menuList[tag].option4Price)
                    + Integer.parseInt(menuList[tag].option5Price)) * num));
            hapPrice += Integer.parseInt(minHapPriceText[tag].getText().toString());    //합계금액
            basketBinding.hapPriceText.setText(String.valueOf(hapPrice));

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

}
