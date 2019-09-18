package xiv;

import com.oocourse.uml2.interact.exceptions.user.UmlRule008Exception;
import com.oocourse.uml2.interact.exceptions.user.UmlRule009Exception;
import com.oocourse.uml2.models.elements.UmlClassOrInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class AoeGraph {
    private ArrayList<UmlClassOrInterface> all = new ArrayList<>();
    private HashMap<UmlClassOrInterface, ArrayList<UmlClassOrInterface>> aoe =
        new HashMap<>();

    public void addVertex(UmlClassOrInterface e) {
        all.add(e);
        aoe.put(e, new ArrayList<>());
    }

    public void addEdge(UmlClassOrInterface from, UmlClassOrInterface to) {

        aoe.get(from).add(to);

    }

    public void check() throws UmlRule008Exception {
        Set<UmlClassOrInterface> tmp =
            new HashSet<UmlClassOrInterface>();
        for (UmlClassOrInterface c : all) {
            HashSet<UmlClassOrInterface> been = new HashSet<>();
            LinkedList<UmlClassOrInterface> bfs = new LinkedList<>();
            bfs.add(c);
            while (bfs.size() > 0) {
                UmlClassOrInterface pop = bfs.removeFirst();
                for (UmlClassOrInterface i : aoe.get(pop)) {
                    if (i.equals(c)) {
                        tmp.add(c);
                        break;
                    }
                    if (been.contains(i)) {
                        continue;
                    }
                    been.add(i);
                    bfs.addLast(i);
                }
            }
        }
        //THERE EXISTS A CIRCLE
        if (tmp.size() > 0) {
            throw new UmlRule008Exception(tmp);
        }

    }

    public void check009() throws UmlRule009Exception {
        Set<UmlClassOrInterface> tmp =
            new HashSet<UmlClassOrInterface>();
        for (UmlClassOrInterface c : all) {
            HashSet<UmlClassOrInterface> been = new HashSet<>();
            LinkedList<UmlClassOrInterface> bfs = new LinkedList<>();
            bfs.add(c);
            while (bfs.size() > 0) {
                UmlClassOrInterface pop = bfs.removeFirst();
                //been.add(pop);
                for (UmlClassOrInterface i : aoe.get(pop)) {
                    if (been.contains(i)) {
                        tmp.add(c);
                        break;
                    }
                    been.add(i);
                    bfs.addLast(i);
                }
            }
        }
        if (tmp.size() > 0) {
            throw new UmlRule009Exception(tmp);
        }
    }

}
