package io.github.arrayv.visuals;

import io.github.arrayv.main.ArrayVisualizer;

public abstract class VisualFeature {
    private boolean featureEnabled;

    private ArrayVisualizer arrayVisualizer;
    private String listName;
    private String listID;
    
    private String[] idPullsUp;
    private String[] idPullsDown;
    private String[] idFlipsUp;
    private String[] idFlipsDown;

    protected VisualFeature(ArrayVisualizer arrayVisualizer) {
    	this.arrayVisualizer = arrayVisualizer;
    	
        this.enableFeature(true);
        this.setListID("");
        this.setListName("");
        this.setPullsUp();
        this.setPullsDown();
        this.setFlipsUp();
        this.setFlipsDown();
    }

    protected void enableFeature(boolean Bool) {
        this.featureEnabled = Bool;
    }
    
    protected void setListID(String ID) {
        this.listID = ID;
    }
    protected void setListName(String ID) {
        this.listName = ID;
    }
    
    protected void setPullsUp(String... IDs) {
        this.idPullsUp = IDs;
    }
    protected void setPullsDown(String... IDs) {
        this.idPullsDown = IDs;
    }
    protected void setFlipsUp(String... IDs) {
        this.idFlipsUp = IDs;
    }
    protected void setFlipsDown(String... IDs) {
        this.idFlipsDown = IDs;
    }
    
    public boolean isDisabled() {
        return !this.featureEnabled;
    }
    
    public String getListID() {
        return this.listID;
    }
    public String getListName() {
        return this.listName;
    }
    
    public String[] getIDsPulledUp() {
        return this.idPullsUp;
    }
    public String[] getIDsPulledDown() {
        return this.idPullsDown;
    }
    public String[] getIDsFlippedUp() {
        return this.idFlipsUp;
    }
    public String[] getIDsFlippedDown() {
        return this.idFlipsDown;
    }
    
    public void pullUp() {}
    public void localPrerender(int[] array, int n) {}
    public void localPostrender(int[] array, int n) {}
    public void globalPrerender() {}
    public void globalPostrender() {}
    public void pullDown() {}
}