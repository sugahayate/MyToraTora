package jp.ac.asojuku.mytoratora

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    //追加したライフサイクルメソッド
    override fun onResume() {
        super.onResume()
        //ボタンがクリックされたら処理を呼び出し
        tora.setOnClickListener{ onJankenButtonTapped(it); }//グーボタンが押されたら実行する処理を設定
        oba.setOnClickListener{ onJankenButtonTapped(it); }
        katou.setOnClickListener{ onJankenButtonTapped(it); }
    }
    //ボタンがクリックされたら呼び出される処理
    fun onJankenButtonTapped(view: View?){
        //画面遷移のためのインテントのインスタンスを作る
        val intent = Intent(this,ResultActivity::class.java);
        //インテントにおまけ情報(Extra)でどのボタンを選んだかを設定する
        intent.putExtra("MY_HAND",view?.id);


        //　osにインテントを引き渡して画面遷移を実行してもらう
        startActivity(intent);
    }

}
