package tfsapps.dragonquestquiz;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuizSearch {

    private List<ListData> Quiz_List = new ArrayList<ListData>();
    private final Random rand = new Random(System.currentTimeMillis());
    static public ListData nowquiz;

    //コンストラクタ
    public QuizSearch(CsvReader csvreader) {
        // CSVファイルのロード＆メモリ展開
        int i;
        int max = csvreader.objects.size();

        for (i = 0; i < max; i++) {
            ListData temp = csvreader.objects.get(i);
            temp.isAlive = true;
            Quiz_List.add(temp);
        }
    }

    //クイズサーチ処理
    public ListData QuizTableSearch()
    {
        int index1 = 0;
        int i;
        int j;

        for(i=0;i<Quiz_List.size();i++)
        {
            index1 = rand.nextInt(Quiz_List.size());

            //検索済みのクイズ（すでに表示済み）
            if (Quiz_List.get(index1).isAlive == false)
            {
                continue;
            }
            //初ヒットしたクイズ
            else
            {
                Quiz_List.get(index1).isAlive = false;
                nowquiz = Quiz_List.get(index1);
                return nowquiz;
            }
        }

        //ランダム検索でヒットしなかったクイズをもう一度全件サーチする
        for (j=0; j<Quiz_List.size(); j++)
        {
            if(Quiz_List.get(j).isAlive == false)
            {
                continue;
            }
            else
            {
                Quiz_List.get(j).isAlive = false;
                nowquiz = Quiz_List.get(j);
                return nowquiz;
            }
        }

        //本当になにもなければNULLを返す
        return null;
    }

    //クイズの検索状態の初期化
    public void QuizTableSearchReset()
    {
        int i;
        for(i=0;i<Quiz_List.size();i++)
        {
            Quiz_List.get(i).isAlive = true;
        }
    }

    public int QuizTableMax()
    {
        return this.Quiz_List.size();
    }

}
