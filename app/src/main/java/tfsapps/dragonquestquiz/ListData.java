package tfsapps.dragonquestquiz;

/* クイズのデータリスト */
public class ListData {
    String Series;          //シリーズ
    String QuizLevel;       //問題レベル
    String Question;        //設問
    String Answer1;         //答１
    String Answer2;         //答２
    String Answer3;         //答３
    String Answer4;         //答４
    String Result;          //結果

    /*********************************************
        GET　処理
     ********************************************/
    public String getSeries(){
        return Series;
    }
    public String getQuizLevel(){
        return QuizLevel;
    }
    public String getQuestion(){
        return Question;
    }
    public String getAnswer1(){
        return Answer1;
    }
    public String getAnswer2(){
        return Answer2;
    }
    public String getAnswer3(){
        return Answer3;
    }
    public String getAnswer4(){
        return Answer4;
    }
    public String getResult(){
        return Result;
    }

    /*********************************************
        SET　処理
     ********************************************/
    public void setSeries(String data){
        this.Series = data;
    }
    public void setQuizLevel(String data){
        this.QuizLevel = data;
    }
    public void setQuestion(String data){
        this.Question = data;
    }
    public void setAnswer1(String data){
        this.Answer1 = data;
    }
    public void setAnswer2(String data){
        this.Answer2 = data;
    }
    public void setAnswer3(String data){
        this.Answer3 = data;
    }
    public void setAnswer4(String data){
        this.Answer4 = data;
    }
    public void setResult(String data){
        this.Result = data;
    }
}
