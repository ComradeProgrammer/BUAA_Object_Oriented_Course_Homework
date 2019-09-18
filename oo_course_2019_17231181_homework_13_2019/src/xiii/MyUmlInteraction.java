package xiii;

import com.oocourse.uml1.interact.common.AttributeClassInformation;
import com.oocourse.uml1.interact.common.AttributeQueryType;
import com.oocourse.uml1.interact.common.OperationQueryType;
import com.oocourse.uml1.interact.exceptions.user.AttributeDuplicatedException;
import com.oocourse.uml1.interact.exceptions.user.AttributeNotFoundException;
import com.oocourse.uml1.interact.exceptions.user.ClassDuplicatedException;
import com.oocourse.uml1.interact.exceptions.user.ClassNotFoundException;
import com.oocourse.uml1.interact.format.UmlInteraction;
import com.oocourse.uml1.models.common.ElementType;
import com.oocourse.uml1.models.common.Visibility;
import com.oocourse.uml1.models.elements.UmlAssociation;
import com.oocourse.uml1.models.elements.UmlAssociationEnd;
import com.oocourse.uml1.models.elements.UmlAttribute;
import com.oocourse.uml1.models.elements.UmlClass;
import com.oocourse.uml1.models.elements.UmlElement;
import com.oocourse.uml1.models.elements.UmlGeneralization;
import com.oocourse.uml1.models.elements.UmlInterface;
import com.oocourse.uml1.models.elements.UmlInterfaceRealization;
import com.oocourse.uml1.models.elements.UmlOperation;
import com.oocourse.uml1.models.elements.UmlParameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class MyUmlInteraction implements UmlInteraction {
    private HashMap<String, MyObject> objects = new HashMap<>();
    private HashMap<String, UmlElement> elementmap = new HashMap<>();
    private HashSet<MyClass> classes = new HashSet<>();
    //private HashSet<MyInterface> interfaces = new HashSet<>();
    private HashMap<String, HashSet<MyClass>> classmap = new HashMap<>();
    private HashSet<MyOperation> operations = new HashSet<>();
    private HashSet<MyAttribute> attributes = new HashSet<>();
    private HashSet<UmlAssociation> associations = new HashSet<>();
    private HashSet<UmlParameter> parameters = new HashSet<>();
    private HashSet<UmlGeneralization> generalizations = new HashSet<>();
    private HashMap<String, UmlAssociationEnd> ends = new HashMap<>();
    private HashSet<UmlInterfaceRealization> realziations = new HashSet<>();

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
                break;
            case UML_INTERFACE:
                UmlInterface tmpi = (UmlInterface) e;
                MyInterface tmpi2 = new MyInterface(tmpi);
                objects.put(e.getId(), tmpi2);
                //interfaces.add(tmpi2);
                break;
            case UML_ATTRIBUTE:
                UmlAttribute tmpa = (UmlAttribute) e;
                if (elementmap.get(e.getParentId()).getElementType()
                    == ElementType.UML_CLASS) {
                    MyAttribute tmpa2 = new MyAttribute(tmpa, objects);
                    objects.put(e.getId(), tmpa2);
                    attributes.add(tmpa2);
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
            if (elementmap.get(sourceid).getElementType()
                == ElementType.UML_CLASS) {
                MyClass tmpc = (MyClass) (objects.get(sourceid));
                tmpc.setParent((MyClass) (objects.get(targetid)));
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
                tmpc1.associate(tmpc2, a);
                tmpc2.associate(tmpc1, a);
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
        //FINISHED

    }


    /**
     * 获取类数量
     * 指令：CLASS_COUNT
     *
     * @return 类数量
     */
    public int getClassCount() {
        return classes.size();
    }

    /**
     * 获取类操作数量
     * 指令：CLASS_OPERATION_COUNT
     *
     * @param className 类名
     * @param queryType 操作类型
     * @return 指定类型的操作数量
     * @throws ClassNotFoundException   类未找到异常
     * @throws ClassDuplicatedException 类重复异常
     */
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

    /**
     * 获取类属性数量
     * 指令：CLASS_ATTR_COUNT
     *
     * @param className 类名
     * @param queryType 操作类型
     * @return 指定类型的操作数量
     * @throws ClassNotFoundException   类未找到异常
     * @throws ClassDuplicatedException 类重复异常
     */
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

    /**
     * 获取类关联数量
     * 指令：CLASS_ASSO_COUNT
     *
     * @param className 类名
     * @return 类关联数量
     * @throws ClassNotFoundException   类未找到异常
     * @throws ClassDuplicatedException 类重复异常
     */
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

    /**
     * 获取与类相关联的类列表
     * 指令：CLASS_ASSO_CLASS_LIST
     *
     * @param className 类名
     * @return 与类关联的列表
     * @throws ClassNotFoundException   类未找到异常
     * @throws ClassDuplicatedException 类重复异常
     */
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

    /**
     * 统计类操作可见性
     * 指令：CLASS_OPERATION_VISIBILITY
     *
     * @param className     类名
     * @param operationName 操作名
     * @return 类操作可见性统计结果
     * @throws ClassNotFoundException   类未找到异常
     * @throws ClassDuplicatedException 类重复异常
     */
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

    /**
     * 获取类属性可见性
     * 指令：CLASS_ATTR_VISIBILITY
     *
     * @param className     类名
     * @param attributeName 属性名
     * @return 属性可见性
     * @throws ClassNotFoundException       类未找到异常
     * @throws ClassDuplicatedException     类重复异常
     * @throws AttributeNotFoundException   属性未找到异常
     * @throws AttributeDuplicatedException 属性重复异常
     */
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

    ;

    /**
     * 获取顶级父类
     * 指令：CLASS_TOP_BASE
     *
     * @param className 类名
     * @return 顶级父类名
     * @throws ClassNotFoundException   类未找到异常
     * @throws ClassDuplicatedException 类重复异常
     */
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

    /**
     * 获取实现的接口列表
     * 指令：CLASS_IMPLEMENT_INTERFACE_LIST
     *
     * @param className 类名
     * @return 实现的接口列表
     * @throws ClassNotFoundException   类未找到异常
     * @throws ClassDuplicatedException 类重复异常
     */
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

    /**
     * 获取类中未隐藏的属性
     * 即违背了面向对象设计中的隐藏信息原则的属性
     * 指令：CLASS_INFO_HIDDEN
     *
     * @param className 类名
     * @return 未隐藏的属性信息列表
     * @throws ClassNotFoundException   类未找到异常
     * @throws ClassDuplicatedException 类重复异常
     */
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
}
