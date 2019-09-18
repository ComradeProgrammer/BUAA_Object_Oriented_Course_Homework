package xiv;

import com.oocourse.uml2.models.common.Direction;
import com.oocourse.uml2.models.common.Visibility;
import com.oocourse.uml2.models.elements.UmlOperation;
import com.oocourse.uml2.models.elements.UmlParameter;

import java.util.HashMap;

public class MyOperation implements MyObject {
    private String name;
    private String id;
    private Visibility visibility;
    private MyClass belongingClass = null;
    private boolean haveParameter = false;
    private boolean haveReturnValue = false;
    private String belongingClassId;
    private HashMap<String, MyObject> objectMap;

    public MyOperation(String name, String id, Visibility visibility,
                       HashMap<String, MyObject> objectMap) {
        this.name = name;
        this.id = id;
        this.visibility = visibility;
        this.objectMap = objectMap;
    }

    public MyOperation(UmlOperation o, HashMap<String, MyObject> objectMap) {
        this.name = o.getName();
        this.id = o.getId();
        this.visibility = o.getVisibility();
        this.objectMap = objectMap;
        //this.belongingClass = (MyClass) (objectMap.get(o.getParentId()));
        this.belongingClassId = o.getParentId();

    }

    public void setBelongingClass() {
        this.belongingClass = (MyClass) (objectMap.get(belongingClassId));
        this.belongingClass.addOperation(this);
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

    public MyClass getBelongingClass() {
        return this.belongingClass;
    }

    public boolean hasParameter() {
        return haveParameter;
    }

    public boolean hasReturnValue() {
        return this.haveReturnValue;
    }

    public void addParameter(UmlParameter p) {
        if (p.getDirection() == Direction.RETURN) {
            haveReturnValue = true;
        }
        else if (p.getDirection() == Direction.IN) {
            haveParameter = true;
        }
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        MyOperation tmp = (MyOperation) o;
        if (tmp.id.equals(this.id)) {
            return true;
        }
        else {
            return false;
        }
    }
}
