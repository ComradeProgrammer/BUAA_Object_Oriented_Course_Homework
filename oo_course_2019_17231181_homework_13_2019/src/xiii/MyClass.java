package xiii;

import com.oocourse.uml1.interact.common.AttributeClassInformation;
import com.oocourse.uml1.interact.common.AttributeQueryType;
import com.oocourse.uml1.interact.common.OperationQueryType;
import com.oocourse.uml1.interact.exceptions.user.AttributeDuplicatedException;
import com.oocourse.uml1.interact.exceptions.user.AttributeNotFoundException;
import com.oocourse.uml1.models.common.Visibility;
import com.oocourse.uml1.models.elements.UmlAssociation;
import com.oocourse.uml1.models.elements.UmlClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MyClass implements MyObject {
    private String name;
    private String id;
    private Visibility visibility;

    private MyClass parent = null;
    private MyClass topParent = null;

    private HashSet<MyOperation> selfOperation = new HashSet<>();
    //private HashSet<MyOperation> allOperation = new HashSet<>();
    private HashMap<String, HashSet<MyOperation>> mapOperation =
        new HashMap<>();

    private HashSet<MyInterface> selfInterface = new HashSet<>();
    private HashSet<MyInterface> allParentInterface = new HashSet<>();
    private HashSet<MyInterface> allInterface = null;

    private HashSet<MyAttribute> selfAttribute = new HashSet<>();
    private HashSet<MyAttribute> allAttribute = new HashSet<>();
    private HashMap<String, HashSet<MyAttribute>> mapAttribute =
        new HashMap<String, HashSet<MyAttribute>>();

    private HashSet<MyClass> association = new HashSet<>();
    //private HashSet<UmlAssociation> ass = new HashSet<>();
    private int ass = 0;
    private HashSet<UmlAssociation> assInterface = new HashSet<>();
    private int associatenum = -1;

    MyClass(String name, String id, Visibility visibility) {
        this.name = name;
        this.id = id;
        this.visibility = visibility;
    }

    MyClass(UmlClass c) {
        this.name = c.getName();
        this.id = c.getId();
        this.visibility = c.getVisibility();
    }

    public void addInterfaceAssociation(UmlAssociation a) {
        this.assInterface.add(a);
    }

    public String getname() {
        return this.name;
    }

    public String getId() {
        return this.id;
    }

    public Visibility getVisibility() {
        return this.visibility;
    }

    public MyClass getTopParent() {
        if (topParent == null) {
            MyClass.setfind(this);
        }
        return topParent;
    }

    public int getAssNum() {
        if (topParent == null) {
            MyClass.setfind(this);
        }
        return associatenum;
    }

    public HashMap<Visibility, Integer> getClassOperationVisibility(
        String opname) {
        HashMap<Visibility, Integer> ret = new HashMap<>();
        ret.put(Visibility.PUBLIC, 0);
        ret.put(Visibility.PRIVATE, 0);
        ret.put(Visibility.PROTECTED, 0);
        ret.put(Visibility.PACKAGE, 0);
        ret.put(Visibility.DEFAULT, 0);
        if (mapOperation.containsKey(opname)) {
            for (MyOperation op : mapOperation.get(opname)) {
                int tmp = ret.get(op.getVisibility());
                ret.put(op.getVisibility(), tmp + 1);
            }
        }
        return ret;
    }

    public Visibility getClassAttributeVisibility(String attrname)
        throws AttributeNotFoundException, AttributeDuplicatedException {
        if (topParent == null) {
            MyClass.setfind(this);
        }
        if (!mapAttribute.containsKey(attrname)) {
            throw new AttributeNotFoundException(this.name, attrname);
        }
        if (mapAttribute.get(attrname).size() >= 2) {
            throw new AttributeDuplicatedException(this.name, attrname);
        }
        return new ArrayList<MyAttribute>(mapAttribute.get(attrname))
            .get(0).getVisibility();
    }

    public int opertionCountQuery(OperationQueryType t) {
        if (t == OperationQueryType.ALL) {
            return selfOperation.size();
        }
        else if (t == OperationQueryType.RETURN
            || t == OperationQueryType.NON_RETURN) {
            int count = 0;
            for (MyOperation op : selfOperation) {
                if (op.hasReturnValue()) {
                    count++;
                }
            }
            if (t == OperationQueryType.NON_RETURN) {
                return selfOperation.size() - count;
            }
            else {
                return count;
            }
        }
        else if (t == OperationQueryType.PARAM
            || t == OperationQueryType.NON_PARAM) {
            int count = 0;
            for (MyOperation op : selfOperation) {
                if (op.hasParameter()) {
                    count++;
                }
            }
            if (t == OperationQueryType.NON_PARAM) {
                return selfOperation.size() - count;
            }
            else {
                return count;
            }
        }
        return -1;
    }

    public int attributeCountQuery(AttributeQueryType t) {
        if (t == AttributeQueryType.SELF_ONLY) {
            return selfAttribute.size();
        }
        else {
            if (topParent == null) {
                MyClass.setfind(this);
            }
            return allAttribute.size();
        }
    }

    public List<AttributeClassInformation> getInformationNotHidden() {
        List<AttributeClassInformation> tmp = new ArrayList<>();
        if (topParent == null) {
            MyClass.setfind(this);
        }
        for (MyAttribute a : allAttribute) {
            if (a.getVisibility() != Visibility.PRIVATE) {
                tmp.add(new AttributeClassInformation(a.getname(),
                    a.getBeloningClass().name));
            }
        }
        return tmp;

    }

    public HashSet<MyInterface> getAllInterface() {
        if (allInterface != null) {
            return allInterface;
        }
        else {
            allInterface = new HashSet<>();
        }
        if (topParent == null) {
            MyClass.setfind(this);

        }
        for (MyInterface i : selfInterface) {
            allInterface.addAll(MyInterface.getAllInterfaces(i));
        }
        allInterface.addAll(allParentInterface);
        for (MyInterface i : allParentInterface) {
            allInterface.addAll(MyInterface.getAllInterfaces(i));
        }
        return allInterface;
    }

    public HashSet<MyClass> getAssociation() {
        if (topParent == null) {
            MyClass.setfind(this);
        }
        return this.association;
    }

    public void associate(MyClass c, UmlAssociation a) {
        //System.err.println(this.name + "associate" + c.getname());
        //this.ass.add(a);
        this.ass++;
        this.association.add(c);
    }

    public void setParent(MyClass parent) {
        this.parent = parent;
    }

    public void addInterface(MyInterface myinterface) {
        selfInterface.add(myinterface);
        allParentInterface.add(myinterface);
        //String name = myinterface.getname();
    }

    public void addOperation(MyOperation myoperation) {
        selfOperation.add(myoperation);
        //allOperation.add(myoperation);
        String name = myoperation.getname();
        if (!mapOperation.containsKey(name)) {
            mapOperation.put(name, new HashSet<MyOperation>());
        }
        mapOperation.get(name).add(myoperation);
    }

    public void addAttribute(MyAttribute myattribute) {
        selfAttribute.add(myattribute);
        allAttribute.add(myattribute);
        if (!mapAttribute.containsKey(myattribute.getname())) {
            mapAttribute.put(myattribute.getname(), new HashSet<MyAttribute>());
        }
        mapAttribute.get(myattribute.getname()).add(myattribute);
    }

    public static MyClass setfind(MyClass x) {
        if (x.parent == null) {
            x.parent = x;
            x.topParent = x;
            x.associatenum = x.ass + x.assInterface.size();
        }
        else if (x.topParent == null) {
            x.topParent = setfind(x.parent);
            //x.allOperation.addAll(x.topParent.allOperation);
            //System.out.print(x.name+":"+x.parent.assoociation);
            //System.out.println(x.parent.assoociation);
            x.association.addAll(x.parent.association);
            //x.ass.addAll(x.parent.ass);
            x.associatenum =
                x.parent.associatenum + x.ass + x.assInterface.size();
            x.allParentInterface.addAll(x.parent.allParentInterface);
            x.allAttribute.addAll(x.parent.allAttribute);
            for (MyAttribute a : x.allAttribute) {
                String tmpname = a.getname();
                if (!x.mapAttribute.containsKey(tmpname)) {
                    //System.out.println(x.toString()+tmpname);
                    x.mapAttribute.put(tmpname, new HashSet<>());
                }
                x.mapAttribute.get(tmpname).add(a);
            }
        }
        return x.topParent;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        MyClass tmp = (MyClass) o;
        if (tmp.id.equals(this.id)) {
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public String toString() {
        return this.name;
    }

}
