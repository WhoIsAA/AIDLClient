package com.aa.aidlclient;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.aa.aidlserver.Book;
import com.aa.aidlserver.IBookManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView mListView;
    private ArrayAdapter<String> mAdapter;
    private List<String> mBooks = new ArrayList<>();

    private Button btnLoadmore, btnAdd, btnDetele;

    private IBookManager mIBookManager;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //Service已连接
            mIBookManager = IBookManager.Stub.asInterface(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            //Service已断开
            mIBookManager = null;
        }
    };

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Log.e("AA", "收到Log：" + msg);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.lv_main);
        btnLoadmore = (Button) findViewById(R.id.btn_main_loadmore);
        btnLoadmore.setOnClickListener(this);
        btnAdd = (Button) findViewById(R.id.btn_main_add);
        btnAdd.setOnClickListener(this);
        btnDetele = (Button) findViewById(R.id.btn_main_delete);
        btnDetele.setOnClickListener(this);

        bindAIDLService();
    }

    @Override
    protected void onDestroy() {
        unbindAIDLService();
        super.onDestroy();
    }

    /**
     * 绑定AIDL服务
     */
    private void bindAIDLService() {
        Intent intent = new Intent("com.aa.aidlserver.AIDLService");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Android 5.0以上需要设置包名
        intent.setPackage("com.aa.aidlserver");
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
    }

    /**
     * 解绑AIDL服务
     */
    private void unbindAIDLService() {
        if(mIBookManager != null) {
            unbindService(mServiceConnection);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_main_loadmore:
                //加载更多
                try {
                    List<Book> books = mIBookManager.getBookList();
                    if(books != null && books.size() > 0) {
                        if(mBooks.size() > 0) {
                            mBooks.clear();
                        }

                        //解析数据
                        for(Book book : books) {
                            mBooks.add("Name：" + book.name + " --- Describe：" + book.describe);
                        }
                        //填充数据
                        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, mBooks);
                        mListView.setAdapter(mAdapter);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.btn_main_add:
                //新增图书
                int len = mBooks.size() + 1;
                String name = "图书" + len;
                String describe = "这是第" + len + "本书";
                Book book = new Book(name, describe);
                try {
                    mIBookManager.addBook(book);
                    mBooks.add("Name：" + book.name + " --- Describe：" + book.describe);
                    mAdapter.notifyDataSetChanged();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.btn_main_delete:
                //删除图书
                if(mBooks.size() > 0) {
                    try {
                        Book book1 = mIBookManager.getBookList().get(0);
                        mIBookManager.deleteBook(book1);
                        mBooks.remove(0);
                        mAdapter.notifyDataSetChanged();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    toast("已经没有书可以删除了");
                }
                break;
        }
    }
}
