package dovsynnikov;

import edu.princeton.cs.introcs.StdDraw;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.hash.THashSet;

import java.awt.*;
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Iterator;

public class Life {

    private static int widthField = 10000;
    private static int heightField = 10000;

    private static int centerX = 5000;
    private static int centerY = -5000;

    public static THashSet<Cell> currentLife = new THashSet<>();
    public static THashSet<Cell> nextStepLife = new THashSet<>();
    public static THashSet<Cell> neighborhoodOfCells = new THashSet<>();

    static String fileName = "caterpillar.rle";

    public static void main(String[] args) {

        StdDraw.enableDoubleBuffering();
        StdDraw.setCanvasSize(700, 700);
        StdDraw.setPenColor(Color.BLACK);

        createField();
        runLife();
    }

    private static void createField() {

        ReadFile.read(fileName, Charset.defaultCharset());

        //глайдер
        /*currentLife.add(new Cell(0, 0));
        currentLife.add(new Cell(0, 1));
        currentLife.add(new Cell(0, 2));
        currentLife.add(new Cell(1, 2));
        currentLife.add(new Cell(2, 1));*/
    }


    private static void runLife() {

        //вывод в консоль начальных точек
        /*Iterator<Cell> i = currentLife.iterator();
        while (i.hasNext()) {
            System.out.println(i.next().toString());
        }*/

        while (true) {

            drawCurrentLife();
            change();
            moveField();

            //задержка 0.5 сек
            //StdDraw.pause(500);
        }
    }

    private static void moveField() {

        char key = ' ';

        if (StdDraw.hasNextKeyTyped()) {

            key = StdDraw.nextKeyTyped();
        }

        if (widthField > 2 && widthField < 16000000) {

            switch (key) {

                case '-':
                    widthField *= 2;
                    heightField *= 2;
                    break;

                case '+':
                    widthField /= 2;
                    heightField /= 2;
                    break;

                case 's':
                    centerY -= heightField / 3;
                    break;

                case 'w':
                    centerY += heightField / 3;
                    break;

                case 'a':
                    centerX -= widthField / 3;
                    break;

                case 'd':
                    centerX += widthField / 3;
                    break;
            }
        }
    }


    private static void drawCurrentLife() {

        long start = System.currentTimeMillis();

        StdDraw.clear();

        StdDraw.setXscale(centerX - (widthField / 2), centerX + (widthField / 2));
        StdDraw.setYscale(centerY - (heightField / 2), centerY + (heightField / 2));

        for (Cell c : currentLife) {

            StdDraw.filledSquare(c.x, c.y, 0.000001);
        }

        StdDraw.show();

        long finish = System.currentTimeMillis();

        System.out.println("Время отрисовки: " + (finish - start));
    }

    private static int countNeighboursAndRememberNeighborhood(Cell c) {

        int cnt = 0;

        Cell[] neighboursCell = Cell.neighbours(c);
        for (Cell nbCell : neighboursCell) {

            if (currentLife.contains(nbCell)) {
                cnt++;
            } else {
                neighborhoodOfCells.add(nbCell);
            }
        }

        return cnt;
    }

    private static int countNeighboursForRevival(Cell c) {

        int cnt = 0;

        Cell[] neighboursCell = Cell.neighbours(c);
        for (Cell nbCell : neighboursCell) {

            if (currentLife.contains(nbCell)) {
                cnt++;
            } else {
                WeakReference<Cell> cellWeakReference = new WeakReference<>(nbCell);
                nbCell = null;
            }
        }

        return cnt;
    }

    private static void change() {

        long start = System.currentTimeMillis();

        THashSet<Cell> tmp;

        // проверка живых клеток
        for (Cell c : currentLife) {

            int nb = countNeighboursAndRememberNeighborhood(c);

            if (((nb == 2) || (nb == 3))) {
                nextStepLife.add(c);
            } else {
                WeakReference<Cell> cellWeakReference = new WeakReference<>(c);
            }
        }

        //проверка клеток, которые могут возродиться
        for (Cell c : neighborhoodOfCells) {

            if (countNeighboursForRevival(c) == 3) {
                nextStepLife.add(c);
            } else {
                WeakReference<Cell> cellWeakReference = new WeakReference<>(c);
            }
        }

        currentLife.clear();
        tmp = currentLife;
        currentLife = nextStepLife;
        nextStepLife = tmp;

        long finish = System.currentTimeMillis();

        System.out.println("Время рассчета след. поколения: " + (finish - start));
    }
}