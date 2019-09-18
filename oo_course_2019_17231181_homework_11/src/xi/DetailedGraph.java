package xi;

import com.oocourse.specs3.models.Path;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

public class DetailedGraph {
    private static int count = 0;
    private final int stationEdgeWeight;
    private final int transferEdgeWeight;
    private final int inOutEdgeWeight;

    DetailedGraph(int stationEdgeWeight, int transferEdgeWeight,
                  int inOutEdgeWeight) {
        this.inOutEdgeWeight = inOutEdgeWeight;
        this.stationEdgeWeight = stationEdgeWeight;
        this.transferEdgeWeight = transferEdgeWeight;
    }

    /*节点编号-->(线路id,新的换乘节点编号)*/
    private HashMap<Integer, HashMap<Integer, Integer>> platforms =
        new HashMap<>();
    /*节点编号-->（入口编号，出口编号）*/
    private HashMap<Integer, int[]> stations = new HashMap<>();
    /*有向图*/
    private HashMap<Integer, HashMap<Integer, Integer>> map = new HashMap<>();
    /*最短路缓存*/
    private HashMap<Integer, HashMap<Integer, Integer>> buffer =
        new HashMap<>();

    private int getEntrance(int node) {
        return stations.get(node)[0];
    }

    private int getExit(int node) {
        return stations.get(node)[1];
    }

    private int getPlatform(int node, int pathid) {
        return platforms.get(node).get(pathid);
    }

    private void addPoint(int i) {
        map.put(i, new HashMap<>());
        buffer.put(i, new HashMap<>());
    }

    private void addEdge(int i, int j, int weight) {
        map.get(i).put(j, weight);
        map.get(j).put(i, weight);
    }

    private void removeEdge(int i, int j) {
        if (!map.containsKey(i) || !map.containsKey(j)) {
            return;
        }
        map.get(i).remove(j);
        map.get(j).remove(i);
    }

    private void removePoint(int i) {
        if (!map.containsKey(i)) {
            return;
        }
        map.remove(i);
        buffer.remove(i);
    }

    private void clearBuffer() {
        for (int i : buffer.keySet()) {
            buffer.get(i).clear();
        }
    }

    private int getEdge(int i, int j) {
        if (i == j) {
            return 0;
        }
        return map.get(i).get(j);
    }

    public int u(int from, int to) {
        return stationEdgeWeight;
    }

    public void addPath(Path path, int id) {
        clearBuffer();
        int[] tmp = new int[85];
        int i = 0;
        for (int node : path) {
            tmp[i++] = node;
            if (!stations.containsKey(node)) {
                /*if this is a new station,map it in the stations and
                platforms */
                int[] stationtmp = new int[2];
                stationtmp[0] = count++;
                stationtmp[1] = count++;
                addPoint(stationtmp[0]);
                addPoint(stationtmp[1]);
                /*set the entrance and exit*/
                stations.put(node, stationtmp);
                platforms.put(node, new HashMap<>());
            }
            /*set the platform*/
            if (!platforms.get(node).containsKey(id)) {
                int platform = count++;
                addPoint(platform);
                platforms.get(node).put(id, platform);
                /*connect new platform with entrance and exit*/
                int entrance = getEntrance(node);
                int exit = getExit(node);
                map.get(entrance).put(platform, inOutEdgeWeight);
                map.get(platform).put(exit, inOutEdgeWeight);
                /*connect platforms in this station*/
                ArrayList<Integer> oldplatforms =
                    new ArrayList<>(platforms.get(node).values());
                for (int k : oldplatforms) {
                    if (k != platform) {
                        addEdge(k, platform, transferEdgeWeight);
                    }
                }
            }

        }
        for (int j = 0; j < i - 1; j++) {
            int plat1 = getPlatform(tmp[j], id);
            int plat2 = getPlatform(tmp[j + 1], id);
            addEdge(plat1, plat2, u(tmp[j], tmp[j + 1]));
        }
    }

    public void removePath(Path path, int id) {
        clearBuffer();
        HashSet<Integer> fuck = new HashSet<Integer>();
        for (int node : path) {
            fuck.add(node);
        }
        for (int node : fuck) {
            /*disconnect the platform*/
            int platform = getPlatform(node, id);
            ArrayList<Integer> oldplatforms =
                new ArrayList<>(platforms.get(node).values());
            for (int j : oldplatforms) {
                if (platform != j) {
                    removeEdge(j, platform);
                }
            }
            /*destroy the platform*/
            int entrance = getEntrance(node);
            int exit = getExit(node);
            platforms.get(node).remove(id);
            map.get(entrance).remove(platform);
            removePoint(platform);
        }
    }

    private void heapdij(int src) {
        HashMap<Integer, Integer> srcmap = buffer.get(src);

        PriorityQueue<int[]> heap =
            new PriorityQueue<>(new Comparator<int[]>() {
                @Override
                public int compare(int[] o1, int[] o2) {
                    return Integer.compare(o1[1], o2[1]);
                }
            });
        int[] init = {src, 0};
        heap.add(init);
        while (!heap.isEmpty()) {
            int[] tmp = heap.poll();
            int target = tmp[0];
            int weight = tmp[1];
            if (srcmap.containsKey(target)) {
                continue;
            }
            srcmap.put(target, weight);
            for (int i : map.get(target).keySet()) {
                int[] newitem = new int[2];
                newitem[0] = i;
                newitem[1] = weight + getEdge(target, i);
                heap.offer(newitem);
            }
        }
    }

    public int getShortestPath(int i, int j) {
        int from = getEntrance(i);
        int to = getExit(j);
        if (!buffer.get(from).containsKey(to)) {
            heapdij(from);
        }
        return buffer.get(from).get(to);
    }


    /*public static void main(String[] args) {
        DetailedGraph tp = new DetailedGraph(64, 32, 0);
        MyPath p1 = new MyPath(1, 2, 3, 4);
        MyPath p2 = new MyPath(5, 2, 6, 7);
        MyPath p3 = new MyPath(8, 7, 9, 10);
        MyPath p4 = new MyPath(1, 7, 11);
        tp.addPath(p1, 1);
        tp.addPath(p2, 2);
        tp.addPath(p3, 3);
        tp.addPath(p4, 4);
        tp.removePath(p4,4);

        System.out.println(tp.getCheapestPrice(2,5));
    }*/
}
