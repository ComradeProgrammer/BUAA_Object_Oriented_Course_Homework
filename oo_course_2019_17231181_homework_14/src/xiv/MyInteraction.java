package xiv;

import com.oocourse.uml2.interact.exceptions.user.LifelineDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.LifelineNotFoundException;
import com.oocourse.uml2.models.elements.UmlLifeline;
import com.oocourse.uml2.models.elements.UmlMessage;

import java.util.ArrayList;
import java.util.HashMap;

public class MyInteraction {
    private String name;
    private HashMap<String, ArrayList<UmlMessage>> map = new HashMap<>();
    private HashMap<String, ArrayList<UmlMessage>> idmap = new HashMap<>();
    private int count = 0;
    private int lifelinecnt = 0;
    private ArrayList<String> duplicateLifeLine = new ArrayList<>();

    public MyInteraction(String name) {
        this.name = name;
    }

    public void addLifeLine(UmlLifeline e) {
        if (duplicateLifeLine.contains(e.getName())) {
            return;
        }
        else if (map.containsKey(e.getName())) {
            map.remove(e.getName());
            duplicateLifeLine.add(e.getName());
        }
        else {
            ArrayList<UmlMessage> tmp = new ArrayList<>();
            map.put(e.getName(), tmp);
            idmap.put(e.getId(), tmp);
        }
        lifelinecnt += 1;
    }

    public void addMessage(UmlMessage e) {
        if (idmap.containsKey(e.getTarget())) {
            idmap.get(e.getTarget()).add(e);
            //PREVENT POINT
        }
        count++;
    }

    public int getIncomingMessageCount(String l) throws
        LifelineNotFoundException, LifelineDuplicatedException {
        if (duplicateLifeLine.contains(l)) {
            throw new LifelineDuplicatedException(name, l);
        }
        else if (!map.containsKey(l)) {
            throw new LifelineNotFoundException(name, l);
        }
        return map.get(l).size();
    }

    public int getMessageCount() {
        return count;
    }

    public int getgetParticipantCount() {
        return lifelinecnt;
    }
}

