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
    final int QUIZMAX = 10;                 //クイズの最大値
    final int BOSSHP = 400;                 //竜王のＨＰ
    private int quiz_index = 0;

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


/***************************************************************************************

     サブ画面　処理

***************************************************************************************/

    /***********************************************
        画面表示処理（メイン画面）
    ***********************************************/
    /* メイン画面へ移動 */
    private void setScreenMain(){
        setContentView(R.layout.activity_main);

        /* テキスト（正解率）の表示 */
        TextView title = (TextView) findViewById(R.id.text_title);
        title.setText("クイズ【ドラクエ１】\n〜 勇者 Lv "+db_user_level+" 〜");

        /* テキスト（正解率）の表示 */
        TextView result1 = (TextView) findViewById(R.id.text_result1);
        result1.setText("経験値："+db_quest1_rate+"%");

        TextView result2 = (TextView) findViewById(R.id.text_result2);
        result2.setText("経験値："+db_quest2_rate+"%");

        TextView result3 = (TextView) findViewById(R.id.text_result3);
        result3.setText("経験値："+db_quest3_rate+"%");

        TextView result4 = (TextView) findViewById(R.id.text_result4);
        result4.setText("竜王残りＨＰ："+(BOSSHP-db_random_rate));

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
        prog4.setMax(BOSSHP);
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
        各種ボタン処理（メイン画面）
    ***********************************************/
    //
    // ボタン　------> ドラクエ１のクイズへ
    //
    public void onQuiz1(View view){
        quiz_index = 1;
        setScreenSub();
    }
    //
    // ボタン　------> ドラクエ２のクイズへ
    //
    public void onQuiz2(View view){
        quiz_index = 2;
        setScreenSub();
    }
    //
    // ボタン　------> ドラクエ３のクイズへ
    //
    public void onQuiz3(View view){
        quiz_index = 3;
        setScreenSub();
    }
    //
    // ボタン　------> ドラクエ４のクイズへ
    //
    public void onQuiz4(View view){
        AlertDialog.Builder guide = new AlertDialog.Builder(this);
        TextView vmessage = new TextView(this);

        if (db_user_level < 20) {
            //メッセージ
            vmessage.setText("\n\n レベルが20以上必要です\n 現在 Lv "+db_user_level+"\n\n\n\n");
            vmessage.setBackgroundColor(Color.DKGRAY);
            vmessage.setTextColor(Color.WHITE);
            vmessage.setTextSize(20);
            guide.setTitle("勇者が未熟です");
            guide.setIcon(R.drawable.ng);
            guide.setView(vmessage);
            guide.setPositiveButton("確認", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            guide.create();
            guide.show();
        }
        else{
            quiz_index = 4;
            setScreenSub();
        }
    }
    //
    // ボタン　------> 勇者ステータスのクイズへ
    //
    public void onStatus(View view){
        AlertDialog.Builder guide = new AlertDialog.Builder(this);
        TextView vmessage = new TextView(this);

        if (db_user_level < 5)
            vmessage.setText("\n Lv "+db_user_level+"　称号：ひよっこ\n\n 〜装備〜\n 武器：たけのさお\n 鎧　：かわのふく\n 盾　：なし\n\n\n");
        else if (db_user_level < 10){
            vmessage.setText("\n Lv "+db_user_level+"　称号：かけだし\n\n 〜装備〜\n 武器：どうのつるぎ\n 鎧　：くさりかたびら\n 盾　：なし\n\n\n");
        }
        else if(db_user_level <20){
            vmessage.setText("\n Lv "+db_user_level+"　称号：つわもの\n\n 〜装備〜\n 武器：はがねつるぎ\n 鎧　：てつのよろい\n 盾　：かわのたて\n\n\n");
        }
        else if(db_user_level < 29){
            vmessage.setText("\n Lv "+db_user_level+" 称号：勇者\n\n 〜装備〜\n 武器：ほのおつるぎ\n 鎧　：まほうのよろい\n 盾　：てつのたて\n\n\n");
        }
        else{
            vmessage.setText("\n Lv "+db_user_level+" 称号：伝説の勇者\n\n 〜装備〜\n 武器：ロトのつるぎ\n 鎧　：ロトのよろい\n 盾　：みかがみのたて\n\n\n");
        }

        //メッセージ
        vmessage.setBackgroundColor(Color.DKGRAY);
        vmessage.setTextColor(Color.WHITE);
        vmessage.setTextSize(16);
        guide.setTitle("勇者ステータス");
        guide.setIcon(R.drawable.para);
        guide.setView(vmessage);
        guide.setPositiveButton("確認", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        guide.create();
        guide.show();
    }
    //
    // ボタン　------> はじめからプレイのクイズへ
    //
    public void dataResetDone(){
        AlertDialog.Builder guide = new AlertDialog.Builder(this);
        TextView vmessage = new TextView(this);
        //メッセージ
        vmessage.setText("\n\n 本当に旅の記録を消去しますね？\n\n [消去] 本当に消去します\n [中止] 間違えたそのまま\n\n\n\n");
        vmessage.setBackgroundColor(Color.DKGRAY);
        vmessage.setTextColor(Color.WHITE);
        vmessage.setTextSize(16);
        guide.setTitle("ぼうけんの書");
        guide.setIcon(R.drawable.para);
        guide.setView(vmessage);
        guide.setPositiveButton("消去", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db_user_level = 1;
                db_quest1_rate = 0;
                db_quest2_rate = 0;
                db_quest3_rate = 0;
                db_random_rate = 0;
                AppDBUpdated();
                setScreenMain();
            }
        });
        guide.setNegativeButton("中止", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        guide.create();
        guide.show();
    }


    public void onReset(View view){
        AlertDialog.Builder guide = new AlertDialog.Builder(this);
        TextView vmessage = new TextView(this);
        //メッセージ
        vmessage.setText("\n\n 旅の記録を消去してもいいですか？\n レベル、装備がすべて初期化されます\n\n [消去] 旅の記録を消去します\n [中止] 旅の記録はそのままです\n\n\n\n");
        vmessage.setBackgroundColor(Color.DKGRAY);
        vmessage.setTextColor(Color.WHITE);
        vmessage.setTextSize(16);
        guide.setTitle("ぼうけんの書");
        guide.setIcon(R.drawable.para);
        guide.setView(vmessage);
        guide.setPositiveButton("消去", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dataResetDone();
            }
        });
        guide.setNegativeButton("中止", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        guide.create();
        guide.show();
    }

 /***************************************************************************************

        サブ画面　処理

 ***************************************************************************************/


     /***********************************************
     画面表示処理（サブ画面）
     ***********************************************/
    private void screensubDispleyComplete(){

        int temp_rate;
        int before_level;
        int temp_level;
        int quiz_level;
        int boss_hp_bf = BOSSHP;
        int boss_hp_af = BOSSHP;
        AlertDialog.Builder guide = new AlertDialog.Builder(this);
        TextView vmessage = new TextView(this);

        //クイズを途中でスキップした場合は成績の更新はしない
        if (quizCount >= QUIZMAX) {
            temp_rate = (OkCount * 100 / QUIZMAX);

            //成績の更新
            quiz_level = Integer.parseInt(quizSearch.QuizNowListData().QuizLevel);
            switch (quiz_level) {
                case 1:
                    if (db_quest1_rate <= temp_rate)    db_quest1_rate = temp_rate;
                    break;
                case 2:
                    if (db_quest2_rate <= temp_rate)    db_quest2_rate = temp_rate;
                    break;
                case 3:
                    if (db_quest3_rate <= temp_rate)    db_quest3_rate = temp_rate;
                    break;
                // ラスボスだけは別処理
                case 4:
                    boss_hp_bf -= db_random_rate;
                    if (db_random_rate <= temp_rate)    db_random_rate = temp_rate;
                    boss_hp_af -= db_random_rate;
                    break;
            }

            //通常問題の処理
            //プレイヤーレベル更新処理
            if (quiz_level <= 3) {
                before_level = db_user_level;
                temp_level = (db_quest1_rate + db_quest2_rate + db_quest3_rate) / 10;
                if (temp_level <= 0) {
                    temp_level = 1;
                }
                if (db_user_level <= temp_level) {
                    db_user_level = temp_level;

                    //ダイアログ
                    //メッセージ
                    vmessage.setText("\n\n 勇者はレベルアップしました\n  Lv " + before_level + " → " + db_user_level + "\n\n Lv20以上で竜王挑戦\n\n\n\n");
                    vmessage.setBackgroundColor(Color.DKGRAY);
                    vmessage.setTextColor(Color.WHITE);
                    vmessage.setTextSize(20);
                    guide.setTitle("Level UP");
                    guide.setIcon(R.drawable.lv);
                    guide.setView(vmessage);
                    guide.setPositiveButton("確認", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    guide.create();
                    guide.show();
                }
            }
            // ラスボスの処理
            else{
                if (boss_hp_af >= boss_hp_bf){
                    vmessage.setText("\n\n 勇者の一撃は竜王に回避された!!\n\n  竜王　残ＨＰ" + boss_hp_bf + " → " + boss_hp_af + "\n\n\n　(残ＨＰゼロ＝GAMEクリア）\n\n\n\n");
                }
                else {
                    vmessage.setText("\n\n 勇者の一撃が竜王に直撃した!!\n\n  竜王　残ＨＰ" + boss_hp_bf + " → " + boss_hp_af + "\n\n\n　(残ＨＰゼロ＝GAMEクリア)\n\n\n\n");
                }
                vmessage.setBackgroundColor(Color.DKGRAY);
                vmessage.setTextColor(Color.WHITE);
                vmessage.setTextSize(16);
                guide.setTitle("死闘結果");
                guide.setIcon(R.drawable.lv);
                guide.setView(vmessage);
                guide.setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                guide.create();
                guide.show();
            }
        }

        //とりあえずメイン画面へ遷移
        quizSearch.QuizTableSearchReset();  //出題状態をリセット
        quizCount = 0;
        OkCount = 0;
        NgCount = 0;
    }

    private void screenSubDisplay(){

        /* 問題のカウントアップ */
        quizCount++;

        /* ランダムな取得処理 */
        switch (quiz_index){
            case 1: dispmsg = quizSearch.QuizTableSearch_1();   break;
            case 2: dispmsg = quizSearch.QuizTableSearch_2();   break;
            case 3: dispmsg = quizSearch.QuizTableSearch_3();   break;
            case 4: dispmsg = quizSearch.QuizTableSearch_4();   break;
        }

        /*　全問終了　*/
        if (dispmsg == null){
            screensubDispleyComplete();
            setScreenMain();
            return;
        }

        //dispmsg = csvreader.objects.get(1);

        //ステータス
        TextView title = (TextView) findViewById(R.id.text_title);
        title.setText("Lv "+db_user_level+"　　ドラクエ："+dispmsg.getSeries()+"　　クイズ："+quizCount+"/"+QUIZMAX);
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

        switch (result){
            case 1:     vmessage.setText("\n 答え：\n\n  " + dispmsg.getAnswer1()+"\n\n\n");  break;
            case 2:     vmessage.setText("\n 答え：\n\n  " + dispmsg.getAnswer2()+"\n\n\n");  break;
            case 3:     vmessage.setText("\n 答え：\n\n  " + dispmsg.getAnswer3()+"\n\n\n");  break;
            case 4:     vmessage.setText("\n 答え：\n\n  " + dispmsg.getAnswer4()+"\n\n\n");  break;
            default:    vmessage.setText("\n 答え：\n\n  " + "");                             break;
        }

        //メッセージ
        vmessage.setBackgroundColor(Color.DKGRAY);
        vmessage.setTextColor(Color.WHITE);
        vmessage.setTextSize(20);

        //タイトル
        /* 正解の場合 */
        if ( result == select_answer){
            OkCount++;
            alert.setTitle("正解");
            alert.setIcon(R.drawable.ok);

        }
        /* 間違いの場合 */
        else{
            NgCount++;
            alert.setTitle("間違い");
            alert.setIcon(R.drawable.ng);
        }
        alert.setView(vmessage);
        alert.setPositiveButton("次の問題へ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 次の問題へ
                setScreenSub();
            }
        });
        alert.create();
        alert.show();
    }


    /* サブ画面へ移動 */
    private void setScreenSub(){
        setContentView(R.layout.activity_sub);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        screenSubDisplay();
    }

    /***********************************************
        各種ボタン処理（サブ画面）
    ***********************************************/
    // 答１の解答
    public void onAnswer1(View view){
        screenSubAnswer(1);
    }
    // 答２の解答
    public void onAnswer2(View view){
        screenSubAnswer(2);
    }
    // 答３の解答
    public void onAnswer3(View view){
        screenSubAnswer(3);
    }
    // 答４の解答
    public void onAnswer4(View view){
        screenSubAnswer(4);
    }
    // メイン画面へ
    public void onMenu(View view){
        screensubDispleyComplete();
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

        //音声初期化
        /*
        if (am == null) {
            am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            init_volume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
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