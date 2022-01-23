package tfsapps.dragonquestquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

//DB関連
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

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

    //  現在表示中のクイズ情報
    private ListData dispmsg;
    private QuizSearch quizSearch;
    private int quizCount = 0;              //クイズの回数（何問目か？）
    private int OkCount = 0;                //正解の回数
    private int NgCount = 0;                //間違いの回数
    final int QUIZMAX = 20;                 //クイズの最大値

    //  画面パーツ
    private ProgressBar prog1;              //
    private ProgressBar prog2;              //
    private ProgressBar prog3;              //
    private ProgressBar prog4;              //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        setScreenMain();
    }
    /***********************************************
        画面表示処理（メイン画面）
    ***********************************************/
    /* メイン画面へ移動 */
    private void setScreenMain(){
        setContentView(R.layout.activity_main);

        /* テキスト（正解率）の表示 */
        TextView result1 = (TextView) findViewById(R.id.text_result1);
        result1.setText("正解率："+db_quest1_rate+"%");

        TextView result2 = (TextView) findViewById(R.id.text_result2);
        result2.setText("正解率："+db_quest2_rate+"%");

        TextView result3 = (TextView) findViewById(R.id.text_result3);
        result3.setText("正解率："+db_quest3_rate+"%");

        TextView result4 = (TextView) findViewById(R.id.text_result4);
        result4.setText("正解率："+db_random_rate+"%");

        /* プログレスバーの表示 */
        prog1 = (ProgressBar) findViewById(R.id.progress1);
        prog1.setMin(0);
        prog1.setMax(100);
        prog1.setProgress(db_quest1_rate);

        prog2 = (ProgressBar) findViewById(R.id.progress2);
        prog2.setMin(0);
        prog2.setMax(100);
        prog2.setProgress(db_quest2_rate);

        prog3 = (ProgressBar) findViewById(R.id.progress3);
        prog3.setMin(0);
        prog3.setMax(100);
        prog3.setProgress(db_quest3_rate);

        prog4 = (ProgressBar) findViewById(R.id.progress4);
        prog4.setMin(0);
        prog4.setMax(100);
        prog4.setProgress(db_random_rate);


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

    /***********************************************
        画面表示処理（サブ画面）
    ***********************************************/
    private void screenSubDisplay(){

        /* 問題のカウントアップ */
        quizCount++;

        /* ランダムな取得処理 */
        dispmsg = quizSearch.QuizTableSearch();

        /*　全問終了　*/
        if (dispmsg == null){

            //成績の更新
            switch (Integer.parseInt(quizSearch.QuizNowListData().Series)){
                case 1: db_quest1_rate = (OkCount*100/QUIZMAX); break;
                case 2: db_quest2_rate = (OkCount*100/QUIZMAX); break;
                case 3: db_quest3_rate = (OkCount*100/QUIZMAX); break;
                case 4: db_random_rate = (OkCount+100/QUIZMAX); break;
            }

            //とりあえずメイン画面へ遷移
            quizSearch.QuizTableSearchReset();  //出題状態をリセット
            setScreenMain();
            return;
        }

        //dispmsg = csvreader.objects.get(1);

        //ステータス
        TextView title = (TextView) findViewById(R.id.text_title);
        title.setText("Lv "+99+"　　ドラクエ："+dispmsg.getSeries()+"　　クイズ："+quizCount+"/"+QUIZMAX);
        //ステータス
        TextView status = (TextView) findViewById(R.id.text_status);
        status.setText("正解:"+OkCount+"個　　間違い:"+NgCount+"個");
        //設問
        TextView question = (TextView) findViewById(R.id.text_question);
        question.setText(dispmsg.getQuestion());
        //解答１
        Button answer1 = (Button) findViewById(R.id.btn_answer1);
        answer1.setText(dispmsg.getAnswer1());
        //解答２
        Button answer2 = (Button) findViewById(R.id.btn_answer2);
        answer2.setText(dispmsg.getAnswer2());
        //解答３
        Button answer3 = (Button) findViewById(R.id.btn_answer3);
        answer3.setText(dispmsg.getAnswer3());
        //解答４
        Button answer4 = (Button) findViewById(R.id.btn_answer4);
        answer4.setText(dispmsg.getAnswer4());

    }

    /***********************************************
        解答画面（サブ画面）
     ***********************************************/
    private void screenSubAnswer(int select_answer){

        TextView vtitle = new TextView(this);
        TextView vmessage = new TextView(this);

        int result = Integer.parseInt(dispmsg.getResult());

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        /*
        switch (result){
            case 1:     alert.setMessage("\n\n\n正解：" + dispmsg.getAnswer1()+"\n\n\n");  break;
            case 2:     alert.setMessage("\n\n\n正解：" + dispmsg.getAnswer2()+"\n\n\n");  break;
            case 3:     alert.setMessage("\n\n\n正解：" + dispmsg.getAnswer3()+"\n\n\n");  break;
            case 4:     alert.setMessage("\n\n\n正解：" + dispmsg.getAnswer4()+"\n\n\n");  break;
            default:    alert.setMessage("\n\n\n正解：" + "");                             break;
        }
        */
        switch (result){
            case 1:     vmessage.setText("\n 答え：\n\n  " + dispmsg.getAnswer1()+"\n\n\n");  break;
            case 2:     vmessage.setText("\n 答え：\n\n  " + dispmsg.getAnswer2()+"\n\n\n");  break;
            case 3:     vmessage.setText("\n 答え：\n\n  " + dispmsg.getAnswer3()+"\n\n\n");  break;
            case 4:     vmessage.setText("\n 答え：\n\n  " + dispmsg.getAnswer4()+"\n\n\n");  break;
            default:    vmessage.setText("\n 答え：\n\n  " + "");                             break;
        }

        //メッセージ
        //vmessage.setTextColor(Color.WHITE);
        //vmessage.setBackgroundColor(Color.BLACK);
        vmessage.setTextSize(20);

        //タイトル
        vtitle.setBackgroundColor(Color.DKGRAY);
        vtitle.setTextColor(Color.WHITE);
        vtitle.setTextSize(48);
        /* 正解の場合 */
        if ( result == select_answer){
            OkCount++;
            vtitle.setText("　正解");
            //alert.setTitle("正解");
            //alert.setIcon(R.id.ok);

        }
        /* 間違いの場合 */
        else{
            NgCount++;
            vtitle.setText("　間違い");
            //alert.setTitle("間違い");
            //alert.setIcon(R.id.ok);
        }

        alert.setCustomTitle(vtitle);
        alert.setView(vmessage);
//        alert.setPositiveButton("次の問題へ", null );
        alert.setPositiveButton("次の問題へ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 次の問題へ
                setScreenSub();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();

        //  複数選択のダイアログ  リスト表示の処理
        /*
        AlertDialog.Builder alert05 = new AlertDialog.Builder(this);
        final CharSequence[] Items = { "00001", "00002", "00003"};
        //ダイアログタイトルをセット
        alert05.setTitle("ここにタイトルを設定");
        // 表示項目とリスナの設定
        alert05.setItems(Items, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {
                //リストを選択した時のみ
                Toast.makeText(MainActivity.this, String.format("%s Selected", Items[which]), Toast.LENGTH_LONG).show();
            }});
        // back keyを使用不可に設定
        alert05.setCancelable(false);
        AlertDialog dialog = alert05.create();
        dialog.show();
        */
    }


    /* サブ画面へ移動 */
    private void setScreenSub(){
        setContentView(R.layout.activity_sub);
        screenSubDisplay();
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
    ***********************************************/
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
    ***********************************************/
    // 答１の解答
    public void onAnswer1(View view){
        screenSubAnswer(1);
        //setScreenMain();
    }
    // 答２の解答
    public void onAnswer2(View view){
        screenSubAnswer(2);
        //setScreenMain();
    }
    // 答３の解答
    public void onAnswer3(View view){
        screenSubAnswer(3);
        //setScreenMain();
    }
    // 答４の解答
    public void onAnswer4(View view){
        screenSubAnswer(4);
        //setScreenMain();
    }
    // メイン画面へ
    public void onMenu(View view){
        setScreenMain();
    }


    /***************************************************
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

        quizSearch = new QuizSearch(csvreader);

        setScreenMain();

        //読込サンプル
        /*  ランダムクラスでロードしてデータ抽出を行う
        ListData sample;
        sample = csvreader.objects.get(1);
        Toast.makeText(this, ""+sample.getQuestion(), Toast.LENGTH_SHORT).show();
        */

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

    /***************************************************
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

    /***************************************************
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