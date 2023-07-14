package com.example.number_juggling;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private final static String BR = //改行
            System.getProperty("line.separator");
    private  volatile  boolean gameover;

    private StateTTT state ;

    private byte aiPlayer ;

    private TextView tv;

    private Button aiBtn;

    private TextView[] gameboard=new TextView[9];//接続用textview
    private int[] tile={R.id.tile0,R.id.tile1,R.id.tile2,
                        R.id.tile3,R.id.tile4,R.id.tile5,
                        R.id.tile6,R.id.tile7,R.id.tile8};//idを取得しておく

    private int [][] gamearray=new int[9][7];//ランダムで生成する場合

    //private int [][] gamearray={{1,2,6,3,4,5},{5,2,3,1,4,6},{6,2,4,5,1,3},
                                //{5,6,4,3,1,2},{3,2,4,6,5,1},{4,6,3,5,2,1},
                                //{3,4,1,2,5,6},{6,4,5,1,2,3},{1,6,5,2,4,3}};//固定で配列を使用テスト用


    private int [] arrayposi={0,0,0,0,0,0,0,0,0};//gamearrayの位置の記憶用 max 4

    Random rand =  new Random() ;

    private int turn=0;



    public StateTTT getInitialState(byte player) {
        StateTTT s = null ;
        try {
            int[][] b = new int[3][3] ;
            s = new StateTTT(b, player) ;
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Failed to create the initial state.",
                    Toast.LENGTH_LONG).show();
        }
        return s ;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);



        boolean isFirst = rand.nextBoolean() ;

        aiBtn=findViewById(R.id.AI_btn);//aibtn登録
        tv=findViewById(R.id.turn_name);//ターンの登録



        Generate_Rand_array generate1=new Generate_Rand_array();
        gamearray=generate1.fisher_yates_alg(gamearray);



        int i,j,count=0;

        for(i=0;i<tile.length;i++){
            gameboard[i]=findViewById(tile[i]);//textviewのリスナー登録
            gameboard[i].setText(String.valueOf(gamearray[i][arrayposi[i]]));//初期値の登録
        }


        if (isFirst) {
            tv.setText("player1のターンです\n") ;
            state = getInitialState(State.Player1);
            aiBtn.setEnabled(false) ;
        } else {
            tv.setText("player2のターンです\n");
            state = getInitialState(State.Player1);
            aiBtn.setEnabled(true) ;
        }
        int len=state.getBoard().length;

        for(i=0;i<len;i++){//stateのボードに現在の盤面を受け渡し
            for(j=0;j<len;j++){
                state.setBoard(i,j,gamearray[count][0]);
                count++;
            }
        }

    }
    @SuppressLint("ResourceType")
    public boolean onClickEvent(View view){
        if(gameover) return false;





        String str = (String)view.getTag();
        int tag = Integer.parseInt(str);
        int r=tag/3;
        int c=tag%3;


        byte player= state.getPlayer();//playerの取得

        if(player==1){
            tv.setText("player1のターンです\n") ;
        }else{
            tv.setText("player2のターンです\n") ;
        }


        arrayposi[tag]=arrayposi[tag]+1;//textの数字を更新するためにarrayの位置を一つ更新
        if(arrayposi[tag]>=gamearray[0].length){//更新した値がgamearrayの長さを超えたら位置を0に戻す
            arrayposi[tag]=0;
        }
        int textnumber=gamearray[tag][arrayposi[tag]];//クリックしたところの数字取得
        state.setNumber(textnumber);//取得した数字をstateに送る


        if(state.makeAmove(new Move(r,c))==false){
            arrayposi[tag]=arrayposi[tag]-1;//textの数字が移動できなかったら、元に戻す
            if(arrayposi[tag]<0) arrayposi[tag]=0; //配列が0以下の場合のエラー対処
            return false;
        }//移動できるか判定




        gameboard[tag].setText(String.valueOf(gamearray[tag][arrayposi[tag]]));



        aiPlayer=player;//player引き渡し
        Object result = state.isTerminal(aiPlayer) ; // 今の手を，ボード記憶用の配列に記憶し，
        if (result != null) { //勝負決定
            double d = (double)result ;
            String message ;
            if (d != 0.0 && d != State.lossValue && d != State.winValue ) {
                tv.setTextColor(Color.RED); // 先手への情報の表示色を赤にする.
                message = "終局盤面の報酬が期待の値になっていません．" + BR ;
            } else {

                if (d == State.winValue)
                    message = "あなたの負けです．" + BR ;
                else if (d == 0.0)
                    message = "引き分けです．" + BR ;
                else
                    message = "あなたの勝ちです．" + BR ;
            }
            gameover = true ;
            tv.setText(message); ;//messageをゲームフレームに表示
            aiBtn.setEnabled(false) ;//aibtnを押せるようにする

        }
        state.setPlayer(state.getOpponent());





        return false;
    }

    @Override
    public void onBackPressed() {
        ActivityCompat.finishAffinity(this) ;//TTTActivityを終了
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    //配列に1～5の整数を被り無しかつランダムに格納

}