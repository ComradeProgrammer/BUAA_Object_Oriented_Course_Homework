package ixre;

import com.oocourse.specs1.models.PathContainer;
import com.oocourse.specs1.models.Path;
import com.oocourse.specs1.models.PathIdNotFoundException;
import com.oocourse.specs1.models.PathNotFoundException;

import java.util.HashMap;

public class MyPathContainer implements PathContainer {
    private HashMap<Integer, Path> pathmap = new HashMap<>();
    private HashMap<Path, Integer> reversemap = new HashMap<>();
    private HashMap<Integer, Integer> distinctnodemap = new HashMap<>();
    private static int count = 0;

    public MyPathContainer() {
    }

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

        if (!reversemap.containsKey(path)) {
            pathmap.put(++count, path);
            reversemap.put(path, count);
            for (int i : path) {
                if (distinctnodemap.containsKey(i)) {
                    distinctnodemap.put(i, distinctnodemap.get(i) + 1);
                }
                else {
                    distinctnodemap.put(i, 1);
                }
            }
            //System.out.println(reversemap.size());
            return count;
        }

        //System.out.println(distinctnodemap);
        return reversemap.get(path);
    }

    public int removePath(Path path) throws PathNotFoundException {
        if (path == null || !path.isValid() || !pathmap.containsValue(path)) {
            throw new PathNotFoundException(path);
        }
        int id = reversemap.get(path);
        reversemap.remove(path);
        pathmap.remove(id);
        for (int i : path) {
            //System.out.println(distinctnodemap);
            if (distinctnodemap.get(i) > 1) {
                distinctnodemap.put(i, distinctnodemap.get(i) - 1);
            }
            else {
                distinctnodemap.remove(i);
            }
        }
        return id;
    }

    public void removePathById(int pathId) throws PathIdNotFoundException {
        if (!pathmap.containsKey(pathId)) {
            throw new PathIdNotFoundException(pathId);
        }
        Path tmp = pathmap.get(pathId);
        pathmap.remove(pathId);
        reversemap.remove(tmp);
        for (int i : tmp) {
            if (distinctnodemap.get(i) > 1) {
                distinctnodemap.put(i, distinctnodemap.get(i) - 1);
            }
            else {
                distinctnodemap.remove(i);
            }
        }
    }

    public int getDistinctNodeCount() {
        return distinctnodemap.size();
    }
}
