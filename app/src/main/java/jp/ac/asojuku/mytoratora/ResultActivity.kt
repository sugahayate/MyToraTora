package jp.ac.asojuku.mytoratora

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {
    val tora = 0;val oba = 1;val katou = 2;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
    }

    //再表示のたびに呼ばれるらいふサイクルコールパックメソッド
    override fun onResume() {
        super.onResume()
        val id = intent.getIntExtra("MY_HAND",0);
        //前の画面で選んだ手を保持する定数を定義する
        val myHand:Int;
        //idの値によって処理を分岐、自分のじゃんけん画像を切り替える
        myHand = when(id){
            R.id.tora -> {myHandImage.setImageResource(R.drawable.tora);tora}//グー画像で上書き保存
            R.id.oba -> {myHandImage.setImageResource(R.drawable.oba);oba}//チョキ画像で上書き保存
            R.id.katou -> {myHandImage.setImageResource(R.drawable.katou);katou}//パー画像で上書き保存
            else -> tora;
        }
        //コンピュータの手をランダムに決める
        //val comHand = (Math.random()*3).toInt()    //0~2がランダムに入る
        val comHand = getHand()//メソッドで組み立てた手を採用する
        //コンピュータの手に合わせてコンピュータの画像を切り替える
        when(comHand){
            tora -> {comHandImage.setImageResource(R.drawable.tora)} //{}はなくてもいい
            oba -> {comHandImage.setImageResource(R.drawable.oba)}
            katou -> comHandImage.setImageResource(R.drawable.katou)

        }
        //勝敗を判定する
        val gameResult = (comHand - myHand + 3) % 3 //計算結果が０：引き分け　１：自分の勝ち　２：負け
        when(gameResult){
            0 -> resultLavel.setText(R.string.result_draw)
            1 -> resultLavel.setText(R.string.result_win)
            2 -> resultLavel.setText(R.string.result_lose)
        }
        //戻るボタンにタップされた時の処理を設定する
        backButton.setOnClickListener{ this.finish() }//戻るボタンが押されたら結果画面を破棄する

        //勝敗とじゃんけんで出した手を保存する
        this.saveData(myHand,comHand,gameResult);//引数はユーザの手、コンピュータの手、勝敗、それぞれの変数値

    }
    //ResultActiveityクラスに勝敗データを保存するメソッドを追加する
    private fun saveData(myHand: Int, comHand:Int, gameResult:Int){
        //共有プリファレンスを使う⑴インスタンスを取得
        val pref = PreferenceManager.getDefaultSharedPreferences(this);
        //2 値を取得する：キーを指定して値を取得する。該当するものがなけれヴァデフォルト値が帰る
        val gameCount = pref.getInt("GAME_COUNT",0);//デフォルト値：０　勝負の数
        val winningStreakCount = pref.getInt("WINNING_STREAK_COUNT",0)//連勝数
        val lastComHand = pref.getInt("LAST_COM_HAND",0)//前回のコンピュータのて
        val lastGameResult = pref.getInt("LAST_GAME_RESULT",-1)//前回の勝敗
        //保存を始めていく、まず値を組み立てる
        //連勝数を更新
        val editWinningStreakCount = when{
            //前回勝って今回も勝ったら連勝＋１する
            (lastGameResult == 2 && gameResult == 2) ->
                winningStreakCount+1
            else ->
                //それ以外は０を返す
                0
        }
        //3 共有プリファレンスの編集モードを取得
        val editor = pref.edit();
        //editのメソッドをメソッドチェーンで呼び出し
        editor.putInt("GAME_COUNT",gameCount+1)//勝負数
            .putInt("WINNING_STREAK_COUNT",editWinningStreakCount)//連勝数
            .putInt("LAST_MY_HAND",myHand)//ユーザの前の手
            .putInt("LAST_COM_HAND",comHand)//コンピュータの前の手
            .putInt("BEFORE_LAST_COM_HAND",lastComHand)//コンピュータの前々回の手
            .putInt("GAME_RESULT",gameResult)//勝敗
            .apply()//編集モードを確定して閉じる

    }


    private fun getHand():Int{
        var hand = (Math.random()*3).toInt();//0~９までの乱数を3倍して整数にすると０か１か２か
        //ここから心理学のロジックを使ってhandの値を上書きするがどうか処理する
        //共有プリファレンスに保存したデータを取り出すためにインスタンスを取得する
        val pref = PreferenceManager.getDefaultSharedPreferences(this)//インスタンスを取得
        //共有プリファレンスのインスタンス変数prefを使って保存値を取得していく
        val gameCount = pref.getInt("GAME_COUNT",0);//何回戦か
        val winningStreakCount = pref.getInt("WINNING_STREAK_COUNT",0);//何連勝か
        val lastMyHand = pref.getInt("LAST_MY_HAND",0);//前のユーザの手
        val lastComHand = pref.getInt("LAST_COM_HAND",0);//前のコンピュータのて
        val beforeLastComHand = pref.getInt("BEFORE_LAST_COM_HAND",0);//前の前のコンピュータのて
        val gameResult = pref.getInt("GAME_RESULT",-1);//勝敗

        //取得した保存値を使ってコンピュータの出す手（戻り値）を上書きする
        //前回が一回戦の時
        if(gameCount == 1){
            if(gameResult == 2){
                //前回が一回戦で、さらにコンピュータの勝ち（ユーザの負け）
                //コンピュータ次に出す手を変える
                while(lastComHand == hand){
                    hand = (Math.random()*3).toInt();
                }
            }else if(gameResult == 1){
                hand = (lastMyHand-1 + 3)%3;//グー（０）チョキ（１）パー（２）
            }
        }else if(winningStreakCount>0){
            //連勝中の時
            if(beforeLastComHand == lastComHand){//同じ手で連勝した
                while (lastComHand == hand){//前回のてと今の手が同じなら、変える
                    hand = (Math.random()*3).toInt()//ランダムな値で更新
                }
            }
        }

        return hand;//最終的な値を決定して返す
    }

}
