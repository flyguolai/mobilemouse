package com.mobilecontrol.client.state;

import android.util.Log;

import com.mobilecontrol.client.data.TouchData;
import com.mobilecontrol.client.net.MobileControlClient;

public class StateManager {
    public State currentState = null;
    public NoneState noneState;
    public DragState dragState;
    public ScrollState scrollState;
    public MoveState moveState;
    private MobileControlClient mControlClient;

    public StateManager(MobileControlClient mControlClient){
        this.mControlClient = mControlClient;

        this.dragState = new DragState(this);
        this.scrollState = new ScrollState(this);
        this.moveState = new MoveState(this);
        this.noneState = new NoneState(this);
    }

    public void setState(State state){
        if(state == null){
            this.currentState = noneState;
        }else{
            this.currentState = state;
        }
    }

    public void nextState(){
        this.currentState.nextState();
        Log.d("currentState","is "+ currentState.name);
    }

    public void prevState(){
        this.currentState.prevState();
        Log.d("currentState","is "+ currentState.name);
    }

    public void send(TouchData td){
        if (td == null || !mControlClient.isConnected()) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(td.getHead()).append(",")
                .append(td.getType()).append(",")
                .append(td.getX()).append(",")
                .append(td.getY());
        String jsonStr = sb.toString();
        mControlClient.send(jsonStr);
    }

    public void move(float dx,float dy){
        this.currentState.move(dx,dy);
    }
}
