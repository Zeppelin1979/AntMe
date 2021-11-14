package AntMe.Simulation;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/// <summary>
/// Implementiert ein Zellrasterverfahren zum schnellen Auffinden von
/// Spielelementen auf dem Spielfeld.
/// </summary>
/// <remarks>
/// Ein Spielelement wird unabh�ngig von seinem Radius in genau eine Zelle
/// einsortiert. Spielelemente "zwei Zellen weiter" werden daher auch dann
/// nicht gefunden, wenn sie (durch ihren Radius) eigentlich nahe genug
/// am Referenzelement liegen.
/// </remarks>
/// <typeparam name="T">Typ der Spielelemente.</typeparam>
/// <author>Wolfgang Gallo (wolfgang@antme.net)</author>

public class Grid implements Collection<CoreMarker> {
    /// <summary>
    /// Speichert die Abmessungen eines Gitters.
    /// </summary>
    private static class GridSize
    {
        /// <summary>
        /// Die Breite des Gitters.
        /// </summary>
        public int width;

        /// <summary>
        /// Die H�he des Gitters.
        /// </summary>
        public int height;

        /// <summary>
        /// Die Seitenl�nge einer Gitterzelle.
        /// </summary>
        public int sideLength;

        /// <summary>
        /// Erzeugt eine neue Instanz der Struktur.
        /// </summary>
        /// <param name="width">Die Breite des Gitters.</param>
        /// <param name="height">Die H�he des Gitters.</param>
        /// <param name="sideLength">Die Seitenl�nge einer Gitterzelle.</param>
        public GridSize(int width, int height, int sideLength)
        {
            this.width = width;
            this.height = height;
            this.sideLength = sideLength;
        }
    }

    // Liste aller bereits erzeugten Gitter.
    private static HashMap<GridSize, Grid> grids =
        new HashMap<GridSize, Grid>();

    /// <summary>
    /// Erzeugt ein Gitter mit den angegebenen Ma�en oder gibt ein vorhandenes
    /// Gitter mit diesem Ma�en zur�ck.
    /// </summary>
    /// <param name="width">Breite des Gitters.</param>
    /// <param name="height">H�he des Gitters.</param>
    /// <param name="sideLength">Seitenl�nge einer Gitterzelle. Bestimmt die
    /// maximale Entfernung bis zu der ein Spielelement ein anderes sehen kann.</param>
    public static Grid create(int width, int height, int sideLength)
    {
        GridSize size = new GridSize(width, height, sideLength);
        if (!grids.containsKey(size))
            grids.put(size, new Grid(size));
        return grids.get(size);
    }

    // Die Abmessungen des Gitters.
    private int columns;
    private int rows;
    private int sideLength;

    // Die einzelnen Gitterzellen.
    private ArrayList<CoreMarker>[][] cells;


    /// <summary>
    /// Erzeugt eine neue Instanz der Klasse.
    /// </summary>
    /// <param name="size">Die Ma�e des Gitters.</param>
    private Grid(GridSize size)
    {
        sideLength = size.sideLength;
        columns = size.width / size.sideLength + 1;
        rows = size.height / size.sideLength + 1;

        cells = new ArrayList[columns][rows];
        for (int c = 0; c < columns; c++)
            for (int r = 0; r < rows; r++)
                cells[c][r] = new ArrayList<CoreMarker>();
    }

    /// <summary>
    /// Erzeugt einen neue Instanz der Klasse.
    /// </summary>
    /// <param name="width">Breite des Gitters.</param>
    /// <param name="height">H�he des Gitters.</param>
    /// <param name="sideLength">Seitenl�nge einer Gitterzelle. Bestimmt die
    /// maximale Entfernung bis zu der ein Spielelement ein anderes sehen kann.</param>
    public Grid(int width, int height, int sideLength)
    {
        this.sideLength = sideLength;
        this.columns = width / sideLength + 1;
        this.rows = height / sideLength + 1;

        cells = new ArrayList[this.columns][this.rows];
        for (int c = 0; c < this.columns; c++)
            for (int r = 0; r < this.rows; r++)
                cells[c][r] = new ArrayList<CoreMarker>();
    }

    @Override
    public Iterator<CoreMarker> iterator()
    {
        Iterator<CoreMarker> it = new Iterator<CoreMarker>() {
            private int c = 0;
            private int r = 0;
            private int i = 0;

            @Override
            public boolean hasNext() {
                if (c < columns) {
                    if (r < rows) {
                        if (i < cells[c][r].size()) {
                            return true;
                        }
                    }
                }
                return false;
            }

            @Override
            public CoreMarker next() {
                CoreMarker out = cells[c][r].get(i++);
                if (i == cells[c][r].size()) {
                    r++;
                    i=0;
                }
                if (r == rows) {
                    c++;
                    r=0;
                }

            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
            
        };
        return it;
    }

    private int count = 0;

    public int size()
    {
        return count;
    }

    @Override
    public void clear()
    {
        for (int x = 0; x < columns; x++)
            for (int y = 0; y < rows; y++)
                cells[x][y].clear();
        count = 0;
    }

    public boolean add(CoreMarker element)
    {
        int c = element.getCoordinateBase().getX() / sideLength;
        int r = element.getCoordinateBase().getY() / sideLength;
        if (c < 0 || c >= columns || r < 0 || r >= rows)
            return false;

        cells[c][r].add(element);
        count++;
        return true;
    }

    /// <summary>
    /// F�gt dem Gitter mehrere Spielelemente hinzu.
    /// </summary>
    /// <param name="elemente">Eine Liste von Spielelementen.</param>
    public void addAll(ArrayList<CoreMarker> elemente)
    {
        for (int i = 0; i < elemente.size(); i++)
            add(elemente.get(i));
    }

    public boolean remove(CoreMarker element)
    {
        int c = element.getCoordinateBase().getX() / sideLength;
        int r = element.getCoordinateBase().getY() / sideLength;
        if (c < 0 || c >= columns || r < 0 || r >= rows)
            return false;

        boolean success = cells[c][r].remove(element);
        if (success)
            count--;
        return success;
    }

    /// <summary>
    /// Entfernt mehrere Elemente aus dem Gitter.
    /// </summary>
    /// <param name="elemente">Eine Liste von Spielelementen.</param>
    public boolean remove(Collection<CoreMarker> elemente)
    {
        boolean result = true;
        Iterator<CoreMarker> it = elemente.iterator();
        while(it.hasNext())
            result &= remove(it.next());
    }

    public boolean contains(CoreMarker element)
    {
        int c = element.getCoordinateBase().getX() / sideLength;
        int r = element.getCoordinateBase().getY() / sideLength;
        if (c < 0 || c >= columns || r < 0 || r >= rows)
            return false;
        return cells[c][r].contains(element);
    }

    private class Tupel implements Comparable<Tupel>
    {
        private ICoordinate element;
        public ICoordinate getElement() {
            return element;
        }
        private Integer distance;
        public int getDistance() {
            return distance;
        }

        public Tupel(ICoordinate element, int distance)
        {
            this.element = element;
            this.distance = distance;
        }

        @Override
        public int compareTo(Tupel o) {
            return this.distance.compareTo(o.getDistance());
        }
    }

    private class TupelComparer implements Comparator<Tupel>
    {
        @Override
        public int compare(Tupel arg0, Tupel arg1) {
           return arg0.compareTo(arg1);
        }
    }

    private TupelComparer comparer = new TupelComparer();


    /// <summary>
    /// Findet alle Spielelemente innerhalb des gegebenen Sichtkreis des gegebenen Spielelements.
    /// </summary>
    /// <param name="coordinate">Das Referenzspielelement.</param>
    /// <param name="maximumDistance">Die maximale Entfernung.</param>
    /// <returns>Eine nach Entfernung sortierte Liste von Spielelementen.</returns>
    public ArrayList<CoreMarker> findSorted(ICoordinate coordinate, int maximumDistance)
    {
        // Speichert alle gefundenen Tupel (Spielelement, Entfernung).
        ArrayList<Tupel> tupels = new ArrayList<Tupel>();

        // Bestimme die Zelle in der das �bergebene Spielelement sich befindet.
        int col = coordinate.getCoordinateBase().getX() / sideLength;
        int row = coordinate.getCoordinateBase().getY() / sideLength;

        // Betrachte die Zelle und die acht Zellen daneben.
        for (int c = -1; c <= 1; c++)
            if (col + c >= 0 && col + c < columns)
                for (int r = -1; r <= 1; r++)
                    if (row + r >= 0 && row + r < rows)
                    {
                        ArrayList<CoreMarker> cell = cells[col + c][row + r];
                        for (int i = 0; i < cell.size(); i++)
                        {
                            if (cell.get(i).equals(coordinate))
                                continue;

                            int distance = CoreCoordinate.bestimmeEntfernungI(coordinate.getCoordinateBase(), cell.get(i).getCoordinateBase());
                            if (distance <= maximumDistance)
                                tupels.add(new Tupel(cell.get(i), distance));
                        }
                    }

        // Sortiere die Tupel und gib die Spielelemente zur�ck.
        tupels.sort(comparer);
        ArrayList<CoreMarker> elements = new ArrayList<CoreMarker>(tupels.size());
        for (int i = 0; i < tupels.size(); i++)
            elements.add((CoreMarker)tupels.get(i).getElement());
        return elements;
    }

    /// <summary>
    /// Findet alle Spielelemente innerhalb des Sichtkreis der gegebenen Wanze.
    /// </summary>
    /// <remarks>
    /// Die Simulation legt ein Gitter mit der maximalen Sichtweite der Wanzen als
    /// Seitenl�nge an und benutzt diese Methode auf dieser Instanz zum Finden von
    /// Ameisen. In dieses Gitter werden nur Ameisen einsortiert.
    /// </remarks>
    /// <param name="bug">Die Referenzwanze.</param>
    /// <returns>Eine Liste von Ameisen.</returns>
    public ArrayList<CoreMarker> findAnts(CoreBug bug)
    {
        // Speichert alle gefundenen Ameisen.
        ArrayList<CoreMarker> ants = new ArrayList<CoreMarker>();

        // Bestimme die Zelle in der die �bergebene Wanze sich befindet.
        int col = bug.getCoordinateBase().getX() / sideLength;
        int row = bug.getCoordinateBase().getY() / sideLength;

        // Betrachte die Zelle und die acht Zellen daneben.
        for (int c = -1; c <= 1; c++)
            if (col + c >= 0 && col + c < columns)
                for (int r = -1; r <= 1; r++)
                    if (row + r >= 0 && row + r < rows)
                    {
                        List<CoreMarker> cell = cells[col + c][row + r];
                        for (int i = 0; i < cell.size(); i++)
                        {
                            int distance = CoreCoordinate.bestimmeEntfernungI(bug.getCoordinateBase(), cell.get(i).getCoordinateBase());
                            if (distance <= sideLength)
                                ants.add(cell.get(i));
                        }
                    }

        return ants;
    }

    /// <summary>
    /// Findet die Markierung, die die gegebene Ameise noch nicht gerochen hat
    /// und die der Ameise am n�chsten liegt.
    /// </summary>
    /// <remarks>
    /// Die Simulation legt ein Gitter mit dem maximalen Radius einer Markierung als
    /// Seitenl�nge an und benutzt diese Methode auf dieser Instanz zum Finden von
    /// Markierungen. In dieses Gitter werden nur Markierungen einsortiert.
    /// </remarks>
    /// <param name="ant">Die Referenzameise.</param>
    /// <returns>Eine Markierung.</returns>
    public CoreMarker findMarker(CoreAnt ant)
    {
        CoreMarker nearestMarker = null;
        int nearestMarkerDistance = Integer.MAX_VALUE;

        // Bestimme die Zelle in der die �bergebene Ameise sich befindet.
        int col = ant.getCoordinateBase().getX() / sideLength;
        int row = ant.getCoordinateBase().getY() / sideLength;

        // Betrachte die Zelle und die acht Zellen daneben.
        for (int c = -1; c <= 1; c++)
            if (col + c >= 0 && col + c < columns)
                for (int r = -1; r <= 1; r++)
                    if (row + r >= 0 && row + r < rows)
                    {
                        List<CoreMarker> cell = cells[col + c][row + r];

                        // Betrachte alle Markierungen in der aktuellen Zelle.
                        for (int i = 0; i < cell.size(); i++)
                        {
                            CoreMarker marker = cell.get(i);
                            //Debug.Assert(marker != null);

                            // Bestimme die Entfernung der Mittelpunkte und der Kreise.
                            int distance = CoreCoordinate.bestimmeEntfernungDerMittelpunkteI(ant.getCoordinateBase(), marker.getCoordinateBase());
                            int circleDistance = distance - ant.getCoordinateBase().getRadius() - marker.getCoordinateBase().getRadius();

                            // Die neue Markierung wurde noch nicht gerochen und
                            // liegt n�her als die gemerkte.
                            if (circleDistance <= 0 && distance < nearestMarkerDistance &&
                                !ant.getSmelledMarker().contains(marker))
                            {
                                nearestMarkerDistance = distance;
                                nearestMarker = marker;
                            }
                        }
                    }

        return nearestMarker;
    }

    /// <summary>
    /// Findet die Wanze, die feindliche Ameise und die befreundete Ameise mit der
    /// geringsten Entfernung innerhalb des Sichtkreis der gegebenen Ameise und
    /// z�hlt die Anzahl an Wanzen, feindlichen und befreundeten Ameisen im Sichtkreis.
    /// </summary>
    /// <remarks>
    /// Wird f�r Ameisen verwendet. Die Simulation legt f�r jeden vorkommenden Sichtradius
    /// eine eigenes Gitter an und benutzt diese Methode auf der passenden Instanz zum Finden
    /// von Insekten. Die Seitenl�nge dieses Gitters ist also der Sichradius der Ameise.
    /// In diese Gitter werden Wanzen und Ameisen einsortiert.
    /// </remarks>
    /// <param name="ant">Die Referenzameise.</param>
    /// <param name="nearestBug">Eine Wanze.</param>
    /// <param name="bugCount">Die Anzahl an Wanzen.</param>
    /// <param name="nearestEnemyAnt">Eine feindliche Ameise.</param>
    /// <param name="enemyAntCount">Die Anzahl an feindlichen Ameisen.</param>
    /// <param name="nearestColonyAnt">Eine befreundete Ameise.</param>
    /// <param name="colonyAntCount">Die Anzahl an befreundeten Ameisen.</param>
    /// <param name="casteAntCount">Die Anzahl an befreundeten Ameisen der selben Kaste.</param>
    public void findAndCountInsects(CoreAnt ant, out CoreBug nearestBug, out int bugCount,
        out CoreAnt nearestEnemyAnt, out int enemyAntCount, out CoreAnt nearestColonyAnt,
        out int colonyAntCount, out int casteAntCount, out CoreAnt nearestTeamAnt,
        out int teamAntCount)
    {
        // Die n�chstliegenden gefundenen Wanzen und Ameisen.
        nearestBug = null;
        nearestEnemyAnt = null;
        nearestColonyAnt = null;
        nearestTeamAnt = null;

        // Die Entfernungen zu den n�chstliegenden gefundenen Wanzen und Ameisen.
        int nearestBugDistance = int.MaxValue;
        int nearestEnemyAntDistance = int.MaxValue;
        int nearestColonyAntDistance = int.MaxValue;
        int nearestTeamAntDistance = int.MaxValue;

        // Die Anzahlen der gefundenen Wanzen und Ameisen.
        bugCount = 0;
        enemyAntCount = 0;
        colonyAntCount = 0;
        casteAntCount = 0;
        teamAntCount = 0;

        // Bestimme die Zelle in der die �bergebene Ameise sich befindet.
        int col = ant.CoordinateBase.X / sideLength;
        int row = ant.CoordinateBase.Y / sideLength;

        // Betrachte die Zelle und die acht Zellen daneben.
        for (int c = -1; c <= 1; c++)
            if (col + c >= 0 && col + c < columns)
                for (int r = -1; r <= 1; r++)
                    if (row + r >= 0 && row + r < rows)
                    {

                        // Betrachte alle Insekten in der aktuellen Zelle.
                        List<T> cell = cells[col + c, row + r];
                        for (int i = 0; i < cell.Count; i++)
                        {
                            CoreInsect insect = cell[i] as CoreInsect;
                            Debug.Assert(insect != null);

                            if (insect == ant)
                                continue;

                            // Vergleiche die Entfernung zum aktuellen Insekt mit der
                            // Sichtweite der Ameise bzw. der Seitenl�nge des Gitters.
                            int distance = CoreCoordinate.BestimmeEntfernungI(ant.CoordinateBase, insect.CoordinateBase);
                            if (distance > sideLength)
                                continue;

                            // Selbes Volk. Die Abfrage "insect is CoreAnt" ist unn�tig.
                            if (insect.colony == ant.colony)
                            {
                                colonyAntCount++;
                                if (insect.CasteIndexBase == ant.CasteIndexBase)
                                    casteAntCount++;

                                // Die neue Ameise liegt n�her als die gemerkte.
                                if (distance < nearestColonyAntDistance)
                                {
                                    nearestColonyAntDistance = distance;
                                    nearestColonyAnt = (CoreAnt)insect;
                                }
                            }

                            // Selbes Team.
                            else if (insect.colony.Team == ant.colony.Team)
                            {
                                teamAntCount++;

                                // Die neue Ameise liegt n�her als die gemerkte.
                                if (distance < nearestTeamAntDistance)
                                {
                                    nearestTeamAntDistance = distance;
                                    nearestTeamAnt = (CoreAnt)insect;
                                }
                            }

                            // Wanze.
                            else if (insect is CoreBug)
                            {
                                bugCount++;

                                // Die neue Wanze liegt n�her als die gemerkte.
                                if (distance < nearestBugDistance)
                                {
                                    nearestBugDistance = distance;
                                    nearestBug = (CoreBug)insect;
                                }
                            }

                            // Feindliche Ameise.
                            else
                            {
                                enemyAntCount++;

                                // Die neue Ameise liegt n�her als die gemerkte.
                                if (distance < nearestEnemyAntDistance)
                                {
                                    nearestEnemyAntDistance = distance;
                                    nearestEnemyAnt = (CoreAnt)insect;
                                }
                            }
                        }
                    }
    }



    @Override
    public boolean addAll(Collection<? extends AntMe.Simulation.CoreMarker> c) {
        if (c.isEmpty()) return false;
        if (c.iterator().next().getClass() == CoreMarker.class)
        {
            return addAll((Collection<CoreMarker>)c)
        }
        return false;    }

    @Override
    public boolean contains(Object o) {
        if (o.getClass() == CoreMarker.class) return contains((CoreMarker) o);
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        boolean result = true;
        if (c.isEmpty()) return false;
        Iterator<?> it = c.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (o.getClass() == CoreMarker.class)
            {
                result &= contains((CoreMarker) o);
            } else {
                return false;
            }
        }
        return result;
    }

    @Override
    public boolean isEmpty() {
        return count == 0;
    }

    @Override
    public boolean remove(Object o) {
        if (o.getClass() == CoreMarker.class)
        {
            return remove((CoreMarker)o);
        }
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c.isEmpty()) return false;
        if (c.iterator().next().getClass() == CoreMarker.class)
        {
            @SuppressWarnings("unchecked")
            return remove((Collection<CoreMarker>)c);
        }
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        clear();
        @SuppressWarnings("unchecked")
        addAll((Collection<CoreMarker>)c);
        return false;
    }

    @Override
    public Object[] toArray() {
        ArrayList<CoreMarker> resultList = new ArrayList<CoreMarker>(count); 
        for (int c = 0; c < columns; c++) {
            for (int r = 0; r < rows; r++) {
                resultList.addAll(cells[c][r]);
            }
        }
        return resultList.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        List<T> resultList = new ArrayList<T>(count); 
        for (int c = 0; c < columns; c++) {
            for (int r = 0; r < rows; r++) {
                @SuppressWarnings("unchecked")
                T[] resultArray = (T[]) Array.newInstance(a.getClass().getComponentType(), 0);
                Collections.addAll(resultList,cells[c][r].toArray(resultArray));
            }
        }
        return resultList.toArray(a);
     }}
