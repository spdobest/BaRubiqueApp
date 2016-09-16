package com.bestdealfinance.bdfpartner.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vikas on 25/3/16.
 */
public class Tree {
    private Node root;

    public Tree(String rootID) {
        root = new Node(rootID);
        root.id=rootID;
        root.fieldName="Devesh";
        root.children = new ArrayList<Node>();
        root.isVisible=false;
        root.parent=null;
        root.showCondition="root";
        root.isRoot=true;
    }


    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }
}