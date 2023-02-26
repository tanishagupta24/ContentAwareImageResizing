package com.carving;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class SeamCarver {
    private Picture picture; //the picture of the seamcarver object
    public static double[][] grid;
    public static double [][] paddedGrid;
    public static double[][] memo;

    public SeamCarver(Picture picture) { // create a seam carver object based on the given picture
        this.picture = picture;
    } //constructor
    public Picture picture() { //returns the current picture
        return picture;
    }
    public int width() {
        return picture.width();
    } // width of current picture
    public int height() { // height of current picture
        return picture.height();
    }
    public double energy(int x, int y)  { //calculates energy of a particular col, row location
        //if(x == 0 && y == 0)
        double energy;
        Color color_x_1 = picture.get((x+1)%width(), y);
        Color color_x_2 = picture.get(((x-1)+width())%width(), y);
        Color color_y_1 = picture.get(x, (y+1)%height());
        Color color_y_2 = picture.get(x, ((y-1)+height())%height());
        double delta_x_r = Math.abs(color_x_1.getRed()-color_x_2.getRed());
        double delta_x_g = Math.abs(color_x_1.getGreen()-color_x_2.getGreen());
        double delta_x_b = Math.abs(color_x_1.getBlue()-color_x_2.getBlue());
        double delta_y_r = Math.abs(color_y_1.getRed()-color_y_2.getRed());
        double delta_y_g = Math.abs(color_y_1.getGreen()-color_y_2.getGreen());
        double delta_y_b = Math.abs(color_y_1.getBlue()-color_y_2.getBlue());
        energy = delta_x_r*delta_x_r + delta_x_g*delta_x_g + delta_x_b*delta_x_b + delta_y_r*delta_y_r + delta_y_g*delta_y_g + delta_y_b*delta_y_b;
        return energy;

    }

    public double[][] calculateEnergyTable() { // calculates energy for the entire images
        double[][] energyTable = new double[height()][width()]; //this is the energyTable
        //loops through col and then row in order to provide correct input to the energy function
        for(int col = 0; col < width(); col++) {
            for(int row = 0; row < height(); row++) {
                energyTable[row][col] = energy(col, row);
                //System.out.println(energyTable[row][col]);
            }
        }
        return energyTable;
    }
    public int[] findHorizontalSeam() { // sequence of indices for horizontal seam
        double[][] arr = calculateEnergyTable();
        grid = arr; //stores the energy table values
        padGridHorizontal(); //pads the grid
        fillMemoTableHorizontal(); //fills the memo table with correct values
        arr = memo; //reassignms arr to the memo table
        double[][] memo = new double[grid.length][grid[0].length]; // creates a new array that will be filled with memo values without padding

        for (int row = 1; row < arr.length-1; row++) {
            for (int col = 0; col < arr[0].length; col++) {
                memo[row-1][col] = arr[row][col];
            }
        } // puts the arr values into memo wihthout the padding

        //for(int i = 0; i < memo.length; i++) {
        //System.out.println(Arrays.toString(memo[i]));
        //}

        int indexVal = findMinIndexLastColumn();
        ArrayList<Integer> reversedIndexes = new ArrayList<>();
        reversedIndexes.add(indexVal);
        //System.out.println(indexVal);
        //creates arraylist of indexes to be returned

        //this for loop finds the minimum energy by checking values in the memo table
        for(int j= paddedGrid[0].length-2;j >= 0;j--) {
            if (indexVal == 0) {
                indexVal = 1;
                reversedIndexes.add(0, indexVal);
            } else if (indexVal >= height()-1) {
                indexVal = height()-1;
                reversedIndexes.add(0, indexVal);
            } else if ((memo[indexVal-1][j] < memo[indexVal][j]) && (memo[indexVal-1][j] < memo[indexVal+1][j])) {
                reversedIndexes.add(0, indexVal-1);
                indexVal = indexVal-1;
            } else if (memo[indexVal][j] < memo[indexVal-1][j] && memo[indexVal][j] < memo[indexVal+1][j]) {
                reversedIndexes.add(0, indexVal);
            } else {
                reversedIndexes.add(0, indexVal+1);
                indexVal = indexVal+1;
            }
        }

        //converts the aforementioned arraylist to an array so it can be returned
        int[] arrToReturn = new int[reversedIndexes.size()];
        for(int i = 0; i < reversedIndexes.size(); i++) {
            arrToReturn[i] = reversedIndexes.get(i);
        }
        return arrToReturn; //returns the indexes
    }

    public int[] findVerticalSeam() { //very similar to horizontal but for vertical
        double[][] arr = calculateEnergyTable();
        grid = arr;
        padGridVertical();
        fillMemoTableVert();
        arr = memo;
        double[][] memo = new double[grid.length][grid[0].length];
        //System.out.println("Array is : " + Arrays.deepToString(arr));
        for (int i = 0; i < arr.length; i++) {
            for (int j = 1; j < arr[0].length - 1; j++) {
                memo[i][j - 1] = arr[i][j];
            }
        }
        //System.out.println("Memo is: " + Arrays.deepToString(memo));

        //for(int i = 0; i < memo.length; i++) {
            //System.out.println(Arrays.toString(memo[i]));
        //}

        int indexVal = findMinIndexLastRow();
        ArrayList<Integer> reversedIndexes = new ArrayList<>();
        reversedIndexes.add(indexVal);


        for(int j= paddedGrid.length-1;j >= 1;j--) {
            if (indexVal == 0) {
                indexVal = 1;
                reversedIndexes.add(0, indexVal);
            } else if (indexVal >= width()-1) {
                indexVal = width()-1;
                reversedIndexes.add(0, indexVal);
            } else if ((memo[j][indexVal-1] < memo[j][indexVal]) && (memo[j][indexVal-1] < memo[j][indexVal+1])) {
                reversedIndexes.add(0, indexVal-1);
                indexVal = indexVal-1;
            } else if (memo[j][indexVal] < memo[j][indexVal-1] && memo[j][indexVal] < memo[j][indexVal+1]) {
                reversedIndexes.add(0, indexVal);
            } else {
                reversedIndexes.add(0, indexVal+1);
                indexVal = indexVal+1;
            }
        }

        int[] arrToReturn = new int[reversedIndexes.size()];
        for(int i = 0; i < reversedIndexes.size(); i++) {
            arrToReturn[i] = reversedIndexes.get(i);
        }
        return arrToReturn;
    }

    public int findMinIndexLastRow() { //finds the minimum index of the last row and is the starting position for the vertical seam

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

    public int findMinIndexLastColumn() { //finds the minimum index of the last column and is the starting position for the horizontal seam

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

    public static void fillMemoTableVert(){ //fills the memo table for the vertical direction
        for(int j=1;j<paddedGrid.length;j++){
            for(int i=1;i<paddedGrid[0].length-1;i++){
                int k=Math.min((int)memo[j-1][i-1], (int)memo[j-1][i]);
                k=Math.min(k, (int)memo[j-1][i+1]);
                memo[j][i]+=k;
            }
        }
    }

    public static void fillMemoTableHorizontal(){ //fills the memo table for the horizontal direction
        for(int col=1;col<paddedGrid[0].length;col++){
            for(int row=1;row<paddedGrid.length-1;row++){
                int k=Math.min((int)memo[row-1][col-1], (int)memo[row][col-1]);
                k=Math.min(k, (int)memo[row+1][col-1]);
                memo[row][col]+=k;
            }
        }
    }



    public static void padGridVertical(){ //pads the grid on left and right for vertical deletion

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

    public static void padGridHorizontal(){ //pads the grid on the top and bottom for horizontal deletion
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
    public void removeHorizontalSeam(int[] seam) { // remove horizontal seam from current picture
        //color the seam black for testing purposes mainly...
        for(int i = 0; i < seam.length; i++) {
            picture.set(i, seam[i], new Color(0, 0, 0));
        }

        //actually remove the seam in a new picture
        Picture target = new Picture(width(), height()-1);
        for(int col = 0; col < target.width(); col++) {
            for(int row = 0; row < target.height()-1; row++) {
                if(row < seam[col]) {
                    target.set(col, row, picture.get(col, row));
                } else {
                    target.set(col, row, picture.get(col, row+1));
                }
            }
        }
        //copies new picture into original picture
        picture = new Picture(target.width(), target.height());
        for (int col = 0; col < width(); col++) {
            for (int row = 0; row < height(); row++) {
                picture.set(col, row, target.get(col, row));
            }
        }
        //System.out.println(width());

    }
    public void removeVerticalSeam(int[] seam) { // remove vertical seam from current picture (functions very similar)
        for(int i = 0; i < seam.length; i++) {
            picture.set(seam[i], i, new Color(0, 0, 0));
        }

        Picture target = new Picture(width()-1, height());
        for(int row = 0; row < target.height(); row++) {
            for(int col = 0; col < target.width()-1; col++) {
                if(col < seam[row]) {
                    target.set(col, row, picture.get(col, row));
                } else {
                    target.set(col, row, picture.get(col+1, row));
                }
            }
        }
        picture = new Picture(target.width(), target.height());
        for (int col = 0; col < width(); col++) {
           for (int row = 0; row < height(); row++) {
               picture.set(col, row, target.get(col, row));
           }
        }
        //System.out.println(width());
    }

    public static void main(String args[]) { //user interface
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the file path: ");
        String filePath = scanner.nextLine();
        Picture picture = new Picture(filePath);

        SeamCarver meep = new SeamCarver(picture);
        meep.picture().show();

        System.out.println("Would you like to remove horizontally or vertically? (1/2)");
        int choice = scanner.nextInt();

        if(choice == 1) {
            System.out.println("Enter the number of seams you would like to be removed: ");
            int numSeams = scanner.nextInt();
            for(int i = 0; i < numSeams; i++) {
                meep.removeHorizontalSeam(meep.findHorizontalSeam());
            }
            meep.picture().show();

        } else {
            System.out.println("Enter the number of seams you would like to be removed: ");
            int numSeams = scanner.nextInt();
            for(int i = 0; i < numSeams; i++) {
                meep.removeVerticalSeam(meep.findVerticalSeam());
            }
            meep.picture().show();
        }
    }
}