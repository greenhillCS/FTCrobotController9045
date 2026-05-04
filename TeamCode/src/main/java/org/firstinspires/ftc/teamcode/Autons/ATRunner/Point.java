package org.firstinspires.ftc.teamcode.Autons.ATRunner;

public class Point {
    private double range = 0;
    private double yaw = 0;
    private double heading = 0;
    private int id = 0;
    private double time = 0;
    private double speed = 1;
    public Point(double range, double yaw, double heading, int id, double time, double speed){
        this.range = range;
        this.yaw = yaw;
        this.heading = heading;
        this.id = id;
        this.time = time;
        this.speed = speed;
    }
    public Point(double range, double yaw, double heading, int id){
        this.range = range;
        this.yaw = yaw;
        this.heading = heading;
        this.id = id;
        this.time = 0;
        this.speed = 1;
    }
    public double getRange(){
        return range;
    }
    public double getYaw(){
        return yaw;
    }
    public double getHeading(){
        return heading;
    }
    public int getId(){
        return id;
    }
    public double getTime(){return time;}
    public double getSpeed(){return speed;}
    public String get(){return "[Range: " + range + ", Heading: " + heading + ", Yaw: " + yaw + ", ID" + id + "]";}
}
