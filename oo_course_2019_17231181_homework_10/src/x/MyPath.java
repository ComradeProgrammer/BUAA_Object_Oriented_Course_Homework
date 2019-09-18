package x;

import com.oocourse.specs2.models.Path;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class MyPath implements Path {
    private ArrayList<Integer> list = new ArrayList<>();
    private int hash = 0;

    public MyPath(int... nodeList) {
        for (int i : nodeList) {
            list.add(i);
            hash += i;
        }
    }

    public int size() {
        return list.size();
    }

    public int getNode(int index) {
        return list.get(index);
    }

    public boolean containsNode(int node) {
        return list.contains(node);
    }

    public int getDistinctNodeCount() {
        return new HashSet<Integer>(list).size();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Path)) {
            //System.out.println("instance error");
            return false;
        }
        if (((Path) obj).size() != list.size()) {
            return false;
        }
        int i = 0;
        for (int j : ((Path) obj)) {
            if (list.get(i) != j) {
                return false;
            }
            i++;
        }
        return true;
    }

    public /*@pure@*/ boolean isValid() {
        return list.size() >= 2;
    }

    public Iterator<Integer> iterator() {
        return list.iterator();
    }

    public int compareTo(Path o) {
        int olength = o.size();
        int miniter = Math.min(olength, list.size());
        if (o.equals(this)) {
            return 0;
        }
        int i = 0;
        for (int j : o) {
            if (i >= miniter) {
                break;
            }
            int tmpa = list.get(i);
            if (tmpa != j) {
                return Integer.compare(tmpa, j);
            }
            i++;
        }
        return Integer.compare(list.size(), olength);
    }

    public int hashCode() {
        return hash;
    }
}

