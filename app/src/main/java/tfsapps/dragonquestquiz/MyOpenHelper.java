package tfsapps.dragonquestquiz;

import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyOpenHelper extends SQLiteOpenHelper
{
    private static final String TABLE = "appinfo";
    public MyOpenHelper(Context context) {
        super(context, "AppDB", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE + "("
                + "isopen integer,"             //DBオープン
                + "user_level integer,"         //プレイヤー レベル
                + "option_level integer,"       //オプション　レベル
                + "quest1_rate integer,"        //ドラクエ１　正解率
                + "quest2_rate integer,"        //ドラクエ２　正解率
                + "quest3_rate integer,"        //ドラクエ３　正解率
                + "random_rate integer,"        //ランダム　正解率
                + "fame integer,"               //名声
                + "boss1 integer,"              //ボス１
                + "boss2 integer,"              //ボス２
                + "boss3 integer,"              //ボス３
                + "boss4 integer,"              //ボス４
                + "data1 integer,"              //
                + "data2 integer,"              //
                + "data3 integer,"              //
                + "data4 integer,"              //
                + "data5 integer);");           //
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}