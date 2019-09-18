package xiv;

import com.oocourse.uml2.interact.exceptions.user.InteractionDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.InteractionNotFoundException;
import com.oocourse.uml2.interact.exceptions.user.LifelineDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.LifelineNotFoundException;
import com.oocourse.uml2.models.common.ElementType;
import com.oocourse.uml2.models.elements.UmlElement;
import com.oocourse.uml2.models.elements.UmlInteraction;
import com.oocourse.uml2.models.elements.UmlLifeline;
import com.oocourse.uml2.models.elements.UmlMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class MyCollaboration {
    private HashMap<String, HashSet<MyInteraction>> nameMap = new HashMap<>();
    private HashMap<String, MyInteraction> idMap = new HashMap<>();
    private ArrayList<UmlLifeline> lifelines = new ArrayList<>();
    private ArrayList<UmlInteraction> interactions = new ArrayList<>();
    private ArrayList<UmlMessage> messages = new ArrayList<>();

    public MyCollaboration() {
    }

    public void init(UmlElement e) {
        if (e.getElementType() == ElementType.UML_MESSAGE) {
            messages.add((UmlMessage) e);
        }
        else if (e.getElementType() == ElementType.UML_LIFELINE) {
            lifelines.add((UmlLifeline) e);
        }
        else if (e.getElementType() == ElementType.UML_INTERACTION) {
            interactions.add((UmlInteraction) e);
        }
    }

    public void establish() {
        for (UmlInteraction e : interactions) {
            if (!nameMap.containsKey(e.getName())) {
                nameMap.put(e.getName(), new HashSet<>());
            }
            MyInteraction tmp = new MyInteraction(e.getName());
            nameMap.get(e.getName()).add(tmp);
            idMap.put(e.getId(), tmp);
        }
        for (UmlLifeline e : lifelines) {
            idMap.get(e.getParentId()).addLifeLine(e);
        }
        for (UmlMessage e : messages) {
            idMap.get(e.getParentId()).addMessage(e);
        }
    }

    public int getParticipantCount(String interactionName)
        throws InteractionNotFoundException, InteractionDuplicatedException {
        if (!nameMap.containsKey(interactionName)) {
            throw new InteractionNotFoundException(interactionName);
        }
        else if (nameMap.get(interactionName).size() >= 2) {
            throw new InteractionDuplicatedException(interactionName);
        }
        else {
            return new ArrayList<MyInteraction>(nameMap.get(interactionName))
                .get(0).getgetParticipantCount();
        }
    }

    public int getMessageCount(String interactionName)
        throws InteractionNotFoundException, InteractionDuplicatedException {
        if (!nameMap.containsKey(interactionName)) {
            throw new InteractionNotFoundException(interactionName);
        }
        else if (nameMap.get(interactionName).size() >= 2) {
            throw new InteractionDuplicatedException(interactionName);
        }
        else {
            return new ArrayList<MyInteraction>(nameMap.get(interactionName))
                .get(0).getMessageCount();
        }
    }

    public int getIncomingMessageCount(String interactionName,
                                       String lifelineName)
        throws InteractionNotFoundException, InteractionDuplicatedException,
        LifelineNotFoundException, LifelineDuplicatedException {
        if (!nameMap.containsKey(interactionName)) {
            throw new InteractionNotFoundException(interactionName);
        }
        else if (nameMap.get(interactionName).size() >= 2) {
            throw new InteractionDuplicatedException(interactionName);
        }
        else {
            return new ArrayList<MyInteraction>(nameMap.get(interactionName))
                .get(0).getIncomingMessageCount(lifelineName);
        }
    }
}
