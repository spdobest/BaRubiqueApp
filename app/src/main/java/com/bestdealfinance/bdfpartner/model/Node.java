package com.bestdealfinance.bdfpartner.model;

import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vikas on 25/3/16.
 */
public class Node {
    public String id;
    public String fieldName;
    public String uiName;
    public String showCondition;
    public String fieldType;
    public Boolean mandatory;
    public Node parent;
    public boolean isVisible;
    public List<Node> children=new ArrayList<>();
    public List<String> options, values;
    public LinearLayout view;
    public boolean isRoot;

    public Node(String show,String id,  String uiName, String fieldType, Boolean mandatory, Node parent,
                LinearLayout layout, List<String>fieldOptions, List<String>fieldValues,String fieldName) {
        this.id = id;
        this.uiName = uiName;
        this.fieldType = fieldType;
        this.mandatory = mandatory;
        this.parent = parent;
        this.view=layout;
        this.options=fieldOptions;
        this.values=fieldValues;
        this.showCondition=show;
        this.isRoot=false;
        this.fieldName=fieldName;

    }
    public Node(String id) {
        this.id = id;
    }

    public List<Node> getChildren(){
        return children;
    }


    public boolean hasChild(){
        return getChildren().size()>0;
    }


    public int getChildSize(){
        return children.size();
    }

    public void addChild(Node node){
        children.add(node);
    }
    @Override
    public String toString()
    {
        return "Name: " + this.uiName + " showCondition: " +this.showCondition+ " type " +this.fieldType+" id "+id;
    }
}