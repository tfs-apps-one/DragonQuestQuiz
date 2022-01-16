package tfsapps.dragonquestquiz;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/* CSVファイルの読込処理 */
public class CsvReader {
    List<ListData> objects = new ArrayList<ListData>();
    public void reader(Context context) {
        AssetManager assetManager = context.getResources().getAssets();
        try {
            // CSVファイルの読み込み
            InputStream inputStream = assetManager.open("data.csv");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferReader.readLine()) != null) {

                //カンマ区切りで１つづつ配列に入れる
                ListData data = new ListData();
                String[] RowData = line.split(",");

                //CSVの左([0]番目)から順番にセット
                data.setSeries(RowData[0]);
                data.setQuizLevel(RowData[1]);
                data.setQuestion(RowData[2]);
                data.setAnswer1(RowData[3]);
                data.setAnswer2(RowData[4]);
                data.setAnswer3(RowData[5]);
                data.setAnswer4(RowData[6]);
                data.setResult(RowData[7]);
                objects.add(data);
            }
            bufferReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
