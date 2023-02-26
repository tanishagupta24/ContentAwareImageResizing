package com.carving;

import java.util.*;

class Main {

    static double[][] grid;
    static double [][] paddedGrid;// for if we do padding
    static double[][] memo;


    public static void main(String[] args) {
        double[][] arr = {{2, 1, 3, 256, 6, 4},
                          {4, 3, 2, 5, 6, 7},
                          {1, 780, 3, 9, 0, 0},
                          {5555, 67, 1, 3, 2, 1}};
        grid = arr;
        padGridHorizontal();
        fillMemoTableHorizontal();
        arr = memo;


        for(int i = 0; i < memo.length; i++) {
            System.out.println(Arrays.toString(memo[i]));
        }

        int indexVal = findMinIndexLastColumn();
        ArrayList<Integer> reversedIndexes = new ArrayList<>();
        reversedIndexes.add(indexVal);
        System.out.println(indexVal);

        for(int j= paddedGrid[0].length-2;j >= 0;j--) {
            if ((memo[indexVal-1][j] < memo[indexVal][j]) && (memo[indexVal-1][j] < memo[indexVal+1][j])) {
                reversedIndexes.add(0, indexVal-1);
                indexVal = indexVal-1;
            } else if (memo[indexVal][j] < memo[indexVal-1][j] && memo[indexVal][j] < memo[indexVal+1][j]) {
                reversedIndexes.add(0, indexVal);
            } else {
                reversedIndexes.add(0, indexVal+1);
                indexVal = indexVal+1;
            }
        }

        System.out.println(reversedIndexes);




    }

    public static void fillMemoTable(){
        for(int j=1;j<paddedGrid.length;j++){
            for(int i=1;i<paddedGrid[0].length-1;i++){
                int k=Math.min((int)memo[j-1][i-1], (int)memo[j-1][i]);
                k=Math.min(k, (int)memo[j-1][i+1]);
                memo[j][i]+=k;
            }
        }
    }

    public static void padGrid(){

        paddedGrid = new double[grid.length][grid[0].length+2];
        memo = new double[grid.length][grid[0].length+2];
        for (int i = 0; i<grid.length; i++){
            paddedGrid[i][0] = Integer.MAX_VALUE;
            paddedGrid[i][grid[0].length+1] = Integer.MAX_VALUE;
            memo[i][0] = Integer.MAX_VALUE;
            memo[i][grid[0].length+1] = Integer.MAX_VALUE;
            for(int j=1;j<memo[0].length-1;j++){
                memo[i][j]=grid[i][j-1];
                paddedGrid[i][j]=grid[i][j-1];
            }
        }


    }

    public static int findMinIndexLastRow() {

        double[] lastRow = memo[memo.length-1];
        int lowestIndex = 0;
        double lowest = Integer.MAX_VALUE;
        for (int i = 0; i < lastRow.length; i++) {
            if (lastRow[i] < lowest) {
                lowest = lastRow[i];
                lowestIndex = i;
            }
        }
        return lowestIndex;
    }

    public static void padGridHorizontal(){
        paddedGrid = new double[grid.length+2][grid[0].length];
        memo = new double[grid.length+2][grid[0].length];
        for (int col = 0; col<grid[0].length; col++){
            paddedGrid[0][col] = Integer.MAX_VALUE;
            paddedGrid[grid.length+1][col] = Integer.MAX_VALUE;
            memo[0][col] = Integer.MAX_VALUE;
            memo[grid.length+1][col] = Integer.MAX_VALUE;
            for(int row=1;row<memo.length-1;row++){
                memo[row][col]=grid[row-1][col];
                paddedGrid[row][col]=grid[row-1][col];
            }

        }

    }

    public static void fillMemoTableHorizontal(){
        for(int col=1;col<paddedGrid[0].length;col++){
            for(int row=1;row<paddedGrid.length-1;row++){
                int k=Math.min((int)memo[row-1][col-1], (int)memo[row][col-1]);
                k=Math.min(k, (int)memo[row+1][col-1]);
                memo[row][col]+=k;
            }
        }
    }

    public static int findMinIndexLastColumn() {

        double[] lastColumn = new double[memo.length];
        for(int row = 0; row < memo.length; row++) {
            lastColumn[row] = memo[row][memo[0].length-1];
        }
        int lowestIndex = 0;
        double lowest = Integer.MAX_VALUE;
        for (int i = 0; i < lastColumn.length; i++) {
            if (lastColumn[i] < lowest) {
                lowest = lastColumn[i];
                lowestIndex = i;
            }
        }
        return lowestIndex;
    }

}