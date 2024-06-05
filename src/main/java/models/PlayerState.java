package models;

public class PlayerState {
    private String playerName;
    private int x;
    private int y;
    private int diameter;
    private double speed;
    private String color;
    private boolean isFollowing;
    private String parentName;
    private static int splitIdCounter = 0;
    private boolean isSplitBall;

    public PlayerState(String playerName, int x, int y, int diameter, double speed, String color) {
        this.playerName = playerName;
        this.x = x;
        this.y = y;
        this.diameter = diameter;
        this.speed = speed;
        this.color = color;
        this.isSplitBall = false;
    }

    public PlayerState(String playerName, int x, int y, int diameter, double speed, String color, boolean isSplitBall) {
        this.playerName = playerName;
        this.x = x;
        this.y = y;
        this.diameter = diameter;
        this.speed = speed;
        this.color = color;
        this.isSplitBall = isSplitBall;
    }

    //getters et setters
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getDiameter() {
        return diameter;
    }

    public void setDiameter(int d){
        this.diameter = d;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public String getColor(){
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isFollowing() {
        return isFollowing;
    }
    
    public void setFollowing(boolean following) {
        isFollowing = following;
    }
    
    public String getParentName() {
        return parentName;
    }
    
    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public static String getNextSplitName(String baseName) {
        return baseName + "_split" + (splitIdCounter++);
    }

    public boolean isSplitBall() {
        return isSplitBall;
    }

    public void setSplitBall(boolean splitBall) {
        isSplitBall = splitBall;
    }
}
