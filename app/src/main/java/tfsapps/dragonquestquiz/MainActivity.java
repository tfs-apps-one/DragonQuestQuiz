package tfsapps.dragonquestquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaParser;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

//DB関連
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

//広告
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;


public class MainActivity extends AppCompatActivity implements RewardedVideoAdListener {
//public class MainActivity extends AppCompatActivity {

    //  DB関連
    private MyOpenHelper helper;            //DBアクセス
    private int db_isopen = 0;              //DB使用したか
    private int db_user_level = 0;          //DB勇者のレベル
    private int db_option_level = 0;        //DBオプションレベル
    private int db_quest1_rate = 0;         //DBクイズ１の正解率
    private int db_quest2_rate = 0;         //DBクイズ２の正解率
    private int db_quest3_rate = 0;         //DBクイズ３の正解率
    private int db_random_rate = 0;         //DBボスのダメージ
    private int db_fame = 0;                //DB名声
    private int db_boss1 = 0;               //DBボス１を倒したか？
    private int db_boss2 = 0;               //DBボス２を倒したか？
    private int db_boss3 = 0;               //DBボス３を倒したか？
    private int db_boss4 = 0;               //DBボス４を倒したか？

    //  CSVファイル関連
    private CsvReader csvreader;

    //  現在表示中のクイズ情報
    private ListData dispmsg;
    private QuizSearch quizSearch;
    private int quizCount = 0;              //クイズの回数（何問目か？）
    private int OkCount = 0;                //正解の回数
    private int NgCount = 0;                //間違いの回数
    final int QUIZMAX = 10;                 //クイズの最大値
    private int BOSSHP = 400;               //ボスのＨＰ
    private int fame;                       //名声
    final int BOSS_1_HP = 400;              //竜王のＨＰ
    final int BOSS_2_HP = 990;              //シドーのＨＰ
    final int BOSS_3_HP = 400;              //ゾーマのＨＰ
    final int BOSS_4_HP = 400;              //デスピサロのＨＰ
    private int quiz_index = 0;
    //  ゲームモード
    final int MODE_BOSS_1 = 0;              //竜王編
    final int MODE_BOSS_2 = 1;              //シドー編
    final int MODE_BOSS_3 = 2;              //ゾーマ編
    final int MODE_BOSS_4 = 3;              //デスピサロ編


    //  画面パーツ
    private ProgressBar prog1;              //
    private ProgressBar prog2;              //
    private ProgressBar prog3;              //
    private ProgressBar prog4;              //

    //  音源
    private AudioManager am;
    private int start_volume;
    private MediaPlayer bgm;
    private int bgm_index;
    // 広告
    private AdView mAdview;
    private boolean isAdLoad = false;
    private RewardedVideoAd mRewardedVideoAd;

    // テストID
    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917";
    // 本物ID
//    private static final String AD_UNIT_ID = "ca-app-pub-4924620089567925/3397937263";

    // テストID(APPは本物でOK)
    private static final String APP_ID = "ca-app-pub-4924620089567925~2765746055";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        //音声初期化
        if (am == null) {
            am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            start_volume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        }
        if (bgm == null){
            bgm = (MediaPlayer) MediaPlayer.create(this, R.raw.menu);
        }

        setScreenMain();

        //  広告
        mAdview = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdview.loadAd(adRequest);

        // リワード広告
        MobileAds.initialize(this, APP_ID);
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();
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

        if (bgm != null){
            bgm.stop();
            bgm.release();
            bgm = null;
        }
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        //  DB更新
        AppDBUpdated();

        /* 音量の戻しの処理 */
        if (bgm != null){
            bgm.stop();
            bgm.release();
            bgm = null;
        }
        if (am != null){
            am.setStreamVolume(AudioManager.STREAM_MUSIC, start_volume, 0);
            am = null;
        }
    }

    /* 名声に応じたモード判定 */
    /*
        モード=0:通常シナリオモード（竜王討伐前）
        モード=1:竜王討伐後、　シドー編　挑戦
        モード=2:シドー討伐後、ゾーマ編　挑戦
        モード=3=ゾーマ討伐、　デスピサロ編　挑戦
     */
    public int GetGameMode(){
        if(db_boss1 <= 0)   {
            return MODE_BOSS_1;
        }
        else if (db_boss2 <= 0){
            return MODE_BOSS_2;
        }
        else if (db_boss3 <= 0){
            return MODE_BOSS_3;
        }
        else {
            return MODE_BOSS_4;
        }
    }


/***************************************************************************************

     サブ画面　処理

***************************************************************************************/

    /***********************************************
        画面表示処理（メイン画面）
    ***********************************************/
    /* メイン画面へ移動 */
    private void setScreenMain(){

        BgmStart(1);

        //TODO: 追加コンテンツ対応
        switch (GetGameMode()){
//            default:   BOSSHP = BOSS_1_HP; break;

            default:            BOSSHP = BOSS_1_HP; break;
            case MODE_BOSS_2:   BOSSHP = BOSS_2_HP; break;
//          case MODE_BOSS_3:   BOSSHP = BOSS_3_HP; break;
//          default:            BOSSHP = BOSS_4_HP; break;
        }
        /*
        if (GetGameMode() <= 0){
            BOSSHP = BOSS_1_HP;
        }
         */

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

        //TODO: 追加コンテンツ対応
        TextView result4 = (TextView) findViewById(R.id.text_result4);
        switch (GetGameMode()){
            default:            result4.setText("竜王残りＨＰ："+(BOSSHP-db_random_rate));     break;
            case MODE_BOSS_2:   result4.setText("シドー残りＨＰ："+(BOSSHP-db_random_rate));    break;
            /*
            case MODE_BOSS_1: result4.setText("竜王残りＨＰ："+(BOSSHP-db_random_rate)); break;
            case MODE_BOSS_2: result4.setText("シドー残りＨＰ："+(BOSSHP-db_random_rate)); break;
            case MODE_BOSS_3: result4.setText("ゾーマＨＰ："+(BOSSHP-db_random_rate)); break;
            default:          result4.setText("デスピサロＨＰ："+(BOSSHP-db_random_rate)); break;
             */
        }

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
    }

    /**
     リワード広告処理
     */
    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd(AD_UNIT_ID,new AdRequest.Builder().build());
    }

    @Override
    public void onRewarded(RewardItem reward) {
        // Reward the user.
        int temp = db_fame;
        db_fame = db_fame + 5;

        if (db_fame > 90) {
            db_fame = 90;
        }
        Toast.makeText(this, "名声：" + temp + " ==> "+db_fame, Toast.LENGTH_SHORT).show();
        //ユーザーレベルアップ
        AppDBUpdated();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
//        Toast.makeText(this, "onRewardedVideoAdLeftApplication", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdClosed() {
//        Toast.makeText(this, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
        Toast.makeText(this, "報酬動画の準備エラー ERR="+errorCode, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        Toast.makeText(this, "報酬動画の準備完了", Toast.LENGTH_SHORT).show();
//      Toast.makeText(this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdOpened() {
//        Toast.makeText(this, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoStarted() {
//        Toast.makeText(this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoCompleted() {
//        Toast.makeText(this, "onRewardedVideoCompleted", Toast.LENGTH_SHORT).show();
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
                    setScreenMain();
                }
            });
            guide.create();
            guide.show();
        }
        else{
            //TODO: 追加コンテンツ対応
            switch (GetGameMode()){
                default:            quiz_index = 4; break;
                case MODE_BOSS_2:   quiz_index = 5; break;
                /*
                case MODE_BOSS_1:   quiz_index = 4; break;
                case MODE_BOSS_2:   quiz_index = 5; break;
                case MODE_BOSS_3:   quiz_index = 6; break;
                default:            quiz_index = 7; break;

                 */
            }
            //quiz_index = 4;
            setScreenSub();
        }
    }
    //
    // ボタン　------> 勇者ステータスのクイズへ
    //
    public void onStatus(View view) {
        AlertDialog.Builder guide = new AlertDialog.Builder(this);
        TextView vmessage = new TextView(this);
        String str = "";

        if (db_user_level < 5){
            str += "\n";
            str += "　Lv " + db_user_level + "　称号：ひよっこ\n";
            str += "\n";
            str += " 〜〜〜〜〜装備〜〜〜〜〜\n";
            str += " 武器：たけのさお\n";
            str += " 鎧　：かわのふく\n";
            str += " 盾　：なし\n";
            str += "\n";
            str += " 効果：特になし\n\n";
        }
        else if (db_user_level < 10){
            str += "\n";
            str += "　Lv " + db_user_level + "　称号：かけだし\n";
            str += "\n";
            str += " 〜〜〜〜〜装備〜〜〜〜〜\n";
            str += "　武器：どうのつるぎ\n";
            str += "　鎧　：くさりかたびら\n";
            str += "　盾　：なし\n";
            str += "\n";
            str += "　効果：特になし\n\n";
        }
        else if(db_user_level <20){
            str += "\n";
            str += "　Lv " + db_user_level + "　称号：つわもの\n";
            str += "\n";
            str += " 〜〜〜〜〜装備〜〜〜〜〜\n";
            str += "　武器：はがねのつるぎ\n";
            str += "　鎧　：てつのよろい\n";
            str += "　盾　：かわのたて\n";
            str += "\n";
            str += "　効果：特になし\n\n";
        }
        else if(db_user_level < 29){
            str += "\n";
            str += "　Lv " + db_user_level + "　称号：勇者\n";
            str += "\n";
            str += " 〜〜〜〜〜装備〜〜〜〜〜\n";
            str += "　武器：ほのおのつるぎ\n";
            str += "　鎧　：まほうのよろい\n";
            str += "　盾　：てつのたて\n";
            str += "\n";
            str += "　効果：特になし\n\n";
        }
        else{
            if (db_fame <= 10) {
                str += "\n";
                str += "　Lv " + db_user_level + "　称号：伝説の勇者\n";
                str += "\n";
                str += " 〜〜〜〜〜装備〜〜〜〜〜\n";
                str += "　武器：ロトのつるぎ\n";
                str += "　鎧　：ロトのよろい\n";
                str += "　盾　：みかがみのたて\n";
                str += "\n";
                str += "　効果：ボスダメージ1.5倍\n\n";
            }
            else if (db_fame <= 30) {
                str += "\n";
                str += "　Lv " + db_user_level + "　称号：伝説の勇者\n";
                str += "\n";
                str += " 〜〜〜〜〜装備〜〜〜〜〜\n";
                str += "　武器：ロトのつるぎ・改\n";
                str += "　鎧　：ロトのよろい・改\n";
                str += "　盾　：ロトのたて・改\n";
                str += "\n";
                str += "　効果：ボスダメージ1.6倍\n\n";
            }
            else if (db_fame <= 50) {
                str += "\n";
                str += "　Lv " + db_user_level + "　称号：伝説の勇者\n";
                str += "\n";
                str += " 〜〜〜〜〜装備〜〜〜〜〜\n";
                str += "　武器：ロトのつるぎ・真\n";
                str += "　鎧　：ロトのよろい・真\n";
                str += "　盾　：ロトのたて・真\n";
                str += "\n";
                str += "　効果：ボスダメージ1.8倍\n\n";
            }
            else if (db_fame <= 70) {
                str += "\n";
                str += "　Lv " + db_user_level + "　称号：伝説の勇者\n";
                str += "\n";
                str += " 〜〜〜〜〜装備〜〜〜〜〜\n";
                str += "　武器：ロトのつるぎ・神\n";
                str += "　鎧　：ロトのよろい・神\n";
                str += "　盾　：ロトのたて・神\n";
                str += "\n";
                str += "　効果：ボスダメージ2.0倍\n\n";
            }
            else if (db_fame <= 999) {
                str += "\n";
                str += "　Lv " + db_user_level + "　称号：極めし者\n";
                str += "\n";
                str += " 〜〜〜〜〜装備〜〜〜〜〜\n";
                str += "　武器：ロトのつるぎ・極\n";
                str += "　鎧　：ロトのよろい・極\n";
                str += "　盾　：ロトのたて・極\n";
                str += "\n";
                str += "　効果：ボスダメージ5.0倍\n\n";
            }
        }

        //TODO: 追加コンテンツ対応
        //名声ポイント
        //竜王を倒してから
        if (db_boss1 > 0) {
            if (db_boss2 <= 0) {
                str += " 〜〜〜〜〜〜〜〜〜〜〜〜\n";
                str += "　名声：" + db_fame + "\n";
                str += "　倒したボス： 竜王 \n\n";
            }
            else if (db_boss3 <= 0) {
                str += " 〜〜〜〜〜〜〜〜〜〜〜〜\n";
                str += "　名声：" + db_fame + "\n";
                str += "　倒したボス： 竜王、シドー \n\n";
            }
            else if (db_boss4 <= 0) {
                str += " 〜〜〜〜〜〜〜〜〜〜〜〜\n";
                str += "　名声：" + db_fame + "\n";
                str += "　倒したボス： 竜王、シドー、ゾーマ\n\n";
            }
            else {
                str += " 〜〜〜〜〜〜〜〜〜〜〜〜\n";
                str += "　名声：" + db_fame + "\n";
                str += "　倒したボス： 竜王、シドー、ゾーマ\n　　　　　　　　デスピサロ\n\n";
            }
        }
        /*
        if (db_boss1 > 0){
            str += " 〜〜〜〜〜〜〜〜〜〜〜〜\n";
            str += "　名声："+ db_fame + "\n";
            str += "　倒したボス： 竜王 \n\n";
        }
         */


        vmessage.setText(str);

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
                setScreenMain();
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
                setScreenMain();
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
                setScreenMain();
            }
        });

        guide.create();
        guide.show();
    }
    public void onTreasure(View view){
        AlertDialog.Builder guide = new AlertDialog.Builder(this);
        TextView vmessage = new TextView(this);

        if (db_boss1 <= 0) {
            //メッセージ
            vmessage.setText("\n\n 竜王を倒してから挑戦可能です・・・\n\n さらなる強敵が待つ！！\n\n\n\n\n\n");
            vmessage.setBackgroundColor(Color.DKGRAY);
            vmessage.setTextColor(Color.WHITE);
            vmessage.setTextSize(16);
            guide.setTitle("追加コンテンツ");
            guide.setIcon(R.drawable.dq128x128);
            guide.setView(vmessage);
            guide.setPositiveButton("確認", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setScreenMain();
                }
            });
        }
        else {
            //メッセージ
            vmessage.setText("\n\n 動画を視聴して名声を上げよう！！\n 名声を上げて強力な武器をGETしよう♪\n\n" +
                    " 現在の名声："+db_fame+"　=>　視聴後の名声："+(db_fame+5)+ "\n\n"+
                    " [名声 11以上] ロト装備・改 \n" +
                    " [名声 31以上] ロト装備・真 \n" +
                    " [名声 51以上] ロト装備・神 \n" +
                    " [名声 71以上] ロト装備・極 \n" +
                    "\n\n");
            vmessage.setBackgroundColor(Color.DKGRAY);
            vmessage.setTextColor(Color.WHITE);
            vmessage.setTextSize(16);
            guide.setTitle("追加コンテンツ");
            guide.setIcon(R.drawable.dq128x128);
            guide.setView(vmessage);

            guide.setPositiveButton("視聴", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO リワード処理　MAX名声90
                    if (mRewardedVideoAd.isLoaded()) {
                        mRewardedVideoAd.show();
                    }
/*test*/
                    /*
                    db_fame += 20;
                    setScreenMain();
                     */
                }
            });
            guide.setNegativeButton("中止", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setScreenMain();
                }
            });
        }
        guide.create();
        guide.show();
    }

 /***************************************************************************************

        サブ画面　処理

 ***************************************************************************************/


    /***********************************************
        画面表示処理（ボス討伐後）
     ***********************************************/
    public void boss_fight_result(){

        AlertDialog.Builder guide = new AlertDialog.Builder(this);
        TextView vmessage = new TextView(this);
        String str = "";

        if (BOSSHP <= db_random_rate){
            db_fame = 10;
            db_boss1 = 1;
            db_user_level = 30;
            db_random_rate = 0;

            //メッセージ
            str += "\n\n";
            str += " 勇者はBOSSを倒した！！\n\n";
            str += " 名声が [ "+ db_fame + " ] になりました\n\n";
            str += " 名声を上げて\n";
            str += " さらなる強敵に挑戦しよう\n";
            str += " [ステータス]も確認してね\n\n\n";
            vmessage.setText(str);

            vmessage.setBackgroundColor(Color.DKGRAY);
            vmessage.setTextColor(Color.WHITE);
            vmessage.setTextSize(16);
            guide.setTitle("討伐完了！！");
            guide.setIcon(R.drawable.para);
            guide.setView(vmessage);
            guide.setPositiveButton("確認", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AppDBUpdated();
                    setScreenMain();
                }
            });
            guide.create();
            guide.show();
        }
        else{
            setScreenMain();
        }
    }

    /***********************************************
        画面表示処理（ボス討伐後）
        ダメージ補正処理
     ***********************************************/
    public int  boss_damage_calcurate(int damage){
        int result = damage;
        int add_damage = 150;

        if (db_fame > 10 && db_fame <= 30){
            add_damage = 160;
        }
        else if (db_fame <= 50){
            add_damage = 180;
        }
        else if (db_fame <= 70){
            add_damage = 200;
        }
        else if (db_fame <= 90){
            add_damage = 500;
        }


        // ロト装備で1.5倍
        if (db_user_level >= 30){
            result = damage * add_damage;
            result /= 100;
        }

        return result;
    }

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
        String mess = "";

        ImageView imageView = new ImageView( this );

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
                default:
                    boss_hp_bf -= db_random_rate;
                    db_random_rate += boss_damage_calcurate(temp_rate);
                    boss_hp_af -= db_random_rate;
                    // ＨＰがゼロ以下の処理
                    if(boss_hp_af <= 0) {
                        boss_hp_af = 0;
//                        boss_hp_af = db_random_rate = BOSSHP;
                    }
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
                    vmessage.setText("\n\n 勇者はレベルアップしました\n  Lv " + before_level + " → " + db_user_level + "\n\n Lv20以上で竜王挑戦\n\n「ステータス」チェックしてね\n\n");
                    vmessage.setBackgroundColor(Color.DKGRAY);
                    vmessage.setTextColor(Color.WHITE);
                    vmessage.setTextSize(16);
                    guide.setTitle("Level UP");
                    guide.setIcon(R.drawable.lv);
                    guide.setView(vmessage);
                    guide.setPositiveButton("確認", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setScreenMain();
                        }
                    });
                    guide.create();
                    guide.show();
                }
            }
            // ラスボスの処理
            else{
                guide.setIcon(R.drawable.boss);
                if (boss_hp_af >= boss_hp_bf && boss_hp_af != BOSSHP){
                    //if (boss_hp_af >= boss_hp_bf && boss_hp_af != BOSSHP){
                    mess += "\n\n";
                    mess += " 勇者の一撃は回避された!!\n";
                    mess += "\n";
                    mess += "BOSS　残ＨＰ" + boss_hp_bf + " → " + boss_hp_af + "\n";
                    mess += "\n\n";
                    mess += "　(残ＨＰ→ゼロ　ゲームクリア）\n";
                    mess += "\n\n";
//                  vmessage.setText("\n\n 勇者の一撃は竜王に回避された!!\n\n  竜王　残ＨＰ" + boss_hp_bf + " → " + boss_hp_af + "\n\n\n　(残ＨＰゼロ＝GAMEクリア）\n\n\n\n");
                    switch (GetGameMode()){
                        default:            imageView.setImageResource(R.drawable.boss1);   break;
                        case MODE_BOSS_2:   imageView.setImageResource(R.drawable.boss21);   break;
                    }
                }
                else {
                    mess += "\n\n";
                    mess += " 勇者の一撃が直撃した!!\n";
                    mess += "\n";
                    mess += "BOSS　残ＨＰ" + boss_hp_bf + " → " + boss_hp_af + "\n";
                    mess += "\n\n";
                    mess += "　(残ＨＰ→ゼロ　ゲームクリア）\n";
                    mess += "\n\n";
//                  vmessage.setText("\n\n 勇者の一撃が竜王に直撃した!!\n\n  竜王　残ＨＰ" + boss_hp_bf + " → " + boss_hp_af + "\n\n\n　(残ＨＰゼロ＝GAMEクリア)\n\n\n\n");
                    switch (GetGameMode()){
                        default:            imageView.setImageResource(R.drawable.boss2);   break;
                        case MODE_BOSS_2:   imageView.setImageResource(R.drawable.boss22);   break;
                    }

                }
                vmessage.setBackgroundColor(Color.DKGRAY);
                vmessage.setTextColor(Color.WHITE);
                vmessage.setTextSize(16);
                guide.setTitle("死闘結果");
                guide.setView(imageView);
                guide.setMessage(mess);
//                guide.setView(vmessage);
                guide.setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boss_fight_result();
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
            case 1: dispmsg = quizSearch.QuizTableSearch_1();   BgmStart(2);    break;
            case 2: dispmsg = quizSearch.QuizTableSearch_2();   BgmStart(2);    break;
            case 3: dispmsg = quizSearch.QuizTableSearch_3();   BgmStart(2);    break;
            case 4: dispmsg = quizSearch.QuizTableSearch_4();   BgmStart(3);    break;
            case 5: dispmsg = quizSearch.QuizTableSearch_5();   BgmStart(3);    break;
        }

        /*　全問終了　*/
        if (dispmsg == null || quizCount > QUIZMAX){
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

        BOSSHP = BOSS_1_HP; //ボスのＨＰをセット

// test
        /*
        db_user_level = 30;
        db_quest1_rate = 100;
        db_quest2_rate = 100;
        db_quest3_rate = 100;
        db_random_rate = BOSS_1_HP-10;
         */

        //音声初期化
        if (am == null) {
            am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            start_volume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        }
        if (bgm == null){
            bgm = (MediaPlayer) MediaPlayer.create(this, R.raw.menu);
        }

        setScreenMain();
    }
    /***************************************************
        音源処理
     ****************************************************/
    public void BgmStart(int index){

        if (bgm == null){
            return;
        }
        else{
            if (bgm_index != index) {
                bgm.stop();
                bgm.release();
                bgm = null;
            }
        }
        bgm_index = index;

        switch (index){
            default:
            case 1:
                if (bgm == null){
                    bgm = (MediaPlayer) MediaPlayer.create(this, R.raw.menu);
                }
                break;
            case 2:
                if (bgm == null){
                    bgm = (MediaPlayer) MediaPlayer.create(this, R.raw.battle);
                }
               break;
            case 3:
                if (bgm == null){
                    bgm = (MediaPlayer) MediaPlayer.create(this, R.raw.boss);
                }
                break;
        }

        if (bgm.isPlaying() == false) {
            bgm.setLooping(true);
            bgm.start();
        }

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
            insertValues.put("fame", 0);
            insertValues.put("boss1", 0);
            insertValues.put("boss2", 0);
            insertValues.put("boss3", 0);
            insertValues.put("boss4", 0);
            insertValues.put("data1", 0);
            insertValues.put("data2", 0);
            insertValues.put("data3", 0);
            insertValues.put("data4", 0);
            insertValues.put("data5", 0);
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
        insertValues.put("fame", db_fame);
        insertValues.put("boss1", db_boss1);
        insertValues.put("boss2", db_boss2);
        insertValues.put("boss3", db_boss3);
        insertValues.put("boss4", db_boss4);
        int ret;
        try {
            ret = db.update("appinfo", insertValues, null, null);
        } finally {
            db.close();
        }
        /*
        if (ret == -1) {
            Toast.makeText(this, "Saving.... ERROR ", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Saving.... OK "+ "isopen= "+db_isopen, Toast.LENGTH_SHORT).show();
        }
         */
    }

}