package xiii;

import com.oocourse.uml1.models.common.Visibility;
import com.oocourse.uml1.models.elements.UmlAttribute;

import java.util.HashMap;

public class MyAttribute implements MyObject {
    private String name;
    private String id;
    private Visibility visibility;
    private MyClass belongingClass;
    private HashMap<String, MyObject> objectMap;
    private String belongingClassId;

    public MyAttribute(String name, String id, Visibility visibility,
                       MyClass beloningClass,
                       HashMap<String, MyObject> objectMap) {
        this.name = name;
        this.id = id;
        this.visibility = visibility;
        this.objectMap = objectMap;
        //this.beloningClass = beloningClass;
    }

    public MyAttribute(UmlAttribute a, HashMap<String, MyObject> objectMap) {
        visibility = a.getVisibility();
        name = a.getName();
        id = a.getId();
        this.objectMap = objectMap;
        //this.beloningClass = (MyClass) (objectMap.get(a.getParentId()));
        this.belongingClassId = a.getParentId();
    }

    public void setBelongingClass() {
        this.belongingClass = (MyClass) (objectMap.get(belongingClassId));
        this.belongingClass.addAttribute(this);
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

    public MyClass getBeloningClass() {
        return this.belongingClass;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        MyAttribute tmp = (MyAttribute) o;
        if (tmp.id.equals(this.id)) {
            return true;
        }
        else {
            return false;
        }
    }

}
