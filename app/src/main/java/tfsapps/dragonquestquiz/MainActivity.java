package tfsapps.dragonquestquiz;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

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
}