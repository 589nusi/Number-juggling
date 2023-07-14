package com.example.number_juggling;

public class StateTTT extends State  {

    public int number;
    public StateTTT() throws Exception {
        this.board = new int[3][3] ;
    }

    public StateTTT(int[][] board, byte player) throws Exception {
        this.board = board ;
        this.player = player ;
    }

    //指定のboardとplayerからstateを作成して返す。
    //This is a factory method.
    @Override
    public State getState(int[][] board, byte player) {
        State s = null ;
        try {
            s = new StateTTT(board, player) ;
        } catch (Exception ex) {
            ex.printStackTrace() ;
        }
        return s ;
    }



    //指定の場所で指定のplayerが手を打てるかを判定する。
    @Override
    public boolean isValidMove(Move move, byte player) {
       return true;
    }



    public boolean makeAmove(Move move) {
        int r = move.getRow(), c = move.getCol() ;
        if (r < 0 || r >= board.length || c < 0 || c >= board[0].length)
            return false ;
        else {
            this.board[r][c] = number ;//数字を格納
            return true ;
        }
    }


    @Override
    public Object isTerminal(byte player) {
        int i, j ;
        int match_number;//この数字と同じ数字か判断


        //check rows
        for (i = 0 ; i < board.length ; i++) {
            match_number=board[i][0];

            for (j = 0; j < board[0].length ; j++)
                if ( board[i][j] != match_number) break ;
            if (j == board[0].length) return winValue ;

            for (j = 0; j < board[0].length ; j++)
                if ( board[i][j] != match_number) break ;
            if (j == board[0].length) return lossValue ;
        }

        //check columns
        for (i = 0 ; i < board[0].length ; i++) {
            match_number=board[0][i];
            for (j = 0 ; j < board.length ; j++)
                if (board[j][i] != match_number) break ;
            if (j == board.length) return winValue ;
            for (j = 0 ; j < board.length ; j++)
                if (board[j][i] != -match_number) break ;
            if (j == board.length) return lossValue ;
        }

        //check the main diagonal
        match_number=board[0][0];
        for (j = 0 ; j < board.length ; j++)
            if (board[j][j] != match_number) break;
        if (j == board.length) return winValue ;
        for (j = 0 ; j < board.length ; j++)
            if (board[j][j] != match_number) break;
        if (j == board.length) return lossValue ;

        //check the secondary diagonal
        match_number=board[0][2];
        for (j = 0 ; j < board.length ; j++)
            if (board[j][board.length - 1 - j] != match_number) break;
        if (j == board.length) return winValue ;
        for (j = 0 ; j < board.length ; j++)
            if (board[j][board.length - 1 - j] != match_number) break;
        if (j == board.length) return lossValue ;

        //check if no more move is possible
        /*
        for (i = 0 ; i < board.length ; i++) {
            for (j = 0; j < board[0].length ; j++)
                if (board[i][j] == 0) break ;
            if (j < board[0].length) break ;
        }
        if (i == board.length) return 0.0 ;*/

        return null ;
    }


    @Override
    public double evaluate(byte player) {
        //終局の場合，その報酬を返す
        //評価関数のため、後で変更
        Object result = isTerminal(player) ;
        if (result != null)
            return (double)result ;

        //終局ではない場合，近似値を返す
        byte r = player ;
        int[] counter = new int[2] ;
        for (int k = 0 ; k < 2 ; k++) {
            int i , j , numberOfzero ;

            //check hopeful rows for the opponent of r
            for (i = 0 ; i < board.length ; i++) {
                numberOfzero = 0 ;
                for (j = 0; j < board[0].length ; j++) {
                    if (board[i][j] == r) break;
                    else if (board[i][j] == 0) numberOfzero++ ;
                }
                if (j == board[0].length) {
                    if (numberOfzero == 1)
                        counter[k] += 9 ;
                    else
                        counter[k]++ ;
                }
            }

            //check hopeful columns for the opponent of r
            for (i = 0 ; i < board[0].length ; i++) {
                numberOfzero = 0 ;
                for (j = 0; j < board.length ; j++) {
                    if ( board[j][i] == r) break ;
                    else if (board[j][i] == 0) numberOfzero++ ;
                }
                if (j == board.length) {
                    if (numberOfzero == 1)
                        counter[k] += 9 ;
                    else
                        counter[k]++ ;
                }
            }

            //check if the main diagonal is hopeful for the opponent of r
            numberOfzero = 0 ;
            for (j = 0 ; j < board.length ; j++) {
                if (board[j][j] == r) break;
                else if (board[j][j] == 0) numberOfzero++ ;
            }
            if (j == board.length) {
                if (numberOfzero == 1)
                    counter[k] += 9 ;
                else
                    counter[k]++ ;
            }

            //check if the secondary diagonal is hopeful for the opponent of r
            numberOfzero = 0 ;
            for (j = 0 ; j < board.length ; j++) {
                if (board[j][board.length - 1 - j] == r) break;
                else if (board[j][board.length - 1 - j] == 0) numberOfzero++ ;
            }
            if (j == board.length) {
                if (numberOfzero == 1)
                    counter[k] += 9 ;
                else
                    counter[k]++ ;
            }

            r = (byte)-player ;
        }

        return (counter[1] - counter[0]) / 10.0 ;
    }



    @Override
    public int getDepth() {
        return 2 ;
    }

    public void setNumber(int number){
        this.number=number;
    }//クリックしたマスはどの数字になったか

    public int getnumber(){
        return number;
    }





}
