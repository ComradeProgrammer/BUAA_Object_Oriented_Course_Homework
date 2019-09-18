package xiv;

import com.oocourse.uml2.interact.exceptions.user.StateDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.StateNotFoundException;
import com.oocourse.uml2.models.common.ElementType;
import com.oocourse.uml2.models.elements.UmlElement;
import com.oocourse.uml2.models.elements.UmlFinalState;
import com.oocourse.uml2.models.elements.UmlPseudostate;
import com.oocourse.uml2.models.elements.UmlState;
import com.oocourse.uml2.models.elements.UmlTransition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;

public class MyStateMachine {
    private String name;
    private HashMap<UmlElement, HashSet<UmlElement>> adjmap = new HashMap<>();
    private HashMap<String, HashSet<UmlState>> nameMap = new HashMap<>();
    private int statecount = 0;
    private int transitioncount = 0;
    private UmlFinalState finalstate = null;
    private UmlPseudostate initialstate = null;

    MyStateMachine(String name) {
        this.name = name;
    }

    public void addState(UmlElement e) {
        if (e.getElementType() == ElementType.UML_FINAL_STATE) {
            if (finalstate == null) {
                statecount++;
                finalstate = (UmlFinalState) e;
            }
            adjmap.put(e, new HashSet<>());
            return;
        }
        if (e.getElementType() == ElementType.UML_STATE) {
            if (!nameMap.containsKey(e.getName())) {
                nameMap.put(e.getName(), new HashSet<>());
            }
            nameMap.get(e.getName()).add((UmlState) e);
            statecount++;
            adjmap.put(e, new HashSet<>());
            return;
        }
        if (e.getElementType() == ElementType.UML_PSEUDOSTATE) {
            if (initialstate == null) {
                statecount++;
                initialstate = (UmlPseudostate) e;
            }
            adjmap.put(e, new HashSet<>());
            return;
        }
    }

    public void addTransion(UmlElement start, UmlElement end) {
        if (adjmap.containsKey(start)) {
            adjmap.get(start).add(end);
            transitioncount++;
        }
        else {
            System.err.println("adjmap error?");
        }
    }

    public int getStateCount() {
        return this.statecount;
    }

    public int getTransitionCount() {
        return this.transitioncount;
    }

    public int baosou(String s) throws StateNotFoundException,
        StateDuplicatedException {
        LinkedList<UmlElement> tmp = new LinkedList<>();
        HashSet<UmlElement> been = new HashSet<>();
        boolean initial = true;
        if (!nameMap.containsKey(s)) {
            throw new StateNotFoundException(name, s);
        }
        if (nameMap.get(s).size() >= 2) {
            throw new StateDuplicatedException(name, s);
        }
        tmp.addLast(new ArrayList<UmlState>(nameMap.get(s)).get(0));
        while (tmp.size() > 0) {
            UmlElement pop = tmp.removeFirst();
            if (!initial) {
                been.add(pop);
            }
            else {
                initial = false;
            }
            for (UmlElement i : adjmap.get(pop)) {
                if (been.contains(i)) {
                    continue;
                }
                tmp.add(i);
            }
        }
        return been.size();
    }

}
