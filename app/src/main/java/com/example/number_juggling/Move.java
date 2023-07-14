package com.example.number_juggling;

public class Move {
    //この手で打った場所（行と列の番号）
    private int row, col ;

    public Move(int row, int col) {
        this.row = row ;
        this.col = col ;
    }

    public int getRow() {
        return row ;
    }

    public int getCol() {
        return col ;
    }

    //引数：ゲームボードの列数。
    //機能：この手をユニークの整数に変換して返す。
    public int hash(int n_cols) {
        return row * n_cols + col ;
    }
}