import java.util.Arrays;
import java.util.concurrent.RecursiveTask;
import java.util.Optional;
import java.util.Random;


public class SortTree {
    private int[] sorted;
    private int begin;
    private int end;
    private SortTree leftChild;
    private SortTree rightChild;

    public SortTree(int[] sorted, int begin, int end, SortTree leftChild, SortTree rightChild) {
        this.sorted = sorted;
        this.begin = begin;
        this.end = end;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
    }

    public boolean isLeaf() {
        return leftChild == null && rightChild == null;
    }

    public Optional<SortTree> leftChild() {
        return Optional.ofNullable(leftChild);
    }

    public Optional<SortTree> rightChild() {
        return Optional.ofNullable(rightChild);
    }

    public void print(String prefix) {
        System.out.print(prefix + "(" + begin + ", " + end + "): ");
        for (int i = 0; i < sorted.length; ++i) {
            System.out.print("" + sorted[i] + ", ");
        }
        System.out.println();
        if (!isLeaf()) {
            leftChild.print(prefix + "  ");
            rightChild.print(prefix + "  ");
        }
    }

    public static class RecursiveTaskBuilder extends RecursiveTask<SortTree> {
        static final long serialVersionUID = 0L;
        private int[] unsorted;
        private int begin;
        private int end;

        public RecursiveTaskBuilder(int[] unsorted, int begin, int end) {
            this.unsorted = unsorted;
            this.begin = begin;
            this.end = end;
        }

        @Override
        protected SortTree compute() {
            if (end - begin <= 1) {
                int[] sorted = subArray(unsorted, begin, end);
                return new SortTree(sorted, begin, end, null, null);
            }
            
            // System.out.println("compute " + begin + " " + end);
            
            int mid = (begin + end) / 2;
            RecursiveTaskBuilder leftBuilder = new RecursiveTaskBuilder(unsorted, begin, mid);
            leftBuilder.fork();
            RecursiveTaskBuilder rightBuilder = new RecursiveTaskBuilder(unsorted, mid, end);
            rightBuilder.fork();
            
            // System.out.println("sorting " + begin + " " + end);
            
            int[] sorted = subArray(unsorted, begin, end);
            Arrays.sort(sorted);
            
            // System.out.println("sorted " + begin + " " + end);
            
            SortTree rightChild = rightBuilder.join();
            // QSortTree rightChild = rightBuilder.compute();
            // Arrays.sort(curInd);
            SortTree leftChild = leftBuilder.join();
            // System.out.println("sorting " + begin + " " + end);
            // int l = 0, r = 0;
            // int[] indl = leftChild.sorted;
            // int[] indr = rightChild.sorted;
            // for(int i = 0; i < curInd.length; ++i) {
            // if(r == indr.length) {
            // curInd[i] = indl[l++];
            // } else if(l == indl.length) {
            // curInd[i] = indr[r++];
            // } else if(indr[r] < indl[l]) {
            // curInd[i] = indr[r++];
            // } else {
            // curInd[i] = indl[l++];
            // }
            // }
            
            // System.out.println("return " + begin + " " + end);
            
            return new SortTree(sorted, begin, end, leftChild, rightChild);
        }

        private static int[] subArray(int[] arr, int begin, int end) {
            int[] ret = new int[end - begin];
            for (int i = 0; i < end - begin; ++i) {
                ret[i] = arr[begin + i];
            }
            return ret;
        }

        public static RecursiveTaskBuilder create(int[] unsorted) {
            return new RecursiveTaskBuilder(unsorted, 0, unsorted.length);
        }
    }

    public static void main(String[] args) {
        int n = 10000000;
        Random ran = new Random();
        int[] s = new int[n];
        for(int i = 0; i < n; ++i) s[i] = i;
        for(int i = n - 1; i > 0; --i) {
            int j = ran.nextInt(i);
            int c = s[i];
            s[i] = s[j];
            s[j] = c;
        }
        RecursiveTaskBuilder builder = RecursiveTaskBuilder.create(s);
        long startTime = System.nanoTime();
        SortTree tree = builder.invoke();
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        System.out.println("time: " + duration);
        // tree.print("");
    }
}
