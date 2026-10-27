
package sortingcomparison;

import java.util.Arrays;
import java.util.Random;
public class SortingComparison {
static final int N = 1_000_000;

    // ---------------- الخوارزميات ----------------

    static void selectionSort(int[] a) {
        int n = a.length;
        for (int i = 0; i < n - 1; i++) {
            int min = i;
            for (int j = i + 1; j < n; j++) {
                if (a[j] < a[min]) min = j;
            }
            int t = a[i]; a[i] = a[min]; a[min] = t;
        }
    }

    static void insertionSort(int[] a) {
        for (int i = 1; i < a.length; i++) {
            int key = a[i];
            int j = i - 1;
            while (j >= 0 && a[j] > key) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = key;
        }
    }

    static void bubbleSort(int[] a) {
        int n = a.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (a[j] > a[j + 1]) {
                    int t = a[j]; a[j] = a[j + 1]; a[j + 1] = t;
                    swapped = true;
                }
            }
            if (!swapped) break; // المصفوفة صارت مرتبة
        }
    }

    static void quickSort(int[] a, int low, int high) {
        while (low < high) {
            int p = partition(a, low, high);
            // نعمل الاستدعاء على الجزء الأصغر ونكمل بالحلقة على الأكبر (يمنع StackOverflow)
            if (p - low < high - p) {
                quickSort(a, low, p);
                low = p + 1;
            } else {
                quickSort(a, p + 1, high);
                high = p;
            }
        }
    }

    static int partition(int[] a, int low, int high) {
        int pivot = a[low + (high - low) / 2]; // pivot من الوسط
        int i = low - 1, j = high + 1;
        while (true) {
            do { i++; } while (a[i] < pivot);
            do { j--; } while (a[j] > pivot);
            if (i >= j) return j;
            int t = a[i]; a[i] = a[j]; a[j] = t;
        }
    }

    // ---------------- دوال مساعدة ----------------

    static int[] randomArray(int n) {
        Random r = new Random();
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = r.nextInt(1_000_000);
        return a;
    }

    static void reverse(int[] a) {
        for (int i = 0, j = a.length - 1; i < j; i++, j--) {
            int t = a[i]; a[i] = a[j]; a[j] = t;
        }
    }

    // 20% من العناصر في أماكنها الصحيحة موزعة، والباقي (80%) نخلطه
    static int[] partiallySorted(int[] random) {
        int n = random.length;
        int[] a = random.clone();
        Arrays.sort(a); // مرتبة بالكامل

        Random r = new Random();

        // اختيار 80% من المواضع عشوائياً
        int[] idx = new int[n];
        for (int i = 0; i < n; i++) idx[i] = i;
        for (int i = n - 1; i > 0; i--) { // خلط المواضع
            int j = r.nextInt(i + 1);
            int t = idx[i]; idx[i] = idx[j]; idx[j] = t;
        }
        int m = (int) (n * 0.8);

        // خلط القيم الموجودة في تلك المواضع فقط
        for (int i = m - 1; i > 0; i--) {
            int j = r.nextInt(i + 1);
            int p1 = idx[i], p2 = idx[j];
            int t = a[p1]; a[p1] = a[p2]; a[p2] = t;
        }
        return a;
    }

    // نفرز نسخة من المصفوفة ونرجع الزمن بالنانو ثانية
    static long measure(String name, int[] original) {
        int[] copy = Arrays.copyOf(original, original.length);
        long start = System.nanoTime();
        switch (name) {
            case "Selection": selectionSort(copy); break;
            case "Insertion": insertionSort(copy); break;
            case "Bubble":    bubbleSort(copy); break;
            case "Quick":     quickSort(copy, 0, copy.length - 1); break;
        }
        long end = System.nanoTime();
        return end - start;
    }

    static void runScenario(String title, int[] data) {
        System.out.println("\n===== " + title + " =====");
        String[] names = {"Selection", "Insertion", "Bubble", "Quick"};
        long best = Long.MAX_VALUE;
        String bestName = "";
        for (String name : names) {
            System.out.println("جاري تشغيل " + name + " ...");
            long t = measure(name, data);
            System.out.printf("%-10s : %,d ns  (%.3f seconds)%n", name, t, t / 1e9);
            if (t < best) { best = t; bestName = name; }
        }
        System.out.println(">>> الأسرع: " + bestName);
    }

    // ---------------- main ----------------

    public static void main(String[] args) {
        int[] random = randomArray(N);

        // 1) عشوائية
        runScenario("Random", random);

        // 2) مرتبة
        int[] sorted = random.clone();
        Arrays.sort(sorted);
        runScenario("Sorted", sorted);

        // 3) معكوسة
        int[] reversed = sorted.clone();
        reverse(reversed);
        runScenario("Reversed", reversed);

        // 4) 20% مرتبة
        int[] partial = partiallySorted(random);
        runScenario("20% Sorted", partial);
    }
}
    
