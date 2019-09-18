package xi;

import com.oocourse.specs3.models.NodeIdNotFoundException;
import com.oocourse.specs3.models.NodeNotConnectedException;
import com.oocourse.specs3.models.Path;
import com.oocourse.specs3.models.PathIdNotFoundException;
import com.oocourse.specs3.models.PathNotFoundException;
import com.oocourse.specs3.models.RailwaySystem;

import java.util.HashMap;
import java.util.LinkedList;

public class MyRailwaySystem implements RailwaySystem {
    private HashMap<Integer, Path> pathmap = new HashMap<>();
    private HashMap<Path, Integer> reversemap = new HashMap<>();
    private HashMap<Integer, Integer> distinctnodemap = new HashMap<>();
    //不同的点和其数目对应
    private HashMap<Integer, LinkedList<Integer>> disjointSets =
        new HashMap<>();//点和其对应的并查集对应
    private HashMap<Integer, HashMap<Integer, Integer>> graphmap =
        new HashMap<>();//hashmap构建的二维数组
    private HashMap<Integer, HashMap<Integer, Integer>> shortestPathMap =
        new HashMap<>();//存储最短路的结果的map
    private int blocknums;
    private DetailedGraph price = new DetailedGraph(1, 2, 0);
    private DetailedGraph transfer = new DetailedGraph(0, 1, 0);
    private UnpleasantGraph unplesant = new UnpleasantGraph();

    public MyRailwaySystem() {

    }

    private int getShortestDistance(int i, int j) {
        HashMap<Integer, Integer> tmp = shortestPathMap.get(i);
        if (!tmp.containsKey(j)) {
            bfs(i);
            tmp = shortestPathMap.get(i);
        }
        return tmp.get(j);
    }

    private void clearShortestPathMap() {
        for (int i : distinctnodemap.keySet()) {
            shortestPathMap.put(i, new HashMap<Integer, Integer>());
        }
    }

    private void bfs(int src) {
        HashMap<Integer, Integer> res = new HashMap<>();
        LinkedList<Integer> list1 = new LinkedList<>();
        LinkedList<Integer> list2 = new LinkedList<>();
        list1.add(src);
        int i = 0;
        res.put(src, 0);
        while (true) {
            if (list1.isEmpty()) {
                if (list2.isEmpty()) {
                    break;
                }
                i++;
                LinkedList<Integer> tmp = list2;
                list2 = list1;
                list1 = tmp;
                list2.clear();
                continue;
            }
            int point = list1.removeFirst();

            for (int next : graphmap.get(point).keySet()) {
                if (next != point && !res.containsKey(next)) {
                    list2.add(next);
                    res.put(next, i + 1);
                }
            }
        }
        for (int j : res.keySet()) {
            shortestPathMap.get(src).put(j, res.get(j));
            shortestPathMap.get(j).put(src, res.get(j));
        }
        //System.out.println(res);
    }

    private int getedge(int i, int j) {
        HashMap<Integer, Integer> tmp = graphmap.get(i);
        if (!tmp.containsKey(j)) {
            return 0;
        }
        return tmp.get(j);
    }

    private void setedge(int i, int j, int num) {
        HashMap<Integer, Integer> tmp = graphmap.get(i);
        if (num == 0) {
            if (tmp.containsKey(j)) {
                tmp.remove(j);
            }
        }
        else {
            tmp.put(j, num);
        }
    }

    private void validateDisjointSetByEdge(int i, int j) {
        LinkedList<Integer> tmp1 = disjointSets.get(i);
        LinkedList<Integer> tmp2 = disjointSets.get(j);
        if (tmp1 != tmp2) {
            tmp1.addAll(tmp2);
            blocknums--;
            for (int t : tmp2) {
                if (disjointSets.get(t) == tmp2) {
                    disjointSets.put(t, tmp1);

                }
            }
        }
    }

    private void validateDisjointSetByPath(Path path) {
        int[] tmp = new int[250];
        int pt = 0;
        for (int i : path) {
            tmp[pt++] = i;
        }
        for (int i = 0; i < path.size() - 1; i++) {
            validateDisjointSetByEdge(tmp[i], tmp[i + 1]);
        }
    }

    private void regenerateDisjointset() {
        for (int i : distinctnodemap.keySet()) {
            disjointSets.put(i, new LinkedList<Integer>());
            disjointSets.get(i).add(i);
        }
        blocknums = disjointSets.size();
        for (Path path : reversemap.keySet()) {
            validateDisjointSetByPath(path);
        }
    }

    private static int count = 0;

    public int size() {
        return pathmap.size();
    }

    public boolean containsPath(Path path) {
        return reversemap.containsKey(path);
    }

    public boolean containsPathId(int pathId) {
        return pathmap.containsKey(pathId);
    }

    public Path getPathById(int pathId) throws PathIdNotFoundException {
        if (!pathmap.containsKey(pathId)) {
            throw new PathIdNotFoundException(pathId);
        }
        return pathmap.get(pathId);
    }

    public int getPathId(Path path) throws PathNotFoundException {
        if (path == null || !path.isValid() || !pathmap.containsValue(path)) {
            throw new PathNotFoundException(path);
        }
        return reversemap.get(path);
    }

    public int addPath(Path path) {

        if (path == null || path.isValid() == false) {
            return 0;
        }
        int[] tmp = new int[250];
        int pt = 0;
        if (!reversemap.containsKey(path)) {
            //没有对应的path
            pathmap.put(++count, path);
            reversemap.put(path, count);
            for (int i : path) {
                tmp[pt++] = i;
                if (distinctnodemap.containsKey(i)) {
                    //如果我们已经有这个顶点了
                    distinctnodemap.put(i, distinctnodemap.get(i) + 1);
                }
                else {
                    //如果这是一个全新的顶点
                    distinctnodemap.put(i, 1);
                    graphmap.put(i, new HashMap<Integer, Integer>());
                    disjointSets.put(i, new LinkedList<Integer>());
                    disjointSets.get(i).add(i);
                    blocknums++;
                    shortestPathMap.put(i, new HashMap<Integer, Integer>());
                }
            }
            //开始处理path中的边
            for (int i = 0; i < path.size() - 1; i++) {
                setedge(tmp[i], tmp[i + 1], getedge(tmp[i], tmp[i + 1]) + 1);
                setedge(tmp[i + 1], tmp[i], getedge(tmp[i + 1], tmp[i]) + 1);
            }
            validateDisjointSetByPath(path);
            clearShortestPathMap();
            price.addPath(path, count);
            transfer.addPath(path, count);
            unplesant.addPath(path, count);
            return count;
        }
        //有重复的path
        return reversemap.get(path);
    }

    public int removePath(Path path) throws PathNotFoundException {
        if (path == null || !path.isValid() || !pathmap.containsValue(path)) {
            throw new PathNotFoundException(path);
        }
        int id = reversemap.get(path);
        reversemap.remove(path);
        pathmap.remove(id);
        int[] tmp = new int[250];
        int pt = 0;
        for (int i : path) {
            tmp[pt++] = i;
        }
        for (int i = 0; i < path.size() - 1; i++) {
            setedge(tmp[i], tmp[i + 1], getedge(tmp[i], tmp[i + 1]) - 1);
            setedge(tmp[i + 1], tmp[i], getedge(tmp[i + 1], tmp[i]) - 1);
        }
        for (int i : path) {
            if (distinctnodemap.get(i) > 1) {
                //说明这个顶点依然存在
                distinctnodemap.put(i, distinctnodemap.get(i) - 1);
            }
            else {
                //说明这个顶点已经不再存在
                distinctnodemap.remove(i);
                graphmap.remove(i);
                disjointSets.remove(i);
                shortestPathMap.remove(i);
            }
        }
        regenerateDisjointset();
        clearShortestPathMap();
        price.removePath(path, id);
        transfer.removePath(path, id);
        unplesant.removePath(path, id);
        return id;
    }

    public void removePathById(int pathId) throws PathIdNotFoundException {
        if (!pathmap.containsKey(pathId)) {
            throw new PathIdNotFoundException(pathId);
        }
        Path tmp = pathmap.get(pathId);
        pathmap.remove(pathId);
        reversemap.remove(tmp);
        int[] temp = new int[250];
        int pt = 0;
        for (int i : tmp) {
            temp[pt++] = i;
        }
        for (int i = 0; i < tmp.size() - 1; i++) {
            setedge(temp[i], temp[i + 1], getedge(temp[i], temp[i + 1]) - 1);
            setedge(temp[i + 1], temp[i], getedge(temp[i + 1], temp[i]) - 1);
        }
        for (int i : tmp) {

            if (distinctnodemap.get(i) > 1) {
                //说明这个顶点依然存在
                distinctnodemap.put(i, distinctnodemap.get(i) - 1);
            }
            else {
                //说明这个顶点已经不再存在
                distinctnodemap.remove(i);
                graphmap.remove(i);
                disjointSets.remove(i);
                shortestPathMap.remove(i);
            }
        }
        regenerateDisjointset();
        clearShortestPathMap();
        price.removePath(tmp, pathId);
        transfer.removePath(tmp, pathId);
        unplesant.removePath(tmp, pathId);
    }

    public int getDistinctNodeCount() {
        return distinctnodemap.size();
    }

    public boolean containsNode(int nodeId) {
        return distinctnodemap.containsKey(nodeId);
    }

    public boolean containsEdge(int fromNodeId, int toNodeId) {
        if (!containsNode(fromNodeId) || !containsNode(toNodeId)) {
            return false;
        }
        /*if (fromNodeId == toNodeId) {
            return true;
        }*/
        if (getedge(fromNodeId, toNodeId) > 0) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean isConnected(int fromNodeId, int toNodeId)
        throws NodeIdNotFoundException {
        if (!containsNode(fromNodeId)) {
            throw new NodeIdNotFoundException(fromNodeId);
        }
        else if (!containsNode(toNodeId)) {
            throw new NodeIdNotFoundException(toNodeId);
        }
        if (fromNodeId == toNodeId) {
            return true;
        }
        if (disjointSets.get(fromNodeId) == disjointSets.get(toNodeId)) {
            return true;
        }
        return false;
    }

    public int getShortestPathLength(int fromNodeId, int toNodeId)
        throws NodeIdNotFoundException, NodeNotConnectedException {
        if (!isConnected(fromNodeId, toNodeId)) {
            throw new NodeNotConnectedException(fromNodeId, toNodeId);
        }
        if (fromNodeId == toNodeId) {
            return 0;
        }
        return getShortestDistance(fromNodeId, toNodeId);
    }

    public int getLeastTicketPrice(int fromNodeId, int toNodeId)
        throws NodeIdNotFoundException, NodeNotConnectedException {
        if (!containsNode(fromNodeId)) {
            throw new NodeIdNotFoundException(fromNodeId);
        }
        if (!containsNode(toNodeId)) {
            throw new NodeIdNotFoundException(toNodeId);
        }
        if (!isConnected(fromNodeId, toNodeId)) {
            throw new NodeNotConnectedException(fromNodeId, toNodeId);
        }
        return price.getShortestPath(fromNodeId, toNodeId);
    }

    public int getLeastTransferCount(int fromNodeId, int toNodeId)
        throws NodeIdNotFoundException, NodeNotConnectedException {
        if (!containsNode(fromNodeId)) {
            throw new NodeIdNotFoundException(fromNodeId);
        }
        if (!containsNode(toNodeId)) {
            throw new NodeIdNotFoundException(toNodeId);
        }
        if (!isConnected(fromNodeId, toNodeId)) {
            throw new NodeNotConnectedException(fromNodeId, toNodeId);
        }
        return transfer.getShortestPath(fromNodeId, toNodeId);
    }

    public int getLeastUnpleasantValue(int fromNodeId, int toNodeId)
        throws NodeIdNotFoundException, NodeNotConnectedException {
        if (!containsNode(fromNodeId)) {
            throw new NodeIdNotFoundException(fromNodeId);
        }
        if (!containsNode(toNodeId)) {
            throw new NodeIdNotFoundException(toNodeId);
        }
        if (!isConnected(fromNodeId, toNodeId)) {
            throw new NodeNotConnectedException(fromNodeId, toNodeId);
        }
        return unplesant.getShortestPath(fromNodeId, toNodeId);
    }

    public int getConnectedBlockCount() {
        return blocknums;
    }

    public int getUnpleasantValue(Path path, int fromIndex,
                                  int toIndex) {
        return 0;
    }
    /*
    public static void main(String[] args) throws Exception {
        MyPath p1=new MyPath(10,9,8,7,6,6,6,6,6,6,6,5,5,5,5,5,5,5,4,3,2,1);
        MyPath p2=new MyPath(10,9,8,7,6,6,6,6,6,6,6,5,5,5,5,5,5,5,4,3,2,2,1);
        MyRailwaySystem m=new MyRailwaySystem();
        m.addPath(p1);
        m.addPath(p2);
        m.removePath(p2);
    }*/
}
