package com.mobilecontrol.client.state;

import android.util.Log;

import com.mobilecontrol.client.data.TouchData;

public class NoneState extends  State {

    NoneState(StateManager stateManager){
        super();
        this.name = "none";
        this.stateManager = stateManager;
        this.touchDataType = TouchData.TOUCH_TYPE_INVALID;
    }

    @Override
    public void nextState() {
        this.stateManager.setState(stateManager.moveState);
    }

    @Override
    public void prevState() {
        Log.d(this.name,"none state has not got a prev state");
        this.stateManager.setState(this);
    }

    @Override
    public void move(float dx, float dy) {
        Log.d(this.name,"no move~");
    }
}
