package org.firstinspires.ftc.teamcode.Teacher;

import org.firstinspires.ftc.teamcode.Testing.AlianceColor.AlianceColorSyncTool;

import java.util.HashMap;

public class Waypoint {
    public int id;
    public double distance;
    public double yaw;
    public double heading;
    public double shootTPS;
    public String name;
    public Waypoint(int id, double distance, double yaw, double heading, double shootTPS, String name){
        this.id = id;
        this.name = name;
        this.distance = distance;
        this.yaw = yaw;
        this.heading = heading;
        this.shootTPS = shootTPS;
    }

    public static HashMap<String, Waypoint> makeWaypoints(){
        int multiplier = 1;
        int id=24;
        if (AlianceColorSyncTool.getSelectedColor().equals("Blue")){
            multiplier = multiplier * -1;
            id = 20;
        }
        HashMap<String, Waypoint> waypoints = new HashMap<>();
        waypoints.put("humanDrop",new Waypoint(id, 154,-1 * multiplier, 0 * multiplier, 0, "humanDrop"));
        waypoints.put("ballRelease",new Waypoint(id, 68,-50 * multiplier, -4 * multiplier, 0, "ballRelease"));
        waypoints.put("longShoot", new Waypoint(id, 130, -24 * multiplier,-5, 2000, "longShoot"));
        waypoints.put("mediumShoot", new Waypoint(id, 90, 0 * multiplier, -5, 1700, "mediumShoot"));
        waypoints.put("shortShoot", new Waypoint(id, 54, 0 * multiplier,-5, 1500, "shortShoot"));
        waypoints.put("park", new Waypoint(id, 60, -34 * multiplier,6 * multiplier, 0, "park"));
        return waypoints;
    }
}
