package com.example.number_juggling;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public abstract class State {
    //Define the players. A player cannot be 0.
    public static final byte Player1 = 1 ;
    public static final byte Player2 = -1 ;

    //instance fields
    protected int[][] board ;//ボードの中身
    protected byte player ;//この盤面で手を打とうとするplayer

    //引き分けのrewardを0.0に固定．
    public static double winValue = 1 ;//勝ったときの報酬
    public static double lossValue = -1 ;//負けたときの報酬

    private int wins = 0 ;//この盤面からランダムに複数回play_outするとき勝つ回数
    public int playOutTimes = 100 ;//playout回数。ゲームに基づいて調整すべき。

    public int n_turn=1;//turn数



    //constructor
    public State() throws Exception {
        setWinLossValues() ;
        if (Math.abs(winValue) != Math.abs(lossValue) ||
                winValue == lossValue || winValue == 0.0)
            throw new Exception("勝ち・負けの報酬が0ではない異なる値で，その絶対値が同じでなければならない．") ;
    }

    //指定の場所で指定のplayerが手を打てるかを判定する。
    public abstract boolean isValidMove(Move move, byte player) ;

    //指定のboardとplayerからstateを作成して返す。
    //This is a factory method.
    public abstract State getState(int[][] board, byte player) ;

    //make one move of the current player.
    //Return true if and only if the play is valid
    public abstract boolean makeAmove(Move move) ;





    //先読みの深さを返す
    public abstract int getDepth() ;

    //return null  if the state is not terminal
    //return the reward of the (terminal) state for the given player, otherwise.
    public abstract Object isTerminal(byte player) ;

    //playerを設定
    public void setPlayer(byte player) {
        this.player = player ;
    }

    //play_outの回数を設定
    public void setPlayOutTimes(int times) {
        this.playOutTimes = times ;
    }

    //return the opponent player
    public byte getOpponent() {
        n_turn+=1;
        if (this.player == Player1) return Player2;
        else return Player1;
    }
    public int getTurn(){//何ターン目か
        return n_turn;
    }

    //勝ち・負けの報酬を設定するためのメソッド。Defaultでは何もしない。．
    //子クラスで設定する場合、その絶対値が非０で，一致しないとだめ。
    public void setWinLossValues() {  }

    //2次元配列のコピーを行うメソッド
    public static int[][] copy2Darray(int[][] matrix) {
        int[][] m = new int[matrix.length][] ;
        for (int i = 0 ; i < m.length ; i++)
            m[i] = matrix[i].clone() ;
        return m ;
    }

    //次に打てる手の場所のリストを返す。
    public List<Move> getMoves() {
        List<Move> moves = new LinkedList<>();
        for (int i = 0 ; i < board.length ; i++) {
            for (int j = 0 ; j < board[0].length ; j++) {
                if ( isValidMove(new Move(i,j), player) ) {
                    moves.add(new Move(i,j)) ;
                }
            }
        }
        return moves ;
    }

    //指定moveに対応する子供を返す。
    public State getNextState(Move move) {
        State s = getState(copy2Darray(board), player) ;
        s.makeAmove(move) ;
        s.setPlayer(getOpponent());
        return s ;
    }

    //getPlays()を呼び出して次に手を打てる場所をすべて列挙して，
    //それぞれ打った後のStateを返すメソッド.
    public List<State> getNextStates() {
        List<Move> moves = getMoves() ;
        List<State> states = new LinkedList<State>() ;
        for (Move move : moves) {
            states.add(getNextState(move)) ;
        }
        return states ;
    }


    //現在の盤面の（指定playerから見た時の）良さを返すメソッド。
    //Defaultでは、ランダムなplay_outを十分な回数行い、その勝率を返す。
    public double evaluate(byte player) {
        //終局の場合，その報酬を返す
        Object result = isTerminal(player) ;
        if (result != null)
            return (double)result ;

        //ランダムにplay outするが、各playerが１手で勝つ場合その手を選ぶ。
        this.wins = 0 ;//勝利数
        final State s = this ;//現在の状態からplay out
        Thread t = new Thread(new Runnable() {//playoutを行うスレッドを定義
            @Override
            public void run() {
                for (int i = 1 ; i <= playOutTimes ; i++) {//指定回数play out
                    Random randGen = new Random() ;//毎回のplayoutで違う乱数列を使う。
                    State state1 = s;//現在の状態
                    Object value = null ;//状態の値を覚える変数

                    //現在の状態から終局までplay out
                    while ( (value = state1.isTerminal(player)) == null) {
                        //後１手で勝負が付くかをチェック
                        State tempState = null ;
                        List<Move> moveList = state1.getMoves() ;
                        for (Move m : moveList) {
                            State s2 = state1.getNextState(m) ;//１手後の状態

                            //１手後の状態の(state1のplayerから見たときの）値
                            Object value2 = s2.isTerminal(state1.getPlayer()) ;

                            if (value2 == null) continue;//１手後が終局でなければ、他の手を見に行く
                            double d2 = (double)value2 ;//１手後が終局であれば、その値を見る。
                            if (d2 == winValue){ //&& state1.getPlayer() != s2.getPlayer()) {
                                tempState = s2 ;
                            }
                        }

                        if (tempState != null) //後１手で勝負が付くならその手を選ぶ
                            state1 = tempState ;
                        else {//さもなければ、ランダムに手を選ぶ
                            Collections.shuffle(moveList) ;
                            //Move[] moves = new Move[moveList.size()] ;
                            //moves = moveList.toArray(moves) ;
                            //Move m = moves[randGen.nextInt(moves.length)] ;
                            state1 = state1.getNextState(moveList.get(0)) ;
                        }/*
                        try { state1 = MCTStree.mctsDecision(state1,
                                "robust", -1, 5) ;}
                        catch (Exception ex) { ex.printStackTrace() ; }*/
                    }

                    double d = (double)value ;//終局の値
                    if (d == winValue && state1.getPlayer() != player) //勝ったなら
                        wins++ ;//勝利数を更新
                }
            }
        }) ;

        t.start() ;//playoutを行うスレッドを起動。
        try {
            t.join() ;//playoutスレッドの終了を待つ
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return this.wins / playOutTimes ;//勝率を返す
    }

    //print out the state
    public void printState() {
        for (int i = 0 ; i < board.length ; i++) {
            for (int j = 0 ; j < board[0].length ; j++)
                System.out.printf("%3d ", board[i][j]) ;
            System.out.println("") ;
        }
        System.out.println("") ;
    }

    //gettor methods
    public int[][] getBoard() {
        return board ;
    }

    public  void setBoard(int m,int n,int number){
        //後で直す
        this.board[m][n]=number;

    }

    public byte getPlayer() {
        return player ;
    }




}

