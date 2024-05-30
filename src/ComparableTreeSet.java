import java.util.TreeSet;
import java.util.Iterator;
//TreeSet except it can be compared with other tree sets
public class ComparableTreeSet<T extends Comparable<T>> extends TreeSet<T> implements Comparable<ComparableTreeSet<T>>{
    public ComparableTreeSet(ComparableTreeSet<T> set) {
        super((TreeSet<T>)set);
    }
    public ComparableTreeSet() {
        super();
    }
    @Override
    public int compareTo(final ComparableTreeSet<T> that) {
        Iterator<T> t1 = this.iterator();
        Iterator<T> t2 = that.iterator();
        while (t1.hasNext() && t2.hasNext()) {
            int comp = t1.next().compareTo(t2.next());
            if (comp != 0) {
                return comp;
            }
        }
        if (this.size() != that.size()) {
            return this.size() - that.size();
        }
        return 0;
    }
}
