package com.example.niaba.bluetoothlocalisator;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by niaba on 20/05/17.
 */
// parser pour recup des donner depuis le fichier de test.txt
public class TestDataParser {

    Context context;
    static InputStream inputStream;
    private static File file;


    public TestDataParser() {
    }

    public static ArrayList<Products> getProducts() {
        ArrayList<Products> products_list = new ArrayList<>();
        Products prod = null;
        String[] lines = Data.data.split("\n");

        for (String line : lines) {
            String[] split = line.split("\\|");
            prod = new Products(split[0], Integer.parseInt(split[1]),
                    split[2], Integer.parseInt(split[3]), Integer.parseInt(split[4]),
                    Integer.parseInt(split[5]));
            products_list.add(prod);
        }
        return products_list;
    }
}
