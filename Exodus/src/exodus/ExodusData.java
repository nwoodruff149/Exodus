/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exodus;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 12nwoodruff
 */

public class ExodusData {
    private float difficulty;
    private Island[] islands = new Island[3];
    private RocketBase base;
    private int worldPopulation;
    private float worldTime;
    private float worldEndTime;
    private float climateChange;
    private float yearLength;
    
    List<String[]> history = new ArrayList<String[]>();

    public float getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(float difficulty) {
        this.difficulty = difficulty;
    }

    public Island[] getIslands() {
        return islands;
    }

    public void setIslands(Island[] islands) {
        this.islands = islands;
    }

    public RocketBase getBase() {
        return base;
    }

    public void setBase(RocketBase base) {
        this.base = base;
    }

    public int getWorldPopulation() {
        return worldPopulation;
    }

    public void setWorldPopulation(int worldPopulation) {
        this.worldPopulation = worldPopulation;
    }

    public float getWorldTime() {
        return worldTime;
    }

    public void setWorldTime(float worldTime) {
        this.worldTime = worldTime;
    }

    public float getWorldEndTime() {
        return worldEndTime;
    }

    public void setWorldEndTime(float worldEndTime) {
        this.worldEndTime = worldEndTime;
    }

    public float getClimateChange() {
        return climateChange;
    }

    public void setClimateChange(float climateChange) {
        this.climateChange = climateChange;
    }

    public float getYearLength() {
        return yearLength;
    }

    public void setYearLength(float yearLength) {
        this.yearLength = yearLength;
    }

    public List<String[]> getHistory() {
        return history;
    }

    public void setHistory(List<String[]> history) {
        this.history = history;
    }
    
    
    
    public ExodusData(float difficulty, float numberOfMinutes)
    {
        worldPopulation = 0;
        for(int i = 0; i < 3; i++)
        {
            islands[i] = new Island(difficulty);
            worldPopulation += islands[i].getPopulation();
        }
        base  = new RocketBase(difficulty);
        worldTime = 0;
        yearLength = numberOfMinutes * 60 / 100;
        worldEndTime = 100;
        climateChange = 0;
    }
    public void nextYear()
    {
        printYearlySummary();
        climateChange += 0.01f * (Math.random() - 0.5);
        //occurs every year
        for(int i = 0; i < 3; i++)
        {
            islands[i].collectTax();
            islands[i].updatePopulation();
            islands[i].setClimateChange(climateChange);
        }
        worldTime++;
        climateChange += 1 / worldEndTime;
    }
    public void movePopulation(int fromIslandIndex, int toIslandIndex, float size) throws Exception
    {
        if(islands[fromIslandIndex].getPopulation() > size && size > 0)
        {
            islands[fromIslandIndex].changePopulation(-size);
            islands[toIslandIndex].changePopulation(size);
            islands[fromIslandIndex].pay(10 / islands[fromIslandIndex].getGdpPerCapita());
        }
        else
        {
            throw new Exception();
        }
    }
    public void investIn(float amount, int choice)
    {
        
        for(Island i : islands)
        {
            i.pay(amount / 3);
            switch(choice)
            {
                case 0:
                    i.setEnergySecurity(i.addToClamped(i.getEnergySecurity(), 0.0002f * amount));
                    i.setJobSecurity(i.addToClamped(i.getJobSecurity(), -0.000007f * amount));
                    break;
                case 1:
                    i.setEnergySecurity(i.addToClamped(i.getEnergySecurity(), -0.0002f * amount));
                    i.setJobSecurity(i.addToClamped(i.getJobSecurity(), 0.0004f * amount));
                    break;
                case 2:
                    i.setFoodSecurity(i.addToClamped(i.getFoodSecurity(), 0.0003f * amount));
                    i.setJobSecurity(i.addToClamped(i.getJobSecurity(), 0.0005f * amount));
                    i.setEnergySecurity(i.addToClamped(i.getEnergySecurity(), -0.0002f * amount));
                    break;
                case 3:
                    i.setEnergySecurity(i.addToClamped(i.getEnergySecurity(), 0.0004f * amount));
                    i.setJobSecurity(i.addToClamped(i.getJobSecurity(), -0.0006f * amount));
                    i.setFoodSecurity(i.addToClamped(i.getFoodSecurity(), -0.0002f * amount));
                    break;
                case 4:
                    break;
                case 5:
                    i.setHappiness(i.addToClamped(i.getHappiness(), 0.005f * amount));
                    break;
                case 6:
                    i.setHappiness(i.addToClamped(i.getHappiness(), -0.0005f * amount));
                    i.setGdpPerCapita(i.addToClamped(i.getGdpPerCapita(), 0.005f * amount));
                    break;
            }
        }
    }
    public String rollTheDice()
    {
        float random = (float) Math.random() * (climateChange * 20);
        if(random > 12)
        {
            switch((int) Math.ceil(random))
            {
                case 13:
                    return "drought";
                case 14: case 15:
                    return "heatwave";
                case 16: case 17:
                    return "tsunami";
                case 18: case 19:
                    return "earthquake";
                case 20:
                    return "meteor";
            }
        }
        return null;
    }
    public boolean[] getDestroyed()
    {
        boolean[] destroyed = {false, false, false};
        float[] score = new float[3];
        for(int i = 0; i < 3; i++)
        {
            score[i] = ((1 - islands[i].getCrimeRate()) + islands[i].getEnergySecurity() + islands[i].getFoodSecurity() + islands[i].getJobSecurity() + islands[i].getHappiness()) / 5;
            if(score[i] < 0.4)
            {
                destroyed[i] = true;
            }
        }
        return destroyed;
    }
    void printYearlySummary() //Just for csv data analysis
    {
        String[] historicEntry = new String[37];
        historicEntry[0] = String.valueOf(worldPopulation);
        historicEntry[1] = String.valueOf(worldTime);
        historicEntry[2] = String.valueOf(worldEndTime);
        historicEntry[3] = String.valueOf(climateChange);
        for(int i = 0; i < islands.length; i++)
        {
            historicEntry[4+(i * 10)] = String.valueOf(islands[i].getPopulation());
            historicEntry[5+(i * 10)] = String.valueOf(islands[i].getMoney());
            historicEntry[6+(i * 10)] = String.valueOf(islands[i].getGdpPerCapita());
            historicEntry[7+(i * 10)] = String.valueOf(islands[i].getTaxRate());
            historicEntry[8+(i * 10)] = String.valueOf(islands[i].getCrimeRate());
            historicEntry[9+(i * 10)] = String.valueOf(islands[i].getFoodSecurity());
            historicEntry[10+(i * 10)] = String.valueOf(islands[i].getJobSecurity());
            historicEntry[11+(i * 10)] = String.valueOf(islands[i].getLandArea());
            historicEntry[12+(i * 10)] = String.valueOf(islands[i].getHappiness());
            historicEntry[13+(i * 10)] = String.valueOf(islands[i].getClimateChange());
        }
        history.add(historicEntry);
    }
}
