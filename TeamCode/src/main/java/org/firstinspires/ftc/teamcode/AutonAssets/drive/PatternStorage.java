package org.firstinspires.ftc.teamcode.AutonAssets.drive;

public class PatternStorage {
    static String id = "";
    public static void store(String pattern){
        id = pattern;
    }
    public static String get(){
        return id;
    }

}