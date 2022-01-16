package tfsapps.dragonquestquiz;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

//DB関連
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //  DB関連
    private MyOpenHelper helper;            //DBアクセス
    private int db_isopen = 0;              //DB使用したか
    private int db_user_level = 0;          //DB
    private int db_option_level = 0;        //DB
    private int db_quest1_rate = 0;         //DB
    private int db_quest2_rate = 0;         //DB
    private int db_quest3_rate = 0;         //DB
    private int db_random_rate = 0;         //DB

    //  CSVファイル関連
    private CsvReader csvreader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        setScreenMain();
    }

    /* メイン画面へ移動 */
    private void setScreenMain(){
        setContentView(R.layout.activity_main);
/*
        Button sendButton = findViewById(R.id.send_button);
//        sendButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setScreenSub();
//            }
//        });
        // lambda式
        sendButton.setOnClickListener(v -> setScreenSub());
*/
    }


    /* サブ画面へ移動 */
    private void setScreenSub(){
        setContentView(R.layout.activity_sub);
/*
        Button returnButton = findViewById(R.id.return_button);
//        returnButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setScreenMain();
//            }
//        });
        // lambda式
        returnButton.setOnClickListener(v -> setScreenMain());

 */
    }

    /***********************************************
     各種ボタン処理（メイン画面）
     ********************************************* */
    // ドラクエ１のクイズへ
    public void onQuiz1(View view){
        setScreenSub();
    }
    // ドラクエ２のクイズへ
    public void onQuiz2(View view){
        setScreenSub();
    }
    // ドラクエ３のクイズへ
    public void onQuiz3(View view){
        setScreenSub();
    }

    /***********************************************
     各種ボタン処理（サブ画面）
     ********************************************* */
    // 答１の解答
    public void onAnswer1(View view){
        setScreenMain();
    }
    // 答２の解答
    public void onAnswer2(View view){
        setScreenMain();
    }
    // 答３の解答
    public void onAnswer3(View view){
        setScreenMain();
    }
    // 答４の解答
    public void onAnswer4(View view){
        setScreenMain();
    }
    // メイン画面へ
    public void onMenu(View view){
        setScreenMain();
    }


    /* **************************************************
        各種OS上の動作定義
    ****************************************************/
    @Override
    public void onStart() {
        super.onStart();

        //DBのロード
        /* データベース */
        helper = new MyOpenHelper(this);
        AppDBInitRoad();

        //CSVファイルの読込
        ListData list;
        csvreader = new CsvReader();
        csvreader.reader(getApplicationContext());

        //読込サンプル
        ListData sample;
        sample = csvreader.objects.get(1);
        Toast.makeText(this, ""+sample.getQuestion(), Toast.LENGTH_SHORT).show();

        //音声初期化
        /*
        if (am == null) {
            am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            init_volume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        }
         */
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
        //  DB更新
        AppDBUpdated();
    }
    @Override
    public void onStop(){
        super.onStop();
        //  DB更新
        AppDBUpdated();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        //  DB更新
        AppDBUpdated();

        /* 音量の戻しの処理 */
        /*
        if (am != null){
            am.setStreamVolume(AudioManager.STREAM_MUSIC, init_volume, 0);
            am = null;
        }
         */
    }

    /* **************************************************
        DB初期ロードおよび設定
    ****************************************************/
    public void AppDBInitRoad() {
        SQLiteDatabase db = helper.getReadableDatabase();
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT");
        sql.append(" isopen");
        sql.append(" ,user_level");
        sql.append(" ,option_level");
        sql.append(" ,quest1_rate");
        sql.append(" ,quest2_rate");
        sql.append(" ,quest3_rate");
        sql.append(" ,random_rate");
        sql.append(" FROM appinfo;");
        try {
            Cursor cursor = db.rawQuery(sql.toString(), null);
            //TextViewに表示
            StringBuilder text = new StringBuilder();
            if (cursor.moveToNext()) {
                db_isopen = cursor.getInt(0);
                db_user_level = cursor.getInt(1);
                db_option_level = cursor.getInt(2);
                db_quest1_rate = cursor.getInt(3);
                db_quest2_rate = cursor.getInt(4);
                db_quest3_rate = cursor.getInt(5);
                db_random_rate = cursor.getInt(6);
            }
        } finally {
            db.close();
        }

        db = helper.getWritableDatabase();
        if (db_isopen == 0) {
            long ret;
            /* 新規レコード追加 */
            ContentValues insertValues = new ContentValues();
            insertValues.put("isopen", 1);
            insertValues.put("user_level", 1);
            insertValues.put("option_level", 1);
            insertValues.put("quest1_rate", 0);
            insertValues.put("quest2_rate", 0);
            insertValues.put("quest3_rate", 0);
            insertValues.put("random_rate", 0);
            insertValues.put("data1", 0);
            insertValues.put("data2", 0);
            insertValues.put("data3", 0);
            insertValues.put("data4", 0);
            insertValues.put("data5", 0);
            insertValues.put("data6", 0);
            insertValues.put("data7", 0);
            insertValues.put("data8", 0);
            insertValues.put("data9", 0);
            insertValues.put("data10", 0);
            try {
                ret = db.insert("appinfo", null, insertValues);
            } finally {
                db.close();
            }
            db_isopen = 1;
            db_user_level = 1;
            db_option_level = 1;
            /*
            if (ret == -1) {
                Toast.makeText(this, "DataBase Create.... ERROR", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "DataBase Create.... OK", Toast.LENGTH_SHORT).show();
            }
             */
        } else {
            /*
            Toast.makeText(this, "Data Loading...  interval:" + db_interval, Toast.LENGTH_SHORT).show();
             */
        }
    }

    /* **************************************************
        DB更新
    ****************************************************/
    public void AppDBUpdated() {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues insertValues = new ContentValues();
        insertValues.put("isopen", db_isopen);
        insertValues.put("user_level", db_user_level);
        insertValues.put("option_level", db_option_level);
        insertValues.put("quest1_rate", db_quest1_rate);
        insertValues.put("quest2_rate", db_quest2_rate);
        insertValues.put("quest3_rate", db_quest3_rate);
        insertValues.put("random_rate", db_random_rate);
        int ret;
        try {
            ret = db.update("appinfo", insertValues, null, null);
        } finally {
            db.close();
        }
        if (ret == -1) {
            Toast.makeText(this, "Saving.... ERROR ", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Saving.... OK "+ "isopen= "+db_isopen, Toast.LENGTH_SHORT).show();
        }
    }

}