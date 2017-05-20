package com.example.niaba.bluetoothlocalisator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by niaba on 20/05/17.
 */
// parser pour recup des donner depuis le fichier de test.txt
public class TestDataParser {
    private final File test_file = new File("STORE_CATERGORY_LOCALIZATION.txt");
    private FileInputStream fis = null;

    public ArrayList<Products> getProducts() {
        ArrayList<Products> products_list = new ArrayList<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(test_file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line;
        try {
            Products prod = null;
            while ((line = br.readLine()) != null){
                String[] split = line.split("|") ;
                prod = new Products(split[0], Integer.parseInt(split[1]),
                        split[2], Integer.parseInt(split[3]),Integer.parseInt(split[4]),
                        Integer.parseInt(split[5]));
                products_list.add(prod);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return products_list;
    }

}
