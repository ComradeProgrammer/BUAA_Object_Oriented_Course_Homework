package xiv;

import com.oocourse.uml2.interact.common.AttributeClassInformation;
import com.oocourse.uml2.interact.common.AttributeQueryType;
import com.oocourse.uml2.interact.common.OperationQueryType;
import com.oocourse.uml2.interact.exceptions.user.AttributeDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.AttributeNotFoundException;
import com.oocourse.uml2.interact.exceptions.user.ClassDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.ClassNotFoundException;
import com.oocourse.uml2.interact.exceptions.user.InteractionDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.InteractionNotFoundException;
import com.oocourse.uml2.interact.exceptions.user.LifelineDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.LifelineNotFoundException;
import com.oocourse.uml2.interact.exceptions.user.StateDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.StateMachineDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.StateMachineNotFoundException;
import com.oocourse.uml2.interact.exceptions.user.StateNotFoundException;
import com.oocourse.uml2.interact.exceptions.user.UmlRule002Exception;
import com.oocourse.uml2.interact.exceptions.user.UmlRule008Exception;
import com.oocourse.uml2.interact.exceptions.user.UmlRule009Exception;
import com.oocourse.uml2.interact.format.UmlClassModelInteraction;
import com.oocourse.uml2.interact.format.UmlCollaborationInteraction;
import com.oocourse.uml2.interact.format.UmlGeneralInteraction;
import com.oocourse.uml2.interact.format.UmlStandardPreCheck;
import com.oocourse.uml2.interact.format.UmlStateChartInteraction;
import com.oocourse.uml2.models.common.ElementType;
import com.oocourse.uml2.models.common.Visibility;
import com.oocourse.uml2.models.elements.UmlAssociation;
import com.oocourse.uml2.models.elements.UmlAssociationEnd;
import com.oocourse.uml2.models.elements.UmlAttribute;
import com.oocourse.uml2.models.elements.UmlClass;
import com.oocourse.uml2.models.elements.UmlClassOrInterface;
import com.oocourse.uml2.models.elements.UmlElement;
import com.oocourse.uml2.models.elements.UmlGeneralization;
import com.oocourse.uml2.models.elements.UmlInterface;
import com.oocourse.uml2.models.elements.UmlInterfaceRealization;
import com.oocourse.uml2.models.elements.UmlOperation;
import com.oocourse.uml2.models.elements.UmlParameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class MyUmlInteraction implements UmlClassModelInteraction,
    UmlStandardPreCheck, UmlCollaborationInteraction,
    UmlStateChartInteraction, UmlGeneralInteraction {
    private HashMap<String, MyObject> objects = new HashMap<>();
    private HashMap<String, UmlElement> elementmap = new HashMap<>();
    private HashSet<MyClass> classes = new HashSet<>();
    private HashSet<MyInterface> interfaces = new HashSet<>();
    private HashMap<String, HashSet<MyClass>> classmap = new HashMap<>();
    private HashSet<MyOperation> operations = new HashSet<>();
    private HashSet<MyAttribute> attributes = new HashSet<>();
    private HashSet<UmlAssociation> associations = new HashSet<>();
    private HashSet<UmlParameter> parameters = new HashSet<>();
    private HashSet<UmlGeneralization> generalizations = new HashSet<>();
    private HashMap<String, UmlAssociationEnd> ends = new HashMap<>();
    private HashSet<UmlInterfaceRealization> realziations = new HashSet<>();
    private AoeGraph aoegraph = new AoeGraph();
    private MyCollaboration collaborations = new MyCollaboration();
    private MyStateMachines statemachines = new MyStateMachines();

    private void caseFunc(UmlElement e) {
        switch (e.getElementType()) {
            case UML_CLASS:
                //System.err.println(e.getName());
                UmlClass tmpc = (UmlClass) e;
                MyClass tmpc2 = new MyClass(tmpc);
                objects.put(e.getId(), tmpc2);
                if (!classmap.containsKey(e.getName())) {
                    classmap.put(e.getName(), new HashSet<>());
                }
                classmap.get(e.getName()).add(tmpc2);
                classes.add(tmpc2);
                aoegraph.addVertex(tmpc);
                break;
            case UML_INTERFACE:
                UmlInterface tmpi = (UmlInterface) e;
                MyInterface tmpi2 = new MyInterface(tmpi);
                objects.put(e.getId(), tmpi2);
                interfaces.add(tmpi2);
                aoegraph.addVertex(tmpi);
                break;
            case UML_ATTRIBUTE:
                UmlAttribute tmpa = (UmlAttribute) e;
                if (elementmap.containsKey(e.getParentId())) {
                    if (elementmap.get(e.getParentId()).getElementType()
                        == ElementType.UML_CLASS) {
                        MyAttribute tmpa2 = new MyAttribute(tmpa, objects);
                        objects.put(e.getId(), tmpa2);
                        attributes.add(tmpa2);
                    }
                }
                break;
            case UML_OPERATION:
                UmlOperation tmpo = (UmlOperation) e;
                if (elementmap.get(e.getParentId()).getElementType()
                    == ElementType.UML_CLASS) {
                    MyOperation tmpo2 = new MyOperation(tmpo, objects);
                    objects.put(e.getId(), tmpo2);
                    operations.add(tmpo2);
                }
                break;
            case UML_ASSOCIATION:
                UmlAssociation tmpas = (UmlAssociation) e;
                associations.add(tmpas);
                break;
            case UML_GENERALIZATION:
                UmlGeneralization tmpg = (UmlGeneralization) e;
                generalizations.add(tmpg);
                break;
            case UML_ASSOCIATION_END:
                UmlAssociationEnd tmpe = (UmlAssociationEnd) e;
                ends.put(e.getId(), tmpe);
                //System.err.println("adding" + e.getId());
                break;
            case UML_INTERFACE_REALIZATION:
                UmlInterfaceRealization tmpr = (UmlInterfaceRealization) e;
                realziations.add(tmpr);
                break;
            case UML_PARAMETER:
                UmlParameter tmpp = (UmlParameter) e;
                parameters.add(tmpp);
                break;
            case UML_INTERACTION:
            case UML_LIFELINE:
            case UML_MESSAGE:
            case UML_ENDPOINT:
                collaborations.init(e);
                break;
            case UML_EVENT:
            case UML_OPAQUE_BEHAVIOR:
            case UML_FINAL_STATE:
            case UML_PSEUDOSTATE:
            case UML_REGION:
            case UML_STATE:
            case UML_STATE_MACHINE:
            case UML_TRANSITION:
                statemachines.init(e);
                break;

            default:
                System.err.println("wtf???");
        }
    }

    public MyUmlInteraction(UmlElement[] elements) {
        //System.out.println(elements.length);
        int i = 0;
        for (UmlElement e : elements) {
            elementmap.put(e.getId(), e);
            //System.err.println(new Integer(++i).toString() + e
            // .getElementType());
            caseFunc(e);
        }
        //FIRST HANDLE THE INTERFACE'S AND CLASS'S EXTENSION
        for (UmlGeneralization g : generalizations) {
            String sourceid = g.getSource();
            String targetid = g.getTarget();
            aoegraph.addEdge((UmlClassOrInterface) elementmap.get(sourceid),
                (UmlClassOrInterface) elementmap.get(targetid));
            if (elementmap.get(sourceid).getElementType()
                == ElementType.UML_CLASS) {
                MyClass tmpc = (MyClass) (objects.get(sourceid));
                tmpc.setParent((MyClass) (objects.get(targetid)));
                /*aoegraph.addEdge((UmlClassOrInterface) elementmap.get
                (sourceid),
                    (UmlClassOrInterface) elementmap.get(targetid));*/
            }
            else if (elementmap.get(sourceid).getElementType()
                == ElementType.UML_INTERFACE) {
                MyInterface tmpi = (MyInterface) (objects.get(sourceid));
                tmpi.addInterface((MyInterface) (objects.get(targetid)));
            }
        }
        //THEN WE HANDLE THE IMPLEMENTION BETWEEN CLASSES AND INTERFACE
        for (UmlInterfaceRealization r : realziations) {
            String sourceid = r.getSource();
            String targetid = r.getTarget();
            MyClass tmpc = (MyClass) (objects.get(sourceid));
            MyInterface tmpi = (MyInterface) (objects.get(targetid));
            tmpc.addInterface(tmpi);
            aoegraph.addEdge((UmlClassOrInterface) elementmap.get(sourceid),
                (UmlClassOrInterface) elementmap.get(targetid));
        }
        // THEN WE HANDLE THE ASSOCIATION
        for (UmlAssociation a : associations) {
            /*
            MyClass tmpc1 =
                (MyClass) (objects.get(ends.get(a.getEnd1()).getReference()));
            MyClass tmpc2 =
                (MyClass) (objects.get(ends.get(a.getEnd2()).getReference()));
            tmpc1.associate(tmpc2,a);
            tmpc2.associate(tmpc1,a);*/
            UmlElement end1 =
                elementmap.get(ends.get(a.getEnd1()).getReference());
            UmlElement end2 =
                elementmap.get(ends.get(a.getEnd2()).getReference());
            if (end1.getElementType() == ElementType.UML_CLASS
                && end2.getElementType() == ElementType.UML_CLASS) {
                MyClass tmpc1 =
                    (MyClass) (objects.get(end1.getId()));
                MyClass tmpc2 =
                    (MyClass) (objects.get(end2.getId()));
                tmpc1.associate(tmpc2, a,
                    elementmap.get(a.getEnd2()).getName());
                tmpc2.associate(tmpc1, a,
                    elementmap.get(a.getEnd1()).getName());
            }
            else if (end1.getElementType() == ElementType.UML_CLASS
                && end2.getElementType() == ElementType.UML_INTERFACE) {
                MyClass tmpc1 =
                    (MyClass) (objects.get(end1.getId()));
                tmpc1.addInterfaceAssociation(a);
            }
            else if (end2.getElementType() == ElementType.UML_CLASS
                && end1.getElementType() == ElementType.UML_INTERFACE) {
                MyClass tmpc2 =
                    (MyClass) (objects.get(end2.getId()));
                tmpc2.addInterfaceAssociation(a);
            }

        }
        //THEN WE HANDLE THE PARAMETERS
        for (UmlParameter p : parameters) {
            //System.err.println(p.getName());
            MyOperation op = (MyOperation) (objects.get(p.getParentId()));
            //System.err.println(op);
            if (op != null) {
                op.addParameter(p);
            }
        }

        // Then WE HANDLE THE OPERATIONS & ATTRIBUTES
        for (MyOperation op : operations) {
            op.setBelongingClass();
        }


        for (MyAttribute attr : attributes) {
            attr.setBelongingClass();
        }
        collaborations.establish();
        statemachines.establish();
        //FINISHED

    }

    public int getClassCount() {
        return classes.size();
    }

    public int getClassOperationCount(String className,
                                      OperationQueryType queryType)
        throws ClassNotFoundException, ClassDuplicatedException {
        if (!classmap.containsKey(className)) {
            throw new ClassNotFoundException(className);
        }
        if (classmap.get(className).size() >= 2) {
            throw new ClassDuplicatedException(className);
        }
        return new ArrayList<>(classmap.get(className))
            .get(0).opertionCountQuery(queryType);
    }

    public int getClassAttributeCount(String className,
                                      AttributeQueryType queryType)
        throws ClassNotFoundException, ClassDuplicatedException {
        if (!classmap.containsKey(className)) {
            throw new ClassNotFoundException(className);
        }
        if (classmap.get(className).size() >= 2) {
            throw new ClassDuplicatedException(className);
        }
        return new ArrayList<>(classmap.get(className))
            .get(0).attributeCountQuery(queryType);
    }

    public int getClassAssociationCount(String className)
        throws ClassNotFoundException, ClassDuplicatedException {
        if (!classmap.containsKey(className)) {
            throw new ClassNotFoundException(className);
        }
        if (classmap.get(className).size() >= 2) {
            throw new ClassDuplicatedException(className);
        }
        return new ArrayList<>(classmap.get(className)).get(0).getAssNum();
    }

    public List<String> getClassAssociatedClassList(String className)
        throws ClassNotFoundException, ClassDuplicatedException {
        if (!classmap.containsKey(className)) {
            throw new ClassNotFoundException(className);
        }
        if (classmap.get(className).size() >= 2) {
            throw new ClassDuplicatedException(className);
        }
        HashSet<MyClass> tmp =
            new ArrayList<>(classmap.get(className)).get(0).getAssociation();
        List<String> ret = new ArrayList<>();
        for (MyClass c : tmp) {
            ret.add(c.toString());
        }
        return ret;
    }

    public Map<Visibility, Integer> getClassOperationVisibility(
        String className, String operationName)
        throws ClassNotFoundException, ClassDuplicatedException {
        if (!classmap.containsKey(className)) {
            throw new ClassNotFoundException(className);
        }
        if (classmap.get(className).size() >= 2) {
            throw new ClassDuplicatedException(className);
        }
        return new ArrayList<>(classmap.get(className))
            .get(0).getClassOperationVisibility(operationName);
    }

    public Visibility getClassAttributeVisibility(String className,
                                                  String attributeName)
        throws ClassNotFoundException, ClassDuplicatedException,
        AttributeNotFoundException, AttributeDuplicatedException {
        if (!classmap.containsKey(className)) {
            throw new ClassNotFoundException(className);
        }
        if (classmap.get(className).size() >= 2) {
            throw new ClassDuplicatedException(className);
        }
        return new ArrayList<>(classmap.get(className))
            .get(0).getClassAttributeVisibility(attributeName);
    }

    public String getTopParentClass(String className)
        throws ClassNotFoundException, ClassDuplicatedException {
        if (!classmap.containsKey(className)) {
            throw new ClassNotFoundException(className);
        }
        if (classmap.get(className).size() >= 2) {
            throw new ClassDuplicatedException(className);
        }
        return new ArrayList<>(classmap.get(className))
            .get(0).getTopParent().toString();
    }

    public List<String> getImplementInterfaceList(String className)
        throws ClassNotFoundException, ClassDuplicatedException {
        if (!classmap.containsKey(className)) {
            throw new ClassNotFoundException(className);
        }
        if (classmap.get(className).size() >= 2) {
            throw new ClassDuplicatedException(className);
        }
        HashSet<MyInterface> tmp =
            new ArrayList<>(classmap.get(className)).get(0).getAllInterface();
        List<String> ret = new ArrayList<>();
        for (MyInterface i : tmp) {
            ret.add(i.toString());
        }
        return ret;
    }

    public List<AttributeClassInformation> getInformationNotHidden(
        String className)
        throws ClassNotFoundException, ClassDuplicatedException {
        if (!classmap.containsKey(className)) {
            throw new ClassNotFoundException(className);
        }
        if (classmap.get(className).size() >= 2) {
            throw new ClassDuplicatedException(className);
        }
        return new ArrayList<>(classmap.get(className))
            .get(0).getInformationNotHidden();
    }

    public void checkForUml008() throws UmlRule008Exception {
        aoegraph.check();
    }

    public void checkForUml002() throws UmlRule002Exception {
        HashSet<AttributeClassInformation> res = new HashSet<>();
        for (MyClass c : classes) {
            res.addAll(c.check002part1());
        }
        /*for (UmlAssociation a : associations) {
            UmlElement end1 =
                elementmap.get(ends.get(a.getEnd1()).getReference());
            UmlElement end2 =
                elementmap.get(ends.get(a.getEnd2()).getReference());
            if (end1.getElementType() == ElementType.UML_CLASS
                && end2.getElementType() == ElementType.UML_CLASS) {
                MyClass tmpc1 =
                    (MyClass) (objects.get(end1.getId()));
                MyClass tmpc2 =
                    (MyClass) (objects.get(end2.getId()));
                if (tmpc1.check002part2(end2.getName())) {
                    res.add(new AttributeClassInformation(end2.getName(),
                        tmpc1.getname()));
                }
                if (tmpc2.check002part2(end1.getName())) {
                    res.add(new AttributeClassInformation(end1.getName(),
                        tmpc2.getname()));
                }
            }
        }*/
        if (res.size() != 0) {
            throw new UmlRule002Exception(res);
        }
    }

    public void checkForUml009() throws UmlRule009Exception {
        aoegraph.check009();
    }

    public int getParticipantCount(String interactionName)
        throws InteractionNotFoundException, InteractionDuplicatedException {
        return collaborations.getParticipantCount(interactionName);
    }

    public int getMessageCount(String interactionName)
        throws InteractionNotFoundException, InteractionDuplicatedException {
        return collaborations.getMessageCount(interactionName);
    }

    public int getIncomingMessageCount(String interactionName,
                                       String lifelineName)
        throws InteractionNotFoundException, InteractionDuplicatedException,
        LifelineNotFoundException, LifelineDuplicatedException {
        return collaborations.getIncomingMessageCount(interactionName,
            lifelineName);
    }

    public int getStateCount(String stateMachineName)
        throws StateMachineNotFoundException, StateMachineDuplicatedException {
        return statemachines.getStateCount(stateMachineName);
    }

    public int getTransitionCount(String stateMachineName)
        throws StateMachineNotFoundException, StateMachineDuplicatedException {
        return statemachines.getTransitionCount(stateMachineName);
    }

    public int getSubsequentStateCount(String stateMachineName,
                                       String stateName)
        throws StateMachineNotFoundException, StateMachineDuplicatedException,
        StateNotFoundException, StateDuplicatedException {
        return statemachines.getSubsequentStateCount(stateMachineName,
            stateName);
    }

}
