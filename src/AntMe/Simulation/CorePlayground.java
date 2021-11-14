package AntMe.Simulation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/// <summary>
/// Das Spielfeld.
/// </summary>
/// <author>Patrick Kirsch</author>
public class CorePlayground {

    private final boolean ENABLE_DEBUGINGSCREEN = false;
    private Thread debugThread;

    private int width;
    public int getWidth() {
        return width;
    }
    private int height;
    public int getHeight() {
        return height;
    }

    private Random mapRandom;

    private ArrayList<Vector2D> antHillPoints;
    private Cell[][] cellArray;
    private ArrayList<Cell> cellSpawnList;

    private float antHillRandomdisplacement = SimulationSettings.getDefault().AntHillRandomDisplacement;
    private int spawnCellSize= SimulationSettings.getDefault().SpawnCellSize;
    private int restrictedZoneRadius = SimulationSettings.getDefault().RestrictedZoneRadius;
    private int farZoneRadius= SimulationSettings.getDefault().FarZoneRadius;
    private float decreasValue = SimulationSettings.getDefault().DecreaseValue;
    private float regenerationValue = SimulationSettings.getDefault().RegenerationValue;

    // öffentliche Bestandslisten(werden von Außen gelesen)
    private ArrayList<CoreFruit> fruits;
    public ArrayList<CoreFruit> getFruits() {
        return fruits;
    }
    private ArrayList<CoreSugar> sugarHills;
    public ArrayList<CoreSugar> getSugarHills() {
        return sugarHills;
    }
    private ArrayList<CoreAnthill> antHills;
    public ArrayList<CoreAnthill> getAntHills() {
        return antHills;
    }

    /// <summary>
    /// Erzeugt eine neue Instanz der Spielfeld-Klasse.
    /// </summary>
    /// <param name="width">Die Breite in Schritten.</param>
    /// <param name="height">Die Höhe in Schritten.</param>
    /// <param name="random">Initialwert für Zufallsgenerator</param>
    /// /// <param name="playercount">Anzahl der Spieler</param>
    public CorePlayground(int width, int height, Random random, int playercount)
    {
        this.width = width;
        this.height = height;
        this.mapRandom = random;

        //Überprüfen der Settings
        if (SimulationSettings.getCustom().AntHillRandomDisplacement != 0)
            antHillRandomdisplacement = SimulationSettings.getCustom().AntHillRandomDisplacement;
        if (SimulationSettings.getCustom().SpawnCellSize != 0)
            spawnCellSize = SimulationSettings.getCustom().SpawnCellSize;
        if (SimulationSettings.getCustom().RestrictedZoneRadius != 0)
            restrictedZoneRadius = SimulationSettings.getCustom().RestrictedZoneRadius;
        if (SimulationSettings.getCustom().FarZoneRadius != 0)
            farZoneRadius = SimulationSettings.getCustom().FarZoneRadius;
        if (SimulationSettings.getCustom().DecreaseValue != 0)
            decreasValue = SimulationSettings.getCustom().DecreaseValue;
        if (SimulationSettings.getCustom().RegenerationValue != 0)
            regenerationValue = SimulationSettings.getCustom().RegenerationValue;


        // Initialisierungen
        fruits = new ArrayList<CoreFruit>();
        sugarHills = new ArrayList<CoreSugar>();
        antHills = new ArrayList<CoreAnthill>();

        antHillPoints = new ArrayList<Vector2D>(playercount);
        cellArray = new Cell[(int)Math.ceil((float)width / spawnCellSize)][(int)Math.ceil((float)height / spawnCellSize)];
        cellSpawnList = new ArrayList<Cell>(cellArray.length);

        //if (ENABLE_DEBUGINGSCREEN)
        //{
        //    debugThread = new Thread(new ThreadStart(debug));
        //    debugThread.Start();
        //}


        // Bestimme zufälligen Rotationswinkel des Spawnkreises.
        int startAngle = mapRandom.nextInt(359);

        // Fülle die Liste der möglichen Spawnpositionen.
        if (playercount == 1)
        {
            antHillPoints.add(new Vector2D(width / 2, height / 2));
        }
        else
        {

            float angle = 360f / playercount;
            int radius = Math.min(height / 3, width / 3);

            for (int i = 0; i < playercount; i++)
            {
                int nx = (int)(radius * Math.cos(((angle * i) + startAngle) * Math.PI / 180d));
                int ny = (int)(radius * Math.sin(((angle * i) + startAngle) * Math.PI / 180d));

                antHillPoints.add(new Vector2D(nx + (width / 2), ny + (height / 2)));
            }
        }

        // Weise jedem Spieler eine zufällige Spawnposition zu.
        for (int i = 0; i < playercount; i++)
        {
            int attempts = 5;
            boolean again = true;
            Vector2D targetPoint = null;
            while (again) {
                again = false;
                Vector2D punkt = new Vector2D(mapRandom.nextInt(width), mapRandom.nextInt(height));
                targetPoint = new Vector2D((int)((punkt.getX() * antHillRandomdisplacement) + (antHillPoints.get(i).getX() * (1 - antHillRandomdisplacement))), (int)(((punkt.getY() * antHillRandomdisplacement) + (antHillPoints.get(i).getY() * (1 - antHillRandomdisplacement)))));

                for (int p = 0; p < i; p++)
                {
                    if ((antHillPoints.get(p).distance(targetPoint) < restrictedZoneRadius) && attempts > 0)
                    {
                        attempts--;
                        again=true;
                        break;
                    }
                }
            }

            antHillPoints.set(i, targetPoint);
        }


        for (int cellX = 0; cellX < cellArray.length; cellX++)
        {
            for (int cellY = 0; cellY < cellArray[cellX].length; cellY++)
            {
                // Position der oberen linken Ecke der Zelle
                Vector2D pos = new Vector2D(cellX * spawnCellSize, cellY * spawnCellSize);

                // Ermittlung ob die Zelle kleiner sein muss, da sie an den Rand stößt.
                int cellWidth = Math.min(spawnCellSize, width - (cellX * spawnCellSize));
                int cellHeight = Math.min(spawnCellSize, height - (cellY * spawnCellSize));

                // Neue Zelle erstellen.
                Cell cell = cellArray[cellX][cellY] = new Cell(pos, cellWidth, cellHeight);


                Vector2D totalDisplacementVector = new Vector2D(0,0);

                for (Vector2D antHill : antHillPoints)
                {
                    // Berechnung des Abstands Vectors.
                    Vector2D displacmentVector = antHill.subtract(cell.getPosition());

                    int distance = (int)Vector2D.distance(antHill, cell.getPosition());

                    if (distance < restrictedZoneRadius)
                    {
                        // Zelle liegt im  gesperrten Bereich mindestens eines Ameisenhügels.
                        totalDisplacementVector = new Vector2D(0, 0);
                        break;
                    }

                    // Abstands Vectoren aufaddieren.
                    totalDisplacementVector = totalDisplacementVector.add(displacmentVector);
                }

                // Durchschnittswert berechnen.
                totalDisplacementVector.scalarMultiply(1/playercount);

                // Sperrung der Zelle, wenn sie in einem gesperrten Bereich sich befindet, oder sich am Spielfeldrand befindet.
                if (totalDisplacementVector.getNorm() < restrictedZoneRadius || totalDisplacementVector.getNorm() > farZoneRadius || cellX == 0 || cellY == 0 || cellX == cellArray.length - 1 || cellY == cellArray[cellX].length - 1)
                {
                    cell.restricted = true;
                }
            }
        }

        // Nicht gesperrte Zellen der Spawnliste hinzufügen.
        for (Cell[] cellrow : cellArray)
        {
            for (Cell cell : cellrow) {
                if (!cell.restricted)
                    cellSpawnList.add(cell);
            }
        }
    }

    /// <summary>
    /// Wählt eine zufällige Zelle mit einem hohen Spawnwert aus.
    /// </summary>
    /// <returns>Gibt die Zielzelle zurück.</returns>
    private Cell findeRohstoffZelle()
    {
        regeneriereZellen();

        // Sortiere die Spawnliste absteigend nach dem SpawnValue.
        cellSpawnList.sort(new Comparator<Cell>() {

            @Override
            public int compare(Cell arg0, Cell arg1) {
                return Float.compare(arg1.SpawnValue, arg0.SpawnValue);
            }
        } );

        // Wählt alle Zellen aus, welche dem "höchseten Spawnvalue - 0,1" entsprechen.
        ArrayList<Cell> randomList = new ArrayList<Cell>();
        for (Cell cell: cellSpawnList) {
            if (cell.SpawnValue >= cellSpawnList.get(0).SpawnValue - 0.1f) {
                if (cell.SpawnedFood == null) {
                    randomList.add(cell);
                }
            }
        }

        //Sollte es keine Zelle geben, wir auch in schon bespawnten Zellen gesucht
        if (randomList.size() == 0) {
            for (Cell cell: cellSpawnList) {
                if (cell.SpawnValue >= cellSpawnList.get(0).SpawnValue - 0.1f) {
                    randomList.add(cell);
                }
            }
        }

        // Wählt ein zufällige Zelle aus dem Bereich aus.
        Cell cell = randomList.get(mapRandom.nextInt(randomList.size() - 1));

        cell.SpawnValue = 0f;

        decreaseCells(cell);

        return cell;
    }

    /// <summary>
    /// Regeneriert alle Zellen auf dem Spielfeld
    /// </summary>
    private void regeneriereZellen()
    {
        for (Cell[] cellrow : cellArray)
        {
            for (Cell cell : cellrow) {
                if (cell.SpawnedFood != null)
                    continue;

                cell.SpawnValue += regenerationValue;
                if (cell.SpawnValue > 1f)
                    cell.SpawnValue = 1f;
            }
        }
    }

    /// <summary>
    /// Verringert den Spawnwert aller Zellen, auf basis der entfernugn von der AusgangsZelle. 
    /// </summary>
    /// <param name="startCell">Die Ausgangszelle.</param>
    private void decreaseCells(Cell startCell)
    {
        for (Cell[] cellrow : cellArray)
        {
            for (Cell cell : cellrow) {
                if (cell == startCell)
                    continue;

                double abstand = (Vector2D.distance(cell.getPosition(), startCell.getPosition()) / spawnCellSize);

                cell.SpawnValue -= decreasValue / (abstand * abstand);

                if (cell.SpawnValue < 0)
                    cell.SpawnValue = 0;
            }
        }
    }



    /// <summary>
    /// Erzeugt einen neuen Zuckerhaufen.
    /// </summary>
    public void neuerZucker()
    {
        Cell cell = findeRohstoffZelle();
        int wert = mapRandom.nextInt(SimulationSettings.getCustom().SugarAmountMaximum - SimulationSettings.getCustom().SugarAmountMinimum) + SimulationSettings.getCustom().SugarAmountMinimum;
        Vector2D punkt = cell.getPosition().add(new Vector2D(mapRandom.nextInt(cell.getWidth()), mapRandom.nextInt(cell.getHeight())));
        CoreSugar zucker = new CoreSugar((int)punkt.getX(), (int)punkt.getY(), wert);
        sugarHills.add(zucker);
        cell.SpawnedFood = zucker;
    }

    /// <summary>
    /// Entfernt Zucker und gibt die Zelle wieder frei.
    /// </summary>
    /// <param name="nahrung">Den zu entfernenden Zucker</param>
    public void entferneZucker(CoreSugar sugar)
    {
        sugarHills.remove(sugar);
        Cell cell = cellSpawnList.stream().filter(x -> x.SpawnedFood == sugar).findFirst().orElseThrow();
        cell.SpawnedFood = null;
    }

    /// <summary>
    /// Erzeugt ein neues Obsttück.
    /// </summary>
    public void neuesObst()
    {
        Cell cell = findeRohstoffZelle();
        int wert = mapRandom.nextInt(SimulationSettings.getCustom().FruitAmountMaximum - SimulationSettings.getCustom().FruitAmountMinimum) + SimulationSettings.getCustom().FruitAmountMinimum;
        Vector2D punkt = cell.getPosition().add(new Vector2D(mapRandom.nextInt(cell.getWidth()), mapRandom.nextInt(cell.getHeight())));
        CoreFruit fruit = new CoreFruit((int)punkt.getX(), (int)punkt.getY(), wert);
        fruits.add(fruit);
        cell.SpawnedFood = fruit;
    }

    /// <summary>
    /// Entfernt Obst und gibt die Zelle wieder frei.
    /// </summary>
    /// <param name="nahrung">Das zu entfernenden Obst</param>
    public void entferneObst(CoreFruit fruit)
    {
        fruits.remove(fruit);
        Cell cell = cellSpawnList.stream().filter(x -> x.SpawnedFood == fruit).findFirst().orElseThrow();
        cell.SpawnedFood = null;
    }

    /// <summary>
    /// Erzeugt einen neuen Bau.
    /// </summary>
    /// <param name="colony">ID der Kolonie</param>
    /// <returns>Der neue Bau.</returns>
    public CoreAnthill neuerBau(int colony)
    {
        Vector2D punkt = antHillPoints.get(mapRandom.nextInt(antHillPoints.size() - 1));
        antHillPoints.remove(punkt);
        CoreAnthill bau = new CoreAnthill((int) punkt.getX(), (int) punkt.getY(), SimulationSettings.getCustom().AntHillRadius, colony);
        antHills.add(bau);
        return bau;
    }

    /// <summary>
    /// Vector im 2D Raum
    /// </summary>

    class Cell
    {
        private Vector2D position;
        public Vector2D getPosition() {
            return position;
        };
        private int width;
        public int getWidth() {
            return width;
        }
        private int height;
        public int getHeight() {
            return height;
        }

        public CoreFood SpawnedFood;
        public boolean restricted = false;
        public float SpawnValue = 1f;

        /// <summary>
        /// Erstellt eine Neue Zelle.
        /// </summary>
        /// <param name="position">Die Position der oberen linken Ecke der Zelle.</param>
        /// <param name="width">Die Breite der Zelle</param>
        /// <param name="height">Die Höhe der Zelle</param>
        public Cell(Vector2D position, int width, int height)
        {
            this.position = position;
            this.width = width;
            this.height = height;
        }
    }

    //#region Debugin

    //private void debug()
    //{
    //    DebugViewer DebugForm = new DebugViewer(CellArray);

    //    Application.Run(DebugForm);
    //    DebugForm.Show();
    //}

    //private class DebugViewer : Form
    //{

    //    private ViewCell[,] ViewCells;
    //    private System.Windows.Forms.Timer updateTimer;

    //    public DebugViewer(Cell[,] cells)
    //    {
    //        this.Text = "DebugViewer - Spawnzellen";

    //        ViewCells = new ViewCell[cells.GetLength(0), cells.GetLength(1)];
    //        updateTimer = new System.Windows.Forms.Timer();


    //        for (int x = 0; x < cells.GetLength(0); x++)
    //        {
    //            for (int y = 0; y < cells.GetLength(1); y++)
    //            {
    //                ViewCells[x, y] = new ViewCell(cells[x, y]);
    //                ViewCells[x, y].Location = new Point(x * 52, y * 52);
    //                this.Controls.Add(ViewCells[x, y]);
    //            }
    //        }

    //        updateTimer.Enabled = true;
    //        updateTimer.Start();
    //        updateTimer.Tick += updateTimer_Tick;

    //    }

    //    private void updateTimer_Tick(object sender, EventArgs e)
    //    {
    //        foreach (ViewCell viewCell in ViewCells)
    //        {
    //            viewCell.update();
    //        }
    //        this.Refresh();
    //    }
    //}

    //private class ViewCell : UserControl
    //{
    //    private CorePlayground.Cell cellData;
    //    private Label label_SpawnValue;

    //    public ViewCell(CorePlayground.Cell cell)
    //    {
    //        InitializeComponent();
    //        this.cellData = cell;
    //    }

    //    public void update()
    //    {
    //        if (cellData == null)
    //        {
    //            label_SpawnValue.Text = "Null";
    //            this.BackColor = Color.White;

    //        }
    //        else
    //        {
    //            if (cellData.restricted)
    //                this.BackColor = Color.Red;
    //            else if (cellData.SpawnedFood != null)
    //                this.BackColor = Color.OrangeRed;
    //            else
    //                this.BackColor = Color.LightGreen;

    //            label_SpawnValue.Text = cellData.SpawnValue.ToString("0.000");
    //        }
    //    }

    //    private void InitializeComponent()
    //    {
    //        this.label_SpawnValue = new System.Windows.Forms.Label();
    //        this.SuspendLayout();
    //        // 
    //        // label_SpawnValue
    //        // 
    //        this.label_SpawnValue.Dock = System.Windows.Forms.DockStyle.Fill;
    //        this.label_SpawnValue.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
    //        this.label_SpawnValue.Location = new System.Drawing.Point(0, 0);
    //        this.label_SpawnValue.Name = "label_SpawnValue";
    //        this.label_SpawnValue.Size = new System.Drawing.Size(50, 50);
    //        this.label_SpawnValue.TabIndex = 0;
    //        this.label_SpawnValue.Text = "SpawnValue";
    //        this.label_SpawnValue.TextAlign = System.Drawing.ContentAlignment.MiddleCenter;
    //        // 
    //        // ViewCell
    //        // 
    //        this.Controls.Add(this.label_SpawnValue);
    //        this.Name = "ViewCell";
    //        this.Size = new System.Drawing.Size(50, 50);
    //        this.ResumeLayout(false);

    //    }
    //}

}
