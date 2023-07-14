package com.example.number_juggling;

import java.util.Random;
import java.util.Arrays;

public class Generate_Rand_array {

    Random rand=new Random();

    private static int count=0;
    public int[][] fisher_yates_alg(int[][] array){

        boolean isValid = false;
        int i,j;
        while (!isValid) {
            isValid = true;

        for (i = 0; i < array.length; i++) {
            // 1〜5の数字をランダムに選択して格納
            int[] row = {1, 2, 3, 4, 5, 6, 7};
            for (j = row.length - 1; j > 0; j--) {
                int index = rand.nextInt(j + 1);
                int temp = row[index];
                row[index] = row[j];
                row[j] = temp;
            }

            array[i] = row;
        }


        int first_array[][]= new int[3][3];
        int [] num_count={0,0,0,0,0,0,0};
        count=0;
        for(i=0;i<first_array.length;i++){
            for(j=0;j<first_array.length;j++){
                first_array[i][j]=array[count][0];//初期盤面を取得
                num_count[array[count][0]-1]=num_count[array[count][0]-1]+1;//初期盤面の数字の数をカウント
                count++;
            }
        }


        count=0;
        int sub_count=0;

        for(i=0;i< num_count.length;i++){
            if(num_count[i]>=2) sub_count++;
            if(num_count[i]>=3||sub_count>=3){
                    isValid=false;
                    break;
            }
        }

        /*
        int [] num_count={0,0,0,0,0,0,0};
        for(i=0;i< array.length;i++){
              num_count[array[i][0]-1] =num_count[array[i][0]-1]+1;
              first_array[i][i]= array[i][0];//直す
        }*/



        //縦横のチェック
            // if (hasDuplicate(first_array)) isValid = false;
            // 斜めのチェック
            //if (hasDiagonalDuplicate(array)) isValid = false;



    }

        return array;
    }
    //縦および、横のチェックする関数
    public static boolean hasDuplicate(int[][] arr) {
        count=0;

        int i,j,c_r_count=0;
        //check row
        for ( i = 0; i < arr.length - 1; i++){
            for( j = 0; j > arr[0].length - 1; j++){
                if (arr[i][j] == arr[i][j+1]) {
                    c_r_count++;//配列の大きさが一定以上で使用
                    //リーチのカウント
                    if(c_r_count>= arr.length-2) count++;
                }
            }

        }
        //リーチが２つ以上で再生成
        if(count>=2) return true;

        //check col
        c_r_count=0;
        count=0;
        for(i=0;i<arr[0].length-1;i++){
            for(j=0;j< arr.length-1;j++){
                if(arr[j][i]==arr[j+1][i]){
                    c_r_count++;//配列の大きさが一定以上で使用
                    //リーチのカウント
                    if(c_r_count>= arr[0].length-2) count++;
                }
            }
        }
        //リーチが２つ以上で再生成
        if(count>=2) return true;

        //リーチが一つ以下で生成なし
        return false;
    }

    // 配列内の斜めに重複する要素があるかどうかを判定するメソッド
    public static boolean hasDiagonalDuplicate(int[][] arr) {
        int len = arr.length;

        count=0;
        // 左上から右下への対角線チェック
        for(int i=0;i<len-1;i=i++){
            if(arr[i][i]==arr[i+1][i+1]) count++;
        }
        //右上から左下
        for(int i=0;i<len-1;i++){
            if(arr[i][len-i-1]==arr[i+1][len-i-2])count++;
        }


        if(count>=2) return true;//斜めが両方リーチの場合再生成

        //片方以下がリーチなら生成なし
        return false;
    }

}
