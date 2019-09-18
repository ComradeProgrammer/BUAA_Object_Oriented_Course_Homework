package xiv;

import com.oocourse.uml2.models.common.Visibility;
import com.oocourse.uml2.models.elements.UmlInterface;

import java.util.ArrayList;
import java.util.HashSet;

public class MyInterface implements MyObject {
    private String name;
    private String id;
    private Visibility visibility;
    private HashSet<MyInterface> selfInterface = new HashSet<>();
    private HashSet<MyInterface> allInterface = null;

    public MyInterface(String name, String id, Visibility visibility) {
        this.name = name;
        this.id = id;
        this.visibility = visibility;
    }

    public MyInterface(UmlInterface i) {
        this.name = i.getName();
        this.id = i.getId();
        this.visibility = i.getVisibility();
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

    public void addInterface(MyInterface i) {
        selfInterface.add(i);
        // allInterface.add(i);
    }

    public static HashSet<MyInterface> getAllInterfaces(MyInterface x) {
        if (x.allInterface == null) {
            x.allInterface = new HashSet<>();
            x.allInterface.addAll(x.selfInterface);
            for (MyInterface i : x.selfInterface) {
                HashSet<MyInterface> tmp = getAllInterfaces(i);
                x.allInterface.addAll(tmp);
            }
        }
        return x.allInterface;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        MyInterface tmp = (MyInterface) o;
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
    /*public static void main(String[] args) {
        MyInterface[] fuck=new MyInterface[10];
        for(int i=0;i<10;i++){
            fuck[i]=new MyInterface(i+"",i+"",Visibility.PUBLIC);
        }
        fuck[3].addInterface(fuck[0]);
        fuck[3].addInterface(fuck[1]);
        fuck[4].addInterface(fuck[0]);
        fuck[4].addInterface(fuck[2]);
        fuck[5].addInterface(fuck[1]);
        fuck[5].addInterface(fuck[2]);
        fuck[6].addInterface(fuck[3]);
        fuck[6].addInterface(fuck[4]);
        fuck[6].addInterface(fuck[5]);
        fuck[7].addInterface(fuck[5]);
        fuck[7].addInterface(fuck[4]);
        for(int i=0;i<8;i++){
            System.out.println(MyInterface.getAllInterfaces(fuck[i]));
        }
    }*/
}

/*
public class MyInterface implements MyObject {
    private String name;
    private String id;
    private Visibility visibility;
    private ArrayList<MyInterface> selfInterface = new ArrayList<>();
    private ArrayList<MyInterface> allInterface = null;

    public MyInterface(String name, String id, Visibility visibility) {
        this.name = name;
        this.id = id;
        this.visibility = visibility;
    }

    public MyInterface(UmlInterface i) {
        this.name = i.getName();
        this.id = i.getId();
        this.visibility = i.getVisibility();
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

    public void addInterface(MyInterface i) {
        selfInterface.add(i);
        // allInterface.add(i);
    }

    public static ArrayList<MyInterface> getAllInterfaces(MyInterface x) {
        if (x.allInterface == null) {
            x.allInterface = new ArrayList<>();
            x.allInterface.addAll(x.selfInterface);
            for (MyInterface i : x.selfInterface) {
                ArrayList<MyInterface> tmp = getAllInterfaces(i);
                x.allInterface.addAll(tmp);
            }
        }
        return x.allInterface;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        MyInterface tmp = (MyInterface) o;
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

    public boolean check009() {
        HashSet<MyInterface> tmp = new HashSet<>(getAllInterfaces(this));
        if (tmp.size() < allInterface.size()) {
            return false;
        }
        return true;
    }

}*/
