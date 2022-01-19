package tfsapps.dragonquestquiz;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuizSearch {

    private List<ListData> Quiz_List = new ArrayList<ListData>();
    private final Random rand = new Random(System.currentTimeMillis());
    static private ListData nowquiz;

    //コンストラクタ
    public QuizSearch(CsvReader csvreader) {
        // CSVファイルのロード＆メモリ展開
        int i;
        int max = csvreader.objects.size();

        for (i = 0; i < max; i++) {
            ListData temp = csvreader.objects.get(i);
            Quiz_List.add(temp);
        }
    }


}
