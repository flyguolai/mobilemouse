package com.mobilecontrol.client.state;

import android.util.Log;

import com.mobilecontrol.client.data.TouchData;

public class DragState extends State {

    DragState(StateManager stateManager){
        super();
        this.name = "drag";
        this.stateManager = stateManager;
        this.touchDataType = TouchData.TOUCH_TYPE_INVALID;
    }

    @Override
    public void prevState() {
        this.stateManager.setState(stateManager.scrollState);
    }

    @Override
    public void nextState() {
        Log.d(this.name,"next state is undefined");
    }
}
