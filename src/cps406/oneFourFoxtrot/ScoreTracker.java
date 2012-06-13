/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cps406.oneFourFoxtrot;

public class ScoreTracker {
    
    int score, prevScore;
    long previous;
    final long INTERVAL = 5;
    final int POINTS_PER_KILL = 200;
    int highScore;
    
    public ScoreTracker(){
        score = 0;
        prevScore = 0;
        highScore = 0;
    }
    
    public void start(){
        previous = System.currentTimeMillis();
    }
    
    public void update(){
        long current = System.currentTimeMillis();
        
        if ((current - previous)/1000 == INTERVAL) {
            score += 100;
            previous = current;
        }
    }
    
    public void updateKill(){
        score += POINTS_PER_KILL;
    }
    public void updateKill(int numKills){
        score += POINTS_PER_KILL * numKills;
    }
    
    public int getScore(){
        return score;
    }
    public void resetScore()
    {
    	highScore = score > highScore ? score : highScore;
        prevScore = score;
    	score = 0;
    }
    public int getHighScore()
    {
    	return highScore;
    }
}
