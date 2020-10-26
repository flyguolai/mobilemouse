package com.mobilecontrol.client.state;

import android.util.Log;

import com.mobilecontrol.client.data.TouchData;

abstract public class State {
    protected String name;
    protected StateManager stateManager;
    protected String touchDataType;
    public abstract void nextState();
    public abstract void prevState();

    public void move(float dx, float dy) {
        Log.d("dy","dy is"+dy);

        TouchData td = new TouchData();

        td.setType(this.touchDataType);
        td.setX((int) dx);
        td.setY((int) dy);

        this.stateManager.send(td);
    }
}
