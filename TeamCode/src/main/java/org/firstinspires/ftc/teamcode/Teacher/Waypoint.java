package org.firstinspires.ftc.teamcode.Teacher;

public class Waypoint {
    public int id;
    public double distance;
    public double yaw;
    public double heading;
    public double shootTPS;
    public Waypoint(int id, double distance, double yaw, double heading, double shootTPS){
        this.id = id;
        this.distance = distance;
        this.yaw = yaw;
        this.heading = heading;
        this.shootTPS = shootTPS;
    }
}
