package sortingcomparison;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Random;

/**
 * مقارنة أداء خمس خوارزميات ترتيب على أربع حالات من البيانات.
 * حجم المصفوفة: 100,000 عنصر. الزمن مقاس بالنانو ثانية (ns).
 */
public class SortingComparison {

    static final int N = 100_000;

    static final String[] NAMES = {"Selection", "Insertion", "Bubble", "Merge", "Quick"};

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

    // Merge Sort: نفس توقيع التلميح (arr, low, high)
    static void mergeSort(int[] a, int low, int high) {
        int[] tmp = new int[a.length]; // مصفوفة مساعدة تُنشأ مرة واحدة فقط
        mergeSortRec(a, tmp, low, high);
    }

    private static void mergeSortRec(int[] a, int[] tmp, int low, int high) {
        if (low >= high) return;
        int mid = low + (high - low) / 2;
        mergeSortRec(a, tmp, low, mid);
        mergeSortRec(a, tmp, mid + 1, high);
        merge(a, tmp, low, mid, high);
    }

    private static void merge(int[] a, int[] tmp, int low, int mid, int high) {
        int i = low, j = mid + 1, k = low;
        while (i <= mid && j <= high) {
            if (a[i] <= a[j]) tmp[k++] = a[i++];
            else              tmp[k++] = a[j++];
        }
        while (i <= mid)  tmp[k++] = a[i++];
        while (j <= high) tmp[k++] = a[j++];
        for (k = low; k <= high; k++) a[k] = tmp[k];
    }

    static void quickSort(int[] a, int low, int high) {
        while (low < high) {
            int p = partition(a, low, high);
            // نستدعي على الجزء الأصغر ونكمل بالحلقة على الأكبر (يمنع StackOverflow)
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
        int pivot = a[low]; // pivot = أول عنصر في الجزء الحالي
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

    // 20% من العناصر تبقى في مكانها الصحيح، و80% يتم خلطها
    static int[] partiallySorted(int[] random) {
        int n = random.length;
        int[] a = random.clone();
        Arrays.sort(a); // مرتبة بالكامل

        Random r = new Random();

        // خلط المواضع ثم اختيار 80% منها
        int[] idx = new int[n];
        for (int i = 0; i < n; i++) idx[i] = i;
        for (int i = n - 1; i > 0; i--) {
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

    static void sortByName(String name, int[] a) {
        switch (name) {
            case "Selection": selectionSort(a); break;
            case "Insertion": insertionSort(a); break;
            case "Bubble":    bubbleSort(a); break;
            case "Merge":     mergeSort(a, 0, a.length - 1); break;
            case "Quick":     quickSort(a, 0, a.length - 1); break;
        }
    }

    // نفرز نسخة مستقلة من المصفوفة ونقيس زمن الفرز فقط (بالنانو ثانية)
    static long measure(String name, int[] original) {
        int[] copy = Arrays.copyOf(original, original.length); // خارج القياس
        long start = System.nanoTime();
        sortByName(name, copy);
        long end = System.nanoTime();
        return end - start;
    }

    // تسخين JIT على مصفوفة صغيرة (غير محسوب في النتائج) لتقليل أثر الإحماء
    static void warmUp() {
        int[] small = randomArray(2_000);
        for (int round = 0; round < 5; round++) {
            for (String name : NAMES) {
                sortByName(name, Arrays.copyOf(small, small.length));
            }
        }
    }

    // ---------------- main ----------------

    public static void main(String[] args) {
        System.out.println("N = " + String.format("%,d", N) + " | الوحدة: ns");
        System.out.println("جاري تسخين JVM ...");
        warmUp();

        int[] random = randomArray(N);
        int[] sorted = random.clone();
        Arrays.sort(sorted);
        int[] reversed = sorted.clone();
        reverse(reversed);
        int[] partial = partiallySorted(random);

        String[] scenarioNames = {"Random", "Reversed", "Partially (80%)", "Sorted"};
        int[][] scenarios = {random, reversed, partial, sorted};

        long[][] results = new long[scenarios.length][NAMES.length];
        String[] fastest = new String[scenarios.length];
        String[] slowest = new String[scenarios.length];

        for (int s = 0; s < scenarios.length; s++) {
            long best = Long.MAX_VALUE;
            long worst = Long.MIN_VALUE;
            for (int a = 0; a < NAMES.length; a++) {
                System.out.println("[" + scenarioNames[s] + "] جاري تشغيل " + NAMES[a] + " ...");
                results[s][a] = measure(NAMES[a], scenarios[s]);
                if (results[s][a] < best) {
                    best = results[s][a];
                    fastest[s] = NAMES[a];
                }
                if (results[s][a] > worst) {
                    worst = results[s][a];
                    slowest[s] = NAMES[a];
                }
            }
        }

        printTable(scenarioNames, results, fastest, slowest);
        saveCsv(scenarioNames, results, fastest, slowest);
    }

    // طباعة الجدول: الزمن بالنانو ثانية وبجانبه بين قوسين بالثواني
    static void printTable(String[] sc, long[][] res, String[] fastest, String[] slowest) {
        System.out.println();
        System.out.println("Time in ns (seconds)  |  N = " + N);
        System.out.println();
        System.out.printf("%-17s", "Scenario");
        for (String n : NAMES) System.out.printf("%-30s", n);
        System.out.printf("%-12s", "Fastest");
        System.out.println("Slowest");
        System.out.println();
        for (int s = 0; s < sc.length; s++) {
            System.out.printf("%-17s", sc[s]);
            for (int a = 0; a < NAMES.length; a++) {
                String cell = String.format("%,d (%.6f s)", res[s][a], res[s][a] / 1e9);
                System.out.printf("%-30s", cell);
            }
            System.out.printf("%-12s", fastest[s]);
            System.out.println(slowest[s]);
        }
        System.out.println();
    }

    // حفظ النتائج في ملف CSV لاستخدامها في الرسم البياني
    static void saveCsv(String[] sc, long[][] res, String[] fastest, String[] slowest) {
        try (PrintWriter w = new PrintWriter("results.csv")) {
            w.println("Scenario," + String.join(",", NAMES) + ",Fastest,Slowest");
            for (int s = 0; s < sc.length; s++) {
                StringBuilder sb = new StringBuilder(sc[s]);
                for (long v : res[s]) sb.append(",").append(v);
                sb.append(",").append(fastest[s]).append(",").append(slowest[s]);
                w.println(sb);
            }
            System.out.println("تم حفظ النتائج في results.csv");
        } catch (Exception e) {
            System.out.println("تعذر حفظ results.csv: " + e.getMessage());
        }
    }
}