package net.nightpool.bukkit.nightutils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

//Simple, bare-bones cuboid class. Original idea from bergerkiller on the bukkit forumns (http://forums.bukkit.org/threads/region-general-api-for-creating-cuboids.34644/).
public class Cuboid {
    public Cuboid(Location point1, Location point2) {
        xMin = Math.min(point1.getBlockX(), point2.getBlockX());
        xMax = Math.max(point1.getBlockX(), point2.getBlockX());
        yMin = Math.min(point1.getBlockY(), point2.getBlockY());
        yMax = Math.max(point1.getBlockY(), point2.getBlockY());
        zMin = Math.min(point1.getBlockZ(), point2.getBlockZ());
        zMax = Math.max(point1.getBlockZ(), point2.getBlockZ());
        world = point1.getWorld();
    }
    
    public Cuboid(int xMin, int xMax, int yMin, int yMax, int zMin,int zMax, World world){
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        this.zMin = zMin;
        this.zMax = zMax;
        this.world = world;
    }

    public int xMin, xMax, yMin, yMax, zMin, zMax;
    public World world;

    public boolean contains(Location loc) {
        if (loc.getWorld() != this.world) return false;
        if (loc.getBlockX() < xMin) return false;
        if (loc.getBlockX() > xMax) return false;
        if (loc.getBlockY() < yMin) return false;
        if (loc.getBlockY() > yMax) return false;
        if (loc.getBlockZ() < zMin) return false;
        if (loc.getBlockZ() > zMax) return false;
        return true;
    }
    
    public String serialize(){
        return ""+xMin+","+zMin+","+yMin+":"+xMax+","+zMax+","+yMax+"@"+world.getName();
    }
    
    @Override
    public String toString(){
        return serialize();
    }
    
    public static Cuboid deserialize(JavaPlugin p, String s){
        if(s == null){
            return null;
        }
        String s1=s.split("@")[0];
        World world;
        
        try{
            world = p.getServer().getWorld(s.split("@")[1]);
        }catch (ArrayIndexOutOfBoundsException e){
            world=p.getServer().getWorlds().get(0);
        }
        String s2=s1.split(":")[0];

        int xMin, xMax, yMin, yMax, zMin, zMax;
        String[] sR = s2.split(",");
        
        xMin = new Integer(sR[0]);
        yMin = new Integer(sR[1]);
        zMin = new Integer(sR[2]);
        
        if(s1.contains(":")){
            String s3 = s1.split(":")[1];

            String[] sR1 = s3.split(",");
            
            xMax = new Integer(sR1[0]);
            yMax = new Integer(sR1[1]);
            zMax = new Integer(sR1[2]);
        } else{
            xMax = xMin;
            yMax = yMin;
            zMax = zMin;
        }
        return new Cuboid(xMin,  xMax,  yMin,  yMax,  zMin, zMax, world);
    }
    
    public int getXWidth() {
        return xMax - xMin;
    }
    public int getZWidth() {
        return zMax - zMin;
    }
    public int getHeight() {
        return yMax - yMin;
    }
    public int getArea() {
        return getHeight() * getXWidth() * getZWidth();
    }
}