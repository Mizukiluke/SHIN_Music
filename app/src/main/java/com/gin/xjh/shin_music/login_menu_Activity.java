package com.gin.xjh.shin_music;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.gin.xjh.shin_music.User.User_state;
import com.gin.xjh.shin_music.bean.Follow;
import com.gin.xjh.shin_music.bean.LikeSong;
import com.gin.xjh.shin_music.bean.Song;
import com.gin.xjh.shin_music.bean.User;
import com.gin.xjh.shin_music.util.ListDataSaveUtil;
import com.gin.xjh.shin_music.util.MusicUtil;
import com.gin.xjh.shin_music.util.NetStateUtil;
import com.gin.xjh.shin_music.util.TimesUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class login_menu_Activity extends BaseActivity implements View.OnClickListener {

    private ImageView go_back, User_img, User_Sex;
    private TextView User_Name, User_QQ, User_Sign;
    private LinearLayout edit_user, about, question, updata_password, toConcernList;
    private Button logout;
    private Switch fourg;


    private static int edit_requestCode = 0x110;
    private static int register_requestCode = 0x111;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_menu);
        initView();
        initEvent();
    }

    private void initView() {
        go_back = findViewById(R.id.go_back);
        User_img = findViewById(R.id.User_img);
        User_Name = findViewById(R.id.User_Name);
        User_Sex = findViewById(R.id.User_sex);
        User_QQ = findViewById(R.id.User_QQ);
        User_Sign = findViewById(R.id.User_sign);
        edit_user = findViewById(R.id.edit_user);
        about = findViewById(R.id.about);
        question = findViewById(R.id.question);
        logout = findViewById(R.id.logout);
        updata_password = findViewById(R.id.updata_password);
        fourg = findViewById(R.id.fourg);
        toConcernList = findViewById(R.id.toConcernList);
    }

    private void initEvent() {
        fourg.setChecked(User_state.isUse_4G());

        go_back.setOnClickListener(this);
        User_Name.setOnClickListener(this);
        edit_user.setOnClickListener(this);
        about.setOnClickListener(this);
        question.setOnClickListener(this);
        logout.setOnClickListener(this);
        updata_password.setOnClickListener(this);
        toConcernList.setOnClickListener(this);
        fourg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    List<Song> mSongList = MusicUtil.getSongList();
                    if (mSongList != null) {
                        boolean flag = false;
                        for (Song song : mSongList) {
                            if (song != null && song.isOnline() && NetStateUtil.getNetWorkState(login_menu_Activity.this) == NetStateUtil.DATA_STATE) {
                                Toast.makeText(login_menu_Activity.this, "当前歌单中存在在线歌曲，关闭失败", Toast.LENGTH_SHORT).show();
                                fourg.setChecked(true);
                                flag = true;
                                break;
                            }
                        }
                        if (flag) {
                            return;
                        }
                    }
                }
                User_state.setUse_4G(isChecked);
                SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("use4G", isChecked);
                editor.commit();
                if (isChecked) {
                    Toast.makeText(login_menu_Activity.this, "允许4G播放", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(login_menu_Activity.this, "不允许4G播放", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (User_state.getState()) {
            updataLogin();
        } else {
            updataLogout();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.go_back:
                finish();
                break;
            case R.id.User_Name:
                if (!User_state.getState()) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(login_menu_Activity.this);
                    LayoutInflater inflater1 = LayoutInflater.from(login_menu_Activity.this);
                    View viewDialog1 = inflater1.inflate(R.layout.login_layout, null);
                    final EditText UserId = viewDialog1.findViewById(R.id.UserId);
                    final EditText UserPassword = viewDialog1.findViewById(R.id.User_Password);
                    builder1.setView(viewDialog1);
                    builder1.setPositiveButton("登录", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final String id = UserId.getText().toString();
                            final String password = UserPassword.getText().toString();
                            BmobQuery<User> query = new BmobQuery<>();
                            query.addWhereEqualTo("UserId", id);
                            query.findObjects(new FindListener<User>() {
                                @Override
                                public void done(List<User> list, BmobException e) {
                                    if (e == null) {
                                        if (list.size() > 0) {
                                            for (User user : list) {
                                                String deciphering = "";
                                                int lena = password.length();
                                                int lenb = id.length();
                                                for (int i = 0; i < lena; i++) {
                                                    deciphering += password.charAt(i) % id.charAt(i % lenb);
                                                }
                                                if (deciphering.compareTo(user.getPassWord()) == 0) {
                                                    //修改全局变量，保存对象，并且刷新界面
                                                    User_state.Login(user);
                                                    updataLogin();
                                                } else {
                                                    Toast.makeText(login_menu_Activity.this, "密码错误，请确认后重新输入", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        } else {
                                            Toast.makeText(login_menu_Activity.this, "未找到该用户名，请核对后重新输入", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                        }
                    });
                    builder1.setNegativeButton("注册", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent registerintent = new Intent(login_menu_Activity.this, register_Activity.class);
                            startActivityForResult(registerintent, register_requestCode);
                        }
                    });
                    builder1.create();
                    builder1.show();
                }
                break;
            case R.id.edit_user:
                if (User_state.getState()) {
                    Intent editintent = new Intent(this, updata_Activity.class);
                    startActivityForResult(editintent, edit_requestCode);
                } else {
                    Toast.makeText(this, "请登录后再进行该项操作", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.updata_password:
                if (User_state.getState()) {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(login_menu_Activity.this);
                    LayoutInflater inflater2 = LayoutInflater.from(login_menu_Activity.this);
                    View viewDialog2 = inflater2.inflate(R.layout.password_validate, null);
                    final EditText Password = viewDialog2.findViewById(R.id.UserPassword);
                    builder2.setView(viewDialog2);
                    builder2.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String str = "";
                            final String password = Password.getText().toString();
                            String userid = User_state.getLoginUser().getUserId();
                            int lena = password.length();
                            int lenb = userid.length();
                            for (int i = 0; i < lena; i++) {
                                str += password.charAt(i) % userid.charAt(i % lenb);
                            }
                            if (User_state.getLoginUser().getPassWord().compareTo(str) == 0) {
                                Intent intent = new Intent(login_menu_Activity.this, updata_password_Activity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(login_menu_Activity.this, "密码错误", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder2.create();
                    builder2.show();
                } else {
                    Toast.makeText(this, "请登录后再进行该项操作", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.toConcernList:
                if (User_state.getState()) {
                    Intent intent = new Intent(this, concernList_Activity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "请登录后再进行该项操作", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.about:
                AlertDialog.Builder builder3 = new AlertDialog.Builder(login_menu_Activity.this);
                LayoutInflater inflater3 = LayoutInflater.from(login_menu_Activity.this);
                View viewDialog3 = inflater3.inflate(R.layout.about, null);
                builder3.setView(viewDialog3);
                builder3.create();
                builder3.show();
                break;
            case R.id.question:
                Intent questionintent = new Intent(this, add_question.class);
                startActivity(questionintent);
                break;
            case R.id.logout:
                if (User_state.getState()) {
                    updataLogout();
                    User_state.Logout();
                    Toast.makeText(this, "退出成功", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String str = data.getStringExtra("User");
            if (str.compareTo("yes") == 0) {
                updataLogin();
            }
        }
    }

    private void updataLogin() {
        User user = User_state.getLoginUser();
        User_Name.setText(user.getUserName());
        User_QQ.setText("QQ:" + user.getUserQQ());
        User_Sign.setText("个人简介：" + user.getPersonal_profile());
        User_Sex.setVisibility(View.VISIBLE);
        switch (user.getUserSex()) {
            case 0:
                User_Sex.setImageResource(R.drawable.man);
                break;
            case 1:
                User_Sex.setImageResource(R.drawable.woman);
                break;
            case 2:
                User_Sex.setImageResource(R.drawable.alien);
                break;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_id", user.getUserId());
        editor.putString("user_name", user.getUserName());
        editor.putString("password", user.getPassWord());
        editor.putString("user_qq", user.getUserQQ());
        editor.putInt("user_sex", user.getUserSex());
        editor.putString("personal_profile", user.getPersonal_profile());
        editor.putString("objId", user.getObjectId());
        editor.putLong("time", TimesUtil.dateToLong(new Date(System.currentTimeMillis())));
        editor.putString("likesonglistname", user.getLikeSongListName());
        editor.putBoolean("public_song",user.isPublic_song());
        editor.commit();
        updateBmobLikeEvent();
        updateBmobConcernEvent();

        logout.setVisibility(View.VISIBLE);
    }

    private void updataLogout() {
        User_Name.setText("登录/注册");
        User_QQ.setText("");
        User_Sign.setText("");
        User_Sex.setVisibility(View.GONE);

        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_id", null);
        editor.putString("user_name", null);
        editor.putString("password", null);
        editor.putString("user_qq", null);
        editor.putInt("user_sex", 0);
        editor.putString("personal_profile", null);
        editor.putString("objId", null);
        editor.putLong("time", -1L);
        editor.putString("likesong", null);
        editor.putString("likesonglistname",null);
        editor.putString("concernUser",null);
        editor.putBoolean("public_song",false);
        editor.commit();
        User_state.setLikeSongList(null);
        User_state.setConcernList(null);

        logout.setVisibility(View.GONE);
    }

    private void updateBmobLikeEvent() {
        BmobQuery<LikeSong> query = new BmobQuery<>();
        query.addWhereEqualTo("UserId", User_state.getLoginUser().getUserId());//按当前登录的ID进行查找
        query.findObjects(new FindListener<LikeSong>() {
            @Override
            public void done(List<LikeSong> list, BmobException e) {
                if (list != null && list.size() != 0) {
                    Song song;
                    LikeSong likeSong;
                    List<Song> mSong = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        likeSong = list.get(i);
                        song = likeSong.getSong();
                        song.setObjectId(likeSong.getObjectId());
                        mSong.add(song);
                    }
                    User_state.setLikeSongList(mSong);
                    ListDataSaveUtil.setSongList("likesong", mSong);
                } else {
                    User_state.setLikeSongList(null);
                    ListDataSaveUtil.setSongList("likesong", null);
                }
            }
        });

    }

    private void updateBmobConcernEvent() {
        BmobQuery<Follow> query = new BmobQuery<>();
        query.addWhereEqualTo("UserId", User_state.getLoginUser().getUserId());//按当前登录的ID进行查找
        query.include("FollowUser");
        query.findObjects(new FindListener<Follow>() {
            @Override
            public void done(List<Follow> list, BmobException e) {
                if (list != null && list.size() != 0) {
                    User user;
                    Follow concernUser;
                    List<User> concernList = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        concernUser = list.get(i);
                        user = concernUser.getFollowUser();
                        user.setObjectId(concernUser.getObjectId());
                        concernList.add(user);
                    }
                    User_state.setConcernList(concernList);
                    ListDataSaveUtil.setUserList("concernUser", concernList);
                } else {
                    User_state.setConcernList(null);
                    ListDataSaveUtil.setUserList("concernUser", null);
                }
            }
        });
    }

}
