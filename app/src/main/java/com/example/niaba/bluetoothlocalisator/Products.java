package com.example.niaba.bluetoothlocalisator;

import android.util.Log;

/**
 * Created by niaba on 20/05/17.
 */
// model dans lequelle on recupere les données extraites du txt
class Products {
    private String STORE_KEY ;
    private int HYP_GRP_CLASS_KEY;
    private String HYP_GRP_CLASS_DESC;
    private int ABSCISSA;
    private int ORDINATE;
    private int HEIGHT;


    public Products(String STORE_KEY, int HYP_GRP_CLASS_KEY, String HYP_GRP_CLASS_DESC, int ABSCISSA, int ORDINATE, int HEIGHT) {
        this.STORE_KEY = STORE_KEY;
        this.HYP_GRP_CLASS_KEY = HYP_GRP_CLASS_KEY;
        this.HYP_GRP_CLASS_DESC = HYP_GRP_CLASS_DESC;
        this.ABSCISSA = ABSCISSA;
        this.ORDINATE = ORDINATE;
        this.HEIGHT = HEIGHT;
    }

    public int getHYP_GRP_CLASS_KEY() {

        return HYP_GRP_CLASS_KEY;
    }

    public void setHYP_GRP_CLASS_KEY(int HYP_GRP_CLASS_KEY) {
        this.HYP_GRP_CLASS_KEY = HYP_GRP_CLASS_KEY;
    }

    public String getHYP_GRP_CLASS_DESC() {
        return HYP_GRP_CLASS_DESC;
    }

    public void setHYP_GRP_CLASS_DESC(String HYP_GRP_CLASS_DESC) {
        this.HYP_GRP_CLASS_DESC = HYP_GRP_CLASS_DESC;
    }

    public int getABSCISSA() {
        return ABSCISSA;
    }

    public void setABSCISSA(int ABSCISSA) {
        this.ABSCISSA = ABSCISSA;
    }

    public int getORDINATE() {
        return ORDINATE;
    }

    public void setORDINATE(int ORDINATE) {
        this.ORDINATE = ORDINATE;
    }

    public int getHEIGHT() {
        return HEIGHT;
    }

    public void setHEIGHT(int HEIGHT) {
        this.HEIGHT = HEIGHT;
    }

    public String getSTORE_KEY() {

        return STORE_KEY;
    }

    public void setSTORE_KEY(String STORE_KEY) {
        this.STORE_KEY = STORE_KEY;
    }

    @Override
    public boolean equals(Object obj) {

        Log.e("Products", "equals");
        if(obj instanceof Products) {
            return (this.HYP_GRP_CLASS_DESC.equals(((Products) obj).HYP_GRP_CLASS_DESC));
        } else if (obj instanceof String) {
            return (this.HYP_GRP_CLASS_DESC.contains((CharSequence) obj));
        }

        return super.equals(obj);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("STORE_KEY ").append(this.STORE_KEY).append("\n")
                    .append("HYP_GRP_CLASS_KEY ").append(this.HYP_GRP_CLASS_KEY).append("\n")
                    .append("HYP_GRP_CLASS_DESC ").append(this.HYP_GRP_CLASS_DESC).append("\n")
                    .append("ABSCISSA ").append(this.ABSCISSA).append("\n")
                    .append("ORDINATE ").append(this.ORDINATE).append("\n")
                    .append("HEIGHT ").append(this.HEIGHT).append("\n");

        return stringBuilder.toString();



        /*private int HYP_GRP_CLASS_KEY;
        private String HYP_GRP_CLASS_DESC;
        private int ABSCISSA;
        private int ORDINATE;
        private int HEIGHT;

        */
    }
}
