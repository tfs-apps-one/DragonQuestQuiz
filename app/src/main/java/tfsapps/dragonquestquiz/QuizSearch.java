package tfsapps.dragonquestquiz;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuizSearch {

    private List<ListData> Quiz_List = new ArrayList<ListData>();
    private List<ListData> Quiz_List_1 = new ArrayList<ListData>();
    private List<ListData> Quiz_List_2 = new ArrayList<ListData>();
    private List<ListData> Quiz_List_3 = new ArrayList<ListData>();
    private List<ListData> Quiz_List_4 = new ArrayList<ListData>();
    private List<ListData> Quiz_List_5 = new ArrayList<ListData>();
    private final Random rand = new Random(System.currentTimeMillis());
    static public ListData nowquiz;
    static private int series_1;
    static private int series_2;
    static private int series_3;
    static private int series_4;
    static private int series_5;

    //コンストラクタ
    public QuizSearch(CsvReader csvreader) {
        // CSVファイルのロード＆メモリ展開
        int i;
        int max = csvreader.objects.size();

        for (i = 0; i < max; i++) {
            ListData temp = csvreader.objects.get(i);
            temp.isAlive = true;

            //シリーズ毎の設問数
            switch (Integer.parseInt(temp.QuizLevel)){
                case 1: series_1++; Quiz_List_1.add(temp);  break;
                case 2: series_2++; Quiz_List_2.add(temp);  break;
                case 3: series_3++; Quiz_List_3.add(temp);  break;
                case 4: series_4++; Quiz_List_4.add(temp);  break;
                case 5: series_4++; Quiz_List_5.add(temp);  break;
                default:break;
            }
            Quiz_List.add(temp);
        }
    }

    //クイズサーチ処理
    public ListData QuizTableSearch_1()
    {
        int index1 = 0;
        int i;
        int j;
        for(i=0;i<Quiz_List_1.size();i++)
        {
            index1 = rand.nextInt(Quiz_List_1.size());

            //検索済みのクイズ（すでに表示済み）
            if (Quiz_List_1.get(index1).isAlive == false) {
                continue;
            }
            //初ヒットしたクイズ
            else {
                Quiz_List_1.get(index1).isAlive = false;
                nowquiz = Quiz_List_1.get(index1);
                return nowquiz;
            }
        }
        //ランダム検索でヒットしなかったクイズをもう一度全件サーチする
        for (j=0; j<Quiz_List_1.size(); j++)
        {
            if(Quiz_List_1.get(j).isAlive == false) {
                continue;
            }
            else {
                Quiz_List_1.get(j).isAlive = false;
                nowquiz = Quiz_List_1.get(j);
                return nowquiz;
            }
        }
        //本当になにもなければNULLを返す
        return null;
    }

    //クイズサーチ処理
    public ListData QuizTableSearch_2()
    {
        int index1 = 0;
        int i;
        int j;
        for(i=0;i<Quiz_List_2.size();i++)
        {
            index1 = rand.nextInt(Quiz_List_2.size());

            //検索済みのクイズ（すでに表示済み）
            if (Quiz_List_2.get(index1).isAlive == false) {
                continue;
            }
            //初ヒットしたクイズ
            else {
                Quiz_List_2.get(index1).isAlive = false;
                nowquiz = Quiz_List_2.get(index1);
                return nowquiz;
            }
        }
        //ランダム検索でヒットしなかったクイズをもう一度全件サーチする
        for (j=0; j<Quiz_List_2.size(); j++)
        {
            if(Quiz_List_2.get(j).isAlive == false) {
                continue;
            }
            else {
                Quiz_List_2.get(j).isAlive = false;
                nowquiz = Quiz_List_2.get(j);
                return nowquiz;
            }
        }
        //本当になにもなければNULLを返す
        return null;
    }

    //クイズサーチ処理
    public ListData QuizTableSearch_3()
    {
        int index1 = 0;
        int i;
        int j;
        for(i=0;i<Quiz_List_3.size();i++)
        {
            index1 = rand.nextInt(Quiz_List_3.size());

            //検索済みのクイズ（すでに表示済み）
            if (Quiz_List_3.get(index1).isAlive == false) {
                continue;
            }
            //初ヒットしたクイズ
            else {
                Quiz_List_3.get(index1).isAlive = false;
                nowquiz = Quiz_List_3.get(index1);
                return nowquiz;
            }
        }
        //ランダム検索でヒットしなかったクイズをもう一度全件サーチする
        for (j=0; j<Quiz_List_3.size(); j++)
        {
            if(Quiz_List_3.get(j).isAlive == false) {
                continue;
            }
            else {
                Quiz_List_3.get(j).isAlive = false;
                nowquiz = Quiz_List_3.get(j);
                return nowquiz;
            }
        }
        //本当になにもなければNULLを返す
        return null;
    }

    //クイズサーチ処理
    public ListData QuizTableSearch_4()
    {
        int index1 = 0;
        int i;
        int j;
        for(i=0;i<Quiz_List_4.size();i++)
        {
            index1 = rand.nextInt(Quiz_List_4.size());

            //検索済みのクイズ（すでに表示済み）
            if (Quiz_List_4.get(index1).isAlive == false) {
                continue;
            }
            //初ヒットしたクイズ
            else {
                Quiz_List_4.get(index1).isAlive = false;
                nowquiz = Quiz_List_4.get(index1);
                return nowquiz;
            }
        }
        //ランダム検索でヒットしなかったクイズをもう一度全件サーチする
        for (j=0; j<Quiz_List_4.size(); j++)
        {
            if(Quiz_List_4.get(j).isAlive == false) {
                continue;
            }
            else {
                Quiz_List_4.get(j).isAlive = false;
                nowquiz = Quiz_List_4.get(j);
                return nowquiz;
            }
        }
        //本当になにもなければNULLを返す
        return null;
    }
    public ListData QuizTableSearch_5()
    {
        int index1 = 0;
        int i;
        int j;
        for(i=0;i<Quiz_List_5.size();i++)
        {
            index1 = rand.nextInt(Quiz_List_5.size());

            //検索済みのクイズ（すでに表示済み）
            if (Quiz_List_5.get(index1).isAlive == false) {
                continue;
            }
            //初ヒットしたクイズ
            else {
                Quiz_List_5.get(index1).isAlive = false;
                nowquiz = Quiz_List_5.get(index1);
                return nowquiz;
            }
        }
        //ランダム検索でヒットしなかったクイズをもう一度全件サーチする
        for (j=0; j<Quiz_List_5.size(); j++)
        {
            if(Quiz_List_5.get(j).isAlive == false) {
                continue;
            }
            else {
                Quiz_List_5.get(j).isAlive = false;
                nowquiz = Quiz_List_5.get(j);
                return nowquiz;
            }
        }
        //本当になにもなければNULLを返す
        return null;
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
            if (Quiz_List.get(index1).isAlive == false) {
                continue;
            }
            //初ヒットしたクイズ
            else {
                Quiz_List.get(index1).isAlive = false;
                nowquiz = Quiz_List.get(index1);
                return nowquiz;
            }
        }
        //ランダム検索でヒットしなかったクイズをもう一度全件サーチする
        for (j=0; j<Quiz_List.size(); j++)
        {
            if(Quiz_List.get(j).isAlive == false) {
                continue;
            }
            else {
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

    public ListData QuizNowListData()
    {
        return nowquiz;
    }

}
