package xiv;

import com.oocourse.uml2.interact.exceptions.user.StateDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.StateMachineDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.StateMachineNotFoundException;
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

public class MyStateMachines {
    //private ArrayList<UmlStateMachine> machines = new ArrayList<>();
    private HashMap<String, UmlElement> allidmap = new HashMap<>();
    private ArrayList<UmlState> states = new ArrayList<>();
    private ArrayList<UmlPseudostate> pseudostates = new ArrayList<>();
    private ArrayList<UmlFinalState> finalstates = new ArrayList<>();
    private ArrayList<UmlTransition> transitions = new ArrayList<>();
    private HashMap<String, HashSet<MyStateMachine>> nameMap = new HashMap<>();
    private HashMap<String, MyStateMachine> idMap = new HashMap<>();
    private HashMap<String, String> regiontomachine = new HashMap<>();

    public void init(UmlElement e) {
        allidmap.put(e.getId(), e);
        if (e.getElementType() == ElementType.UML_STATE_MACHINE) {
            MyStateMachine tmp = new MyStateMachine(e.getName());
            idMap.put(e.getId(), tmp);
            if (!nameMap.containsKey(e.getName())) {
                nameMap.put(e.getName(), new HashSet<>());
            }
            nameMap.get(e.getName()).add(tmp);
        }
        else if (e.getElementType() == ElementType.UML_REGION) {
            regiontomachine.put(e.getId(), e.getParentId());
        }
        else if (e.getElementType() == ElementType.UML_STATE) {
            states.add((UmlState) e);
        }
        else if (e.getElementType() == ElementType.UML_PSEUDOSTATE) {
            pseudostates.add((UmlPseudostate) e);
        }
        else if (e.getElementType() == ElementType.UML_TRANSITION) {
            transitions.add((UmlTransition) e);
        }
        else if (e.getElementType() == ElementType.UML_FINAL_STATE) {
            finalstates.add((UmlFinalState) e);
        }
    }

    public void establish() {
        for (UmlState s : states) {
            String id = regiontomachine.get(s.getParentId());
            MyStateMachine m = idMap.get(id);
            m.addState(s);
        }
        for (UmlPseudostate p : pseudostates) {
            String id = regiontomachine.get(p.getParentId());
            MyStateMachine m = idMap.get(id);
            m.addState(p);
        }
        for (UmlFinalState f : finalstates) {
            String id = regiontomachine.get(f.getParentId());
            MyStateMachine m = idMap.get(id);
            m.addState(f);
        }
        for (UmlTransition t : transitions) {
            String id = regiontomachine.get(t.getParentId());
            MyStateMachine m = idMap.get(id);
            m.addTransion(allidmap.get(t.getSource()),
                allidmap.get(t.getTarget()));
        }
    }

    public int getStateCount(String stateMachineName)
        throws StateMachineNotFoundException, StateMachineDuplicatedException {
        if (!nameMap.containsKey(stateMachineName)) {
            throw new StateMachineNotFoundException(stateMachineName);
        }
        else if (nameMap.get(stateMachineName).size() >= 2) {
            throw new StateMachineDuplicatedException(stateMachineName);
        }
        return new ArrayList<MyStateMachine>(nameMap.get(stateMachineName)).get(0).getStateCount();
    }

    public int getTransitionCount(String stateMachineName)
        throws StateMachineNotFoundException, StateMachineDuplicatedException {
        if (!nameMap.containsKey(stateMachineName)) {
            throw new StateMachineNotFoundException(stateMachineName);
        }
        else if (nameMap.get(stateMachineName).size() >= 2) {
            throw new StateMachineDuplicatedException(stateMachineName);
        }
        return new ArrayList<MyStateMachine>(nameMap.get(stateMachineName)).get(0).getTransitionCount();
    }

    public int getSubsequentStateCount(String stateMachineName,
                                       String stateName)
        throws StateMachineNotFoundException, StateMachineDuplicatedException,
        StateNotFoundException, StateDuplicatedException {
        if (!nameMap.containsKey(stateMachineName)) {
            throw new StateMachineNotFoundException(stateMachineName);
        }
        else if (nameMap.get(stateMachineName).size() >= 2) {
            throw new StateMachineDuplicatedException(stateMachineName);
        }
        return new ArrayList<MyStateMachine>(nameMap.get(stateMachineName)).get(0).baosou(stateName);
    }
}
