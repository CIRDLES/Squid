/*
 * Copyright 2017 CIRDLES.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cirdles.squid.utilities;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author James F. Bowring
 */
public class PrefixTree {

    private final static Character ROOT = "/".toCharArray()[0];
    private final static Character LEAF = "\\".toCharArray()[0];

    private List<PrefixTree> children;
    private Character value;

    public PrefixTree() {
        this(ROOT);
    }

    public PrefixTree(Character value) {
        this.value = value;
        this.children = new ArrayList<>();
    }

    public void insert(String word) {
        PrefixTree target = this;
        for (int i = 0; i < word.length(); i++) {
            Character letter = word.charAt(i);
            target = target.findTargetChild(letter);
            // add special leaf condition if last letter
            if (i == (word.length() - 1)) {
                target.children.add(new PrefixTree(LEAF));
            }
        }
    }

    public PrefixTree findTargetChild(Character value) {
        PrefixTree tree = null;
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i).value.compareTo(value) == 0) {
                tree = children.get(i);
            }
        }

        if (tree == null) {
            tree = new PrefixTree(value);
            children.add(tree);
        }

        return tree;
    }

    public PrefixTree findPrefix(String prefix) {
        PrefixTree target = this;
        for (int i = 0; i < prefix.length(); i++) {
            Character letter = prefix.charAt(i);
            target = target.findTargetChild(letter);
        }

        return target;
    }

    public int countLeaves() {
        int retVal = 0;

        for (int i = 0; i < children.size(); i++) {
            if (children.get(i).value.compareTo(LEAF) == 0) {
                retVal++;
            } else {
                retVal += children.get(i).countLeaves();
            }
        }

        return retVal;
    }

    public void prettyPrint(int depth) {
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i).value.compareTo(LEAF) != 0) {
                System.out.println(children.get(i).value + "  = " + children.get(i).countLeaves());
                for (int j = 0; j < depth; j++) {
                    System.out.print("   ");
                }
                children.get(i).prettyPrint(depth + 1);
            }
        }

    }

    public static void main(String[] args) {
        PrefixTree root = new PrefixTree();
        root.insert("uaf.g");
//        root.insert("uaf.h");
//        root.insert("ua");
//        root.insert("abcd");
        root.prettyPrint(0);
    }
}
